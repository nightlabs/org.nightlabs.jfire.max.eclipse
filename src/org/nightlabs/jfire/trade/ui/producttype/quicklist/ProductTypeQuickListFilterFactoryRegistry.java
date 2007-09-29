/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.trade.ui.producttype.quicklist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class ProductTypeQuickListFilterFactoryRegistry extends AbstractEPProcessor {
	
	public static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.trade.ui.productTypeQuickListFilterFactory"; //$NON-NLS-1$
	public static final String FILTER_ELEMENT_NAME = "productTypeQuickListFilterFactory"; //$NON-NLS-1$

	/**
	 * key: String id<br/>
	 * value: ProductTypeQuickListFilterFactory filter
	 */
	protected Map<String, IProductTypeQuickListFilterFactory> factories = new HashMap<String, IProductTypeQuickListFilterFactory>();
	

	public ProductTypeQuickListFilterFactoryRegistry() {
		super();
	}

	/**
	 * Add a factory to the registry.
	 * @param id The factory's id.
	 * @param filter The filter to add
	 */
	public void addProductQuickListFilterFactory(IProductTypeQuickListFilterFactory factory) {
		factories.put(factory.getId(), factory);
	}
	
	
	public IProductTypeQuickListFilterFactory getProductQuickListFilterFactory(String id) 
	throws EPProcessorException 
	{
		checkProcessing();
		return factories.get(id);
	}
	
	/**
	 * Get all registered filters in a sorted collection.
	 * They will be sorted by their index.
	 * 
	 * @return All registered <tt>IProductTypeQuickListFilter</tt>s
	 */
	public Collection<IProductTypeQuickListFilterFactory> getProductQuickListFilterFactories()
	throws EPProcessorException
	{
		checkProcessing();
		List<IProductTypeQuickListFilterFactory> result = new ArrayList<IProductTypeQuickListFilterFactory>(factories.values());
		Collections.sort(result, new Comparator<IProductTypeQuickListFilterFactory>() {
			public int compare(IProductTypeQuickListFilterFactory o1, IProductTypeQuickListFilterFactory o2) {
				return Integer.valueOf(o1.getIndex()).compareTo(o2.getIndex());
			}
		});
		return result;
	}
	
	/**
	 * Get the map of all registered factories,
	 * @return The map of all registered <tt>ProductTypeQuickListFilterFactory</tt>s
	 */
	protected Map<String, IProductTypeQuickListFilterFactory> getProductQuickListFilterFactoryMap()
	throws EPProcessorException
	{
		checkProcessing();
		return factories;
	}

	/**
	 * @see org.nightlabs.base.ui.extensionpoint.IEPProcessor#getExtensionPointID()
	 */
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	/**
	 * @see org.nightlabs.base.ui.extensionpoint.IEPProcessor#processElement(IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	public void processElement(IExtension extension, IConfigurationElement element) throws Exception {
		if (element.getName().equals(FILTER_ELEMENT_NAME)) {
			IProductTypeQuickListFilterFactory factory = null;
			try {
				factory = (IProductTypeQuickListFilterFactory)element.createExecutableExtension("class"); //$NON-NLS-1$
			} catch (CoreException e) {
				throw new EPProcessorException("Could not instanciate the IProductTypeQuickListFilter", e); //$NON-NLS-1$
			}
			addProductQuickListFilterFactory(factory);
		}
		else 
			throw new EPProcessorException("element "+element.getName()+" not supported by extension-point "+EXTENSION_POINT_ID+"!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	
	private static ProductTypeQuickListFilterFactoryRegistry sharedInstance;
	
	public static ProductTypeQuickListFilterFactoryRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new ProductTypeQuickListFilterFactoryRegistry();
		return sharedInstance;
	}

}
