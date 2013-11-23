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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.eclipse.extension.AbstractEPProcessor;
import org.nightlabs.eclipse.extension.EPProcessorException;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ScriptConditionerContextRegistry
extends AbstractEPProcessor
{
	public static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.scripting.editor2d.ui.scriptConditionerContext"; //$NON-NLS-1$

	public static final String ELEMENT_SCRIPT_CONDITIONER_CONTEXT = "scriptConditionerContext";	 //$NON-NLS-1$
	public static final String ATTRIBUTE_ORGANISATION_ID = "organisationID"; //$NON-NLS-1$
	public static final String ATTRIBUTE_CONDITION_CONTEXT_PROVIDER_ID = "conditionContextProviderID";	 //$NON-NLS-1$
	public static final String ATTRIBUTE_SCRIPT_DRAWCOMPONENT_CLASS = "scriptDrawComponentClass";	 //$NON-NLS-1$

	private static ScriptConditionerContextRegistry sharedInstance;
	public static ScriptConditionerContextRegistry sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new ScriptConditionerContextRegistry();
		}
		return sharedInstance;
	}

	protected ScriptConditionerContextRegistry() {
	}

	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception
	{
		if (element.getName().equalsIgnoreCase(ELEMENT_SCRIPT_CONDITIONER_CONTEXT))
		{
			String organisationID = element.getAttribute(ATTRIBUTE_ORGANISATION_ID);
			if (!checkString(organisationID))
				throw new EPProcessorException("Attribute "+ATTRIBUTE_ORGANISATION_ID+" must not be empty!");  //$NON-NLS-1$//$NON-NLS-2$

			String conditionContextProviderID = element.getAttribute(ATTRIBUTE_CONDITION_CONTEXT_PROVIDER_ID);
			if (!checkString(conditionContextProviderID))
				throw new EPProcessorException("Attribute "+ATTRIBUTE_CONDITION_CONTEXT_PROVIDER_ID+" must not be empty!"); //$NON-NLS-1$ //$NON-NLS-2$

			String scriptDrawComponentName = element.getAttribute(ATTRIBUTE_SCRIPT_DRAWCOMPONENT_CLASS);
			if (!checkString(conditionContextProviderID))
				throw new EPProcessorException("Attribute "+ATTRIBUTE_SCRIPT_DRAWCOMPONENT_CLASS+" must not be empty!"); //$NON-NLS-1$ //$NON-NLS-2$

			ScriptConditionerContext context = new ScriptConditionerContext(organisationID, conditionContextProviderID);
			scriptDrawComponentClass2ScriptConditionerContext.put(scriptDrawComponentName, context);
		}
	}

	private Map<String, ScriptConditionerContext> scriptDrawComponentClass2ScriptConditionerContext =
		new HashMap<String, ScriptConditionerContext>();

	public ScriptConditionerContext getScriptConditionerContext(String scriptDrawComponentClass)
	{
		checkProcessing();
		return scriptDrawComponentClass2ScriptConditionerContext.get(scriptDrawComponentClass);
	}
}
