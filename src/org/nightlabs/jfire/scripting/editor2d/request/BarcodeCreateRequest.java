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
package org.nightlabs.jfire.scripting.editor2d.request;

import org.nightlabs.editor2d.ui.request.EditorCreateRequest;
import org.nightlabs.jfire.scripting.editor2d.BarcodeDrawComponent;
import org.nightlabs.jfire.scripting.editor2d.BarcodeDrawComponent.Orientation;
import org.nightlabs.jfire.scripting.editor2d.BarcodeDrawComponent.Type;
import org.nightlabs.jfire.scripting.editor2d.BarcodeDrawComponent.WidthScale;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class BarcodeCreateRequest 
extends EditorCreateRequest 
implements ScriptCreateRequest 
{

	private ScriptRegistryItemID scriptRegistryItemID = null;
	public ScriptRegistryItemID getScriptRegistryItemID() {
		return scriptRegistryItemID;
	}
	public void setScriptRegistryItemID(ScriptRegistryItemID scriptRegistryItemID) {
		this.scriptRegistryItemID = scriptRegistryItemID;
	}

	private boolean humanReadable = BarcodeDrawComponent.HUMAN_READABLE_DEFAULT;
	public boolean isHumanReadable() {
		return humanReadable;
	}
	public void setHumanReadable(boolean humanReadable) {
		this.humanReadable = humanReadable;
	}
	
	private Orientation orientation = BarcodeDrawComponent.ORIENTATION_DEFAULT;
	public Orientation getOrientation() {
		return orientation;
	}
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}	
	
	private Type barcodeType = BarcodeDrawComponent.TYPE_DEFAULT;
	public Type getBarcodeType() {
		return barcodeType;
	}
	public void setBarcodeType(Type type) {
		this.barcodeType = type;
	}	
	
	private WidthScale widthScale = BarcodeDrawComponent.WIDTH_SCALE_DEFAULT;
	public WidthScale getWidthScale() {
		return widthScale;
	}
	public void setWidthScale(WidthScale widthScale) {
		this.widthScale = widthScale;
	}		
	
	private String value = BarcodeDrawComponent.VALUE_DEFAULT;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	private int height;
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}


}
