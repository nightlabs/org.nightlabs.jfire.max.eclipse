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

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.jfire.scripting.condition.Script;
import org.nightlabs.jfire.scripting.condition.ScriptConditioner;
import org.nightlabs.jfire.scripting.ui.condition.SimpleScriptEditorDialog;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ConditionScriptCellEditor extends DialogCellEditor {

	public ConditionScriptCellEditor(Composite parent, Collection<ScriptConditioner> scriptConditioners) {
		super(parent);
		this.scriptConditioners = scriptConditioners;
	}

	public ConditionScriptCellEditor(Composite parent, int style, Collection<ScriptConditioner> scriptConditioners) {
		super(parent, style);
		this.scriptConditioners = scriptConditioners;
	}

	private Collection<ScriptConditioner> scriptConditioners;
	@Override
	protected Object openDialogBox(Control cellEditorWindow) 
	{
		Script script = (Script) doGetValue();
		SimpleScriptEditorDialog dialog = new SimpleScriptEditorDialog(
				cellEditorWindow.getShell(),
				scriptConditioners, 
				script);
		int returnCode = dialog.open();
		if (returnCode == Window.OK) {
			return dialog.getScript();
		}
		if (returnCode == SimpleScriptEditorDialog.ID_DELETE_SCRIPT) {
			return -1;			
		}
		return null;
	}
	
}

