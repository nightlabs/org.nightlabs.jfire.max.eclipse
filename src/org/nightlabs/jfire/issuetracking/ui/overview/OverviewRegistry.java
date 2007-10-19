package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.jfire.base.ui.overview.AbstractEntryFactory;
import org.nightlabs.jfire.base.ui.overview.Category;
import org.nightlabs.jfire.base.ui.overview.CategoryFactory;
import org.nightlabs.jfire.base.ui.overview.DefaultCategoryFactory;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.EntryFactory;
import org.nightlabs.jfire.base.ui.resource.Messages;

/**
 * {@link OverviewRegistry} processes extension points with a schema according
 * to overview.exsd in this plugin. It holds the registered 
 * {@link CategoryFactory} - {@link EntryFactory} structure and can build 
 * {@link Category} - {@link Entry} instances out of it.
 * 
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class OverviewRegistry 
extends AbstractEPProcessor 
{
	public static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.base.ui.overview"; //$NON-NLS-1$
	public static final String ELEMENT_CATEGORY = "categoryFactory"; //$NON-NLS-1$
	public static final String ELEMENT_CATEGORY_ENTRY = "entryFactory"; //$NON-NLS-1$
	public static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$
	public static final String ATTRIBUTE_CATEGORY_ID = "categoryID"; //$NON-NLS-1$
	public static final String ATTRIBUTE_CATEGORY_ENTRY_ID = "categoryEntryID"; //$NON-NLS-1$
	public static final String ATTRIBUTE_ENTRY_FACTORY_CLASS = "class"; //$NON-NLS-1$
	public static final String ATTRIBUTE_ICON = "icon"; //$NON-NLS-1$
	public static final String ATTRIBUTE_INDEX = "index"; //$NON-NLS-1$
	public static final String ATTRIBUTE_CATEGORY_FACTORY_CLASS = "class"; //$NON-NLS-1$
	
	protected OverviewRegistry() {
		super();
	}

	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception 
	{
		if (element.getName().equals(ELEMENT_CATEGORY)) {
			String categoryID = element.getAttribute(ATTRIBUTE_CATEGORY_ID);
			String name = element.getAttribute(ATTRIBUTE_NAME);
			String iconString = element.getAttribute(ATTRIBUTE_ICON);
			String indexString = element.getAttribute(ATTRIBUTE_INDEX);
			CategoryFactory categoryFactory = null;
			String className = element.getAttribute(ATTRIBUTE_CATEGORY_FACTORY_CLASS);
			if (className == null || "".equals(className)) { //$NON-NLS-1$
				DefaultCategoryFactory defaultCategory = new DefaultCategoryFactory();
				defaultCategory.setName(name);
				defaultCategory.setCategoryID(categoryID);
				if (checkString(iconString)) {
					ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
							extension.getNamespaceIdentifier(), iconString);
					if (imageDescriptor != null)
						defaultCategory.setImage(imageDescriptor.createImage());										
				}
				if (checkString(indexString)) {
					int index = Integer.valueOf(indexString);
					defaultCategory.setIndex(index);
				}
				categoryFactory = defaultCategory;
			}
			else {
				try {
					categoryFactory = (CategoryFactory) element.createExecutableExtension(ATTRIBUTE_CATEGORY_FACTORY_CLASS);
				} catch (CoreException e) {
					throw new EPProcessorException(e);
				}
			}
			
			categoryID2CategoryFactory.put(categoryID, categoryFactory);
		}		
		if (element.getName().equals(ELEMENT_CATEGORY_ENTRY)) {
			String categoryID = element.getAttribute(ATTRIBUTE_CATEGORY_ID);
//			String categoryEntryID = element.getAttribute(ATTRIBUTE_CATEGORY_ENTRY_ID);
//			String name = element.getAttribute(ATTRIBUTE_NAME);
//			String iconString = element.getAttribute(ATTRIBUTE_ICON);
			try {
				EntryFactory entryFactory = (EntryFactory) element.createExecutableExtension(ATTRIBUTE_ENTRY_FACTORY_CLASS);
//				entry.setName(name);
//				if (checkString(iconString)) {
//					ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
//							extension.getNamespaceIdentifier(), iconString);
//					if (imageDescriptor != null)
//						entry.setImage(imageDescriptor.createImage());										
//				}				
				List<EntryFactory> entryFactories = tmpCategoryID2EntryFatories.get(categoryID);
				if (entryFactories == null)
					entryFactories = new ArrayList<EntryFactory>();
								
				try {
					if (entryFactory.getIndex() == -1 || entryFactories.get(entryFactory.getIndex()) != null) {
						if (entryFactory instanceof AbstractEntryFactory) {
							int index = entryFactories.size();
							AbstractEntryFactory abstractEntryFactory = (AbstractEntryFactory) entryFactory;
							abstractEntryFactory.setIndex(index);
						}											
					}					
				} catch (IndexOutOfBoundsException e) {
					if (entryFactory instanceof AbstractEntryFactory) {
						int index = entryFactories.size();
						AbstractEntryFactory abstractEntryFactory = (AbstractEntryFactory) entryFactory;
						abstractEntryFactory.setIndex(index);
					}										
				}
				
				entryFactories.add(entryFactory.getIndex(), entryFactory);				
				tmpCategoryID2EntryFatories.put(categoryID, entryFactories);
			} catch (CoreException e) {
				throw new EPProcessorException(e);
			}
		}
	}

	private Map<String, CategoryFactory> categoryID2CategoryFactory = new HashMap<String, CategoryFactory>();

	private Map<String, List<EntryFactory>> tmpCategoryID2EntryFatories = new HashMap<String, List<EntryFactory>>();

	private CategoryFactory fallBackCategory = null;
	
	public CategoryFactory getFallbackCategory() {
		if (fallBackCategory == null) {
			DefaultCategoryFactory fallBackCategory = new DefaultCategoryFactory();
			fallBackCategory.setName(Messages.getString("org.nightlabs.jfire.base.ui.overview.OverviewRegistry.fallBackCategory.name")); //$NON-NLS-1$
			this.fallBackCategory = fallBackCategory;
		}
		return fallBackCategory;
	}

	@Override
	public synchronized void process() {
		super.process();
		assignEntryFactoriesToCategoryFactories();
	}
	
	protected void assignEntryFactoriesToCategoryFactories() {
		checkProcessing();
		for (Map.Entry<String, List<EntryFactory>> mapEntry : tmpCategoryID2EntryFatories.entrySet()) {
			CategoryFactory categoryFactory = categoryID2CategoryFactory.get(mapEntry.getKey());
			List<EntryFactory> entryFactories = mapEntry.getValue();
			if (categoryFactory == null)
				categoryFactory = getFallbackCategory();
			categoryFactory.getEntryFactories().addAll(entryFactories);
		}			
	}

	public List<CategoryFactory> getCategoryFacories() {
		checkProcessing();
		List<CategoryFactory> factories = new ArrayList<CategoryFactory>(categoryID2CategoryFactory.values());
		Collections.sort(factories, categoryComparator);
		if (fallBackCategory != null)
			factories.add(fallBackCategory);
		return factories;
	}
	
	public List<EntryFactory> getEntryFactories(String categoryID) {
		return new ArrayList<EntryFactory>(categoryID2CategoryFactory.get(categoryID).getEntryFactories());
	}
	
	public List<Category> createCategories() {
		List<CategoryFactory> factories = getCategoryFacories();
		List<Category> categories = new ArrayList<Category>(factories.size());
		for (CategoryFactory factory : factories) {
			categories.add(factory.createCategory());
		}
		return categories;
	}
	
	private Comparator<CategoryFactory> categoryComparator = new Comparator<CategoryFactory>(){	
		public int compare(CategoryFactory c1, CategoryFactory c2) {
			return c1.getIndex() - c2.getIndex();
		}	
	};
}