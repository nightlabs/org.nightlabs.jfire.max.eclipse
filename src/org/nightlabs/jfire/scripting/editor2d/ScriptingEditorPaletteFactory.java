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
package org.nightlabs.jfire.scripting.editor2d;

import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.ToolEntry;
import org.nightlabs.editor2d.ui.AbstractPaletteFactory;
import org.nightlabs.editor2d.ui.model.IModelCreationFactory;
import org.nightlabs.jfire.scripting.editor2d.model.ScriptingEditorModelCreationFactory;
import org.nightlabs.jfire.scripting.editor2d.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class ScriptingEditorPaletteFactory 
extends AbstractPaletteFactory 
{
	public ScriptingEditorPaletteFactory(ScriptEditor2DFactory factory) {
		super(factory);
	}

	protected ScriptEditor2DFactory getScriptEditor2DFactory() {
		return (ScriptEditor2DFactory) factory;
	}
	
	 /**
	  * Creates the PaletteRoot and adds all palette elements.
	  * Use this factory method to create a new palette for your graphical editor.
	  * @return a new PaletteRoot
	  */
	 protected PaletteRoot createPalette() 
	 {
		 PaletteRoot palette = new PaletteRoot();
		 palette.add(createToolsGroup(palette));
		 palette.add(createPaletteContainer());
		 return palette;
	 }
	 
	/**
	 * @see org.nightlabs.editor2d.ui.AbstractPaletteFactory#getCreationFactory(java.lang.Class)
	 */
	public IModelCreationFactory getCreationFactory(Class targetClass) {
		return new ScriptingEditorModelCreationFactory(targetClass, getScriptEditor2DFactory());
	}

//	protected PaletteDrawer componentsDrawer = null;
	protected PaletteContainer createPaletteContainer() 
	{
		PaletteDrawer componentsDrawer = new PaletteDrawer(Messages.getString("org.nightlabs.jfire.scripting.editor2d.ScriptingEditorPaletteFactory.category.shapes")); //$NON-NLS-1$

	  // add Rectangle Tool
	  ToolEntry toolEntry = createRectangleToolEntry();
	  componentsDrawer.add(toolEntry);

	  // add Ellipse Tool
	  toolEntry = createEllipseToolEntry();
	  componentsDrawer.add(toolEntry);

	  // add Line Tool
	  toolEntry = createLineToolEntry();
	  componentsDrawer.add(toolEntry);
	  
	  // add Text Tool
	  toolEntry = createTextToolEntry();   
	  componentsDrawer.add(toolEntry);

	  // add Ticket Text Tool
	  toolEntry = createTicketScriptTextToolEntry();   
	  componentsDrawer.add(toolEntry);
	  
	  // add Image Tool
	  toolEntry = createImageToolEntry();
	  componentsDrawer.add(toolEntry);
	  
	  // add Barcode Tool
	  toolEntry = createBarcodeToolEntry();
	  componentsDrawer.add(toolEntry);
	  
	  return componentsDrawer;
	}
	
 	protected abstract ToolEntry createBarcodeToolEntry(); 
	
 	protected abstract ToolEntry createTicketScriptTextToolEntry();
}
