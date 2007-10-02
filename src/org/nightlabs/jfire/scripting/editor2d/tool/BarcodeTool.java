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
package org.nightlabs.jfire.scripting.editor2d.tool;

import org.eclipse.gef.Request;
import org.eclipse.gef.tools.CreationTool;
import org.eclipse.jface.dialogs.Dialog;
import org.nightlabs.editor2d.ui.model.IModelCreationFactory;
import org.nightlabs.jfire.scripting.editor2d.dialog.CreateBarcodeDialog;
import org.nightlabs.jfire.scripting.editor2d.request.BarcodeCreateRequest;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class BarcodeTool 
extends CreationTool 
{

	public BarcodeTool(IModelCreationFactory factory) {
		super(factory);
	}

  /**
   * Creates a {@link TicketBarcodeCreateRequest} and sets this tool's factory on the request.
   */
	@Override
  protected Request createTargetRequest() 
  {
		BarcodeCreateRequest request = new BarcodeCreateRequest();
    request.setFactory(getFactory());
    return request;
  }  	
  
  public BarcodeCreateRequest getBarcodeCreateRequest() {
    return (BarcodeCreateRequest) getTargetRequest();
  }  
  
  /**
   * opens a {@link CreateTicketBarcodeDialog} and performs the creation
   */
  @Override  
  protected boolean handleButtonDown(int button) 
  {
    CreateBarcodeDialog dialog = createBarcodeDialog();
    dialog.open();
        
    if (dialog.getReturnCode() == Dialog.OK) 
    {
      performCreation(1);
      return true;
    }
    return false;    
  }
	
  protected abstract CreateBarcodeDialog createBarcodeDialog();
}
