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

import org.eclipse.ui.views.properties.IPropertySource;
import org.nightlabs.editor2d.ui.edit.DrawComponentEditPart;
import org.nightlabs.jfire.scripting.editor2d.BarcodeDrawComponent;
import org.nightlabs.jfire.scripting.editor2d.ScriptDrawComponent;
import org.nightlabs.jfire.scripting.editor2d.ui.model.BarcodePropertySource;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class BarcodeEditPart
extends DrawComponentEditPart
{

	public BarcodeEditPart(BarcodeDrawComponent drawComponent) {
		super(drawComponent);
	}

	public BarcodeDrawComponent getBarcodeDrawComponent() {
		return (BarcodeDrawComponent) getModel();
	}
	
	@Override
	public IPropertySource getPropertySource()
	{
		if (propertySource == null) {
			propertySource = new BarcodePropertySource(getBarcodeDrawComponent());
		}
		return propertySource;
	}

	@Override
	protected void propertyChanged(PropertyChangeEvent evt)
	{
		String propertyName = evt.getPropertyName();
		
		if (propertyName.equals(BarcodeDrawComponent.PROP_TYPE)) {
			refreshVisuals();
			return;
		}
		if (propertyName.equals(BarcodeDrawComponent.PROP_VALUE)) {
			refreshVisuals();
			return;
		}
		if (propertyName.equals(BarcodeDrawComponent.PROP_HUMAN_READABLE)) {
			refreshVisuals();
			return;
		}
		if (propertyName.equals(ScriptDrawComponent.PROP_SCRIPT_REGISTRY_ITEM_ID)) {
			refreshVisuals();
			return;
		}
		
		super.propertyChanged(evt);
	}
}
