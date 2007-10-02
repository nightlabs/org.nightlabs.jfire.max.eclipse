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

import org.nightlabs.editor2d.ui.model.ModelCreationFactory;
import org.nightlabs.jfire.scripting.editor2d.BarcodeDrawComponent;
import org.nightlabs.jfire.scripting.editor2d.ScriptEditor2DFactory;
import org.nightlabs.jfire.scripting.editor2d.TextScriptDrawComponent;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ScriptingEditorModelCreationFactory 
extends ModelCreationFactory 
{
	public ScriptingEditorModelCreationFactory(Class targetClass, ScriptEditor2DFactory factory) {
		super(targetClass, factory);
	}
	
	protected ScriptEditor2DFactory getScriptingEditor2DFactory() {
		return (ScriptEditor2DFactory) factory;
	}
	
  /** 
   * @see org.eclipse.gef.requests.CreationFactory#getNewObject()
   */
  public Object getNewObject()
  {                  
//    if( targetClass.equals(ScriptRootDrawComponent.class)) {
//      return getScriptingEditor2DFactory().createScriptRootDrawComponent();
//    }
    
    if( targetClass.equals(TextScriptDrawComponent.class)) {
      return getScriptingEditor2DFactory().createTextScriptDrawComponent();
    }

    if( targetClass.equals(BarcodeDrawComponent.class)) {
      return getScriptingEditor2DFactory().createBarcode();
    }
    
    return super.getNewObject();
  } 
}
