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

import java.util.Collection;

import org.eclipse.jface.action.IAction;
import org.nightlabs.jfire.scripting.ScriptRegistryItem;

public interface IScriptRegistryItemAction extends IAction {

	/**
	 * Use this method to set the <code>ReportRegistryItem</code>s
	 * this actions is invoked on.
	 * 
	 * @param reportRegistryItem
	 */
	public abstract void setScriptRegistryItems(Collection<ScriptRegistryItem> scriptRegistryItem);

	/**
	 * Use this method to get the <code>ScriptRegistryItem</code>s
	 * this actions will be invoked on.
	 * 
	 * @return The current <code>ReportRegistryItem</code> set
	 */
	public abstract Collection<ScriptRegistryItem> getScriptRegistryItems();
	
	
	public boolean calculateEnabled(Collection<ScriptRegistryItem> registryItems);
	
	public boolean calculateVisible(Collection<ScriptRegistryItem> registryItems);
}
