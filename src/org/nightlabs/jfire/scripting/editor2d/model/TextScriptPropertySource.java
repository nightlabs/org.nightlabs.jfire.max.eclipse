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
package org.nightlabs.jfire.scripting.editor2d.model;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.nightlabs.base.ui.property.XTextPropertyDescriptor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.TextDrawComponent;
import org.nightlabs.editor2d.ui.model.TextPropertySource;
import org.nightlabs.editor2d.ui.properties.NamePropertyDescriptor;
import org.nightlabs.jfire.scripting.editor2d.TextScriptDrawComponent;
import org.nightlabs.jfire.scripting.editor2d.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class TextScriptPropertySource 
extends TextPropertySource 
{
	/**
	 * @param text
	 */
	public TextScriptPropertySource(TextScriptDrawComponent text) {
		super(text);
	}

	public TextScriptDrawComponent getTextScript() {
		return (TextScriptDrawComponent) drawComponent;
	}
	
	@Override	
	public void setPropertyValue(Object id, Object value) 
	{
		if (id.equals(TextDrawComponent.PROP_NAME)) {
			// do nothing  
		}		
		super.setPropertyValue(id, value);
	}

	@Override
	public Object getPropertyValue(Object id) 
	{
		if (id.equals(TextDrawComponent.PROP_NAME)) {
			// Show the the ID of the ScriptRegistryItem as Name
			return getTextScript().getScriptRegistryItemID().scriptRegistryItemID;
		}				
		return super.getPropertyValue(id);
	}	
		
	@Override
	protected PropertyDescriptor createTextPD() 
	{
		PropertyDescriptor desc = new XTextPropertyDescriptor(TextDrawComponent.PROP_TEXT,
				Messages.getString("org.nightlabs.jfire.scripting.editor2d.model.TextScriptPropertySource.text"), true); //$NON-NLS-1$
		desc.setCategory(CATEGORY_FONT);
		return desc;
	}

	@Override
	protected PropertyDescriptor createNamePD() 
	{
		PropertyDescriptor desc = new NamePropertyDescriptor(drawComponent,
				DrawComponent.PROP_NAME,
				Messages.getString("org.nightlabs.jfire.scripting.editor2d.model.TextScriptPropertySource.name"), true);		 //$NON-NLS-1$
		desc.setCategory(CATEGORY_NAME);
		return desc;
	}	
}
