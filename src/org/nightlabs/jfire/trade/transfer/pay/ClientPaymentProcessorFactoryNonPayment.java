package org.nightlabs.jfire.trade.transfer.pay;

public class ClientPaymentProcessorFactoryNonPayment
		extends AbstractClientPaymentProcessorFactory
{

	public ClientPaymentProcessor createClientPaymentProcessor()
	{
		return new ClientPaymentProcessorNonPayment();
	}

	public void init()
	{
		// TODO Auto-generated method stub

	}

}
