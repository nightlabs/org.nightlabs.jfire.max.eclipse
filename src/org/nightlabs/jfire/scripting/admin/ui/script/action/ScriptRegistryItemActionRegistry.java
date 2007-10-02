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
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.scripting.admin.ui.script.action;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.action.IAction;
import org.nightlabs.base.ui.action.registry.AbstractActionRegistry;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class ScriptRegistryItemActionRegistry extends AbstractActionRegistry {

	public static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.scripting.admin.ui.scriptRegistryItemAction";
	
	/**
	 * 
	 */
	public ScriptRegistryItemActionRegistry() {
		super();
	}

	private static final String ATTRIBUTE_NAME_ACTION_CLASS = "class";
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.action.registry.AbstractActionRegistry#createActionOrContributionItem(org.eclipse.core.runtime.IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	protected Object createActionOrContributionItem(IExtension extension,
			IConfigurationElement element) throws EPProcessorException {
		
		String className = element.getAttribute(ATTRIBUTE_NAME_ACTION_CLASS);
		if (className == null || "".equals(className))
			throw new IllegalArgumentException("There was no classname specified for reportRegistryItemAction with id "+element.getAttribute(ATTRIBUTE_NAME_ACTION_ID));

		IScriptRegistryItemAction registryItemAction;
		try {
			registryItemAction = (IScriptRegistryItemAction) element.createExecutableExtension(ATTRIBUTE_NAME_ACTION_CLASS);
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("The class specified for registryItemAction with id "+element.getAttribute(ATTRIBUTE_NAME_ACTION_ID)+" does not implement "+IScriptRegistryItemAction.class.getName()+". It was set to "+className+".");
		} catch (CoreException e) {
			throw new EPProcessorException(e);
		}
		return registryItemAction;
	}

	@Override
	protected void initAction(IAction action, IExtension extension, IConfigurationElement element) throws EPProcessorException {
		super.initAction(action, extension, element);
		action.setEnabled(true);
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}
	

	@Override
	protected String getActionElementName()
	{
		return "scriptRegistryItemAction";
	}
	
	private static ScriptRegistryItemActionRegistry sharedInstance;
	private static boolean initializingSharedInstance = false;
	public static synchronized ScriptRegistryItemActionRegistry sharedInstance()
	throws EPProcessorException
	{
		if (initializingSharedInstance)
			throw new IllegalStateException("Circular call to the method sharedInstance() during initialization!");

		if (sharedInstance == null) {
			initializingSharedInstance = true;
			try {
				sharedInstance = new ScriptRegistryItemActionRegistry();
				sharedInstance.process();
			} finally {
				initializingSharedInstance = false;
			}
		}

		return sharedInstance;
	}
}
