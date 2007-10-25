package org.nightlabs.jfire.trade.ui.transfer.error;

import java.util.ArrayList;

import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.accounting.pay.Payment;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.NumberFormatter;

public class PaymentTreeNode
		extends TransferTreeNode
{
	private Payment payment;

	public PaymentTreeNode(Payment payment)
	{
		this.payment = payment;
	}

	@Override
	@Implement
	public String getColumnText(int columnIndex)
	{
		switch (columnIndex) {
			case 0:
				return Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.PaymentTreeNode.payment"); //$NON-NLS-1$
			case 1:
				return NumberFormatter.formatCurrency(payment.getAmount(), payment.getCurrency());
			case 2:
				return payment.isFailed() ? Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.PaymentTreeNode.failure") : Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.PaymentTreeNode.success"); //$NON-NLS-1$ //$NON-NLS-2$
			case 3:
				return payment.isRolledBack() ? Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.PaymentTreeNode.rolledBack") : Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.PaymentTreeNode.notRolledBack"); //$NON-NLS-1$ //$NON-NLS-2$
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
			ArrayList<PaymentResultTreeNode> l = new ArrayList<PaymentResultTreeNode>();

			if (payment.getPayBeginClientResult() != null)
				l.add(
						new PaymentResultTreeNode(Phase.beginClient, payment.getPayBeginClientResult())
				);

			if (payment.getPayBeginServerResult() != null)
				l.add(
						new PaymentResultTreeNode(Phase.beginServer, payment.getPayBeginServerResult())
				);

			if (payment.getPayDoWorkClientResult() != null)
				l.add(
						new PaymentResultTreeNode(Phase.doWorkClient, payment.getPayDoWorkClientResult())
				);

			if (payment.getPayDoWorkServerResult() != null)
				l.add(
						new PaymentResultTreeNode(Phase.doWorkServer, payment.getPayDoWorkServerResult())
				);

			if (payment.getPayEndClientResult() != null)
				l.add(
						new PaymentResultTreeNode(Phase.endClient, payment.getPayEndClientResult())
				);

			if (payment.getPayEndServerResult() != null)
				l.add(
						new PaymentResultTreeNode(Phase.endServer, payment.getPayEndServerResult())
				);

			children = l.toArray();
		}
		return children;
	}
}
