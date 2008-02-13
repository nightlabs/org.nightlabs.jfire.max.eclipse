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
package org.nightlabs.jfire.scripting.editor2d.ui.property;

import java.util.Collection;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.property.XPropertyDescriptor;
import org.nightlabs.jfire.scripting.condition.ScriptConditioner;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ConditionScriptPropertyDescriptor
extends XPropertyDescriptor
{

	public ConditionScriptPropertyDescriptor(Object id, String displayName, boolean readOnly,
			Collection<ScriptConditioner> scriptConditioners)
	{
		super(id, displayName, readOnly);
		this.scriptConditioners = scriptConditioners;
	}

	public ConditionScriptPropertyDescriptor(Object id, String displayName,
			Collection<ScriptConditioner> scriptConditioners)
	{
		super(id, displayName);
		this.scriptConditioners = scriptConditioners;
	}

	private Collection<ScriptConditioner> scriptConditioners;
	@Override
	public CellEditor createPropertyEditor(Composite parent) {
		return new ConditionScriptCellEditor(parent, scriptConditioners);
	}
	
}

