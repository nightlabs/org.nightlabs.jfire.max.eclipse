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

package org.nightlabs.jfire.trade.admin.ui.moneyflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.base.ui.tree.TreeContentProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.AccountType;
import org.nightlabs.jfire.accounting.book.LocalAccountantDelegate;
import org.nightlabs.jfire.accounting.book.id.LocalAccountantDelegateID;
import org.nightlabs.jfire.accounting.book.mappingbased.MoneyFlowMapping;
import org.nightlabs.jfire.accounting.book.mappingbased.PFMappingAccountantDelegate;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeLocal;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.accounting.AccountCellEditor;
import org.nightlabs.jfire.trade.ui.accounting.AccountingUtil;
import org.nightlabs.jfire.trade.ui.store.ProductTypeDAO;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class MoneyFlowMappingTree extends AbstractTreeComposite {

	private static final int NODE_MODE_DELEGATE = 1;
	private static final int NODE_MODE_MAPPING = 2;
	
	private static class Node {
		private int mode;
		private MoneyFlowMapping mapping = null;
		private Object nodeObject = null;
		
		private List children = new LinkedList();
		private Node parent;
		
		private Node(Node parent, int mode, Object nodeObject) {
			this.mode = mode;
			switch (mode) {
				case NODE_MODE_DELEGATE:
					if (!(nodeObject instanceof LocalAccountantDelegate))
						throw new IllegalArgumentException("Node with type NODE_MODE_DELEGATE needs a LocalAccountantDelegate as nodeObject"); //$NON-NLS-1$
					this.nodeObject = nodeObject;
					break;
				case NODE_MODE_MAPPING:
					if (!(nodeObject instanceof MoneyFlowMapping))
						throw new IllegalArgumentException("Node with type NODE_MODE_MAPPING needs a LocalAccountantDelegate as nodeObject"); //$NON-NLS-1$
					this.mapping = (MoneyFlowMapping)nodeObject;
					break;
				default:
					throw new IllegalArgumentException("Create a node only with one of the modes: [NODE_MODE_CURRENCY, NODE_MODE_DELEGATE, NODE_MODE_MAPPING]"); //$NON-NLS-1$
			}
			this.parent = parent;
			if (parent != null) {
				parent.addChild(this);
			}
		}
		public static Node delegateNode(Node parent, LocalAccountantDelegate delegate) {
			return new Node(parent, NODE_MODE_DELEGATE, delegate);
		}
		public static Node mappingNode(Node parent, MoneyFlowMapping mapping) {
			return new Node(parent, NODE_MODE_MAPPING, mapping);
		}
		
		/**
		 * @return Returns the mapping.
		 */
		public MoneyFlowMapping getMapping() {
			return mapping;
		}
		/**
		 * @return Returns the mode.
		 */
		public int getMode() {
			return mode;
		}
		/**
		 * @return Returns the nodeObject.
		 */
		public Object getNodeObject() {
			return nodeObject;
		}
		
		public void addChild(Node node) {
			children.add(node);
		}
		
		public boolean hasChildren() {
			return !children.isEmpty();
		}
		
		public List getChildren() {
			return children;
		}
		
		public Node getParent() {
			return parent;
		}
		
		public void clearChildren() {
			children.clear();
		}
	}
		
	private static class ContentProvider extends TreeContentProvider {
		
		private LocalAccountantDelegate currDelegate;
		private ProductTypeID currProductTypeID;
		private Map nodesByMappings = new HashMap();		
		
		/**
		 * value Node delegateNode
		 */
		private List delegateNodes;
		
		public ContentProvider() {
		}
		
		protected void updateDelegateContent() {
			flushContent();			
			if (currDelegate == null)
				return;
			
			LocalAccountantDelegate delegateRun = currDelegate;
			while (delegateRun != null) {
				Node delegateNode = Node.delegateNode(null, delegateRun);
				if (delegateNodes == null)
					delegateNodes = new LinkedList();
				delegateNodes.add(delegateNode);
				if (delegateRun instanceof PFMappingAccountantDelegate) {
					for (Iterator iter = ((PFMappingAccountantDelegate) delegateRun).getMoneyFlowMappings().iterator(); iter.hasNext();) {
						MoneyFlowMapping mapping = (MoneyFlowMapping) iter.next();
						Node mappingNode = Node.mappingNode(delegateNode, mapping);
						nodesByMappings.put(mapping, mappingNode);
					}				
				}
				// TODO IMPROVE: when fetch-depth works change this 
				delegateRun = delegateRun.getExtendedAccountantDelegate();
				if (delegateRun != null)
					delegateRun = LocalAccountantDelegateProvider.sharedInstance()
					.getDelegate((LocalAccountantDelegateID)JDOHelper.getObjectId(delegateRun), DEFAULT_DELEGATE_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
			}
		}
		
		public void setDelegate(LocalAccountantDelegate delegate) {
			currDelegate = delegate;
			updateDelegateContent();
		}
		
		private static Object[] NO_ELEMENTS = new Object[]{};
		public Object[] getElements(Object inputElement) {
			if (delegateNodes == null)
				return NO_ELEMENTS;
			return delegateNodes.toArray();
		}
		
		public void flushContent() {
			delegateNodes = null;
			nodesByMappings.clear();
		}
		
		public Node getNodeByMapping(MoneyFlowMapping mapping) {
			return (Node)nodesByMappings.get(mapping);
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

		@Override
		public void dispose() {
		}
	}
	
	
	
	
	
	private class LabelProvider extends TableLabelProvider {

		public String getColumnText(Object element, int columnIndex) {
			if (!(element instanceof Node))
				return ""; //$NON-NLS-1$
			Node node = (Node)element;
			switch(node.getMode()) {
				case NODE_MODE_DELEGATE:
					if (columnIndex  == 0)
						return ((LocalAccountantDelegate)node.getNodeObject()).getName().getText();
					break;
				
				case NODE_MODE_MAPPING:
					// TODO: Implement LabelProvider mapping type
					MoneyFlowMapping mapping = node.getMapping();
					if (columnIndex == 0) {
//						ProductType productType = ProductTypeDAO.sharedInstance().getProductType(
//								ProductType.primaryKeyToProductTypeID(mapping.getProductTypePK()),
//								DEFAULT_PTYPE_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//								new NullProgressMonitor()
//							);
//						return productType.getName().getText();
						return mapping.getProductType().getName().getText();
					}
					else if (columnIndex == 1) {
						return mapping.getPackageType();
					}
					else if (columnIndex == currDimensionIDs.size() + 2) {
//						Account account = AccountDAO.sharedInstance().getAccount(
//								Account.primaryKeyToAnchorID(mapping.getAccountPK()),
//								AccountCellEditor.DEFAULT_FETCH_GROUPS, 
//								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//								new NullProgressMonitor()
//							);						
//						return account.getName().getText(Locale.getDefault().getLanguage());
						return getAccountName(mapping.getRevenueAccount());
					}
					else if (columnIndex == currDimensionIDs.size() + 3)
						return getAccountName(mapping.getExpenseAccount());
					else if (columnIndex == currDimensionIDs.size() + 4)
						return getAccountName(mapping.getReverseRevenueAccount());
					else if (columnIndex == currDimensionIDs.size() + 5)
						return getAccountName(mapping.getReverseExpenseAccount());
					else {
						MappingDimension dimension = MappingDimensionRegistry.sharedInstance().getDimension(
								(String)currDimensionIDs.get(columnIndex-2)
							);
						return dimension.getValueText(mapping);
					}
			}
			return ""; //$NON-NLS-1$
		}

		private String getAccountName(Account account)
		{
			return account == null ? "" : account.getName().getText();
		}
		
	}

	
	

	private static class CellModifier implements ICellModifier {
		private MoneyFlowMappingTree tree;		

		public CellModifier(MoneyFlowMappingTree tree) {
			this.tree = tree;
		}

		@Implement
		public boolean canModify(Object element, String property) {
			if (element instanceof Node) {
				Node node = (Node)element;
				// Currency and Delegate Nodes are not modifiable 
				if (node.getMode() == NODE_MODE_DELEGATE)
					return false;
				// get the registered dimension for the property
				if (COLUMN_PRODUCT_TYPE.equals(property))
					return false;
				if (COLUMN_PACKAGE_TYPE.equals(property))
					return false;
				if (COLUMN_REVENUE_ACCOUNT.equals(property))
					return true;
				if (COLUMN_EXPENSE_ACCOUNT.equals(property))
					return true;
				if (COLUMN_REVERSE_REVENUE_ACCOUNT.equals(property))
					return true;
				if (COLUMN_REVERSE_EXPENSE_ACCOUNT.equals(property))
					return true;

				MappingDimension dimension = MappingDimensionRegistry.sharedInstance().getDimensionByPropertyName(property);
				if (dimension == null)
					throw new IllegalStateException("Could not find a registered MappingDimension with cellPropertyName "+property); //$NON-NLS-1$
				
				// delegate the request
				return dimension.canModify(node.getMapping());
			}
			else
				return false;
		}

		@Implement
		public Object getValue(Object element, String property) {
			if (element instanceof Node) {
				Node node = (Node)element;
//				if (node.getMode() == NODE_MODE_DELEGATE)
//					// Should never be true as canModify returns false in this case 
//					return null;

				if (COLUMN_REVENUE_ACCOUNT.equals(property))
					return JDOHelper.getObjectId(node.getMapping().getRevenueAccount());
				if (COLUMN_EXPENSE_ACCOUNT.equals(property))
					return JDOHelper.getObjectId(node.getMapping().getExpenseAccount());
				if (COLUMN_REVERSE_REVENUE_ACCOUNT.equals(property))
					return JDOHelper.getObjectId(node.getMapping().getReverseRevenueAccount());
				if (COLUMN_REVERSE_EXPENSE_ACCOUNT.equals(property))
					return JDOHelper.getObjectId(node.getMapping().getReverseExpenseAccount());

				// get the registered dimension for the property
				MappingDimension dimension = MappingDimensionRegistry.sharedInstance().getDimensionByPropertyName(property);
				if (dimension == null)
					throw new IllegalStateException("Could not find a registered MappingDimension with cellPropertyName "+property); //$NON-NLS-1$
				
				// delegate the request
				return dimension.getValue(node.getMapping());
			}
			else
				return null;
		}

		@Implement
		public void modify(Object element, String property, Object value) {
			// we assume canModify was correct and 
			// now we can simply delegate to the MappingDimension
			boolean haveChanged = false;
			Object data = element;
			if (element instanceof TreeItem)
				data = ((TreeItem)element).getData();
			
			if (data instanceof Node) {
				Node node = (Node)data;
				if (!(node.getMode() == NODE_MODE_MAPPING))
					throw new IllegalStateException("Modify should only be called for Nodes with type NODE_MODE_MAPPING"); //$NON-NLS-1$

				if (COLUMN_REVENUE_ACCOUNT.equals(property)) {
//					if (value instanceof Account) {
						node.getMapping().setRevenueAccount((Account)value);
//					}
				}
				else if (COLUMN_EXPENSE_ACCOUNT.equals(property)) {
					node.getMapping().setExpenseAccount((Account)value);
				}
				else if (COLUMN_REVERSE_REVENUE_ACCOUNT.equals(property)) {
					node.getMapping().setReverseRevenueAccount((Account)value);
				}
				else if (COLUMN_REVERSE_EXPENSE_ACCOUNT.equals(property)) {
					node.getMapping().setReverseExpenseAccount((Account)value);
				}
				else {				
					// delegate to the MappingDimension
					MappingDimension dimension = MappingDimensionRegistry.sharedInstance().getDimensionByPropertyName(property);
					if (dimension == null)
						throw new IllegalStateException("Could not find a registered MappingDimension with cellPropertyName "+property); //$NON-NLS-1$
					dimension.modify(node.getMapping(), value);
				}
				if (haveChanged)
					tree.mappingModified(node.getMapping(), property);
			}
		}		
	}
		
	public static final String COLUMN_PRODUCT_TYPE = "productType"; //$NON-NLS-1$
	public static final String COLUMN_PACKAGE_TYPE = "packageType"; //$NON-NLS-1$
	public static final String COLUMN_REVENUE_ACCOUNT = "revenueAccount"; //$NON-NLS-1$
	public static final String COLUMN_EXPENSE_ACCOUNT = "expenseAccount"; //$NON-NLS-1$
	public static final String COLUMN_REVERSE_REVENUE_ACCOUNT = "reverseRevenueAccount"; //$NON-NLS-1$
	public static final String COLUMN_REVERSE_EXPENSE_ACCOUNT = "reverseExpenseAccount"; //$NON-NLS-1$

	public static final String[] DEFAULT_PTYPE_FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		LocalAccountantDelegate.FETCH_GROUP_NAME,
		ProductType.FETCH_GROUP_NAME,
//		ProductType.FETCH_GROUP_FIELD_METADATA_MAP,
		ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL,
		ProductTypeLocal.FETCH_GROUP_FIELD_METADATA_MAP,
		ProductTypeLocal.FETCH_GROUP_LOCAL_ACCOUNTANT_DELEGATE
	};
	
	public static final String[] DEFAULT_DELEGATE_FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		LocalAccountantDelegate.FETCH_GROUP_EXTENDED_ACCOUNTANT_DELEGATE,
		LocalAccountantDelegate.FETCH_GROUP_NAME,
		ProductType.FETCH_GROUP_NAME,
		PFMappingAccountantDelegate.FETCH_GROUP_MONEY_FLOW_MAPPINGS,
		MoneyFlowMapping.FETCH_GROUP_LOCAL_ACCOUNTANT_DELEGATE,
		MoneyFlowMapping.FETCH_GROUP_ALL_DIMENSIONS
		, Account.FETCH_GROUP_NAME
	};
	
	private PFMappingAccountantDelegate delegate;
	private Class currDelegateClass;
	private List currDimensionIDs;
	private CellModifier cellModifier;
	
	/**
	 * @param parent
	 */
	public MoneyFlowMappingTree(Composite parent, PFMappingAccountantDelegate delegate) {
		super(parent, DEFAULT_STYLE_SINGLE | SWT.FULL_SELECTION, true, true, true);
		this.delegate = delegate;
	}

	/**
	 * @see org.nightlabs.base.ui.tree.AbstractTreeComposite#setTreeProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	@Override
	public void setTreeProvider(TreeViewer treeViewer) {
		treeViewer.setContentProvider(new ContentProvider());
		treeViewer.setLabelProvider(new LabelProvider());
		cellModifier = new CellModifier(this);
		treeViewer.setCellModifier(cellModifier);
		
    treeViewer.addDragSupport(DND.DROP_MOVE, MoneyFlowMappingTransfer.MONEY_FLOW_MAPPING_TRANSFERS, new DragListener());    
    treeViewer.addDropSupport(DND.DROP_MOVE, MoneyFlowMappingTransfer.MONEY_FLOW_MAPPING_TRANSFERS, new DropAdapter(treeViewer));  
	}
	
	public PFMappingAccountantDelegate getDelegate() {
		return delegate;
	}
	
	private void setDelegate(PFMappingAccountantDelegate delegate) {
		this.delegate = delegate;
		getContentProvider().setDelegate(delegate);
		Display.getDefault().syncExec(new Runnable(){
			public void run() {
				updateColumnsAndCellEditors();
			}
		});
		refresh(true);
	}
	
	public void setDelegateID(LocalAccountantDelegateID delegateID) {
		setDelegate((PFMappingAccountantDelegate) LocalAccountantDelegateProvider.sharedInstance().getDelegate(delegateID, DEFAULT_DELEGATE_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT));
	}

	/**
	 * @deprecated use {@link #setProductType(ProductType)} instead
	 * 
	 * @param productTypeID the id of the {@link ProductType} to display the MoneyFlowMapping for
	 * @param monitor the monitor to display the progress of the loading
	 */
	@Deprecated
	public void setProductTypeID(ProductTypeID productTypeID, ProgressMonitor monitor) {
		PFMappingAccountantDelegate dDelegate = null;
		if (productTypeID == null) {
			getContentProvider().flushContent();
		}
		else {
			ProductType productType = ProductTypeDAO.sharedInstance().getProductType(
					productTypeID, DEFAULT_PTYPE_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					monitor);
			if (productType != null && productType.getProductTypeLocal().getLocalAccountantDelegate() != null)
				dDelegate = (PFMappingAccountantDelegate) LocalAccountantDelegateProvider.sharedInstance().getDelegate(
						(LocalAccountantDelegateID)JDOHelper.getObjectId(productType.getProductTypeLocal().getLocalAccountantDelegate()),
						DEFAULT_DELEGATE_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT
					);
		}
		setDelegate(dDelegate);
	}
	
	/**
	 * sets the {@link ProductType} to display the MoneyFlowMapping for
	 * @param productType the productType to set
	 */
	public void setProductType(ProductType productType) 
	{
		PFMappingAccountantDelegate dDelegate = null;
		if (productType == null) {
			getContentProvider().flushContent();
		}
		else {
			if (productType.getProductTypeLocal().getLocalAccountantDelegate() != null) {
				dDelegate = (PFMappingAccountantDelegate) LocalAccountantDelegateProvider.sharedInstance().getDelegate(
						(LocalAccountantDelegateID)JDOHelper.getObjectId(productType.getProductTypeLocal().getLocalAccountantDelegate()),
						DEFAULT_DELEGATE_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT
					);				
			}
		}
		setDelegate(dDelegate);
	}
	
	protected void updateColumnsAndCellEditors() {
		if (delegate == null) {
			currDelegateClass = null;
			return;
		}
		if (!delegate.getClass().equals(currDelegateClass)) {
			currDelegateClass = delegate.getClass();
			
			TreeColumn[] columns = getTreeViewer().getTree().getColumns();
			for (int i = 0; i < columns.length; i++) {
				columns[i].dispose();
			}
			currDimensionIDs = delegate.getMoneyFlowDimensionIDs();
			addLeadingColumns();
			for (Iterator iter = currDimensionIDs.iterator(); iter.hasNext();) {
				String moneyFlowDimensionID = (String) iter.next();
				MappingDimension dimension = MappingDimensionRegistry.sharedInstance().getDimension(moneyFlowDimensionID);
				if (dimension == null)
					throw new IllegalStateException("Could not find MappingDimension with moneyFlowDimensionID "+moneyFlowDimensionID+". Maybe it was not registered as extension to "+MappingDimensionRegistry.EXTENSION_POINT_ID); //$NON-NLS-1$ //$NON-NLS-2$
				TreeColumn column = new TreeColumn(getTree(), SWT.LEFT);
				column.setText(dimension.getName());
				column.setResizable(true);
				// TODO: Set better width
				if (iter.hasNext())
					column.setWidth(150);
				else
					column.setWidth(150);
			}
			addTrailingColumns();
			
			getTreeViewer().setColumnProperties(getCurrentColumnProperties());
			getTreeViewer().setCellEditors(getCurrentCellEditors());
		}
	}
	
	protected void addLeadingColumns() {
		TreeColumn column = new TreeColumn(getTree(), SWT.LEFT);
		column.setResizable(true);
		column.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.MoneyFlowMappingTree.productTypeTreeColumn.text")); //$NON-NLS-1$
		// TODO: Set better width
		column.setWidth(200);
		
		TreeColumn packageTypeCol = new TreeColumn(getTree(), SWT.LEFT);
		packageTypeCol.setResizable(true);
		packageTypeCol.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.MoneyFlowMappingTree.packageTypeTreeColumn.text")); //$NON-NLS-1$
		// TODO: Set better width
		packageTypeCol.setWidth(100);		
	}

	protected void addTrailingColumns() {
		TreeColumn column;

		column = new TreeColumn(getTree(), SWT.LEFT);
		column.setText("Revenue account");
		column.setToolTipText("Revenue account: Used to book revenues.");
		column.setResizable(true);
		column.setWidth(150);

		column = new TreeColumn(getTree(), SWT.LEFT);
		column.setText("Expense account");
		column.setToolTipText("Expense account: Used to book expenses.");
		column.setResizable(true);
		column.setWidth(150);

		column = new TreeColumn(getTree(), SWT.LEFT);
		column.setText("Reverse revenue account");
		column.setToolTipText("Reverse revenue account: Used to book reversing entries for revenues - if not assigned, the normal revenue account is used.");
		column.setResizable(true);
		column.setWidth(150);

		column = new TreeColumn(getTree(), SWT.LEFT);
		column.setText("Reverse expense account");
		column.setToolTipText("Reverse expense account: Used to book reversing entries for expenses - if not assigned, the normal expense account is used.");
		column.setResizable(true);
		column.setWidth(150);
	}

	protected String[] getCurrentColumnProperties() {
		int idLength = (currDimensionIDs == null) ? 0 : currDimensionIDs.size();
		String[] result = new String[idLength + 6];
		result[0] = COLUMN_PRODUCT_TYPE;
		result[1] = COLUMN_PACKAGE_TYPE;
//		result[1] = COLUMN_DELEGATE;
		result[result.length-4] = COLUMN_REVENUE_ACCOUNT;
		result[result.length-3] = COLUMN_EXPENSE_ACCOUNT;
		result[result.length-2] = COLUMN_REVERSE_REVENUE_ACCOUNT;
		result[result.length-1] = COLUMN_REVERSE_EXPENSE_ACCOUNT;
		int i = 2;
		if (currDimensionIDs != null) {
			for (Iterator iter = currDimensionIDs.iterator(); iter.hasNext();) {
				String moneyFlowDimensionID = (String) iter.next();
				MappingDimension dimension = MappingDimensionRegistry.sharedInstance().getDimension(moneyFlowDimensionID);
				result[i++] = dimension.getCellEditorPropertyName();
			}
		}
		return result;
	}
	
	protected CellEditor[] getCurrentCellEditors() {
		int idLength = (currDimensionIDs == null) ? 0 : currDimensionIDs.size();
		CellEditor[] result = new CellEditor[idLength + 6];
		result[0] = null;
		result[1] = null;
		result[result.length-4] = new AccountCellEditor(AccountType.ACCOUNT_TYPE_ID_LOCAL_REVENUE, getTree());
		result[result.length-3] = new AccountCellEditor(AccountType.ACCOUNT_TYPE_ID_LOCAL_EXPENSE, getTree());
		result[result.length-2] = new AccountCellEditor(AccountType.ACCOUNT_TYPE_ID_LOCAL_REVENUE, getTree());
		result[result.length-1] = new AccountCellEditor(AccountType.ACCOUNT_TYPE_ID_LOCAL_EXPENSE, getTree());
		int i = 2;
		if (currDimensionIDs != null) {
			for (Iterator iter = currDimensionIDs.iterator(); iter.hasNext();) {
				String moneyFlowDimensionID = (String) iter.next();
				MappingDimension dimension = MappingDimensionRegistry.sharedInstance().getDimension(moneyFlowDimensionID);
				result[i++] = dimension.getCellEditor();
			}
		}
		return result;
	}
	
	
	private MoneyFlowMapping lastAddedMapping;
	private MoneyFlowMapping nextSelectMapping;
	
	/**
	 * Called when a MoneyFlowMapping was modified or created.
	 * 
	 * @param mapping The mapping that was modified or created.
	 * @param property The column-property by which the mapping was modified or null if the mapping was newly created
	 */
	protected void mappingModified(MoneyFlowMapping mapping, String property) {
		try {
			AccountingUtil.getAccountingManager().storeMoneyFlowMapping(mapping, false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
			nextSelectMapping = mapping;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		refresh(false);
	}
	
	protected ContentProvider getContentProvider() {
		return (ContentProvider)getTreeViewer().getContentProvider();
	}

	/**
	 * @see org.nightlabs.base.ui.tree.AbstractTreeComposite#createTreeColumns(org.eclipse.swt.widgets.Tree)
	 */
	@Override
	public void createTreeColumns(Tree tree) {
		// nothing done here columns created dynamically
	}
	
	public PFMappingAccountantDelegate getCurrentDelegate() {
		return delegate;
	}

	@Override
	public void refresh(final boolean refreshInput) {
		getContentProvider().updateDelegateContent();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if ((lastAddedMapping != null) || refreshInput)
					getTreeViewer().setInput(getTreeViewer().getContentProvider());
				if (lastAddedMapping != null)
					lastAddedMapping = null;
				
				getTreeViewer().refresh(true);
				getTreeViewer().expandAll();
				
				if (nextSelectMapping != null) {
					getTreeViewer().setSelection(
							new StructuredSelection(
									getContentProvider().getNodeByMapping(nextSelectMapping)
								), 
							true
						);
					nextSelectMapping = null;
				}
			}
		});
	}
	
	public void setLastAddedMapping(
			MoneyFlowMapping nextRefreshSelectMapping) {
		this.lastAddedMapping = nextRefreshSelectMapping;
	}
	public MoneyFlowMapping getLastAddedMapping() {
		return lastAddedMapping;
	}
	
	private Node getSelectedNode() {
		IStructuredSelection selection = null;
		try {
			selection = (IStructuredSelection)getTreeViewer().getSelection();
		} catch(Throwable t) {
			throw new RuntimeException("Unable to get IStructuredSelection from TreeViewer: "+t.getMessage(), t); //$NON-NLS-1$
		}
		if (selection.isEmpty())
			return null;
		Object element = selection.getFirstElement();
		if (!(element instanceof Node))
			throw new IllegalStateException("Tree-element is not an instance of Node but "+element.getClass().getName()); //$NON-NLS-1$
		return (Node)element;
	}
	
	public LocalAccountantDelegate getSelectedLocalAccountantDelegate() {
		Node node = getSelectedNode();
		if (node == null)
			return null;
		if (node.getMode() == NODE_MODE_DELEGATE)
			return (LocalAccountantDelegate)node.getNodeObject();
		else if (node.getMode() == NODE_MODE_MAPPING)
			return node.getMapping().getLocalAccountantDelegate();
		else
			throw new IllegalStateException("Selected Node has unknown type: "+node.getMode()); //$NON-NLS-1$
	}
	
	public MoneyFlowMapping getSelectedMoneyFlowMapping() {
		Node node = getSelectedNode();
		if (node == null)
			return null;
		if (node.getMode() == NODE_MODE_MAPPING)
			return node.getMapping();
		return null;
	}
	
	private class DragListener implements DragSourceListener {
		
		public void dragStart(DragSourceEvent event) {
			System.out.println("DragStart"); //$NON-NLS-1$
			System.out.println(event.dataType);
			event.doit = true;
		}

		public void dragSetData(DragSourceEvent event) {
			IStructuredSelection selection = (IStructuredSelection) getTreeViewer().getSelection();
			List mappings = new ArrayList(selection.size());
			for (Iterator it = selection.iterator(); it.hasNext(); ) {
				Object o = it.next();
				if (o instanceof Node) {
					if (((Node)o).getMode() == NODE_MODE_MAPPING)
						mappings.add(((Node)o).getMapping());
				}
			}
			event.data = mappings; 
		}

		public void dragFinished(DragSourceEvent event) {
			System.out.println("DragFinished"); //$NON-NLS-1$
			event.doit = true;
		}
		
	};
	
	private class DropAdapter extends ViewerDropAdapter {
		
		protected DropAdapter(Viewer viewer) {
			super(viewer);
		}

		@Override
		public boolean performDrop(Object data) {
			return false;
		}

		@Override
		public boolean validateDrop(Object target, int operation, TransferData transferType) {
//			System.out.println("target == "+((Node)target).getMode());
//			System.out.println("Operation "+operation);
			if ((target instanceof Node && target != null)/* && ((operation & DND.DROP_MOVE) > 0)*/) {
//				System.out.println("Result "+ (((Node)target).getMode() == NODE_MODE_DELEGATE));
				return ((Node)target).getMode() == NODE_MODE_DELEGATE;
			}			
			return false;
		}
		
	};


}
