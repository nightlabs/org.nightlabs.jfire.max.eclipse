package org.nightlabs.jfire.voucher.ui.transfer.pay;

import org.nightlabs.jfire.trade.ui.transfer.pay.AbstractClientPaymentProcessorFactory;
import org.nightlabs.jfire.trade.ui.transfer.pay.ClientPaymentProcessor;

public class ClientPaymentProcessorFactoryVoucher
		extends AbstractClientPaymentProcessorFactory
{
	@Override
	public void init()
	{
		// nothing to do
	}

	@Override
	public ClientPaymentProcessor createClientPaymentProcessor()
	{
		return new ClientPaymentProcessorVoucher();
	}
}
