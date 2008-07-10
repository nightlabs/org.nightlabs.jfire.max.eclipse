/**
 * 
 */
package org.nightlabs.jfire.trade.ui.producttype.quicklist;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.base.ui.tree.TreeContentProvider;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeGroup;
import org.nightlabs.jfire.store.ProductTypeGroupIDSearchResult;
import org.nightlabs.jfire.store.ProductTypeGroupSearchResult;
import org.nightlabs.jfire.store.ProductTypeGroupSearchResult.Entry;
import org.nightlabs.jfire.store.id.ProductTypeGroupID;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.SelectionUtil.SelectionContainment;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.util.NLLocale;

/**
 * Abstract base implementation for trees which display {@link ProductTypeGroup}s.
 * 
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public abstract class AbstractProductTypeGroupTree
extends AbstractTreeComposite<AbstractProductTypeGroupTree.ProductTypeGroupNode>
implements ISelectionHandler
{
	public static String[] FETCH_GROUPS_PRODUCT_TYPE_GROUP = new String[] {
		FetchPlan.DEFAULT,
		ProductTypeGroup.FETCH_GROUP_NAME
	};

	public static String[] FETCH_GROUPS_PRODUCT_TYPE = new String[] {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_NAME
	};
	
	/**
	 * ContentProvider which holds the ProductTypeGroupIDSearchResult
	 * which was set as input for the viewer.
	 *  
	 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
	 *
	 */
	public static class ContentProvider extends TreeContentProvider 
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
	public static class LabelProvider extends TableLabelProvider 
	{	
		public String getColumnText(Object element, int columnIndex) {
			if (!(element instanceof ProductTypeGroupNode))
				throw new IllegalArgumentException("Expected ProductTypeGroupNode as element found "+element.getClass().getName()); //$NON-NLS-1$
			
			ProductTypeGroupNode node = (ProductTypeGroupNode)element;
			if (node.getType() == ProductTypeGroupNode.NODE_TYPE_GROUP) {
				if (columnIndex == 0) {
					ProductTypeGroup group = ((ProductTypeGroup) node.getNodeObject());
//					ProductTypeGroup group = ProductTypeGroupDAO.sharedInstance().getProductTypeGroup(
//							(ProductTypeGroupID)node.getNodeObject(),
//							FETCH_GROUPS_PRODUCT_TYPE_GROUP, 
//							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//							new NullProgressMonitor()
//					);
					return group.getName().getText(NLLocale.getDefault().getLanguage());
				}
				return ""; //$NON-NLS-1$
			}
			else {
				ProductType productType = (ProductType) node.getNodeObject();
//				ProductType productType = ProductTypeDAO.sharedInstance().getProductType(
//						(ProductTypeID)node.getNodeObject(),
//						FETCH_GROUPS_PRODUCT_TYPE, 
//						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//						new NullProgressMonitor()
//					);
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
//	public class ProductTypeGroupNode 
	public static class ProductTypeGroupNode
	{
		public static final int NODE_TYPE_GROUP = 1;
		public static final int NODE_TYPE_SINGLE = 2;
		private int type;
		private ProductTypeGroup productTypeGroup;		
		private ProductType productType;
		private ProductTypeGroupNode parent;
		private Object[] children;
		
		public ProductTypeGroupNode(ProductTypeGroupSearchResult.Entry entry) {
			if (entry.getProductTypes().size() > 1) {
				this.productType = null;
				this.productTypeGroup = entry.getProductTypeGroup();			
				this.type = NODE_TYPE_GROUP;
				List<ProductTypeGroupNode> c = new LinkedList<ProductTypeGroupNode>();
				for (Iterator<ProductType> iter = entry.getProductTypes().iterator(); iter.hasNext();) {
					ProductType pType = iter.next();
					ProductTypeGroupNode node = new ProductTypeGroupNode(pType);
					node.setParent(this);
					c.add(node);
				}
				children = c.toArray();
			}
			else {
				if (entry.getProductTypes().size() <= 0) {
					this.productType = null;
					this.productTypeGroup = entry.getProductTypeGroup();			
					this.type = NODE_TYPE_GROUP;
					return;
				}
				this.productType = (ProductType)entry.getProductTypes().toArray()[0];
				this.productTypeGroup = null;
				this.type = NODE_TYPE_SINGLE; 
			}
		}
		
		public ProductTypeGroupNode(ProductType productType) {
			this.productType = productType;
			this.productTypeGroup = null;
			this.type = NODE_TYPE_SINGLE; 
		}
		
		public ProductType getProductType() {
			return productType;
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
				return productTypeGroup;
			else
				return productType;
		}
		
		public int getType() {
			return type;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((productTypeGroup == null) ? 0 : productTypeGroup.hashCode());
			result = prime * result
					+ ((productType == null) ? 0 : productType.hashCode());
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
			if (productTypeGroup == null) {
				if (other.productTypeGroup != null)
					return false;
			} else if (!productTypeGroup.equals(other.productTypeGroup))
				return false;
			if (productType == null) {
				if (other.productType != null)
					return false;
			} else if (!productType.equals(other.productType))
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
		
		treeViewer.setComparator(new ViewerSorter(Collator.getInstance(NLLocale.getDefault())));
	}

	public void setInput(ProductTypeGroupIDSearchResult result) {
		getTreeViewer().setInput(result);
	}

	@Override
	public boolean canHandleSelection(ISelection selection) 
	{
		SelectionContainment selectionContainment = SelectionUtil.getSelectionContainment(selection);
		Set<ProductTypeGroupID> productTypeGroupsIDs = selectionContainment.getProductTypeGroupIDs();
		Set<ProductTypeID> productTypeIDs = selectionContainment.getProductTypeIDs();		 
		if (!selectionContainment.isEmpty()) {
			if (contentProvider != null && getSearchResult() != null) {
				// first check for contained productTypeGroupIDs
				for (ProductTypeGroupID productTypeGroupID : productTypeGroupsIDs) {
					Entry entry = getSearchResult().getEntry(productTypeGroupID);
					if (entry != null) {
						return true;
					}
				}
				// if nothing found yet, check for contained productTypeIDs in the productTypeGroups
				Collection<ProductTypeID> searchResultProductTypeIDs = getSearchResult().getAllProductTypeIDs();
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
				Entry entry = getSearchResult().getEntry(productTypeGroupID);
				selectedNodes.add(new ProductTypeGroupNode(entry));
			}
			// then create nodes for productTypeIDs
			for (ProductTypeID productTypeID : productTypeIDs) 
			{
				ProductType productType = getSearchResult().getProductType(productTypeID);
				selectedNodes.add(new ProductTypeGroupNode(productType));
			}
			// TODO: ARRGH, reveal does not work for trees
			super.setSelection(selectedNodes, true);
		}
		else {
			super.setSelection(selection);
		}
	}

	// TODO: maybe this should not come from the contentProvider
	protected ProductTypeGroupSearchResult getSearchResult() {
		return contentProvider.getSearchResult();
	}
}
