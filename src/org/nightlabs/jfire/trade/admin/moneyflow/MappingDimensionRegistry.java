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

package org.nightlabs.jfire.trade.admin.moneyflow;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;

/**
 * Registry holding {@link org.nightlabs.jfire.trade.admin.moneyflow.MappingDimension}.
 *  
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public class MappingDimensionRegistry extends AbstractEPProcessor {

	public static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.trade.admin.moneyflowdimension"; //$NON-NLS-1$

	/**
	 * key: String moneyFlowDimensionID
	 * value: MappingDimension dimension
	 */
	private Map dimensionsByID = new HashMap();
	
	/**
	 * key: String cellEditorPropertyName
	 * value: MappingDimension dimension
	 */
	private Map dimensionsByCellProperty = new HashMap();
	
	/**
	 * 
	 */
	public MappingDimensionRegistry() {
		super();
	}

	/**
	 * Returns all registered Dimensions.
	 */
	public Collection getDimensions() {
		checkProcessing();
		return dimensionsByID.values();
	}

	/**
	 * Returns the Dimensin with the given moneyFlowDimensionID, or null if no
	 * MappingDimension with this moneyFlowDimensionID is registered.
	 *  
	 * @param moneyFlowDimensionID The moneyFlowDimensionID of the desired MappingDimension
	 */
	public MappingDimension getDimension(String moneyFlowDimensionID) {
		checkProcessing();
		return (MappingDimension)dimensionsByID.get(moneyFlowDimensionID);
	}
	
	public MappingDimension getDimensionByPropertyName(String cellEditorPropertyName) {
		checkProcessing();
		return (MappingDimension)dimensionsByCellProperty.get(cellEditorPropertyName);
	}

	/**
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	/**
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#processElement(IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	public void processElement(IExtension extension, IConfigurationElement element)
			throws Exception {
		if (element.getName().equalsIgnoreCase("moneyFlowDimension")) { //$NON-NLS-1$
			MappingDimension dimension = null;
			try {
				dimension = (MappingDimension)element.createExecutableExtension("class"); //$NON-NLS-1$
			} catch (CoreException e) {
				throw new EPProcessorException(e);
			}
			dimensionsByID.put(dimension.getMoneyFlowDimensionID(), dimension);
			dimensionsByCellProperty.put(dimension.getCellEditorPropertyName(), dimension);
		}			
	}
	
	private static MappingDimensionRegistry sharedInstance;
	
	/**
	 * Returns the static shared instance of this registry.
	 */
	public static MappingDimensionRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new MappingDimensionRegistry();
		return sharedInstance;
	}
}
