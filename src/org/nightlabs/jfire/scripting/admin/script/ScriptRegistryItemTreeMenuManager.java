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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.DrillDownAdapter;
import org.nightlabs.base.ui.action.registry.ActionDescriptor;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.jfire.scripting.ScriptRegistryItem;
import org.nightlabs.jfire.scripting.admin.script.action.IScriptRegistryItemAction;
import org.nightlabs.jfire.scripting.admin.script.action.ScriptRegistryItemActionRegistry;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.jfire.scripting.ui.ScriptRegistryItemProvider;
import org.nightlabs.jfire.scripting.ui.ScriptRegistryItemTree;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class ScriptRegistryItemTreeMenuManager extends MenuManager {

	private ScriptRegistryItemTree registryItemTree;
	private DrillDownAdapter drillDownAdapter;
	private Menu menu;

	/**
	 * @param text
	 */
	public ScriptRegistryItemTreeMenuManager(ScriptRegistryItemTree registryItemTree) {
		super("#ScriptRegistryItemTreePopup");
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
	}
	
	public ScriptRegistryItemTreeMenuManager(ScriptRegistryItemTree registryItemTree, IViewPart viewPart) {
		this(registryItemTree);
		viewPart.getSite().registerContextMenu(this, registryItemTree.getTreeViewer());
		fillToolBar(viewPart);
	}

	private void fillToolBar(IViewPart viewPart) {
		IToolBarManager toolBarManager = viewPart.getViewSite().getActionBars().getToolBarManager();
		try {
			toolBarManager.removeAll();
			ScriptRegistryItemActionRegistry.sharedInstance().contributeToToolBar(toolBarManager);
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void fillContextMenu(IMenuManager manager) {
	
		try {
			manager.removeAll();
			ScriptRegistryItemActionRegistry.sharedInstance().contributeToContextMenu(manager);
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}
		// Other plug-ins can contribute their actions here
//		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		
		drillDownAdapter.addNavigationActions(manager);
	}
	
	public ScriptRegistryItemTree getRegistryItemTree() {
		return registryItemTree;
	}
	
	public DrillDownAdapter getDrillDownAdapter() {
		return drillDownAdapter;
	}
	
	public void setSelectedRegistryItemIDs(Set itemIDs, boolean calculateEnabled, boolean calculateVisible) {
		Collection<ScriptRegistryItem> scriptRegistryItems = new HashSet<ScriptRegistryItem>();
		for (Iterator iter = itemIDs.iterator(); iter.hasNext();) {
			Object o = iter.next();
			if (o instanceof ScriptRegistryItemID)
				// TODO remove NullProgressMonitor				
				scriptRegistryItems.add(ScriptRegistryItemProvider.sharedInstance().getScriptRegistryItem(
						(ScriptRegistryItemID)o, new NullProgressMonitor()));
		}
		setSelectedRegistryItems(scriptRegistryItems, calculateEnabled, calculateVisible);
	}
	
	
	public void setSelectedRegistryItems(Collection<ScriptRegistryItem> items, boolean calculateEnabled, boolean calculateVisible) {
		Collection actionDescriptors = null;
		try {
			actionDescriptors = ScriptRegistryItemActionRegistry.sharedInstance().getActionDescriptors();
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}
		for (Iterator iter = actionDescriptors.iterator(); iter.hasNext();) {
			ActionDescriptor actionDescriptor = (ActionDescriptor) iter.next();
			if (actionDescriptor.getAction() instanceof IScriptRegistryItemAction) {
				IScriptRegistryItemAction itemAction = (IScriptRegistryItemAction)actionDescriptor.getAction();
				itemAction.setScriptRegistryItems(items);
				if (calculateEnabled)
					itemAction.setEnabled(itemAction.calculateEnabled(items));
				if (calculateVisible)
					actionDescriptor.setVisible(itemAction.calculateVisible(items));
			}
		}
	}
	
	
	
}
