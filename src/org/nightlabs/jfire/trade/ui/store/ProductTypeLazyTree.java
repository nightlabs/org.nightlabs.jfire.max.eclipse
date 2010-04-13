package org.nightlabs.jfire.trade.ui.store;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.DrillDownAdapter;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedEventHandler;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOObjectLazyTreeContentProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOObjectLazyTreeLabelProvider;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.ui.TradePlugin;

/**
 * @author Fitas Amine - fitas [dot] nightlabs [dot] de
 *
 */
public class ProductTypeLazyTree
extends AbstractTreeComposite<ProductType>
{
	protected static class ProductTypeTreeContentProvider
	extends JDOObjectLazyTreeContentProvider<ProductTypeID, ProductType, ProductTypeLazyTreeNode>
	{

	}

	private ActiveProductTypeLazyTreeController activeProductTypeTreeController;

	public ProductTypeLazyTree(Composite parent, int treeStyle)
	{
		super(parent, treeStyle|SWT.VIRTUAL, true, true, false);
		activeProductTypeTreeController = new ActiveProductTypeLazyTreeController()
		{
			@Override
			protected void onJDOObjectsChanged(JDOLazyTreeNodesChangedEvent<ProductTypeID, ProductTypeLazyTreeNode> changedEvent)
			{
				JDOLazyTreeNodesChangedEventHandler.handle(getTreeViewer(), changedEvent);
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

//		drillDownAdapter = new DrillDownAdapter(getTreeViewer());
//		hookContextMenu();
		
		TreeViewer treeViewer = getTreeViewer();
		createContextMenu(new DrillDownAdapter(treeViewer), treeViewer.getControl());
	}

	public ProductTypeLazyTree(Composite parent)
	{
		this(parent, DEFAULT_STYLE_SINGLE);
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
//				ProductTypeLazyTree.this.fillContextMenu(manager);
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

	protected static class ProductTypeTreeLabelProvider extends JDOObjectLazyTreeLabelProvider<ProductTypeID, ProductType, ProductTypeLazyTreeNode>
	{
		@Override
		protected String getJDOObjectText(ProductTypeID jdoObjectID, ProductType jdoObject, int columnIndex) {
			if (jdoObject == null)
				return jdoObjectID == null ? null : jdoObjectID.getPrimaryKey();

			return jdoObject.getName().getText();
		}

		@Override
		protected Image getJDOObjectImage(ProductTypeID jdoObjectID, ProductType productType, int columnIndex) {
			if (columnIndex == 0) {
				if (productType == null)
					return null;

				return SharedImages.getSharedImage(TradePlugin.getDefault(),
						ProductTypeTreeLabelProvider.class, productType.getInheritanceNatureString());
			}

			return super.getJDOObjectImage(jdoObjectID, productType, columnIndex);
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
		treeViewer.setContentProvider(new ProductTypeTreeContentProvider());
		treeViewer.setLabelProvider(new ProductTypeTreeLabelProvider());
	}

	@Override
	protected ProductType getSelectionObject(Object obj)
	{
		if (obj instanceof ProductTypeLazyTreeNode)
			return ((ProductTypeLazyTreeNode)obj).getJdoObject();

		return null;
	}
}
