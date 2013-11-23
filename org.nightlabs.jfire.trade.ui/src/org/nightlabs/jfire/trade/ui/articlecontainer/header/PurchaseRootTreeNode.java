package org.nightlabs.jfire.trade.ui.articlecontainer.header;

import java.util.List;

import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.CollectionUtil;

public class PurchaseRootTreeNode
extends HeaderTreeNode.RootNode
{
	private HeaderTreeNode[] _children;

	private OrderRootTreeNode orderTreeNode;
	private InvoiceRootTreeNode invoiceTreeNode;
	private DeliveryNoteRootTreeNode deliveryNoteTreeNode;

	public PurchaseRootTreeNode(HeaderTreeComposite headerTreeComposite)
	{
		super(headerTreeComposite, Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.header.PurchaseRootTreeNode.name"), headerTreeComposite.imageCustomerRootTreeNode); //$NON-NLS-1$

		orderTreeNode = new OrderRootTreeNode(this, true, false);
		invoiceTreeNode = new InvoiceRootTreeNode(this, true, false);
		deliveryNoteTreeNode = new DeliveryNoteRootTreeNode(this, true, false);

		_children = new HeaderTreeNode[] {
				orderTreeNode,
				invoiceTreeNode,
				deliveryNoteTreeNode
		};

		children = CollectionUtil.array2ArrayList(_children, true);
	}

	@Override
	public HeaderTreeNode[] getChildren()
	{
		return _children;
	}

	@Override
	protected List<HeaderTreeNode> createChildNodes(List<Object> childData)
	{
		throw new UnsupportedOperationException("This method should never be called!"); //$NON-NLS-1$
	}

	@Override
	protected List<Object> loadChildData(ProgressMonitor monitor)
	{
		throw new UnsupportedOperationException("This method should never be called!"); //$NON-NLS-1$
	}
}
