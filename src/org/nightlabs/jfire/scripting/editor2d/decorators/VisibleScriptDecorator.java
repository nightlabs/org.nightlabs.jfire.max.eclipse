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
package org.nightlabs.jfire.scripting.editor2d.decorators;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageDimension;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.ui.edit.tree.DrawComponentTreeEditPart;
import org.nightlabs.jfire.scripting.editor2d.ScriptingConstants;
import org.nightlabs.jfire.scripting.editor2d.ScriptingEditor2DPlugin;
import org.nightlabs.jfire.scripting.editor2d.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VisibleScriptDecorator 
extends LabelProvider 
implements ILightweightLabelDecorator 
{
	public void decorate(Object element, IDecoration decoration) 
	{
		if(element instanceof DrawComponentTreeEditPart) {	
			DrawComponentTreeEditPart dctep = (DrawComponentTreeEditPart) element;
			DrawComponent dc = dctep.getDrawComponent();
			if (dc.getProperties().containsKey(ScriptingConstants.PROP_VISIBLE_SCRIPT)) {
				ImageDescriptor visibleScriptImage = SharedImages.getSharedImageDescriptor(
						ScriptingEditor2DPlugin.getDefault(), 
						VisibleScriptDecorator.class, "", ImageDimension._8x8, ImageFormat.gif);				 //$NON-NLS-1$
				decoration.addOverlay(visibleScriptImage, IDecoration.TOP_LEFT);
				decoration.addSuffix("["+Messages.getString("org.nightlabs.jfire.scripting.editor2d.decorators.VisibleScriptDecorator.visibleScript")+"]");				 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}		
	}

}
