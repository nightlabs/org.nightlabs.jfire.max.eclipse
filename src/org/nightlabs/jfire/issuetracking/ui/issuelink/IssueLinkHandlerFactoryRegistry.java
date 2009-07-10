/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issuelink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueLinkHandlerFactoryRegistry 
extends AbstractEPProcessor
{
	protected static IssueLinkHandlerFactoryRegistry _sharedInstance = null;

	public static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.issuetracking.ui.issueLinkHandlerFactory"; //$NON-NLS-1$
	public static final String ELEMENT_CATEGORY = "issueLinkHandlerCategory"; //$NON-NLS-1$
	public static final String ELEMENT_FACTORY = "issueLinkHandlerFactory"; //$NON-NLS-1$
	public static final String ATTRIBUTE_CATEGORY_ID = "categoryId"; //$NON-NLS-1$
	public static final String ATTRIBUTE_PARENT_CATEGORY_ID = "parentCategoryId"; //$NON-NLS-1$
	public static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$
	public static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$
	public static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$
	public static final String ATTRIBUTE_ICON = "icon"; //$NON-NLS-1$
	
	public static synchronized IssueLinkHandlerFactoryRegistry sharedInstance()
	throws EPProcessorException
	{
		if (_sharedInstance == null) {
			_sharedInstance = new IssueLinkHandlerFactoryRegistry();
			_sharedInstance.process();
		}

		return _sharedInstance;
	}

	private Map<Class<? extends Object>, IssueLinkHandlerFactory> factories = new HashMap<Class<? extends Object>, IssueLinkHandlerFactory>();
	
	private Map<String, List<IssueLinkHandlerCategory>> parentCategoryId2Categories = new HashMap<String, List<IssueLinkHandlerCategory>>();
	
	private Map<String, List<IssueLinkHandlerFactory>> parentCategoryId2Factories = new HashMap<String, List<IssueLinkHandlerFactory>>();
		
	protected IssueLinkHandlerFactory getFactory(Class<? extends Object> linkedObjectClass,
			boolean throwExceptionIfNotFound)
	{
		IssueLinkHandlerFactory factory = factories.get(linkedObjectClass);
		if (throwExceptionIfNotFound && factory == null)
			throw new IllegalStateException("No IssueLinkHandlerFactory registered for linkedObjectClass=\""+ linkedObjectClass +"\""); //$NON-NLS-1$ //$NON-NLS-2$

		return factory;
	}
	
	public List<IssueLinkHandlerCategory> getTopLevelCategories() {
		List<IssueLinkHandlerCategory> cats = parentCategoryId2Categories.get(null);
		if (cats == null)
			return Collections.emptyList();
		return Collections.unmodifiableList(cats);
	}
	
	public IssueLinkHandlerFactory getIssueLinkHandlerFactory(Class<?> linkedObjectClass) {
		// Check for direct class
		IssueLinkHandlerFactory factory = factories.get(linkedObjectClass);
		if (factory == null) {
			// check hierarchy here			
			Class<?> sClass = linkedObjectClass.getSuperclass();
			while (!sClass.equals(Object.class)) {
				factory = factories.get(linkedObjectClass);
				if (factory != null)
					break;
			}
		}
		if (factory == null) {
			return new DefaultIssueLinkHandlerFactory();
		}
		return factory;
	}
	 
	
	protected void addFactory(IssueLinkHandlerFactory factory)
	{
		factories.put(factory.getLinkedObjectClass(), factory);
		List<IssueLinkHandlerFactory> cats = parentCategoryId2Factories.get(factory.getCategoryId());
		if (cats == null) {
			cats = new ArrayList<IssueLinkHandlerFactory>();
			parentCategoryId2Factories.put(factory.getCategoryId(), cats);
		}
		cats.add(factory);

	}
	
	@Override
	public String getExtensionPointID() 
	{
		return "org.nightlabs.jfire.issuetracking.ui.issueLinkHandlerFactory"; //$NON-NLS-1$
	}

	@Override
	public void processElement(IExtension extension, IConfigurationElement element) 
	throws Exception 
	{
		if (element.getName().equals(ELEMENT_FACTORY)) { //$NON-NLS-1$
			processIssueLinkHandlerFactory(extension, element);
		} else if (element.getName().equals(ELEMENT_CATEGORY)) { //$NON-NLS-1$
			processIssueLinkHandlerCategory(extension, element);
		}
	}
	
	private void processIssueLinkHandlerFactory(IExtension extension, IConfigurationElement element) throws Exception {
		try {
			IssueLinkHandlerFactory factory = (IssueLinkHandlerFactory) element.createExecutableExtension(ATTRIBUTE_CLASS); //$NON-NLS-1$
			String name = element.getAttribute(ATTRIBUTE_NAME); //$NON-NLS-1$
			String iconString = element.getAttribute(ATTRIBUTE_ICON);
			
			factory.setName(name);
			
			if (checkString(iconString)) {
				ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
						extension.getNamespaceIdentifier(), iconString);
				if (imageDescriptor != null)
					factory.setImage(imageDescriptor.createImage());
			}
			
			addFactory(factory);
			
		} catch (Throwable t) {
			throw new EPProcessorException("Extension to "+getExtensionPointID()+" with class "+element.getAttribute("class")+" has errors!", t); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}		
	}

	private void processIssueLinkHandlerCategory(IExtension extension, IConfigurationElement element) throws Exception {
		String categoryId = element.getAttribute(ATTRIBUTE_ID); //$NON-NLS-1$
		String name = element.getAttribute(ATTRIBUTE_NAME); //$NON-NLS-1$
		String parentCategoryId = element.getAttribute(ATTRIBUTE_PARENT_CATEGORY_ID); //$NON-NLS-1$
		String iconString = element.getAttribute(ATTRIBUTE_ICON);
		
		IssueLinkHandlerCategory category = new IssueLinkHandlerCategory();
		category.setCategoryId(categoryId);
		category.setName(name);
		if ("".equals(parentCategoryId)) { //$NON-NLS-1$
			parentCategoryId = null;
		}
		category.setParentCategoryId(parentCategoryId);

		if (checkString(iconString)) {
			ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					extension.getNamespaceIdentifier(), iconString);
			if (imageDescriptor != null)
				category.setImage(imageDescriptor.createImage());
		}
		
		List<IssueLinkHandlerCategory> cats = parentCategoryId2Categories.get(parentCategoryId);
		if (cats == null) {
			cats = new ArrayList<IssueLinkHandlerCategory>();
			parentCategoryId2Categories.put(parentCategoryId, cats);
		}
		cats.add(category);
	}
	
	@Override
	public synchronized void process() {
		super.process();
		List<IssueLinkHandlerCategory> tops = getTopLevelCategories();
		for (IssueLinkHandlerCategory topCat : tops) {
			validateCategory(topCat);
		}
	}
	
	private void validateCategory(IssueLinkHandlerCategory category) {
		List<IssueLinkHandlerFactory> factories = parentCategoryId2Factories.get(category.getCategoryId());
		if (factories != null) {
			for (IssueLinkHandlerFactory childFactory : factories) {
				category.addChildFactory(childFactory);
			}
		}
		List<IssueLinkHandlerCategory> children = parentCategoryId2Categories.get(category.getCategoryId());
		if (children != null) {
			for (IssueLinkHandlerCategory child : children) {
				category.addChildCategory(child);
				child.setParent(category);
				validateCategory(child);
			}
		}
	}
}
