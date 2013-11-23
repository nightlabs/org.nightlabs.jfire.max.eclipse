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
package org.nightlabs.jfire.scripting.editor2d.ui.model;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.nightlabs.editor2d.ui.model.AbstractDrawComponentProperty;
import org.nightlabs.jfire.scripting.condition.PossibleValueProvider;
import org.nightlabs.jfire.scripting.condition.Script;
import org.nightlabs.jfire.scripting.condition.ScriptConditioner;
import org.nightlabs.jfire.scripting.condition.dao.ScriptConditionerDAO;
import org.nightlabs.jfire.scripting.condition.id.ConditionContextProviderID;
import org.nightlabs.jfire.scripting.editor2d.ScriptingConstants;
import org.nightlabs.jfire.scripting.editor2d.ui.property.ConditionScriptPropertyDescriptor;
import org.nightlabs.jfire.scripting.editor2d.ui.resource.Messages;
import org.nightlabs.jfire.scripting.editor2d.ui.script.ScriptConditionerContext;
import org.nightlabs.jfire.scripting.editor2d.ui.script.ScriptConditionerContextRegistry;
import org.nightlabs.jfire.scripting.editor2d.ui.script.ScriptParameterProviderRegistry;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VisibleScriptProperty
extends AbstractDrawComponentProperty
{
	private static final Logger logger = Logger.getLogger(VisibleScriptProperty.class);	
	public static final String CATEGORY_SCRIPT = Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.model.VisibleScriptProperty.category.script"); //$NON-NLS-1$
	private Collection<ScriptConditioner> scriptConditioner;	
	
	public String getID() {
		return ScriptingConstants.PROP_VISIBLE_SCRIPT;
	}

	protected Collection<ScriptConditioner> getScriptConditioner()
	{
		if (scriptConditioner == null)
		{
			// TODO should be asynchron fetched at editor start
			ScriptConditionerContext context = ScriptConditionerContextRegistry.sharedInstance().getScriptConditionerContext(
					getDrawComponent().getRoot().getClass().getName());
			
			Set<ScriptRegistryItemID> scriptIDs = ScriptConditionerDAO.sharedInstance().getConditionContextScriptIDs(
					ConditionContextProviderID.create(
							context.getOrganisationID(),
							context.getConditionContextProviderID()),
					new NullProgressMonitor());
			
			Map<ScriptRegistryItemID, Map<String, Object>> scriptID2ParameterValues =
				ScriptParameterProviderRegistry.sharedInstance().getParameterValues(scriptIDs);
			
			Map<ScriptRegistryItemID, ScriptConditioner> scriptID2ScriptConditioner =
				ScriptConditionerDAO.sharedInstance().getScriptConditioners(
					scriptID2ParameterValues, PossibleValueProvider.LIMIT_UNLIMITED,
					new NullProgressMonitor());
			
			scriptConditioner = scriptID2ScriptConditioner.values();
		}
		return scriptConditioner;
	}
		
	public PropertyDescriptor getPropertyDescriptor()
	{
		Collection<ScriptConditioner> scriptConditioner = getScriptConditioner();
		PropertyDescriptor desc = new ConditionScriptPropertyDescriptor(getID(),
				Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.model.VisibleScriptProperty.visibleScript"), //$NON-NLS-1$
				scriptConditioner);
		desc.setCategory(CATEGORY_SCRIPT);
		
		if (logger.isDebugEnabled()) {
			for (ScriptConditioner conditioner : scriptConditioner) {
				logger.debug("conditioner.getPossibleValues().size() = "+conditioner.getPossibleValues().size()); //$NON-NLS-1$
			}
		}
		
		return desc;
	}

	public Object getPropertyValue()
	{
		Script script = (Script) getDrawComponent().getProperties().get(ScriptingConstants.PROP_VISIBLE_SCRIPT);
		return script;
	}

	public void setPropertyValue(Object value)
	{
//		Script script = (Script) value;
//		getDrawComponent().getProperties().put(ScriptingConstants.PROP_VISIBLE_SCRIPT, script);
		if (value instanceof Script) {
			Script script = (Script) value;
			getDrawComponent().getProperties().put(ScriptingConstants.PROP_VISIBLE_SCRIPT, script);
		}
		// To workaround not possible returned null values for cellEditors
		if (value instanceof Number) {
			getDrawComponent().getProperties().put(ScriptingConstants.PROP_VISIBLE_SCRIPT, null);
		}
	}
	
}
