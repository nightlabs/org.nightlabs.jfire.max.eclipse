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
package org.nightlabs.jfire.scripting.editor2d.ui.script;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ScriptParameterProviderRegistry
extends AbstractEPProcessor
{
	private static final Logger logger = Logger.getLogger(ScriptParameterProviderRegistry.class);
	
	public static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.scripting.editor2d.ui.scriptParameterProvider"; //$NON-NLS-1$
	public static final String ELEMENT_SCRIPT_PARAMETER_PROVIDER = "scriptParameterProvider"; //$NON-NLS-1$
	public static final String ATTRIBUTE_ORGANISATION_ID = "organisationID"; //$NON-NLS-1$
	public static final String ATTRIBUTE_SCRIPT_REGISTRY_ITEM_TYPE = "scriptRegistryItemType";	 //$NON-NLS-1$
	public static final String ATTRIBUTE_SCRIPT_REGISTRY_ITEM_ID = "scriptRegistryItemID";	 //$NON-NLS-1$
	public static final String ATTRIBUTE_SCRIPT_PARAMETER_PROVIDER = "scriptParameterProvider";	 //$NON-NLS-1$

	private static ScriptParameterProviderRegistry sharedInstance;
	public static ScriptParameterProviderRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new ScriptParameterProviderRegistry();
		return sharedInstance;
	}
		
	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}
	
	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception
	{
		if (element.getName().equalsIgnoreCase(ELEMENT_SCRIPT_PARAMETER_PROVIDER))
		{
			String organisationID = element.getAttribute(ATTRIBUTE_ORGANISATION_ID);
			if (!checkString(organisationID))
				throw new EPProcessorException("Attribute "+ATTRIBUTE_ORGANISATION_ID+" must not be empty!"); //$NON-NLS-1$ //$NON-NLS-2$
			
			String scriptRegistryItemType = element.getAttribute(ATTRIBUTE_SCRIPT_REGISTRY_ITEM_TYPE);
			if (!checkString(scriptRegistryItemType))
				throw new EPProcessorException("Attribute "+ATTRIBUTE_SCRIPT_REGISTRY_ITEM_TYPE+" must not be empty!"); //$NON-NLS-1$ //$NON-NLS-2$

			String scriptRegistryItemID = element.getAttribute(ATTRIBUTE_SCRIPT_REGISTRY_ITEM_ID);
			if (!checkString(scriptRegistryItemID))
				throw new EPProcessorException("Attribute "+ATTRIBUTE_SCRIPT_REGISTRY_ITEM_ID+" must not be empty!"); //$NON-NLS-1$ //$NON-NLS-2$
					
			try {
				IScriptParameterProvider scriptParameterProvider = (IScriptParameterProvider) element.createExecutableExtension(ATTRIBUTE_SCRIPT_PARAMETER_PROVIDER);
				ScriptRegistryItemID itemID = ScriptRegistryItemID.create(organisationID, scriptRegistryItemType, scriptRegistryItemID);
				scriptID2ParameterProvider.put(itemID, scriptParameterProvider);
			} catch (CoreException e) {
				logger.warn("ScriptParameterProvider "+ATTRIBUTE_SCRIPT_PARAMETER_PROVIDER+" could not be created!", e); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	private Map<ScriptRegistryItemID, IScriptParameterProvider> scriptID2ParameterProvider =
		new HashMap<ScriptRegistryItemID, IScriptParameterProvider>();
	
	public IScriptParameterProvider getScriptParameterProvider(ScriptRegistryItemID scriptRegistryItemID)
	{
		checkProcessing();
		return scriptID2ParameterProvider.get(scriptRegistryItemID);
	}
	
	public Collection<IScriptParameterProvider> getScriptParameterProviders(Collection<ScriptRegistryItemID> scriptIDs)
	{
		checkProcessing();
		Collection<IScriptParameterProvider> providers = new ArrayList<IScriptParameterProvider>(scriptIDs.size());
		for (ScriptRegistryItemID itemID : scriptIDs) {
			providers.add(getScriptParameterProvider(itemID));
		}
		return providers;
	}
	
	public Map<ScriptRegistryItemID, Map<String, Object>> getParameterValues(Collection<ScriptRegistryItemID> scriptIDs)
	{
		Map<ScriptRegistryItemID, Map<String, Object>> scriptID2ParameterValues =
			new HashMap<ScriptRegistryItemID, Map<String,Object>>();
		for (ScriptRegistryItemID itemID : scriptIDs) {
			IScriptParameterProvider provider = getScriptParameterProvider(itemID);
			if (provider != null) {
				scriptID2ParameterValues.put(itemID, provider.getParameterValues());
			}
		}
		return scriptID2ParameterValues;
	}
	
}
