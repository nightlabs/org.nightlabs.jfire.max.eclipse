package org.nightlabs.jfire.dynamictrade.admin.ui.tree;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOObjectTreeContentProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOObjectTreeLabelProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedEventHandler;
import org.nightlabs.jfire.dynamictrade.admin.ui.DynamicTradeAdminPlugin;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;

public class DynamicProductTypeTree
extends AbstractTreeComposite<DynamicProductType>
{
	protected static class DynamicProductTypeTreeContentProvider
	extends JDOObjectTreeContentProvider<ProductTypeID, DynamicProductType, DynamicProductTypeTreeNode>
	{
		@Override
		public boolean hasJDOObjectChildren(DynamicProductType dynamicProductType) {
			return dynamicProductType.isInheritanceBranch();
		}
	}

	private ActiveDynamicProductTypeTreeController activeDynamicProductTypeTreeController;

	public DynamicProductTypeTree(Composite parent)
	{
		super(parent, DEFAULT_STYLE_SINGLE, true, true, false);
		activeDynamicProductTypeTreeController = new ActiveDynamicProductTypeTreeController()
		{
			@Override
			protected void onJDOObjectsChanged(JDOTreeNodesChangedEvent<ProductTypeID, DynamicProductTypeTreeNode> changedEvent)
			{
				JDOTreeNodesChangedEventHandler.handle(getTreeViewer(), changedEvent);
			}
		};

		setInput(activeDynamicProductTypeTreeController);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				activeDynamicProductTypeTreeController.close();
				activeDynamicProductTypeTreeController = null;
			}
		});

//		drillDownAdapter = new DrillDownAdapter(getTreeViewer());
//		hookContextMenu();
		createContextMenu(true);
	}

	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// Note: @Kai
	// Since 2010.02.27, we now have the super class (AbstractTreeComposite) to efficiently manage (priority-ordered) context-menus,
	// which has been streamlined to handle 3 types of contextMenuContributions: 
	//   (i) IContributionItem, (ii) IAction, and (iii) IViewActionDelegate.
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
//	private void hookContextMenu() {
//		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
//		menuMgr.setRemoveAllWhenShown(true);
//		menuMgr.addMenuListener(new IMenuListener() {
//			public void menuAboutToShow(IMenuManager manager) {
//				DynamicProductTypeTree.this.fillContextMenu(manager);
//			}
//		});
//		Menu menu = menuMgr.createContextMenu(getTreeViewer().getControl());
//		getTreeViewer().getControl().setMenu(menu);
////		if (getSite() != null)
////			getSite().registerContextMenu(menuMgr, getTreeViewer());
//	}
//
//	/**
//	 * Contains instances of both, {@link IContributionItem} and {@link IAction}
//	 */
//	private List<Object> contextMenuContributions;
//
//	public void addContextMenuContribution(IContributionItem contributionItem)
//	{
//		if (contextMenuContributions == null)
//			contextMenuContributions = new LinkedList<Object>();
//
//		contextMenuContributions.add(contributionItem);
//	}
//
//	public void addContextMenuContribution(IAction action)
//	{
//		if (contextMenuContributions == null)
//			contextMenuContributions = new LinkedList<Object>();
//
//		contextMenuContributions.add(action);
//	}
//
//	private void fillContextMenu(IMenuManager manager) {
//		if (contextMenuContributions != null) {
//			for (Object contextMenuContribution : contextMenuContributions) {
//				if (contextMenuContribution instanceof IContributionItem)
//					manager.add((IContributionItem)contextMenuContribution);
//				else if (contextMenuContribution instanceof IAction)
//					manager.add((IAction)contextMenuContribution);
//				else
//					throw new IllegalStateException("How the hell got an instance of " + (contextMenuContribution == null ? "null" : contextMenuContribution.getClass()) + " in the contextMenuContributions list?!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//			}
//		}
//
//		drillDownAdapter.addNavigationActions(manager);
//
//		// Other plug-ins can contribute their actions here
//		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
//	}
//
//	protected DrillDownAdapter drillDownAdapter;
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>

	protected static class DynamicProductTypeTreeLabelProvider extends JDOObjectTreeLabelProvider<ProductTypeID, DynamicProductType, DynamicProductTypeTreeNode>
	{
		@Override
		protected String getJDOObjectText(DynamicProductType jdoObject, int columnIndex) {
			return jdoObject.getName().getText();
		}

		@Override
		protected Image getJDOObjectImage(DynamicProductType dynamicProductType, int columnIndex) {
			if (columnIndex == 0)
				return SharedImages.getSharedImage(DynamicTradeAdminPlugin.getDefault(),
						DynamicProductTypeTreeLabelProvider.class, dynamicProductType.getInheritanceNatureString());

			return super.getJDOObjectImage(dynamicProductType, columnIndex);
		}
	}

	@Override
	public void createTreeColumns(Tree tree)
	{
//		TreeColumn column = new TreeColumn(tree, SWT.LEFT);
	}

	@Override
	public void setTreeProvider(TreeViewer treeViewer)
	{
		treeViewer.setContentProvider(new DynamicProductTypeTreeContentProvider());
		treeViewer.setLabelProvider(new DynamicProductTypeTreeLabelProvider());
	}

	@Override
	protected DynamicProductType getSelectionObject(Object obj)
	{
//		return ((DynamicProductTypeTreeNode)obj).getJdoObject();
		if (obj instanceof DynamicProductTypeTreeNode)
			return ((DynamicProductTypeTreeNode)obj).getJdoObject();

		return null;
	}
}
