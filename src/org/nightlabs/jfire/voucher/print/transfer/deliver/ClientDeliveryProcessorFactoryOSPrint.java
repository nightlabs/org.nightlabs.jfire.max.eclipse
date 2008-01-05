package org.nightlabs.jfire.voucher.print.transfer.deliver;

import org.nightlabs.jfire.trade.ui.transfer.deliver.AbstractClientDeliveryProcessorFactory;
import org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ClientDeliveryProcessorFactoryOSPrint 
extends AbstractClientDeliveryProcessorFactory 
{

	public ClientDeliveryProcessor createClientDeliveryProcessor() {
		return new ClientDeliveryProcessorOSPrint();
	}

	public void init() {

	}

}
