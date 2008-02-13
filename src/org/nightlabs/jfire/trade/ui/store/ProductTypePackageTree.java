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
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.trade.ui.store;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.jdo.JDODetachedFieldAccessException;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.base.ui.tree.TreeContentProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.store.NestedProductTypeLocal;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeLocal;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * Generic ProductType-Tree displaying all nested product-types of a certain
 * ProductType.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class ProductTypePackageTree extends AbstractTreeComposite {
	
	public static final String[] DEFAULT_FETCH_GROUPS = new String[]{
		ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL,
		ProductTypeLocal.FETCH_GROUP_NESTED_PRODUCT_TYPE_LOCALS,
		ProductType.FETCH_GROUP_NAME
	};
	
	/**
	 * Instances of Node are the elements of the ProductTypePackageTree
	 */
	public static class Node {
		private Node parent;
		/**
		 * A list of Nodes
		 */
		private List children;
		/**
		 * This nodes name in the current language
		 */
		private String text;
		/**
		 * This nodes ProductTypeID
		 */
		private ProductTypeID nodeProductType;
		private ContentProvider contentProvider;
		
		public Node(ContentProvider contentProvider, Node parent, ProductTypeID nodeProductType) {
			this.nodeProductType = nodeProductType;
			this.parent = parent;
			this.contentProvider = contentProvider;
			refresh();
		}
		
		public void refresh() {
			if (nodeProductType == null)
				return;
			ProductType productType = ProductTypeDAO.sharedInstance().getProductType(
					nodeProductType, DEFAULT_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new NullProgressMonitor());
			contentProvider.nodesByProductTypeID.put(nodeProductType, this);
			try {
				text = productType.getName().getText(Locale.getDefault().getCountry());
			} catch (JDODetachedFieldAccessException e) {
				text = nodeProductType.toString();
			}
			children = new LinkedList();
			for (Iterator iter = productType.getProductTypeLocal().getNestedProductTypeLocals().iterator(); iter.hasNext();) {
				NestedProductTypeLocal nestedProductTypeLocal = (NestedProductTypeLocal) iter.next();
				children.add(
						new Node(
								contentProvider,
								this,
								ProductTypeID.create(
										nestedProductTypeLocal.getInnerProductTypeOrganisationID(),
										nestedProductTypeLocal.getInnerProductTypeProductTypeID()
								)
						)
				);
			}
		}

		/**
		 * @return Returns the text.
		 */
		public String getText() {
			return text;
		}

		/**
		 * @param text The text to set.
		 */
		public void setText(String text) {
			this.text = text;
		}

		/**
		 * @return Returns the children.
		 */
		public List getChildren() {
			return children;
		}
		
		public boolean hasChildren() {
			return children != null && children.size()>0;
		}

		/**
		 * @return Returns the nodeProductType.
		 */
		public ProductTypeID getNodeProductType() {
			return nodeProductType;
		}

		/**
		 * @return Returns the parent.
		 */
		public Node getParent() {
			return parent;
		}
		
	}
	
	/**
	 * ContentProvider that holds a tree of Nodes.
	 * 
	 */
	private class ContentProvider extends TreeContentProvider {

		private ProductTypeID currentProductTypeID = null;
		/**
		 * The root Node for the current ProductTypeID
		 */
		private Node rootNode = null;
		/**
		 * Dummy Object[] wrapping rootNode
		 */
		private Object[] rootElements = null;
		
		/**
		 * Map to find all Nodes fast.
		 * key: ProductTypeID nodeProductTypeID
		 * value: Node node
		 */
		private Map nodesByProductTypeID = new HashMap();
		
		/**
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			return rootElements;
//			if (inputElement instanceof ProductTypeID) {
//				ProductTypeID productTypeID = (ProductTypeID)inputElement;
//				return rootElements;
//			}
//			return null;
		}
		
		public void buildTree(ProductTypeID productTypeID) {
			if ((currentProductTypeID == null) || (!productTypeID.equals(currentProductTypeID))) {
				nodesByProductTypeID.clear();
				rootNode = new Node(this, null, productTypeID);
				rootElements = new Object[] {rootNode};
			}
			currentProductTypeID = productTypeID;
			if (currentProductTypeID == null) {
				rootNode = null;
				rootElements = new Object[] {};
			}
		}

		/**
		 * @see org.nightlabs.base.ui.tree.TreeContentProvider#getChildren(java.lang.Object)
		 */
		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof Node)
				return ((Node)parentElement).getChildren().toArray();
			return super.getChildren(parentElement);
		}

		/**
		 * @see org.nightlabs.base.ui.tree.TreeContentProvider#getParent(java.lang.Object)
		 */
		@Override
		public Object getParent(Object element) {
			if (element instanceof Node)
				return ((Node)element).getParent();
			return super.getParent(element);
		}

		/**
		 * @see org.nightlabs.base.ui.tree.TreeContentProvider#hasChildren(java.lang.Object)
		 */
		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof Node)
				return ((Node)element).hasChildren();
			return super.hasChildren(element);
		}
		
		/**
		 * Returns the ProductTypeID the tree currently loaded.
		 */
		public ProductTypeID getCurrentProductTypeID() {
			return currentProductTypeID;
		}

		@Override
		public void dispose() {
		}
	}

	/**
	 * Simple LabelProvider accessing Nodes
	 */
	private class LabelProvider extends TableLabelProvider {
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Node)
				return ((Node)element).getText();
			return ""; //$NON-NLS-1$
		}

		/**
		 * @see org.nightlabs.base.ui.table.TableLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if (element instanceof Node)
				return ((Node)element).getText();
			return ""; //$NON-NLS-1$
		}
		
		
	}
	
	/**
	 * Creates a new ProductTypePackageTree for the given parent.
	 * 
	 * @param parent The parent of the new ProductTypePackageTree
	 */
	public ProductTypePackageTree(Composite parent) {
		super(parent, SWT.NONE, true, true, false);
	}

	/**
	 * @param parent
	 * @param style
	 * @param setLayoutData
	 * @param init
	 * @param headerVisible
	 */
	public ProductTypePackageTree(Composite parent, int style,
			boolean setLayoutData, boolean init, boolean headerVisible) {
		super(parent, style, setLayoutData, init, headerVisible);
	}
	
	/**
	 * Sets the ProductTypeID for the new top-level ProductType which
	 * nested types should be displayed. If null is passed,
	 * the tree will be flushed.
	 * This can be called from a non-GUI thread it will do data-acquisition
	 * on the calling thread, but display on the SWT-GUI thread.
	 * 
	 * @param productTypeID The ProductTypeID to set. (Might be null)
	 */
	public void setProductTypeID(ProductTypeID productTypeID) {
		getContentProvider().buildTree(productTypeID);
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				getTreeViewer().refresh(true);
			}
		});
	}
	
	/**
	 * Returns the ProductTypeID the tree has currently loaded.
	 */
	public ProductTypeID getCurrentProductTypeID() {
		return getContentProvider().getCurrentProductTypeID();
	}

	private static Object dummyInput = new Object();
	/**
	 * @see org.nightlabs.base.ui.tree.AbstractTreeComposite#setTreeProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	@Override
	public void setTreeProvider(TreeViewer treeViewer) {
		treeViewer.setContentProvider(new ContentProvider());
		treeViewer.setLabelProvider(new LabelProvider());
		getContentProvider().buildTree(null);
		treeViewer.setInput(dummyInput);
	}

	/**
	 * @see org.nightlabs.base.ui.tree.AbstractTreeComposite#createTreeColumns(org.eclipse.swt.widgets.Tree)
	 */
	@Override
	public void createTreeColumns(Tree tree) {
		// TODO: Maybe add columns to ProductyTypePackageTree
	}
	
	protected ContentProvider getContentProvider() {
		return (ContentProvider)getTreeViewer().getContentProvider();
	}
	
	public ProductTypeID getSelectedProductTypeID() {
		if (getTree().getSelectionCount() == 1) {
			return ((Node)getTree().getSelection()[0].getData()).getNodeProductType();
		}
		return null;
	}
}
