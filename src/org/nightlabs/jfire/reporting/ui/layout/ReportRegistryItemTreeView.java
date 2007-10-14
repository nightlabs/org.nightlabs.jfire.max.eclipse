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

package org.nightlabs.jfire.reporting.ui.layout;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.action.registry.ActionDescriptor;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.ui.layout.action.ReportRegistryItemActionMenuManager;
import org.nightlabs.jfire.reporting.ui.layout.action.view.ViewReportLayoutAction;

/**
 * Abstract class as a basis for Views that need to display
 * a tree of {@link ReportRegistryItem}s. This view
 * automatically adds actions to toolbar, menu and contextMenu
 * that are registered to the correct scope by the 
 * extension point
 * <code>org.nightlabs.jfire.reporting.ui.reportRegistryItemAction</code>  
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public abstract class ReportRegistryItemTreeView 
//extends ViewPart
//implements ControllablePart, PartVisibilityListener
extends LSDViewPart
{	
	private XComposite wrapper;
	private ReportRegistryItemTree registryItemTree;
	private ReportRegistryItemActionMenuManager contextMenuManager;
	
	public ReportRegistryItemTreeView() {
		super();
	}

//	/* (non-Javadoc)
//	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
//	 */
//	public void createPartControl(Composite parent) {
//		LSDPartController.sharedInstance().createPartControl(this, parent);
//		PartVisibilityTracker.sharedInstance().addVisibilityListener(this, this);
//	}

//	public void partVisible(IWorkbenchPartReference partRef) {
//	}
//	
//	public void partHidden(IWorkbenchPartReference partRef) {
//	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
	}

	public void createPartContents(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE, XComposite.LayoutMode.TIGHT_WRAPPER);
		registryItemTree = new ReportRegistryItemTree(wrapper, true, getNotificationZone());
		contextMenuManager = new ReportRegistryItemActionMenuManager(registryItemTree, getActionScope(), this);
		registryItemTree.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				contextMenuManager.setSelectedRegistryItems(registryItemTree.getSelectedElements(), true, true);
			}
		});
		registryItemTree.getTreeViewer().expandToLevel(4);
		registryItemTree.getTreeViewer().addDoubleClickListener(doubleClickListener);
	}
	
	public abstract String getActionScope();
	
	public abstract String getNotificationZone();
	
	public boolean canDisplayPart() {
		return Login.isLoggedIn();
	}

	/**
	 * @return The {@link ReportRegistryItemTree} of this view.
	 */
	protected ReportRegistryItemTree getRegistryItemTree() {
		return registryItemTree;
	}
	
	private IDoubleClickListener doubleClickListener = new IDoubleClickListener(){
		public void doubleClick(DoubleClickEvent event) {
			ActionDescriptor ad = contextMenuManager.getActionRegistry().getActionDescriptor(
					ViewReportLayoutAction.ID, false);
			if (ad != null) {
				ad.getAction().run();
			}
		}
	};
}
