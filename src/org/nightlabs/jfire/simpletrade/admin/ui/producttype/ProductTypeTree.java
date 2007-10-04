package org.nightlabs.jfire.simpletrade.admin.ui.producttype;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.DrillDownAdapter;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOObjectTreeContentProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOObjectTreeLabelProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedEventHandler;
import org.nightlabs.jfire.simpletrade.admin.ui.SimpleTradeAdminPlugin;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;

public class ProductTypeTree
extends AbstractTreeComposite<SimpleProductType>
{
	protected static class ProductTypeTreeContentProvider 
	extends JDOObjectTreeContentProvider<ProductTypeID, SimpleProductType, ProductTypeTreeNode>
	{
		@Override
		public boolean hasJDOObjectChildren(SimpleProductType productType) {
			return productType.isInheritanceBranch();
		}
	}

	private ActiveProductTypeTreeController activeProductTypeTreeController;

	public ProductTypeTree(Composite parent, int treeStyle)
	{
		super(parent, treeStyle, true, true, false);
		activeProductTypeTreeController = new ActiveProductTypeTreeController()
		{
			@Override
			protected void onJDOObjectsChanged(JDOTreeNodesChangedEvent<ProductTypeID, ProductTypeTreeNode> changedEvent)
			{
				JDOTreeNodesChangedEventHandler.handle(getTreeViewer(), changedEvent);
			}
		};

		setInput(activeProductTypeTreeController);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				activeProductTypeTreeController.close();
				activeProductTypeTreeController = null;
			}
		});

		drillDownAdapter = new DrillDownAdapter(getTreeViewer());
		hookContextMenu();
	}	

	public ProductTypeTree(Composite parent)
	{
		this(parent, DEFAULT_STYLE_SINGLE);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ProductTypeTree.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(getTreeViewer().getControl());
		getTreeViewer().getControl().setMenu(menu);
//		if (getSite() != null)
//			getSite().registerContextMenu(menuMgr, getTreeViewer());
	}

	/**
	 * Contains instances of both, {@link IContributionItem} and {@link IAction}
	 */
	private List<Object> contextMenuContributions;

	public void addContextMenuContribution(IContributionItem contributionItem)
	{
		if (contextMenuContributions == null)
			contextMenuContributions = new LinkedList<Object>();

		contextMenuContributions.add(contributionItem);
	}

	public void addContextMenuContribution(IAction action)
	{
		if (contextMenuContributions == null)
			contextMenuContributions = new LinkedList<Object>();

		contextMenuContributions.add(action);
	}

	private void fillContextMenu(IMenuManager manager) {
		if (contextMenuContributions != null) {
			for (Object contextMenuContribution : contextMenuContributions) {
				if (contextMenuContribution instanceof IContributionItem)
					manager.add((IContributionItem)contextMenuContribution);
				else if (contextMenuContribution instanceof IAction)
					manager.add((IAction)contextMenuContribution);
				else
					throw new IllegalStateException("How the hell got an instance of " + (contextMenuContribution == null ? "null" : contextMenuContribution.getClass()) + " in the contextMenuContributions list?!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}

		drillDownAdapter.addNavigationActions(manager);

		// Other plug-ins can contribute their actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	protected DrillDownAdapter drillDownAdapter;

	protected static class ProductTypeTreeLabelProvider extends JDOObjectTreeLabelProvider<ProductTypeID, ProductType, ProductTypeTreeNode>
	{
		@Override
		protected String getJDOObjectText(ProductType jdoObject, int columnIndex) {
			return jdoObject.getName().getText();
		}

		@Override
		protected Image getJDOObjectImage(ProductType productType, int columnIndex) {
			if (columnIndex == 0)
				return SharedImages.getSharedImage(SimpleTradeAdminPlugin.getDefault(), 
						ProductTypeTreeLabelProvider.class, productType.getInheritanceNatureString());

			return super.getJDOObjectImage(productType, columnIndex);
		}		
	}

	@Implement
	@Override
	public void createTreeColumns(Tree tree)
	{
//		TreeColumn column = new TreeColumn(tree, SWT.LEFT);
	}

	@Implement
	@Override
	public void setTreeProvider(TreeViewer treeViewer)
	{
		treeViewer.setContentProvider(new ProductTypeTreeContentProvider());
		treeViewer.setLabelProvider(new ProductTypeTreeLabelProvider());
	}

	@Override
	protected SimpleProductType getSelectionObject(Object obj)
	{
		if (obj instanceof ProductTypeTreeNode)
			return ((ProductTypeTreeNode)obj).getJdoObject();

		return null;
	}
}
