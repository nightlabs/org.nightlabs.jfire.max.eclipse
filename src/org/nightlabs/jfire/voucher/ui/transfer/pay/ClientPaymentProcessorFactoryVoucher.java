package org.nightlabs.jfire.voucher.ui.transfer.pay;

import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.trade.ui.transfer.pay.AbstractClientPaymentProcessorFactory;
import org.nightlabs.jfire.trade.ui.transfer.pay.ClientPaymentProcessor;

public class ClientPaymentProcessorFactoryVoucher
		extends AbstractClientPaymentProcessorFactory
{
	@Implement
	public void init()
	{
		// nothing to do
	}

	@Implement
	public ClientPaymentProcessor createClientPaymentProcessor()
	{
		return new ClientPaymentProcessorVoucher();
	}
}
