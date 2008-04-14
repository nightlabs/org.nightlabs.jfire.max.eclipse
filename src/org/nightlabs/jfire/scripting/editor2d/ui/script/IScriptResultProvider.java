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

import java.util.Map;

import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public interface IScriptResultProvider
{
	/**
	 * returns the script results for the current selected object
	 * 
	 * @return the script results for the current selected object
	 */
//	Map<ScriptRegistryItemID, Object> getScriptResults(Set<ScriptRegistryItemID> scriptIDs,
//			IParameterProvider parameterProvider, ProgressMonitor monitor);

	Map<ScriptRegistryItemID, Object> getScriptResults();
	
	Object getScriptResult(ScriptRegistryItemID scriptRegistryItemID);
	
//	/**
//	 * sets the selected object for which scriptResults should be obtained
//	 * 
//	 * @param selectedObject the object to select
//	 */
//	void setSelectedObject(T selectedObject);
//	
//	/**
//	 * returns the selected object
//	 * 
//	 * @return the selected object
//	 */
//	T getSelectedObject();
	
	/**
	 * adds an {@link IScriptResultChangedListener} to listen for script result changes
	 * 
	 * @param listener the {@link IScriptResultChangedListener} to add
	 */
	void addScriptResultsChangedListener(IScriptResultChangedListener listener);
	
	/**
	 * removes an previously added {@link IScriptResultChangedListener}
	 * 
	 * @param listener listener the {@link IScriptResultChangedListener} to remove
	 */
	void removeScriptResultChangedListener(IScriptResultChangedListener listener);
}
