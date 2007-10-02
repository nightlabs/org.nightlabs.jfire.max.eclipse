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

package org.nightlabs.jfire.scripting.admin.script.action.edit;

import java.util.Collection;

import javax.jdo.JDOHelper;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.scripting.Script;
import org.nightlabs.jfire.scripting.ScriptRegistryItem;
import org.nightlabs.jfire.scripting.admin.script.ScriptEditorRegistry;
import org.nightlabs.jfire.scripting.admin.script.action.ScriptRegistryItemAction;
import org.nightlabs.jfire.scripting.admin.script.jscript.ScriptingJScriptEditorInput;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class EditScriptAction extends ScriptRegistryItemAction {

	/**
	 * 
	 */
	public EditScriptAction() {
		super();
	}

	/**
	 * @param text
	 */
	public EditScriptAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param image
	 */
	public EditScriptAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * @param text
	 * @param style
	 */
	public EditScriptAction(String text, int style) {
		super(text, style);
	}

	/*
	 *  (non-Javadoc)
	 * @see org.nightlabs.jfire.scripting.admin.script.action.ScriptRegistryItemAction#run(java.util.Collection)
	 */
	@Override 
	public void run(Collection<ScriptRegistryItem> reportRegistryItems) {
		Script script = (Script)reportRegistryItems.iterator().next();
		String editorID = ScriptEditorRegistry.sharedInstance().getEditorID(script.getLanguage());
		try {
			RCPUtil.openEditor(
//					new ExternalFileEditorInput(new File("/home/alex/test.js")),
					new ScriptingJScriptEditorInput((ScriptRegistryItemID)JDOHelper.getObjectId(script)),
					editorID
				);
		} catch (PartInitException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean calculateEnabled(Collection<ScriptRegistryItem> registryItems) {
		if (registryItems.isEmpty() || (registryItems.size() != 1))
			return false;
		ScriptRegistryItem registryItem = registryItems.iterator().next();
		
		if (registryItem instanceof Script)
			return true;
		return false;
	}

}
