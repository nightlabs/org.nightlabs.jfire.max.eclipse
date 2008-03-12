/**
 * 
 */
package org.nightlabs.jfire.trade.ui.producttype.quicklist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.base.ui.tree.TreeContentProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeGroup;
import org.nightlabs.jfire.store.ProductTypeGroupSearchResult;
import org.nightlabs.jfire.store.ProductTypeGroupSearchResult.Entry;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.dao.ProductTypeGroupDAO;
import org.nightlabs.jfire.store.id.ProductTypeGroupID;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.SelectionUtil.SelectionContainment;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * Abstract base implementation for trees which display {@link ProductTypeGroup}s.
 * 
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
//public abstract class AbstractProductTypeTree<P extends ProductType>
//extends AbstractTreeComposite<P>
public abstract class AbstractProductTypeGroupTree
extends AbstractTreeComposite
implements ISelectionHandler
{
	public static String[] PRODUCT_TYPE_GROUP_FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		ProductTypeGroup.FETCH_GROUP_NAME
	};

	public static String[] PRODUCT_TYPE_FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_NAME
	};
	
	/**
	 * ContentProvider which holds the ProductTypeGroupSearchResult
	 * which was set as input for the viewer.
	 *  
	 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
	 *
	 */
	public class ContentProvider extends TreeContentProvider 
	{		
		private ProductTypeGroupSearchResult searchResult;
		
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof ProductTypeGroupSearchResult) {
				searchResult = (ProductTypeGroupSearchResult) inputElement;
				return getRootElements(searchResult).toArray();
			}
			return null;
		}
		
		private Collection<ProductTypeGroupNode> getRootElements(ProductTypeGroupSearchResult input) {
			List<ProductTypeGroupNode> result = new LinkedList<ProductTypeGroupNode>();
			for (Iterator<ProductTypeGroupSearchResult.Entry> it = input.getEntries().iterator(); it.hasNext();) {
				ProductTypeGroupSearchResult.Entry entry = it.next();
				result.add(new ProductTypeGroupNode(entry));
			}
			return result;
		}
		
		/**
		 * @see org.nightlabs.base.ui.tree.TreeContentProvider#getChildren(java.lang.Object)
		 */
		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof ProductTypeGroupNode)
				return ((ProductTypeGroupNode)parentElement).getChildren();
			return super.getChildren(parentElement);
		}

		/**
		 * @see org.nightlabs.base.ui.tree.TreeContentProvider#getParent(java.lang.Object)
		 */
		@Override
		public Object getParent(Object element) {
			if (element instanceof ProductTypeGroupNode)
				return ((ProductTypeGroupNode)element).getChildren();
			return super.getParent(element);
		}

		/**
		 * @see org.nightlabs.base.ui.tree.TreeContentProvider#hasChildren(java.lang.Object)
		 */
		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof ProductTypeGroupNode)
				return ((ProductTypeGroupNode)element).hasChildren();
			return super.hasChildren(element);
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			super.inputChanged(viewer, oldInput, newInput);
			searchResult = null;
		}
		
		public ProductTypeGroupSearchResult getSearchResult() {
			return searchResult;
		}
	}	
	
	/**
	 * Simple LabelProvider which returns just the name for 
	 * {@link ProductTypeGroup}s, as well as for the contained
	 * {@link ProductType}s 
	 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
	 */
	public class LabelProvider extends TableLabelProvider 
	{	
		public String getColumnText(Object element, int columnIndex) {
			if (!(element instanceof ProductTypeGroupNode))
				throw new IllegalArgumentException("Expected ProductTypeGroupNode as element found "+element.getClass().getName()); //$NON-NLS-1$
			
			ProductTypeGroupNode node = (ProductTypeGroupNode)element;
			if (node.getType() == ProductTypeGroupNode.NODE_TYPE_GROUP) {
				if (columnIndex == 0) { 
					ProductTypeGroup group = ProductTypeGroupDAO.sharedInstance().getProductTypeGroup(
							(ProductTypeGroupID)node.getNodeObject(),
							PRODUCT_TYPE_GROUP_FETCH_GROUPS, 
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							new NullProgressMonitor()
					);
					return group.getName().getText(Locale.getDefault().getLanguage());
				}
				return ""; //$NON-NLS-1$
			}
			else {
				ProductType productType = ProductTypeDAO.sharedInstance().getProductType(
						(ProductTypeID)node.getNodeObject(),
						PRODUCT_TYPE_FETCH_GROUPS, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new NullProgressMonitor()
					);
				switch (columnIndex) {
					case 0: return productType.getName().getText(); 
				}
				return ""; //$NON-NLS-1$
			}
		}
	}	
	
	/**
	 * Node object for {@link AbstractProductTypeGroupTree}
	 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
	 */
	public class ProductTypeGroupNode 
	{
		public static final int NODE_TYPE_GROUP = 1;
		public static final int NODE_TYPE_SINGLE = 2;
		private int type;
		private ProductTypeGroupID productTypeGroupID;		
		private ProductTypeID productTypeID;
		private ProductTypeGroupNode parent;
		private Object[] children;
		
		public ProductTypeGroupNode(ProductTypeGroupSearchResult.Entry entry) {
			if (entry.getProductTypeIDs().size() > 1) {
				this.productTypeID = null;
				this.productTypeGroupID = entry.getProductTypeGroupID();			
				this.type = NODE_TYPE_GROUP;
				List<ProductTypeGroupNode> c = new LinkedList<ProductTypeGroupNode>();
				for (Iterator<ProductTypeID> iter = entry.getProductTypeIDs().iterator(); iter.hasNext();) {
					ProductTypeID pType = iter.next();
					ProductTypeGroupNode node = new ProductTypeGroupNode(pType);
					node.setParent(this);
					c.add(node);
				}
				children = c.toArray();
			}
			else {
				if (entry.getProductTypeIDs().size() <= 0) {
					this.productTypeID = null;
					this.productTypeGroupID = entry.getProductTypeGroupID();			
					this.type = NODE_TYPE_GROUP;
					return;
				}
				this.productTypeID = (ProductTypeID)entry.getProductTypeIDs().toArray()[0];
				this.productTypeGroupID = null;
				this.type = NODE_TYPE_SINGLE; 
			}
		}
		
		public ProductTypeGroupNode(ProductTypeID productTypeID) {
			this.productTypeID = productTypeID;
			this.productTypeGroupID = null;
			this.type = NODE_TYPE_SINGLE; 
		}
		
		public ProductTypeID getProductTypeID() {
			return productTypeID;
		}
		
		public boolean hasChildren() {
			return 
				(type != NODE_TYPE_SINGLE) && 
				((children == null) ? false: children.length > 0 );
		}
		
		public Object[] getChildren() {
			return children;
		}
		
		public ProductTypeGroupNode getParent() {
			return parent;		
		}
		
		public void setParent(ProductTypeGroupNode parent) {
			this.parent = parent;
		}
		
		public Object getNodeObject() {
			if (type == NODE_TYPE_GROUP)
				return productTypeGroupID;
			else
				return productTypeID;
		}
		
		public int getType() {
			return type;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((productTypeGroupID == null) ? 0 : productTypeGroupID.hashCode());
			result = prime * result
					+ ((productTypeID == null) ? 0 : productTypeID.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final ProductTypeGroupNode other = (ProductTypeGroupNode) obj;
			if (productTypeGroupID == null) {
				if (other.productTypeGroupID != null)
					return false;
			} else if (!productTypeGroupID.equals(other.productTypeGroupID))
				return false;
			if (productTypeID == null) {
				if (other.productTypeID != null)
					return false;
			} else if (!productTypeID.equals(other.productTypeID))
				return false;
			return true;
		}

	}
		
	private ContentProvider contentProvider;
	
	/**
	 * @param parent the parent composite
	 */
	public AbstractProductTypeGroupTree(Composite parent) {
		this(parent, AbstractTreeComposite.DEFAULT_STYLE_SINGLE);
	}

	/**
	 * @param parent the parent composite
	 * @param style the SWT style flag
	 */
	public AbstractProductTypeGroupTree(Composite parent, int style) {
		super(parent, style, true, true, true);
	}


	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.tree.AbstractTreeComposite#createTreeColumns(org.eclipse.swt.widgets.Tree)
	 */
	@Override
	public void createTreeColumns(Tree tree) {
		TreeColumn col = new TreeColumn(tree, SWT.LEFT);
		col.setText(Messages.getString("org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeGroupTree.column.name")); //$NON-NLS-1$
		TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(1));
		tree.setLayout(tableLayout);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.tree.AbstractTreeComposite#setTreeProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	@Override
	public void setTreeProvider(TreeViewer treeViewer) {
		contentProvider = new ContentProvider();
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(new LabelProvider());
	}

	public void setInput(ProductTypeGroupSearchResult result) {
		getTreeViewer().setInput(result);
	}

	@Override
	public boolean canHandleSelection(ISelection selection) 
	{
		SelectionContainment selectionContainment = SelectionUtil.getSelectionContainment(selection);
		Set<ProductTypeGroupID> productTypeGroupsIDs = selectionContainment.getProductTypeGroupIDs();
		Set<ProductTypeID> productTypeIDs = selectionContainment.getProductTypeIDs();		 
		if (!selectionContainment.isEmpty()) {
			if (contentProvider != null && contentProvider.getSearchResult() != null) {
				// first check for contained productTypeGroupIDs
				for (ProductTypeGroupID productTypeGroupID : productTypeGroupsIDs) {
					Entry entry = contentProvider.getSearchResult().getEntry(productTypeGroupID);
					if (entry != null) {
						return true;
					}
				}				
				// if nothing found yet, check for contained productTypeIDs in the productTypeGroups
				Set<ProductTypeID> searchResultProductTypeIDs = contentProvider.getSearchResult().getAllProductTypeIDs();
				for (ProductTypeID pTypeID : productTypeIDs) {
					if (searchResultProductTypeIDs.contains(pTypeID)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void setSelection(ISelection selection) {
		SelectionContainment selectionContainment = SelectionUtil.getSelectionContainment(selection);
		if (!selectionContainment.isEmpty()) {
			Set<ProductTypeGroupID> productTypeGroupsIDs = selectionContainment.getProductTypeGroupIDs();
			Set<ProductTypeID> productTypeIDs = selectionContainment.getProductTypeIDs();
			List<ProductTypeGroupNode> selectedNodes = new ArrayList<ProductTypeGroupNode>();
			// first create nodes for productTypeGroupIDs
			for (ProductTypeGroupID productTypeGroupID : productTypeGroupsIDs) {
				ProductTypeGroupSearchResult.Entry entry = new ProductTypeGroupSearchResult.Entry(productTypeGroupID);
				selectedNodes.add(new ProductTypeGroupNode(entry));
			}
			// then create nodes for productTypeIDs
			for (ProductTypeID productTypeID : productTypeIDs) {			
				selectedNodes.add(new ProductTypeGroupNode(productTypeID));
			}
			// TODO: ARRGH, reveal does not work for trees
			super.setSelection(selectedNodes, true);
		}
		else {
			super.setSelection(selection);
		}
	}

}
