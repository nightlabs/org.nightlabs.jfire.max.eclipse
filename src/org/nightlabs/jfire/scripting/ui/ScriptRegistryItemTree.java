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

package org.nightlabs.jfire.scripting.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.jfire.base.ui.jdo.notification.SelectionNotificationProxy;
import org.nightlabs.jfire.scripting.ScriptRegistryItem;

/**
 * Tree of <code>ScriptRegistryItemNode</code>s with one column
 * and a default ContentProvider and LabelProvider.
 *
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class ScriptRegistryItemTree extends AbstractTreeComposite<ScriptRegistryItemNode> {

	public static class SelectionProxy extends SelectionNotificationProxy {

		public SelectionProxy(ScriptRegistryItemTree source, String zone, boolean ignoreInheritance, boolean clearOnEmptySelection) {
			super(source, zone, ignoreInheritance, clearOnEmptySelection);
		}

		@Override
		public void selectionChanged(SelectionChangedEvent event)
		{
			if (event.getSelection() instanceof IStructuredSelection) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				if (sel.getFirstElement() instanceof ScriptRegistryItemNode) {
					ScriptRegistryItemNode node = (ScriptRegistryItemNode) sel.getFirstElement();
					if ("dummy".equals(node.getRegistryItemType())) // ignore - it's the "Loading data" message //$NON-NLS-1$
						return;
				}
			}
			super.selectionChanged(event);
		}

		@Override
		protected Object getPersistenceCapable(Object selectionObject) {
			if (selectionObject instanceof ScriptRegistryItemNode)
				return ((ScriptRegistryItemNode)selectionObject).getRegistryItem();
			return super.getPersistenceCapable(selectionObject);
		}

	}

	private SelectionProxy selectionProxy;
	private String selectionZone;
	private boolean addSelectionProxy;

	// TODO: ignoreinheritance ?
	private static final boolean IGNORE_INHERITANCE = false;



	/**
	 * Creates a new ScriptRegistryItemTree that will trigger
	 * selection changes for the zone specified.
	 *
	 * @param parent The trees parent
	 * @param zone The zone notifications of selection changes will be made
	 */
	public ScriptRegistryItemTree(Composite parent, String zone) {
		this(parent, SWT.FULL_SELECTION, true, true, zone, true);
	}


	/**
	 * Creates a new ScriptRegistryItemTree with the possibility to choose
	 * whether to set the selectionProxy that will trigger SelectionChanges
	 * through the NightLabs Notification framework.
	 *
	 * @param parent The trees parent
	 * @param zone The zone notifications of selection changes will be made
	 * @param addSelectionProxy whether to set the selectionProxy
	 */
	public ScriptRegistryItemTree(Composite parent, String zone, boolean addSelectionProxy) {
		this(parent, SWT.FULL_SELECTION | SWT.BORDER, true, false, zone, addSelectionProxy);
	}

	/**
	 * Create a new
	 *
	 * @param parent The trees parent
	 * @param style SWT style of the tree
	 * @param setLayoutData Whether to set the layout data
	 * @param headerVisible Whether the trees header is visible
	 * @param zone The zone notifications of selection changes will be made
	 */
	public ScriptRegistryItemTree(Composite parent, int style,
			boolean setLayoutData, boolean headerVisible, String zone, boolean addSelectionProxy) {
		super(parent, style, setLayoutData, false, headerVisible);
		this.selectionZone = zone;
		this.addSelectionProxy = addSelectionProxy;
		init();
	}

	/**
	 * Create a new
	 *
	 * @param parent The trees parent
	 * @param style SWT style of the tree
	 * @param setLayoutData Whether to set the layout data
	 * @param init Whether to init the tree (content and label provider)
	 * @param headerVisible Whether the trees header is visible
	 * @param zone The zone notifications of selection changes will be made
	 */
	public ScriptRegistryItemTree(Composite parent, int style,
			boolean setLayoutData, boolean init, boolean headerVisible, String zone, boolean addSelectionProxy) {
		super(parent, style, setLayoutData, init, headerVisible);
		this.selectionZone = zone;
		this.addSelectionProxy = addSelectionProxy;
	}

	@Override
	public void init() {
		super.init();
		initSelectionProxy();
	}

	protected void initSelectionProxy()
	{
		if (addSelectionProxy) {
			selectionProxy = new SelectionProxy(this, selectionZone, IGNORE_INHERITANCE, false);
			getTreeViewer().addSelectionChangedListener(selectionProxy);
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.tree.AbstractTreeComposite#setTreeProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	@Override
	public void setTreeProvider(TreeViewer treeViewer) {
		treeViewer.setContentProvider(new ScriptRegistryItemNode.ContentProvider());
		treeViewer.setLabelProvider(new ScriptRegistryItemNode.LabelProvider());
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.tree.AbstractTreeComposite#createTreeColumns(org.eclipse.swt.widgets.Tree)
	 */
	@Override
	public void createTreeColumns(Tree tree) {
	}

	public ScriptRegistryItemNode.ContentProvider getContentProvider() {
		return (ScriptRegistryItemNode.ContentProvider)getTreeViewer().getContentProvider();
	}

	public void setInput(Collection<ScriptRegistryItemNode> input) {
		getTreeViewer().setInput(input);
	}

	public void setInput(ScriptRegistryItemNode input) {
		Collection<ScriptRegistryItemNode> nodes = new ArrayList<ScriptRegistryItemNode>();
		nodes.add(input);
		setInput(nodes);
	}


	/**
	 * Returns the (first) selected ScriptRegistryItem or null.
	 * @return The (first) selected ScriptRegistryItem or null.
	 */
	public ScriptRegistryItem getSelectedRegistryItem() {
		if (getTree().getSelectionCount() == 1) {
			if (getTree().getSelection()[0].getData() instanceof ScriptRegistryItemNode) {
				ScriptRegistryItemNode node = (ScriptRegistryItemNode) getTree().getSelection()[0].getData();
				return node.getRegistryItem();
			}
		}
		return null;
	}

	/**
	 * Returns all selected ScriptRegistryItems in a Set.
	 * @return All selected ScriptRegistryItems in a Set.
	 */
	public Set<ScriptRegistryItem> getSelectedRegistryItems() {
		Set<ScriptRegistryItem> result = new HashSet<ScriptRegistryItem>();
		TreeItem[] items = getTree().getSelection();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getData() instanceof ScriptRegistryItemNode)
				result.add(((ScriptRegistryItemNode)items[i].getData()).getRegistryItem());
		}
		return result;
	}

}
