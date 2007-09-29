package org.nightlabs.jfire.trade.transfer.wizard;

import org.eclipse.swt.widgets.Shell;

public interface IPaymentEntryPage
{
	Shell getShell();
	void setAmount(long newAmount);
	long getMaxAmount();
}
