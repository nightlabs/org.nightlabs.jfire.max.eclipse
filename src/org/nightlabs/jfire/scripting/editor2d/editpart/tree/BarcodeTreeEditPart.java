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
package org.nightlabs.jfire.scripting.editor2d.editpart.tree;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.editor2d.ui.edit.tree.DrawComponentTreeEditPart;
import org.nightlabs.jfire.scripting.editor2d.BarcodeDrawComponent;
import org.nightlabs.jfire.scripting.editor2d.ScriptingEditor2DPlugin;
import org.nightlabs.jfire.scripting.editor2d.model.BarcodePropertySource;
import org.nightlabs.jfire.scripting.editor2d.tool.BarcodeTool;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class BarcodeTreeEditPart 
extends DrawComponentTreeEditPart
{
	public static final Image BARCODE_ICON = SharedImages.getSharedImageDescriptor(
			ScriptingEditor2DPlugin.getDefault(), 
			BarcodeTool.class, "", ImageFormat.gif).createImage();	 //$NON-NLS-1$

	public BarcodeTreeEditPart(BarcodeDrawComponent barcode) {
		super(barcode);
	}

	public BarcodeDrawComponent getBarcode() {
		return (BarcodeDrawComponent) getModel();
	}
	
	@Override
	protected Image getOutlineImage() {
		return BARCODE_ICON;
	}

	@Override
	public IPropertySource getPropertySource() 
	{
		if (propertySource == null) {
			propertySource = new BarcodePropertySource(getBarcode());
		}
		return propertySource;
	}		
}
