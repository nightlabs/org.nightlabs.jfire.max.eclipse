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
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.ui.edit.GraphicalEditPartFactory;
import org.nightlabs.jfire.scripting.editor2d.BarcodeDrawComponent;
import org.nightlabs.jfire.scripting.editor2d.ScriptRootDrawComponent;
import org.nightlabs.jfire.scripting.editor2d.TextScriptDrawComponent;
import org.nightlabs.jfire.scripting.editor2d.ui.editpart.BarcodeEditPart;
import org.nightlabs.jfire.scripting.editor2d.ui.editpart.ScriptLayerEditPart;
import org.nightlabs.jfire.scripting.editor2d.ui.editpart.ScriptRootDrawComponentEditPart;
import org.nightlabs.jfire.scripting.editor2d.ui.editpart.TextScriptEditPart;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ScriptingEditorEditPartFactory 
extends GraphicalEditPartFactory 
{

	public ScriptingEditorEditPartFactory() {
		super();
	}

	public EditPart createEditPart(EditPart context, Object model)
	{
    if (model instanceof ScriptRootDrawComponent)
      return new ScriptRootDrawComponentEditPart((ScriptRootDrawComponent)model);

    if (model instanceof Layer)
    	return new ScriptLayerEditPart((Layer)model);
    
    if (model instanceof TextScriptDrawComponent)
    	return new TextScriptEditPart((TextScriptDrawComponent)model);

    if (model instanceof BarcodeDrawComponent)
    	return new BarcodeEditPart((BarcodeDrawComponent)model);
    
    return super.createEditPart(context, model);
	}		
}
