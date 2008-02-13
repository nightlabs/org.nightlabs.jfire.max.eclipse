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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.base.ui.tree.TreeContentProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.book.LocalAccountantDelegate;
import org.nightlabs.jfire.accounting.book.id.LocalAccountantDelegateID;
import org.nightlabs.jfire.accounting.book.mappingbased.MoneyFlowMapping;
import org.nightlabs.jfire.accounting.book.mappingbased.PFMappingAccountantDelegate.ResolvedMapEntry;
import org.nightlabs.jfire.accounting.book.mappingbased.PFMappingAccountantDelegate.ResolvedMapKey;
import org.nightlabs.jfire.store.NestedProductTypeLocal;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.accounting.AccountingUtil;
import org.nightlabs.jfire.trade.ui.store.ProductTypeDAO;
import org.nightlabs.jfire.trade.ui.store.ProductTypePackageTree;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class ResolvedMappingTree
extends AbstractTreeComposite
{
	/**
	 * Instances of Node are the elements of the ProductTypePackageTree
	 */
	public static class Node {
		private Node parent;
		private MoneyFlowMapping mapping;
		private ProductTypeID productTypeID;
		private ProductType productType;
		private List<Node> children;
		private int productTypeDelegationLevel;
		
		public Node(Node parent, MoneyFlowMapping mapping, ProductTypeID productTypeID, Map resolvedMappings) {
			this.mapping = mapping;
			this.productTypeID = productTypeID;
			children = new LinkedList<Node>();
			
			if (productTypeID == null)
				return;
			productType = ProductTypeDAO.sharedInstance().getProductType(
					productTypeID, ProductTypePackageTree.DEFAULT_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new NullProgressMonitor());
			
			String packageType = MoneyFlowMapping.PACKAGE_TYPE_INNER;
			if (parent == null)
				packageType = MoneyFlowMapping.PACKAGE_TYPE_PACKAGE;
			
			ResolvedMapKey key = new ResolvedMapKey(productTypeID, packageType);
			
			ResolvedMapEntry entry = (ResolvedMapEntry) resolvedMappings.get(key);
			if (entry == null)
				return;
			productTypeDelegationLevel = entry.getDelegationLevel();
			
			
			for (Iterator iter = entry.getResolvedMappings().values().iterator(); iter.hasNext();) {
					
				MoneyFlowMapping entryMapping = (MoneyFlowMapping) iter.next();
				children.add(
						new Node(
								this,
								entryMapping,
								null,
								resolvedMappings
						)
				);
			}
			
			for (Iterator iter = productType.getProductTypeLocal().getNestedProductTypeLocals().iterator(); iter.hasNext();) {
				NestedProductTypeLocal nestedProductTypeLocal = (NestedProductTypeLocal) iter.next();
				children.add(
						new Node(
								this,
								null,
								ProductTypeID.create(
										nestedProductTypeLocal.getInnerProductTypeOrganisationID(),
										nestedProductTypeLocal.getInnerProductTypeProductTypeID()
								),
								resolvedMappings
						)
				);
			}
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
		public ProductTypeID getProductTypeID() {
			return productTypeID;
		}
		
		/**
		 * @return Returns the parent.
		 */
		public Node getParent() {
			return parent;
		}
		
		public ProductType getProductType() {
			return productType;
		}
		
		public MoneyFlowMapping getMapping() {
			return mapping;
		}
		
	}
		
	private ProductTypeID currentProductTypeID = null;
	/**
	 * The root Node for the current ProductTypeID
	 */
	private Node rootNode = null;
	
	/**
	 * Map to find all Nodes fast.
	 * key: ProductTypeID nodeProductTypeID
	 * value: Node node
	 */
	private Map nodesByProductTypeID = new HashMap();
	
	/**
	 * ContentProvider that holds a tree of Nodes.
	 * 
	 */
	private static class ContentProvider
	extends TreeContentProvider
	{
		/**
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Node)
				return getChildren(inputElement);
			else
				return new Object[] {};
		}
		
		/**
		 * @see org.nightlabs.base.ui.tree.TreeContentProvider#getChildren(java.lang.Object)
		 */
		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof Node && parentElement != null)
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

	public static final String[] FETCH_GROUP_RESOLVED_MONEY_FLOW_MAPPING = new String[] {
		FetchPlan.DEFAULT,
		MoneyFlowMapping.FETCH_GROUP_LOCAL_ACCOUNTANT_DELEGATE,
		MoneyFlowMapping.FETCH_GROUP_ALL_DIMENSIONS,
		Account.FETCH_GROUP_NAME,
		LocalAccountantDelegate.FETCH_GROUP_NAME
	};

	
	protected void buildTree(ProductTypeID productTypeID, LocalAccountantDelegateID delegateID, ProgressMonitor monitor)
	{
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.ResolvedMappingTree.loadMoneyFlowMappingMonitor.task.name"), 2); //$NON-NLS-1$
		monitor.worked(1);
		if ((currentProductTypeID == null) || (!productTypeID.equals(currentProductTypeID)))
		{
			nodesByProductTypeID.clear();
			Map resolvedMappings = null;
			currentProductTypeID = productTypeID;
			
			try {
				resolvedMappings = AccountingUtil.getAccountingManager().getResolvedMoneyFlowMappings(
						currentProductTypeID,
						delegateID,
						FETCH_GROUP_RESOLVED_MONEY_FLOW_MAPPING,
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT
					);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			rootNode = new Node(null, null, productTypeID, resolvedMappings);
		}
		if (currentProductTypeID == null) {
			rootNode = null;
		}
		monitor.worked(1);
	}
	
	/**
	 * Simple LabelProvider accessing Nodes
	 */
	private static class LabelProvider
	extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Node) {
				Node node = (Node)element;
				if (node.getProductType() != null) {
					switch (columnIndex) {
						case 0: return node.getProductType().getName().getText(Locale.getDefault().getLanguage());
					}
				}
				else {
					switch (columnIndex) {
						case 0:
							LocalAccountantDelegateType delegateType = LocalAccountantDelegateRegistry.sharedInstance().getTypeForMapping(node.getMapping().getClass());
							return delegateType.getMappingDescription(node.getMapping());
						case 1:
//							Account account = AccountDAO.sharedInstance().getAccount(
//									Account.primaryKeyToAnchorID(node.getMapping().getAccountPK()),
//									AccountCellEditor.DEFAULT_FETCH_GROUPS,
//									NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//									new NullProgressMonitor()
//								);
//							return account.getName().getText(Locale.getDefault().getLanguage());
							return getAccountName(node.getMapping().getRevenueAccount());
						case 2:
							return getAccountName(node.getMapping().getExpenseAccount());
						case 3:
							return getAccountName(node.getMapping().getReverseRevenueAccount());
						case 4:
							return getAccountName(node.getMapping().getReverseExpenseAccount());
						case 5:
							return node.getMapping().getLocalAccountantDelegate().getName().getText();
					}
				}
			}
			return ""; //$NON-NLS-1$
		}

		private String getAccountName(Account account) {
			return account == null ? "" : account.getName().getText();
		}
		
		/**
		 * @see org.nightlabs.base.ui.table.TableLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			return getColumnText(element, 0);
		}
	}
	
	/**
	 * @param parent
	 */
	public ResolvedMappingTree(Composite parent) {
		this(parent, DEFAULT_STYLE_SINGLE | SWT.FULL_SELECTION, true, true, true);
	}
		
	/**
	 * @param parent
	 * @param style
	 * @param setLayoutData
	 * @param init
	 * @param headerVisible
	 */
	public ResolvedMappingTree(Composite parent, int style,
			boolean setLayoutData, boolean init, boolean headerVisible) {
		super(parent, style, setLayoutData, init, headerVisible);
	}
	
	/**
	 * @see org.nightlabs.base.ui.tree.AbstractTreeComposite#setTreeProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	@Override
	public void setTreeProvider(TreeViewer treeViewer) {
		treeViewer.setContentProvider(new ContentProvider());
		treeViewer.setLabelProvider(new LabelProvider());
	}
	
	/**
	 * @see org.nightlabs.base.ui.tree.AbstractTreeComposite#createTreeColumns(org.eclipse.swt.widgets.Tree)
	 */
	@Override
	public void createTreeColumns(Tree tree) {
		TreeColumn column = new TreeColumn(getTree(), SWT.LEFT);
		column.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.ResolvedMappingTree.ProductTypeMappingTreeColumn.text")); //$NON-NLS-1$
		column.setResizable(true);

		column = new TreeColumn(getTree(), SWT.LEFT);
		column.setText("Revenue account");
		column.setToolTipText("Revenue account: Used to book revenues.");
		column.setResizable(true);

		column = new TreeColumn(getTree(), SWT.LEFT);
		column.setText("Expense account");
		column.setToolTipText("Expense account: Used to book expenses.");
		column.setResizable(true);

		column = new TreeColumn(getTree(), SWT.LEFT);
		column.setText("Reverse revenue account");
		column.setToolTipText("Reverse revenue account: Used to book reversing entries for revenues - if not assigned, the normal revenue account is used.");
		column.setResizable(true);

		column = new TreeColumn(getTree(), SWT.LEFT);
		column.setText("Reverse expense account");
		column.setToolTipText("Reverse expense account: Used to book reversing entries for expenses - if not assigned, the normal expense account is used.");
		column.setResizable(true);

		column = new TreeColumn(getTree(), SWT.LEFT);
		column.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.ResolvedMappingTree.contributingDelegateTreeColumn.text")); //$NON-NLS-1$
		column.setResizable(true);
		
		tree.setLayout(new WeightedTableLayout(new int[] {20, 10, 10, 10, 10, 20}));
	}
	
	protected ContentProvider getContentProvider() {
		return (ContentProvider)getTreeViewer().getContentProvider();
	}
	
	/**
	 * @deprecated use {@link #setProductType(ProductType)} instead
	 * 
	 * @param productTypeID the id of the {@link ProductType} to load
	 * @param monitor the monitor to display the progress of fetching the ProductType
	 */
	@Deprecated
	public void setProductTypeID(final ProductTypeID productTypeID, ProgressMonitor monitor) {
		if (productTypeID == null)
			return;
		
		ProductType productType = ProductTypeDAO.sharedInstance().getProductType(
				productTypeID, MoneyFlowMappingTree.DEFAULT_PTYPE_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor);
		if (!(productType != null && productType.getProductTypeLocal().getLocalAccountantDelegate() != null))
			return;
		final LocalAccountantDelegateID delegateID = (LocalAccountantDelegateID) JDOHelper.getObjectId(productType.getProductTypeLocal().getLocalAccountantDelegate());
		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.ResolvedMappingTree.loadMoneyFlowMappingJob.name")){ //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				buildTree(productTypeID, delegateID, monitor);
				Display.getDefault().syncExec(new Runnable(){
					public void run() {
						getTreeViewer().setInput(rootNode);
						getTreeViewer().expandAll();
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}
	
	/**
	 * sets the ProductType
	 * @param productType the productType to set
	 */
	public void setProductType(final ProductType productType) {
		if (productType == null)
			return;
		
		if (!(productType != null && productType.getProductTypeLocal().getLocalAccountantDelegate() != null))
			return;
		
		if (!productType.isSaleable())
			return;
		
		final ProductTypeID productTypeID = (ProductTypeID) JDOHelper.getObjectId(productType);
		final LocalAccountantDelegateID delegateID = (LocalAccountantDelegateID) JDOHelper.getObjectId(productType.getProductTypeLocal().getLocalAccountantDelegate());
		
		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.ResolvedMappingTree.loadMoneyFlowMappingJob.name")){ //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				buildTree(productTypeID, delegateID, monitor);
				Display.getDefault().syncExec(new Runnable(){
					public void run() {
						getTreeViewer().setInput(rootNode);
						getTreeViewer().expandAll();
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}
	
	public void clear()
	{
		Display.getDefault().syncExec(new Runnable(){
			public void run() {
				setInput(null);
			}
		});
	}
}
