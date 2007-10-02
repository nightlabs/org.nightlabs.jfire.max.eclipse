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
package org.nightlabs.jfire.scripting.editor2d.command;

import org.apache.log4j.Logger;
import org.nightlabs.editor2d.ui.command.CreateDrawComponentCommand;
import org.nightlabs.jfire.scripting.editor2d.BarcodeDrawComponent;
import org.nightlabs.jfire.scripting.editor2d.ScriptEditor2DFactory;
import org.nightlabs.jfire.scripting.editor2d.request.BarcodeCreateRequest;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class CreateBarcodeCommand 
extends CreateDrawComponentCommand 
{
public static final Logger LOGGER = Logger.getLogger(CreateBarcodeCommand.class);
	
	public CreateBarcodeCommand(BarcodeCreateRequest request) {
		super();
		this.request = request;
	}
 
	private BarcodeCreateRequest request = null;
	
  protected ScriptEditor2DFactory getScriptEditorFactory() {
  	return (ScriptEditor2DFactory) request.getModelCreationFactory().getFactory();
  }	
  
	public void execute() 
	{
	  int x = getBounds().x;
	  int y = getBounds().y;
	  BarcodeDrawComponent tb = getScriptEditorFactory().createBarcode(
	  		request.getBarcodeType(), request.getValue(), 
	  		x, y, 
	  		request.getWidthScale(), request.getHeight(), 
	  		request.getOrientation(), request.isHumanReadable(), 
	  		parent, request.getScriptRegistryItemID());    	  
	  drawComponent = tb;
	  parent.addDrawComponent(drawComponent);
		drawOrderIndex = parent.getDrawComponents().indexOf(drawComponent);
	}	

}
