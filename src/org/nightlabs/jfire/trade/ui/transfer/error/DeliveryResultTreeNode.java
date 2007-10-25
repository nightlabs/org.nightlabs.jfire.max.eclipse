package org.nightlabs.jfire.trade.ui.transfer.error;

import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.store.deliver.DeliveryResult;

public class DeliveryResultTreeNode
		extends TransferTreeNode
{
	private Phase phase;
	private DeliveryResult deliveryResult;

	public DeliveryResultTreeNode(Phase phase, DeliveryResult deliveryResult)
	{
		this.phase = phase;
		this.deliveryResult = deliveryResult;
	}

	@Override
	@Implement
	public String getColumnText(int columnIndex)
	{
		switch (columnIndex) {
			case 0:
				return phase.toString();
			case 1:
				return deliveryResult.getCode();
			case 2:
				return ""; //$NON-NLS-1$
			case 3:
				return deliveryResult.getText();
			default:
				return ""; //$NON-NLS-1$
		}
	}

	@Override
	@Implement
	public Object[] getChildren()
	{
		return null;
	}

	public Phase getPhase()
	{
		return phase;
	}

	public DeliveryResult getDeliveryResult()
	{
		return deliveryResult;
	}
}
