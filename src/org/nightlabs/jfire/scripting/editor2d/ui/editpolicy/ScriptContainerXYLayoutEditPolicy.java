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
package org.nightlabs.jfire.scripting.editor2d.ui.editpolicy;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.nightlabs.editor2d.ui.edit.TextEditPart;
import org.nightlabs.editor2d.ui.editpolicy.DrawComponentContainerXYLayoutPolicy;
import org.nightlabs.editor2d.ui.editpolicy.DrawComponentResizeEditPolicy;
import org.nightlabs.editor2d.ui.request.EditorBoundsRequest;
import org.nightlabs.jfire.scripting.editor2d.ui.command.CreateBarcodeCommand;
import org.nightlabs.jfire.scripting.editor2d.ui.command.CreateTextScriptCommand;
import org.nightlabs.jfire.scripting.editor2d.ui.editpart.BarcodeEditPart;
import org.nightlabs.jfire.scripting.editor2d.ui.request.BarcodeCreateRequest;
import org.nightlabs.jfire.scripting.editor2d.ui.request.TextScriptCreateRequest;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ScriptContainerXYLayoutEditPolicy
extends DrawComponentContainerXYLayoutPolicy
{

	public static final Logger LOGGER = Logger.getLogger(ScriptContainerXYLayoutEditPolicy.class);
	
	public ScriptContainerXYLayoutEditPolicy(XYLayout layout) {
		super(layout);
	}

	@Override
  public Command getCommand(Request request)
  {
    if (request instanceof TextScriptCreateRequest)
      return getCreateScriptTextCommand((TextScriptCreateRequest)request);

    if (request instanceof BarcodeCreateRequest)
      return getCreateBarcodeCommand((BarcodeCreateRequest)request);
    
  	return super.getCommand(request);
  }
	
  public Command getCreateScriptTextCommand(TextScriptCreateRequest request)
  {
    // TODO: Optimize Command (dont create each time a new Command)
  	CreateTextScriptCommand create = new CreateTextScriptCommand(request);
    create.setParent(getDrawComponentContainer());
    Rectangle constraint = new Rectangle();
    constraint = (Rectangle)getConstraintFor((EditorBoundsRequest)request);
    create.setBounds(constraint);
    return create;
  }

  public Command getCreateBarcodeCommand(BarcodeCreateRequest request)
  {
    // TODO: Optimize Command (dont create each time a new Command)
  	CreateBarcodeCommand create = new CreateBarcodeCommand(request);
  	create.setParent(getDrawComponentContainer());
    Rectangle constraint = new Rectangle();
    constraint = (Rectangle)getConstraintFor((EditorBoundsRequest)request);
    create.setBounds(constraint);
    return create;
  }
    
	@Override
	protected EditPolicy createChildEditPolicy(EditPart child)
	{
		if (child instanceof TextEditPart) {
			return new DrawComponentResizeEditPolicy(true, false);
		}
		if (child instanceof BarcodeEditPart) {
			return new DrawComponentResizeEditPolicy(false, false);
		}
		
		return super.createChildEditPolicy(child);
	}

}
