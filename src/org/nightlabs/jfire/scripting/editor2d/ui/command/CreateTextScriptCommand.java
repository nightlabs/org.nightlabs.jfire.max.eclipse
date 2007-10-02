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
package org.nightlabs.jfire.scripting.editor2d.ui.command;

import org.nightlabs.editor2d.TextDrawComponent;
import org.nightlabs.editor2d.ui.command.AbstractCreateTextCommand;
import org.nightlabs.editor2d.ui.request.TextCreateRequest;
import org.nightlabs.jfire.scripting.editor2d.ScriptEditor2DFactory;
import org.nightlabs.jfire.scripting.editor2d.TextScriptDrawComponent;
import org.nightlabs.jfire.scripting.editor2d.ui.request.TextScriptCreateRequest;
import org.nightlabs.jfire.scripting.editor2d.ui.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class CreateTextScriptCommand 
extends AbstractCreateTextCommand 
{
	protected TextScriptCreateRequest request = null;
	public CreateTextScriptCommand(TextScriptCreateRequest request) 
	{
		super(request);
		setLabel(Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.command.CreateTextScriptCommand.label")); //$NON-NLS-1$
		this.request = request;
	}

  protected TextScriptDrawComponent getTextScript() {
  	return (TextScriptDrawComponent) getChild();
  }

  protected TextScriptCreateRequest getTextScriptCreateRequest() {
  	return request;
  }
  
  protected ScriptEditor2DFactory getScriptEditorFactory() {
  	return (ScriptEditor2DFactory) getFactory();
  }	
  
	@Override
	public TextDrawComponent createTextDrawComponent(TextCreateRequest request, int x, int y) 
	{
		TextScriptDrawComponent tt = getScriptEditorFactory().createTextScriptDrawComponent(
				request.getText(), 
				request.getFontName(), request.getFontSize(), request.getFontStyle(), 
				x, y, parent);
    tt.setScriptRegistryItemID(getTextScriptCreateRequest().getScriptRegistryItemID());		
		return tt;
	}	
}
