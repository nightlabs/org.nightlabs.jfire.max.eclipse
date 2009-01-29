package org.nightlabs.jfire.voucher.admin.ui.tree;

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
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOObjectTreeContentProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOObjectTreeLabelProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedEventHandler;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.voucher.admin.ui.VoucherAdminPlugin;
import org.nightlabs.jfire.voucher.store.VoucherType;

public class VoucherTypeTree
extends AbstractTreeComposite<VoucherType>
{
	protected static class VoucherTypeTreeContentProvider
	extends JDOObjectTreeContentProvider<ProductTypeID, VoucherType, VoucherTypeTreeNode>
	{
		@Override
		public boolean hasJDOObjectChildren(VoucherType voucherType) {
			return voucherType.isInheritanceBranch();
		}
	}

	private ActiveVoucherTypeTreeController activeVoucherTypeTreeController;

	public VoucherTypeTree(Composite parent, int treeStyle)
	{
		super(parent, treeStyle, true, true, false);
		activeVoucherTypeTreeController = new ActiveVoucherTypeTreeController()
		{
			@Override
			protected void onJDOObjectsChanged(JDOTreeNodesChangedEvent<ProductTypeID, VoucherTypeTreeNode> changedEvent)
			{
				JDOTreeNodesChangedEventHandler.handle(getTreeViewer(), changedEvent);
			}
		};

		setInput(activeVoucherTypeTreeController);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				activeVoucherTypeTreeController.close();
				activeVoucherTypeTreeController = null;
			}
		});

		drillDownAdapter = new DrillDownAdapter(getTreeViewer());
		hookContextMenu();
	}
	
	public VoucherTypeTree(Composite parent)
	{
		this(parent, DEFAULT_STYLE_SINGLE);
	}
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				VoucherTypeTree.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(getTreeViewer().getControl());
		getTreeViewer().getControl().setMenu(menu);
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

	protected static class VoucherTypeTreeLabelProvider extends JDOObjectTreeLabelProvider<ProductTypeID, VoucherType, VoucherTypeTreeNode>
	{
		@Override
		protected String getJDOObjectText(VoucherType jdoObject, int columnIndex) {
			return jdoObject.getName().getText();
		}

		@Override
		protected Image getJDOObjectImage(VoucherType voucherType, int columnIndex) {
			if (columnIndex == 0)
				return SharedImages.getSharedImage(VoucherAdminPlugin.getDefault(),
						VoucherTypeTreeLabelProvider.class, voucherType.getInheritanceNatureString());

			return super.getJDOObjectImage(voucherType, columnIndex);
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
		treeViewer.setContentProvider(new VoucherTypeTreeContentProvider());
		treeViewer.setLabelProvider(new VoucherTypeTreeLabelProvider());
	}

	@Override
	protected VoucherType getSelectionObject(Object obj)
	{
		if (obj instanceof VoucherTypeTreeNode)
			return ((VoucherTypeTreeNode)obj).getJdoObject();

		return null;
	}
}
