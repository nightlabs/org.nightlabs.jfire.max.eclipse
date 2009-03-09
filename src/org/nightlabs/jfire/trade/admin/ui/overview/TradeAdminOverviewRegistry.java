package org.nightlabs.jfire.trade.admin.ui.overview;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class TradeAdminOverviewRegistry
extends AbstractEPProcessor
{
	private static final Logger logger = Logger.getLogger(TradeAdminOverviewRegistry.class);
	
	public static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.trade.admin.ui.overview"; //$NON-NLS-1$
	public static final String ELEMENT_CATEGORY = "category"; //$NON-NLS-1$
	public static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$
	
	private static TradeAdminOverviewRegistry sharedInstance;
	public static TradeAdminOverviewRegistry sharedInstance() {
		if (sharedInstance == null) {
			synchronized (TradeAdminOverviewRegistry.class) {
				if (sharedInstance == null)
					sharedInstance = new TradeAdminOverviewRegistry();
			}
		}
		return sharedInstance;
	}
	
	protected TradeAdminOverviewRegistry() {
		checkProcessing();
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
				TradeAdminCategoryFactory category = (TradeAdminCategoryFactory) element.createExecutableExtension(
						ATTRIBUTE_CLASS);
				int index = category.getIndex();
				if (index2Category.containsKey(index) || index < 0) {
					int lastIndex = index2Category.size();
					index2Category.put(lastIndex, category);
					logger.warn("TradeAdminCategoryFactory with index "+index+" already registered will use last index "+lastIndex);					 //$NON-NLS-1$ //$NON-NLS-2$
				}
				else {
					index2Category.put(index, category);
				}
			} catch (CoreException e) {
				logger.error("Could not instantiate TradeAdminCategoryFactory"); //$NON-NLS-1$
			}
		}
	}

	private SortedMap<Integer, TradeAdminCategoryFactory> index2Category = new TreeMap<Integer, TradeAdminCategoryFactory>();
	
//	public SortedMap<Integer, TradeAdminCategoryFactory> getIndex2Catgeory() {
//		checkProcessing();
//		return index2Category;
//	}
	
	public Collection<TradeAdminCategoryFactory> getCategories()
	{
		return Collections.unmodifiableCollection(index2Category.values());
	}

	public TradeAdminCategoryFactory getCategory(int index) {
		return index2Category.get(index);
	}
}
