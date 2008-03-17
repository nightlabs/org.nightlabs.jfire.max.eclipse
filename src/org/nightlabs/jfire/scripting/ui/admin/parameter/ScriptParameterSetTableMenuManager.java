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

package org.nightlabs.jfire.scripting.ui.admin.parameter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IViewPart;
import org.nightlabs.base.ui.action.registry.ActionDescriptor;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.jfire.scripting.ScriptRegistryItem;
import org.nightlabs.jfire.scripting.admin.ui.parameter.action.ScriptParameterSetActionRegistry;
import org.nightlabs.jfire.scripting.admin.ui.script.action.IScriptRegistryItemAction;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.jfire.scripting.ui.ScriptRegistryItemProvider;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class ScriptParameterSetTableMenuManager extends MenuManager {

//	private ScriptParameterSetTable parameterSetTable;
	private Menu menu;

	/**
	 * @param text
	 */
	public ScriptParameterSetTableMenuManager(String name, Control control) {
		this(name, control, true);
	}

	/**
	 */
	public ScriptParameterSetTableMenuManager(String name, Control control, boolean createContextMenu) {
		super(name);
//		this.parameterSetTable = parameterSetTable;
		setRemoveAllWhenShown(true);
		addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		if (createContextMenu) {
			menu = createContextMenu(control);
			control.setMenu(menu);
		}
	}

	public ScriptParameterSetTableMenuManager(String name, Control control, IViewPart viewPart, ISelectionProvider selectionProvider) {
		this(name, control);
		viewPart.getSite().registerContextMenu(this, selectionProvider);
		fillToolBar(viewPart);
	}

	public void fillToolBar(IToolBarManager toolBarManager) {
		try {
			toolBarManager.removeAll();
			ScriptParameterSetActionRegistry.sharedInstance().contributeToToolBar(toolBarManager);
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}
	}

	public void fillToolBar(IViewPart viewPart) {
		IToolBarManager toolBarManager = viewPart.getViewSite().getActionBars().getToolBarManager();
		fillToolBar(toolBarManager);
	}

	private void fillContextMenu(IMenuManager manager) {

		try {
			manager.removeAll();
			ScriptParameterSetActionRegistry.sharedInstance().contributeToContextMenu(manager);
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}
		// Other plug-ins can contribute their actions here
//		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

	}

//	public ScriptParameterSetTable getParameterSetTable() {
//		return parameterSetTable;
//	}

	public void setSelectedRegistryItemIDs(Set<ScriptRegistryItemID> itemIDs, boolean calculateEnabled, boolean calculateVisible) {
		Collection<ScriptRegistryItem> scriptRegistryItems = new HashSet<ScriptRegistryItem>();
		for (Iterator<ScriptRegistryItemID> iter = itemIDs.iterator(); iter.hasNext();) {
			ScriptRegistryItemID itemID = iter.next();
			// TODO remove NullProgressMonitor
			scriptRegistryItems.add(ScriptRegistryItemProvider.sharedInstance().getScriptRegistryItem(
					itemID, new NullProgressMonitor()));
		}
		setSelectedRegistryItems(scriptRegistryItems, calculateEnabled, calculateVisible);
	}


	public void setSelectedRegistryItems(Collection<ScriptRegistryItem> items, boolean calculateEnabled, boolean calculateVisible) {
		Collection<ActionDescriptor> actionDescriptors = null;
		try {
			actionDescriptors = ScriptParameterSetActionRegistry.sharedInstance().getActionDescriptors();
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}
		for (Iterator<ActionDescriptor> iter = actionDescriptors.iterator(); iter.hasNext();) {
			ActionDescriptor actionDescriptor = iter.next();
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
