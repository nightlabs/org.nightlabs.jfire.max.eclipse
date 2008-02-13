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

package org.nightlabs.jfire.reporting.ui.layout.action;

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
public class ReportRegistryItemActionRegistry extends AbstractActionRegistry {

	public static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.reporting.ui.reportRegistryItemAction"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	public ReportRegistryItemActionRegistry() {
		super();
	}

	private static final String ATTRIBUTE_NAME_ACTION_CLASS = "class"; //$NON-NLS-1$
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.action.registry.AbstractActionRegistry#createActionOrContributionItem(org.eclipse.core.runtime.IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	protected Object createActionOrContributionItem(IExtension extension,
			IConfigurationElement element)
	throws EPProcessorException
	{
		String scope = element.getAttribute("scope"); //$NON-NLS-1$
		String className = element.getAttribute(ATTRIBUTE_NAME_ACTION_CLASS);
		if (className == null || "".equals(className)) //$NON-NLS-1$
			throw new IllegalArgumentException("There was no classname specified for reportRegistryItemAction with id "+element.getAttribute(ATTRIBUTE_NAME_ACTION_ID)); //$NON-NLS-1$

		IReportRegistryItemAction registryItemAction;
		try {
			registryItemAction = (IReportRegistryItemAction) element.createExecutableExtension(ATTRIBUTE_NAME_ACTION_CLASS);
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("The class specified for registryItemAction with id "+element.getAttribute(ATTRIBUTE_NAME_ACTION_ID)+" does not implement "+IReportRegistryItemAction.class.getName()+". It was set to "+className+"."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		} catch (CoreException e) {
			throw new EPProcessorException(e);
		}
		if ("".equals(scope)) //$NON-NLS-1$
			scope = null;
		registryItemAction.setScope(scope);
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
		return "reportRegistryItemAction"; //$NON-NLS-1$
	}
	
	private static ReportRegistryItemActionRegistry sharedInstance;
	private static boolean initializingSharedInstance = false;
	public static synchronized ReportRegistryItemActionRegistry sharedInstance()
	throws EPProcessorException
	{
		if (initializingSharedInstance)
			throw new IllegalStateException("Circular call to the method sharedInstance() during initialization!"); //$NON-NLS-1$

		if (sharedInstance == null) {
			initializingSharedInstance = true;
			try {
				sharedInstance = new ReportRegistryItemActionRegistry();
				sharedInstance.process();
			} finally {
				initializingSharedInstance = false;
			}
		}

		return sharedInstance;
	}
}
