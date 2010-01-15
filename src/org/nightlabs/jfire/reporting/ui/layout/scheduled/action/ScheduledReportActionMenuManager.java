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

package org.nightlabs.jfire.reporting.ui.layout.scheduled.action;

import java.util.Collection;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IViewPart;
import org.nightlabs.base.ui.action.registry.ActionDescriptor;
import org.nightlabs.jfire.reporting.scheduled.ScheduledReport;
import org.nightlabs.jfire.reporting.ui.layout.action.IReportRegistryItemAction;
import org.nightlabs.jfire.reporting.ui.layout.scheduled.ScheduledReportsTable;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class ScheduledReportActionMenuManager extends MenuManager {

	private ScheduledReportsTable scheduledReportsTable;
	private ScheduledReportActionRegistry actionRegistry;
	private Menu menu;

	/**
	 * @param text
	 */
	public ScheduledReportActionMenuManager(ScheduledReportsTable scheduledReportsTable, String scope) {
		super("#ReportRegistryItemActionPopup_"+scope); //$NON-NLS-1$
		this.scheduledReportsTable = scheduledReportsTable;
		setRemoveAllWhenShown(true);
		addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		menu = createContextMenu(scheduledReportsTable.getTableViewer().getControl());
		scheduledReportsTable.getTableViewer().getControl().setMenu(menu);
		actionRegistry = new ScheduledReportActionRegistry();
		actionRegistry.process();
		scheduledReportsTable.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				setSelectedScheduledReports(ScheduledReportActionMenuManager.this.scheduledReportsTable.getSelectedElements(), true, true);
			}
		});
	}
	
	public ScheduledReportActionMenuManager(ScheduledReportsTable registryItemTree, String scope, IViewPart viewPart) {
		this(registryItemTree, scope);
		viewPart.getSite().registerContextMenu(this, registryItemTree.getTableViewer());
		fillToolBar(viewPart);
	}

	private void fillToolBar(IViewPart viewPart) {
		IToolBarManager toolBarManager = viewPart.getViewSite().getActionBars().getToolBarManager();
		toolBarManager.removeAll();
		actionRegistry.contributeToToolBar(toolBarManager);
	}
	
	private void fillContextMenu(IMenuManager manager) {
		manager.removeAll();
		actionRegistry.contributeToContextMenu(manager);
	}
	
	public ScheduledReportsTable getScheduledReportsTable() {
		return scheduledReportsTable;
	}
	
	
	public ScheduledReportActionRegistry getActionRegistry() {
		return actionRegistry;
	}
	
	private void setSelectedScheduledReports(Collection<ScheduledReport> scheduledReports, boolean calculateEnabled, boolean calculateVisible) {
		Collection<ActionDescriptor> actionDescriptors = actionRegistry.getActionDescriptors();
		
		for (ActionDescriptor actionDescriptor : actionDescriptors) {
			if (actionDescriptor.getAction() instanceof IReportRegistryItemAction) {
				IScheduledReportAction itemAction = (IScheduledReportAction)actionDescriptor.getAction();
				itemAction.setScheduledReports(scheduledReports);
				if (calculateEnabled)
					itemAction.setEnabled(itemAction.calculateEnabled(scheduledReports));
				if (calculateVisible)
						actionDescriptor.setVisible(itemAction.calculateVisible(scheduledReports));
			}
		}
	}
	
	
	
}
