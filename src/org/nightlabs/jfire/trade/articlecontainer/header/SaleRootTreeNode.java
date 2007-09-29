package org.nightlabs.jfire.trade.articlecontainer.header;

import java.util.List;

import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.trade.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.CollectionUtil;

public class SaleRootTreeNode
extends HeaderTreeNode.RootNode
{
	private HeaderTreeNode[] _children;

	private OrderRootTreeNode orderTreeNode;
	private InvoiceRootTreeNode invoiceTreeNode;
	private DeliveryNoteRootTreeNode deliveryNoteTreeNode;

	public SaleRootTreeNode(HeaderTreeComposite headerTreeComposite)
	{
		super(headerTreeComposite, Messages.getString("org.nightlabs.jfire.trade.articlecontainer.header.SaleRootTreeNode.name"), headerTreeComposite.imageVendorRootTreeNode); //$NON-NLS-1$

		orderTreeNode = new OrderRootTreeNode(this, false);
		invoiceTreeNode = new InvoiceRootTreeNode(this, false);
		deliveryNoteTreeNode = new DeliveryNoteRootTreeNode(this, false);

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

	@Implement
	protected List<HeaderTreeNode> createChildNodes(List childData)
	{
		throw new UnsupportedOperationException("This method should never be called!"); //$NON-NLS-1$
	}

	@Implement
	protected List loadChildData(ProgressMonitor monitor)
	{
		throw new UnsupportedOperationException("This method should never be called!"); //$NON-NLS-1$
	}

}
