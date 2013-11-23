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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.base.login.ui.part.LSDViewPart;
import org.nightlabs.jfire.reporting.RoleConstants;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.ui.layout.action.ReportRegistryItemActionMenuManager;

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
	@Override
	public void setFocus() {
	}

	public void createPartContents(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE, XComposite.LayoutMode.TIGHT_WRAPPER);
		registryItemTree = createReportRegistryItemTree(wrapper);
		contextMenuManager = new ReportRegistryItemActionMenuManager(registryItemTree, getActionScope(), this);
		registryItemTree.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				contextMenuManager.setSelectedRegistryItems(registryItemTree.getSelectedElements(), true, true);
			}
		});
		registryItemTree.getTreeViewer().expandToLevel(3);
	}

	protected ReportRegistryItemTree createReportRegistryItemTree(Composite parent) {
		return new ReportRegistryItemTree(parent, true, getNotificationZone(), RoleConstants.renderReport);
	}

	public abstract String getActionScope();

	public abstract String getNotificationZone();

	@Override
	public boolean canDisplayPart() {
		return Login.isLoggedIn();
	}

	/**
	 * @return The {@link ReportRegistryItemTree} of this view.
	 */
	protected ReportRegistryItemTree getRegistryItemTree() {
		return registryItemTree;
	}

	protected ReportRegistryItemActionMenuManager getContextMenuManager() {
		return contextMenuManager;
	}
}
