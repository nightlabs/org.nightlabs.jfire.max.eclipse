package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;

public class IssueOverviewRegistry 
extends AbstractEPProcessor
{
	private static final Logger logger = Logger.getLogger(IssueOverviewRegistry.class);
	
	public static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.issuetracking.overview"; //$NON-NLS-1$
	public static final String ELEMENT_CATEGORY = "category"; //$NON-NLS-1$
	public static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$

	private static IssueOverviewRegistry sharedInstance;
	public static IssueOverviewRegistry sharedInstance() {
		if (sharedInstance == null) {
			synchronized (IssueOverviewRegistry.class) {
				if (sharedInstance == null)
					sharedInstance = new IssueOverviewRegistry();
			}
		}
		return sharedInstance;
	}
	
	protected IssueOverviewRegistry() {
	}

	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}
	
	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception 
	{
		if (element.getName().equalsIgnoreCase(ELEMENT_CATEGORY)) 
		{
			try {
				IssueCategoryFactory category = (IssueCategoryFactory) element.createExecutableExtension(
						ATTRIBUTE_CLASS);
				int index = category.getIndex();
				if (index2Category.containsKey(index) || index < 0) {
					int lastIndex = index2Category.size();
					index2Category.put(lastIndex, category);
					logger.warn("IssueCategoryFactory with index "+index+" already registered will use last index "+lastIndex);					 //$NON-NLS-1$ //$NON-NLS-2$
				}
				else {
					index2Category.put(index, category);
				}
			} catch (CoreException e) {
				logger.error("Could not instantiate IssueCategoryFactory"); //$NON-NLS-1$
			}
		}
	}

	private SortedMap<Integer, IssueCategoryFactory> index2Category = new TreeMap<Integer, IssueCategoryFactory>();
	
	public SortedMap<Integer, IssueCategoryFactory> getIndex2Catgeory() {
		checkProcessing();
		return index2Category;
	}
	
	public IssueCategoryFactory getCategory(int index) {
		checkProcessing();
		return index2Category.get(index);
	}
}
