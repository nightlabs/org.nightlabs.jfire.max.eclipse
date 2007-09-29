package org.nightlabs.jfire.trade.ui.transfer.error;

import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.accounting.pay.PaymentResult;

public class PaymentResultTreeNode
		extends TransferTreeNode
{
	private Phase phase;
	private PaymentResult paymentResult;

	public PaymentResultTreeNode(Phase phase, PaymentResult paymentResult)
	{
		this.phase = phase;
		this.paymentResult = paymentResult;
	}

	@Implement
	public String getColumnText(int columnIndex)
	{
		switch (columnIndex) {
			case 0:
				return phase.toString();
			case 1:
				return paymentResult.getCode();
			case 2:
				return ""; //$NON-NLS-1$
			case 3:
				return paymentResult.getText();
			default:
				return ""; //$NON-NLS-1$
		}
	}

	@Implement
	public Object[] getChildren()
	{
		return null;
	}

	public Phase getPhase()
	{
		return phase;
	}
	public PaymentResult getPaymentResult()
	{
		return paymentResult;
	}
}
