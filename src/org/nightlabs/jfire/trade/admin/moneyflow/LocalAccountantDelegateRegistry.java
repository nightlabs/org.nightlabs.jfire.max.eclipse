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
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class LocalAccountantDelegateRegistry extends AbstractEPProcessor {

	public static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.trade.admin.localaccountantdelegatetype"; //$NON-NLS-1$
	
	/**
	 * key: Class delegateClass
	 * value: LocalAccountantDelegateType type
	 */
	private Map<Class, LocalAccountantDelegateType> typesByClass = new HashMap<Class, LocalAccountantDelegateType>();
	
	/**
	 * key: Class mappingClass
	 * value: LocalAccountantDelegateType type
	 */
	private Map<Class, LocalAccountantDelegateType> typesByMappingClasses = new HashMap<Class, LocalAccountantDelegateType>();
	/**
	 * 
	 */
	public LocalAccountantDelegateRegistry() {
		super();
	}

	/**
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}
	
	
	public Collection<LocalAccountantDelegateType> getTypes() {
		checkProcessing();
		return typesByClass.values();
	}
	
	public Collection<Class> getDelegateClasses() {
		checkProcessing();
		return typesByClass.keySet();
	}
	
	public LocalAccountantDelegateType getType(Class delegateClass) {
		checkProcessing();
		return (LocalAccountantDelegateType)typesByClass.get(delegateClass);
	}

	public LocalAccountantDelegateType getTypeForMapping(Class mappingClass) {
		checkProcessing();
		LocalAccountantDelegateType result = (LocalAccountantDelegateType)typesByMappingClasses.get(mappingClass);
		if (result == null) {
			for (LocalAccountantDelegateType type : typesByClass.values()) {
				if (type.canHandleMappingType(mappingClass)) {
					result = type;
					typesByMappingClasses.put(mappingClass, result);
				}
			}
		}
		return result;
		
	}
	
	/**
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#processElement(IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception 
	{
		if (element.getName().equalsIgnoreCase("localAccountantDelegateType")) { //$NON-NLS-1$
			LocalAccountantDelegateType type = null;
			try {
				type = (LocalAccountantDelegateType)element.createExecutableExtension("class"); //$NON-NLS-1$
			} catch (CoreException e) {
				throw new EPProcessorException(e);
			}
			typesByClass.put(type.getDelegateClass(), type);
		}
	}
	
	private static LocalAccountantDelegateRegistry sharedInstance;
	
	public static LocalAccountantDelegateRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new LocalAccountantDelegateRegistry();
		return sharedInstance;
	}

}
