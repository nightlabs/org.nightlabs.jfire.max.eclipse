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

package org.nightlabs.jfire.scripting.admin.ui.parameter.action.edit;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.scripting.ScriptParameter;
import org.nightlabs.jfire.scripting.ui.ScriptParameterTable;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[ÐOT]de>
 *
 */
public class EditScriptParameterTable extends ScriptParameterTable implements ICellModifier {

	public static final String COL_PROPERTY_PARAMETER_ID = "scriptParameterID";
	public static final String COL_PROPERTY_PARAMETER_TYPE = "scriptParameterClassName";
	
	/**
	 * @param parent
	 * @param style
	 */
	public EditScriptParameterTable(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param parent
	 * @param style
	 * @param initTable
	 */
	public EditScriptParameterTable(Composite parent, int style, boolean initTable) {
		super(parent, style, initTable);
	}

	/**
	 * @param parent
	 * @param style
	 * @param initTable
	 * @param viewerStyle
	 */
	public EditScriptParameterTable(Composite parent, int style,
			boolean initTable, int viewerStyle) {
		super(parent, style, initTable, viewerStyle);
	}
	
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		super.setTableProvider(tableViewer);
		tableViewer.setColumnProperties(new String[] {
				COL_PROPERTY_PARAMETER_ID,
				COL_PROPERTY_PARAMETER_TYPE
		});
		tableViewer.setCellEditors(new CellEditor[] {
				new TextCellEditor(tableViewer.getTable()),
				new TextCellEditor(tableViewer.getTable())
		});
		tableViewer.setCellModifier(this);
	}

	public boolean canModify(Object element, String property) {
		if (element instanceof ScriptParameter)
			return true;
		return false;
	}

	public Object getValue(Object element, String property) {
		if (element instanceof ScriptParameter) {
			if (property.equals(COL_PROPERTY_PARAMETER_ID))
				return ((ScriptParameter)element).getScriptParameterID();
			else if (property.equals(COL_PROPERTY_PARAMETER_TYPE))
				return ((ScriptParameter)element).getScriptParameterClassName();
		}
		return null;
	}

	public void modify(Object element, String property, Object value) {
		if (element instanceof ScriptParameter) {
			if (! (value instanceof String))
					return;
			if (property.equals(COL_PROPERTY_PARAMETER_ID))
				((ScriptParameter)element).setScriptParameterID((String)value);
			if (property.equals(COL_PROPERTY_PARAMETER_TYPE))
				((ScriptParameter)element).setScriptParameterClassName((String)value);
		}
	}

}
