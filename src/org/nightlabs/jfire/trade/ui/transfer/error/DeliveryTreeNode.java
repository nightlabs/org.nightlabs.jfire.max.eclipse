package org.nightlabs.jfire.trade.ui.transfer.error;

import java.util.ArrayList;

import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.store.deliver.Delivery;
import org.nightlabs.jfire.trade.ui.resource.Messages;

public class DeliveryTreeNode
		extends TransferTreeNode
{
	private Delivery delivery;

	public DeliveryTreeNode(Delivery delivery)
	{
		this.delivery = delivery;
	}

	@Override
	@Implement
	public String getColumnText(int columnIndex)
	{
		switch (columnIndex) {
			case 0:
				return Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.DeliveryTreeNode.delivery"); //$NON-NLS-1$
			case 1:
				return String.format(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.DeliveryTreeNode.articleCount"), new Integer(delivery.getArticleIDs().size())); //$NON-NLS-1$
			case 2:
				return delivery.isFailed() ? Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.DeliveryTreeNode.failure") : Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.DeliveryTreeNode.success"); //$NON-NLS-1$ //$NON-NLS-2$
			case 3:
				return delivery.isRolledBack() ? Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.DeliveryTreeNode.rolledBack") : Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.DeliveryTreeNode.notRolledBack"); //$NON-NLS-1$ //$NON-NLS-2$
			default:
				return ""; //$NON-NLS-1$
		}
	}

	private Object[] children = null;

	@Override
	@Implement
	public Object[] getChildren()
	{
		if (children == null) {
			ArrayList<DeliveryResultTreeNode> l = new ArrayList<DeliveryResultTreeNode>();

			if (delivery.getDeliverBeginClientResult() != null)
				l.add(
						new DeliveryResultTreeNode(Phase.beginClient, delivery.getDeliverBeginClientResult())
				);

			if (delivery.getDeliverBeginServerResult() != null)
				l.add(
						new DeliveryResultTreeNode(Phase.beginServer, delivery.getDeliverBeginServerResult())
				);

			if (delivery.getDeliverDoWorkClientResult() != null)
				l.add(
						new DeliveryResultTreeNode(Phase.doWorkClient, delivery.getDeliverDoWorkClientResult())
				);

			if (delivery.getDeliverDoWorkServerResult() != null)
				l.add(
						new DeliveryResultTreeNode(Phase.doWorkServer, delivery.getDeliverDoWorkServerResult())
				);

			if (delivery.getDeliverEndClientResult() != null)
				l.add(
						new DeliveryResultTreeNode(Phase.endClient, delivery.getDeliverEndClientResult())
				);

			if (delivery.getDeliverEndServerResult() != null)
				l.add(
						new DeliveryResultTreeNode(Phase.endServer, delivery.getDeliverEndServerResult())
				);

			children = l.toArray();
		}
		return children;
	}
}
