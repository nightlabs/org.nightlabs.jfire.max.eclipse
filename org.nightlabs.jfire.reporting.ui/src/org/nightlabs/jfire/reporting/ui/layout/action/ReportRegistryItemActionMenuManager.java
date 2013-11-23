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

package org.nightlabs.jfire.reporting.ui.layout.action;

import java.util.Collection;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.DrillDownAdapter;
import org.nightlabs.base.ui.action.registry.ActionDescriptor;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.ui.layout.ReportRegistryItemTree;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class ReportRegistryItemActionMenuManager extends MenuManager {

	private ReportRegistryItemTree registryItemTree;
	private String scope;
	private ReportRegistryItemActionRegistry actionRegistry;
	private DrillDownAdapter drillDownAdapter;
	private Menu menu;

	/**
	 * @param text
	 */
	public ReportRegistryItemActionMenuManager(ReportRegistryItemTree registryItemTree, String scope) {
		super("#ReportRegistryItemActionPopup_"+scope); //$NON-NLS-1$
		this.registryItemTree = registryItemTree;
		this.drillDownAdapter = new DrillDownAdapter(registryItemTree.getTreeViewer());
		setRemoveAllWhenShown(true);
		addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		menu = createContextMenu(registryItemTree.getTreeViewer().getControl());
		registryItemTree.getTreeViewer().getControl().setMenu(menu);
		actionRegistry = new ReportRegistryItemActionRegistry();
		actionRegistry.process();
		Collection<ActionDescriptor> actionDescriptors = actionRegistry.getActionDescriptors();
		for (ActionDescriptor actionDescriptor : actionDescriptors) {
			if (actionDescriptor.getAction() instanceof IReportRegistryItemAction) {
				IReportRegistryItemAction itemAction = (IReportRegistryItemAction)actionDescriptor.getAction();
				if (itemAction.getScope() == null || itemAction.getScope().equals(scope))
					actionDescriptor.setVisible(true);
				else
					actionDescriptor.setVisible(false);
			}
		}
	}
	
	public ReportRegistryItemActionMenuManager(ReportRegistryItemTree registryItemTree, String scope, IViewPart viewPart) {
		this(registryItemTree, scope);
		viewPart.getSite().registerContextMenu(this, registryItemTree.getTreeViewer());
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
		drillDownAdapter.addNavigationActions(manager);
	}
	
	public ReportRegistryItemTree getRegistryItemTree() {
		return registryItemTree;
	}
	
	public DrillDownAdapter getDrillDownAdapter() {
		return drillDownAdapter;
	}
	
	public ReportRegistryItemActionRegistry getActionRegistry() {
		return actionRegistry;
	}
	
	public void setSelectedRegistryItems(Collection<ReportRegistryItem> items, boolean calculateEnabled, boolean calculateVisible) {
		Collection<ActionDescriptor> actionDescriptors = actionRegistry.getActionDescriptors();
		
		for (ActionDescriptor actionDescriptor : actionDescriptors) {
			if (actionDescriptor.getAction() instanceof IReportRegistryItemAction) {
				IReportRegistryItemAction itemAction = (IReportRegistryItemAction)actionDescriptor.getAction();
				itemAction.setReportRegistryItems(items);
				if (calculateEnabled)
					itemAction.setEnabled(itemAction.calculateEnabled(items));
				if (itemAction.getScope() == null || itemAction.getScope().equals(scope)) {
					if (calculateVisible)
						actionDescriptor.setVisible(itemAction.calculateVisible(items));
				}
			}
		}
	}
	
	
	
}
