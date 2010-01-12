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

package org.nightlabs.jfire.scripting.admin.ui.script;

import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.part.ControllablePart;
import org.nightlabs.base.ui.part.PartVisibilityListener;
import org.nightlabs.base.ui.part.PartVisibilityTracker;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.login.part.LSDPartController;
import org.nightlabs.jfire.scripting.Script;
import org.nightlabs.jfire.scripting.ScriptRegistryItem;
import org.nightlabs.jfire.scripting.admin.ui.ScriptingAdminPlugin;
import org.nightlabs.jfire.scripting.admin.ui.editor.ScriptEditor;
import org.nightlabs.jfire.scripting.admin.ui.editor.ScriptEditorInput;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.jfire.scripting.ui.tree.ScriptRegistryItemTree;

/**
 * A View of all scripts and categories of the local organisation
 * and providing actions for manipulating them.
 *
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author Fitas Amine - fitas [at] nightlabs [dot] de
 *
 *
 */
public class ScriptView
extends
	ViewPart
implements
	ControllablePart,
	PartVisibilityListener
{

	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(ScriptView.class);

	public static final String ID_VIEW = ScriptView.class.getName();

	private XComposite wrapper;
	private ScriptRegistryItemTree registryItemTree;
	private ScriptRegistryItemTreeMenuManager contextMenuManager;
	
	private IDoubleClickListener treeDoubleClickListener = new IDoubleClickListener () {
		public void doubleClick(DoubleClickEvent event) {			
			ScriptRegistryItem item = registryItemTree.getFirstSelectedElement();
			if (item instanceof Script) {
				try {
					ScriptRegistryItemID objectID = (ScriptRegistryItemID)JDOHelper.getObjectId(item);
					ScriptEditorInput input = new ScriptEditorInput(objectID);
					RCPUtil.openEditor( input, ScriptEditor.ID_EDITOR);
				} catch (PartInitException e) {
					throw new RuntimeException(e);
				}
			}
		}};

	
	/**
	 *
	 */
	public ScriptView() {
		super();
		LSDPartController.sharedInstance().registerPart(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		LSDPartController.sharedInstance().createPartControl(this, parent);
		PartVisibilityTracker.sharedInstance().addVisibilityListener(this, this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}

	public void createPartContents(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE, XComposite.LayoutMode.TIGHT_WRAPPER);
		registryItemTree =new ScriptRegistryItemTree(wrapper, true, ScriptingAdminPlugin.ZONE_ADMIN);
		registryItemTree.getTreeViewer().addDoubleClickListener(treeDoubleClickListener);
		contextMenuManager = new ScriptRegistryItemTreeMenuManager(registryItemTree, this);		
		registryItemTree.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				contextMenuManager.setSelectedRegistryItems(registryItemTree.getSelectedElements(), true, true);
			}
		});
		registryItemTree.getTreeViewer().expandToLevel(2);
	}


	public boolean canDisplayPart() {
		return Login.isLoggedIn();
	}

	public void partVisible(IWorkbenchPartReference partRef) {
	}

	public void partHidden(IWorkbenchPartReference partRef) {
	}

}
