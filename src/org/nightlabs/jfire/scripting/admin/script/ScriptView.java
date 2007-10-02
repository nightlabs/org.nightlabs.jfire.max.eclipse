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

package org.nightlabs.jfire.scripting.admin.script;

import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.notification.NotificationAdapterSWTThreadSync;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.base.ui.part.ControllablePart;
import org.nightlabs.base.ui.part.PartVisibilityListener;
import org.nightlabs.base.ui.part.PartVisibilityTracker;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.login.part.LSDPartController;
import org.nightlabs.jfire.scripting.Script;
import org.nightlabs.jfire.scripting.ScriptRegistryItem;
import org.nightlabs.jfire.scripting.admin.ScriptingAdminPlugin;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.jfire.scripting.ui.ScriptRegistryItemNode;
import org.nightlabs.jfire.scripting.ui.ScriptRegistryItemProvider;
import org.nightlabs.jfire.scripting.ui.ScriptRegistryItemTree;
import org.nightlabs.jfire.scripting.ui.ScriptRegistryListener;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;

/**
 * A View of all scripts and categories of the local organisation
 * and providing actions for manipulating them.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
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
	
	private Job fetchTreeNodesJob = new Job("Fetching Reportlayouts ..."){

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			final Collection<ScriptRegistryItemNode> nodes = ScriptRegistryItemProvider.sharedInstance().getTopLevelNodes();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					registryItemTree.setInput(nodes);
					registryItemTree.refresh(nodes, true);
				}
			});
			return Status.OK_STATUS;
		}
		
	};
	
	private Job refreshTreeNodesJob = new Job("Refreshing Reportlayouts ..."){

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			final Collection<ScriptRegistryItemNode> nodes = ScriptRegistryItemProvider.sharedInstance().getTopLevelNodes();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					registryItemTree.refresh(nodes, true);
				}
			});
			return Status.OK_STATUS;
		}
		
	};
	
	private IDoubleClickListener treeDoubleClickListener = new IDoubleClickListener () {

		public void doubleClick(DoubleClickEvent event) {
//			contextMenuManager.setSelectedRegistryItems(registryItemTree.getSelectedRegistryItems(), true, true);
			ScriptRegistryItem item = registryItemTree.getSelectedRegistryItem();
			if (item instanceof Script) {
//				editScriptAction.run(registryItemTree.getSelectedRegistryItems());				
			}
		}		
	};
	
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
	public void createPartControl(Composite parent) {
		LSDPartController.sharedInstance().createPartControl(this, parent);
		PartVisibilityTracker.sharedInstance().addVisibilityListener(this, this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
	}

	public void createPartContents(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE, XComposite.LayoutMode.TIGHT_WRAPPER);
		registryItemTree = new ScriptRegistryItemTree(wrapper, SWT.NONE, true, false, ScriptingAdminPlugin.ZONE_ADMIN, true);
		fetchTreeNodesJob.schedule();
		registryItemTree.setInput(ScriptRegistryItemNode.STATUS_FETCHING_NODE);
//		registryItemTree.setInput(ReportRegistryItemProvider.sharedInstance().getTopLevelNodes());
		contextMenuManager = new ScriptRegistryItemTreeMenuManager(registryItemTree, this);
		registryItemTree.getTreeViewer().addDoubleClickListener(treeDoubleClickListener);
		SelectionManager.sharedInstance().addNotificationListener(ScriptingAdminPlugin.ZONE_ADMIN, ScriptRegistryItem.class, selectionListener);
		JDOLifecycleManager.sharedInstance().addNotificationListener(ScriptRegistryItemID.class, changeListener);
		ScriptRegistryItemProvider.sharedInstance().addScriptRegistryListener(registryListener);
		
		wrapper.addDisposeListener( new DisposeListener() {
		
			public void widgetDisposed(DisposeEvent e) {
				SelectionManager.sharedInstance().removeNotificationListener(ScriptingAdminPlugin.ZONE_ADMIN, ScriptRegistryItem.class, selectionListener);
				JDOLifecycleManager.sharedInstance().removeNotificationListener(ScriptRegistryItemID.class, changeListener);
				ScriptRegistryItemProvider.sharedInstance().removeScriptRegistryListener(registryListener);
			}
		
		} );
	}
	
	private NotificationListener selectionListener = new NotificationAdapterSWTThreadSync() {

		public void notify(NotificationEvent evt) {
			Set subjects = evt.getSubjects();
			contextMenuManager.setSelectedRegistryItemIDs(subjects, true, true);
//			if (subjects.isEmpty())
				
		}
		
	};
	
	private NotificationListener changeListener = new NotificationAdapterSWTThreadSync() {
		public void notify(NotificationEvent evt) {
			logger.info("changeListener got notified with event "+evt);
			registryItemTree.refresh(true);
		}		
	};
	
	private ScriptRegistryListener registryListener = new ScriptRegistryListener() {
		public void scriptRegistryChanged() {
			refreshTreeNodesJob.schedule();
		}
	};

	public boolean canDisplayPart() {
		return Login.isLoggedIn();
	}

	public void partVisible(IWorkbenchPartReference partRef) {
	}

	public void partHidden(IWorkbenchPartReference partRef) {
	}

}
