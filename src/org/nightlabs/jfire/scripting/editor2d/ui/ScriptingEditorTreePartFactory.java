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
package org.nightlabs.jfire.scripting.editor2d.ui;

import org.eclipse.gef.EditPart;
import org.nightlabs.editor2d.ui.edit.tree.TreePartFactory;
import org.nightlabs.editor2d.ui.outline.filter.FilterManager;
import org.nightlabs.jfire.scripting.editor2d.BarcodeDrawComponent;
import org.nightlabs.jfire.scripting.editor2d.TextScriptDrawComponent;
import org.nightlabs.jfire.scripting.editor2d.ui.editpart.tree.BarcodeTreeEditPart;
import org.nightlabs.jfire.scripting.editor2d.ui.editpart.tree.TextScriptTreeEditPart;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ScriptingEditorTreePartFactory
extends TreePartFactory
{

	/**
	 * @param filterMan
	 */
	public ScriptingEditorTreePartFactory(FilterManager filterMan) {
		super(filterMan);
	}

  @Override
	public EditPart createEditPart(EditPart context, Object model)
  {
  	if (model instanceof TextScriptDrawComponent)
  		return new TextScriptTreeEditPart((TextScriptDrawComponent)model);

    if (model instanceof BarcodeDrawComponent)
    	return new BarcodeTreeEditPart((BarcodeDrawComponent)model);
  	
    return super.createEditPart(context, model);
  }
}
