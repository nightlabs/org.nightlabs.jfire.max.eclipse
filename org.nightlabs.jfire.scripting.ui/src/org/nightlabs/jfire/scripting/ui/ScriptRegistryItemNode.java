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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.base.ui.tree.TreeContentProvider;
import org.nightlabs.jfire.base.jdo.JDOObjectID2PCClassMap;
import org.nightlabs.jfire.scripting.Script;
import org.nightlabs.jfire.scripting.ScriptCategory;
import org.nightlabs.jfire.scripting.ScriptRegistryItem;
import org.nightlabs.jfire.scripting.ScriptRegistryItemCarrier;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.jfire.scripting.ui.resource.Messages;
import org.nightlabs.util.NLLocale;

/**
 * Node object to be used when displaying ScriptRegistryItems in trees.
 *
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class ScriptRegistryItemNode extends ScriptRegistryItemCarrier {
	private static final long serialVersionUID = 1L;

	/**
	 * ContentProvider for a tree of <code>ScriptRegistryItemNode</code>s
	 */
	static class ContentProvider extends TreeContentProvider {

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Collection)
				return ((Collection<Object>)inputElement).toArray();
			else if (inputElement instanceof ScriptRegistryItemNode)
				return ((ScriptRegistryItemNode)inputElement).getChildren();
			return new Object[] { inputElement };
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof ScriptRegistryItemNode)
				return ((ScriptRegistryItemNode)parentElement).getChildren();
			return super.getChildren(parentElement);
		}


		@Override
		public Object getParent(Object element) {
			if (element instanceof ScriptRegistryItemNode)
				return ((ScriptRegistryItemNode)element).getParentCarrier();
			return super.getParent(element);
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof ScriptRegistryItemNode)
				return ((ScriptRegistryItemNode)element).hasChildren();
			return super.hasChildren(element);
		}

		@Override
		public void dispose() {
		}

	}

	/**
	 * Standard LabelProvider for a tree or table of <code>ScriptRegistryItemNode</code>s
	 */
	static class LabelProvider extends TableLabelProvider {

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof ScriptRegistryItemNode)
				return ((ScriptRegistryItemNode)element).getName();

			if (columnIndex == 0)
				return String.valueOf(element);

			return ""; //$NON-NLS-1$
		}

		@Override
		public String getText(Object element) {
			return getColumnText(element, 0);
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex != 0)
				return null;
			if (element instanceof ScriptRegistryItemNode) {
				ScriptRegistryItemNode node = (ScriptRegistryItemNode)element;
				if (node.getRegistryItem() == null)
					return null;
//				if ("dummy".equals(node.getRegistryItem().getScriptRegistryItemType()))
//					return null;

				Class<?> clazz = JDOObjectID2PCClassMap.sharedInstance().getPersistenceCapableClass(node.getRegistryItemID());
				if (clazz == null)
					return null;
				if (clazz.equals(ScriptCategory.class))
					return SharedImages.getSharedImage(ScriptingPlugin.getDefault(), ScriptRegistryItemTree.class, "category"); //$NON-NLS-1$
				else if (clazz.equals(Script.class))
					return SharedImages.getSharedImage(ScriptingPlugin.getDefault(), ScriptRegistryItemTree.class, "script"); //$NON-NLS-1$
			}
			return super.getColumnImage(element, columnIndex);
		}

		@Override
		public Image getImage(Object element) {
			return getColumnImage(element, 0);
		}

	}

	public static final ScriptRegistryItemNode STATUS_FETCHING_NODE = new ScriptRegistryItemNode() {
		private static final long serialVersionUID = 1L;

		@Override
		public String getName() {
			return Messages.getString("org.nightlabs.jfire.scripting.ui.ScriptRegistryItemNode.fetchingCategoriesNode.name"); //$NON-NLS-1$
		}

		@Override
		public ScriptRegistryItem getRegistryItem() {
			return null;
		}

		@Override
		public boolean hasChildren() {
			return false;
		}
	};



	/**
	 * @param parentCarrier
	 * @param item
	 * @param recurse
	 */
	public ScriptRegistryItemNode(
			ScriptRegistryItemCarrier parentCarrier,
			ScriptRegistryItem item, boolean recurse
		)
	{
		super(parentCarrier, item, recurse);
	}

	/**
	 * @param parentCarrier
	 * @param itemType
	 * @param itemID
	 */
	public ScriptRegistryItemNode(
			ScriptRegistryItemCarrier parentCarrier,
			ScriptRegistryItemID itemID
		)
	{
		super(parentCarrier, itemID);
	}

	public ScriptRegistryItemNode(ScriptRegistryItemNode parentNode, ScriptRegistryItemCarrier cloned) {
		setParentCarrier(parentNode);
		setRegistryItemID(cloned.getRegistryItemID());
	}


	public ScriptRegistryItemNode(
			ScriptRegistryItemNode parentNode,
			ScriptRegistryItemCarrier cloned,
			boolean recurse,
			Map<ScriptRegistryItemID, ScriptRegistryItemNode> allItems
		)
	{
		this(parentNode, cloned);
		allItems.put(getRegistryItemID(), this);
		if (recurse) {
			for (ScriptRegistryItemCarrier childCarrier : cloned.getChildCarriers()) {
				addChildCarrier(new ScriptRegistryItemNode(this, childCarrier, recurse, allItems));
			}
		}
	}

	protected ScriptRegistryItemNode() {
		super();
	}


	public boolean hasChildren() {
		return getChildCarriers().size() > 0;
	}

	public Object[] getChildren() {
		return getChildCarriers().toArray();
	}

	public Collection<ScriptRegistryItemNode> getChildNodes() {
		Collection<ScriptRegistryItemCarrier> carriers = getChildCarriers();
		Collection<ScriptRegistryItemNode> result = new HashSet<ScriptRegistryItemNode>();
		for (ScriptRegistryItemCarrier carrier : carriers) {
			result.add((ScriptRegistryItemNode)carrier);
		}
		return result;
	}

	public ScriptRegistryItem getRegistryItem() {
		// TODO remove NullProgressMonitor
		ScriptRegistryItem item = ScriptRegistryItemProvider.sharedInstance().getScriptRegistryItem(
				getRegistryItemID(), new NullProgressMonitor());
		return item;
	}

	public String getName() {
		return getRegistryItem().getName().getText(NLLocale.getDefault().getLanguage());
	}

}
