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
package org.nightlabs.jfire.scripting.editor2d.ui.editpart;

import java.beans.PropertyChangeEvent;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.gef.EditPolicy;
import org.nightlabs.editor2d.RootDrawComponent;
import org.nightlabs.editor2d.ui.edit.RootDrawComponentEditPart;
import org.nightlabs.jfire.scripting.editor2d.ScriptRootDrawComponent;
import org.nightlabs.jfire.scripting.editor2d.ui.editpolicy.ScriptContainerXYLayoutEditPolicy;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ScriptRootDrawComponentEditPart 
extends RootDrawComponentEditPart 
{
	public static final Logger logger = Logger.getLogger(ScriptRootDrawComponentEditPart.class);
	
	/**
	 * @param root
	 */
	public ScriptRootDrawComponentEditPart(RootDrawComponent root) {
		super(root);
	}

	@Override
	protected void propertyChanged(PropertyChangeEvent evt) 
	{
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(ScriptRootDrawComponent.PROP_SCRIPT_VALUES)) {
			logger.debug(propertyName);
			refresh();			
		}
		super.propertyChanged(evt);		
	}	
	
	@Override
  protected void createEditPolicies() 
  {
  	super.createEditPolicies();
		XYLayout layout = (XYLayout) getContentPane().getLayoutManager();
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new ScriptContainerXYLayoutEditPolicy(layout));					
  }	
}
