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
	
	public static synchronized IssueLinkHandlerFactoryRegistry sharedInstance()
	throws EPProcessorException
	{
		if (_sharedInstance == null) {
			_sharedInstance = new IssueLinkHandlerFactoryRegistry();
			_sharedInstance.process();
		}

		return _sharedInstance;
	}

	/**
	 * key: String entryType<br/>
	 * value: IssueAttachmentDocumentAdderFactory
	 */
	private Map<Class<? extends Object>, IssueLinkHandlerFactory> factories = new HashMap<Class<? extends Object>, IssueLinkHandlerFactory>();
	
	private Map<String, List<IssueLinkHandlerCategory>> parentCategoryId2Categories = new HashMap<String, List<IssueLinkHandlerCategory>>();
	
	private Map<String, List<IssueLinkHandlerFactory>> parentCategoryId2Factories = new HashMap<String, List<IssueLinkHandlerFactory>>();
		
	protected IssueLinkHandlerFactory getFactory(Class<? extends Object> linkObjectClass,
			boolean throwExceptionIfNotFound)
	{
		IssueLinkHandlerFactory factory = factories.get(linkObjectClass);
		if (throwExceptionIfNotFound && factory == null)
			throw new IllegalStateException("No IssueAttachmentObjectEntryAdderFactory registered for linkObjectClass=\""+ linkObjectClass +"\"");

		return factory;
	}
	
	public List<IssueLinkHandlerCategory> getTopLevelCategories() {
		List<IssueLinkHandlerCategory> cats = parentCategoryId2Categories.get(null);
		if (cats == null)
			return Collections.emptyList();
		return Collections.unmodifiableList(cats);
	}
	 
	
	protected void addFactory(IssueLinkHandlerFactory factory)
	{
		factories.put(factory.getLinkObjectClass(), factory);
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
		if (element.getName().equals("issueLinkHandlerFactory")) {
			processIssueLinkHandlerFactory(extension, element);
		} else if (element.getName().equals("issueLinkHandlerCategory")) {
			processIssueLinkHandlerFactory(extension, element);
		}
	}
	
	protected void processIssueLinkHandlerFactory(IExtension extension, IConfigurationElement element) throws Exception {
		try {
			IssueLinkHandlerFactory factory = (IssueLinkHandlerFactory) element.createExecutableExtension("class"); //$NON-NLS-1$
			
			String entryType = element.getAttribute("entryType"); //$NON-NLS-1$
			factory.setEntryType(entryType);

			addFactory(factory);
			
		} catch (Throwable t) {
			throw new EPProcessorException("Extension to "+getExtensionPointID()+" with class "+element.getAttribute("class")+" has errors!", t); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}		
	}

	protected void processIssueLinkHandlerCategory(IExtension extension, IConfigurationElement element) throws Exception {
		try {
			String categoryId = element.getAttribute("categoryId");
			String name = element.getAttribute("name");
			String parentCategoryId = element.getAttribute("parentCategoryId");
			
			IssueLinkHandlerCategory category = new IssueLinkHandlerCategory();
			category.setCategoryId(categoryId);
			category.setName(name);
			if ("".equals(parentCategoryId)) {
				parentCategoryId = null;
			}
			category.setParentCategoryId(parentCategoryId);
			
			List<IssueLinkHandlerCategory> cats = parentCategoryId2Categories.get(parentCategoryId);
			if (cats == null) {
				cats = new ArrayList<IssueLinkHandlerCategory>();
				parentCategoryId2Categories.put(parentCategoryId, cats);
			}
			cats.add(category);
			
		} catch (Throwable t) {
			throw new EPProcessorException("Extension to "+getExtensionPointID()+" with class "+element.getAttribute("class")+" has errors!", t); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}		
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
		for (IssueLinkHandlerFactory childFactory : factories) {
			category.addChildFactory(childFactory);
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
