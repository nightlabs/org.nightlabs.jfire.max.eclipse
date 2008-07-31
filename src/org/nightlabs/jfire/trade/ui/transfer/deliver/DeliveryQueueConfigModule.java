package org.nightlabs.jfire.trade.ui.transfer.deliver;

import org.nightlabs.config.ConfigModule;
import org.nightlabs.jfire.store.deliver.id.DeliveryQueueID;

public class DeliveryQueueConfigModule extends ConfigModule {

	private static final long serialVersionUID = 1L;
	
	private DeliveryQueueID lastUsedDeliveryQueueId = null;
	
	public DeliveryQueueConfigModule() {
	}

	public DeliveryQueueID getLastUsedDeliveryQueueId() {
		return lastUsedDeliveryQueueId;
	}

	public void setLastUsedDeliveryQueueId(DeliveryQueueID lastUsedDeliveryQueueId) {
		this.lastUsedDeliveryQueueId = lastUsedDeliveryQueueId;
		setChanged();
	}
}
