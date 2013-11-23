package org.nightlabs.jfire.voucher.admin.ui.overview;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.overview.AbstractTradeAdminCategory;
import org.nightlabs.jfire.trade.admin.ui.overview.TradeAdminCategoryFactory;
import org.nightlabs.jfire.voucher.admin.ui.createvouchertype.CreateVoucherTypeAction;
import org.nightlabs.jfire.voucher.admin.ui.editor.VoucherTypeEditor;
import org.nightlabs.jfire.voucher.admin.ui.editor.VoucherTypeEditorInput;
import org.nightlabs.jfire.voucher.admin.ui.tree.VoucherTypeTree;
import org.nightlabs.jfire.voucher.admin.ui.tree.VoucherTypeTreeNode;
import org.nightlabs.jfire.voucher.store.VoucherType;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public class VoucherTypeCategory
		extends AbstractTradeAdminCategory
{
	public VoucherTypeCategory(TradeAdminCategoryFactory tradeAdminCategoryFactory)
	{
		super(tradeAdminCategoryFactory);
	}

	@Override
	protected Composite _createComposite(Composite parent)
	{
		VoucherTypeTree voucherTypeTree = new VoucherTypeTree(parent);
		voucherTypeTree.getTreeViewer().expandToLevel(3);
		CreateVoucherTypeAction createVoucherTypeAction = new CreateVoucherTypeAction(voucherTypeTree);
		voucherTypeTree.addContextMenuContribution(createVoucherTypeAction);
		voucherTypeTree.getTreeViewer().addDoubleClickListener(doubleClickListener);
		return voucherTypeTree;
	}

	private IDoubleClickListener doubleClickListener = new IDoubleClickListener(){
		public void doubleClick(DoubleClickEvent event) {
			if (!event.getSelection().isEmpty()) {
				StructuredSelection sel = (StructuredSelection) event.getSelection();
				Object firstElement = sel.getFirstElement();
				if (firstElement instanceof VoucherTypeTreeNode) {
					VoucherTypeTreeNode treeNode = (VoucherTypeTreeNode) firstElement;
					VoucherType voucherType = treeNode.getJdoObject();
					ProductTypeID voucherTypeID = (ProductTypeID) JDOHelper.getObjectId(voucherType);
					try {
						RCPUtil.openEditor(new VoucherTypeEditorInput(voucherTypeID),
								VoucherTypeEditor.EDITOR_ID);
					} catch (PartInitException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	};
}
