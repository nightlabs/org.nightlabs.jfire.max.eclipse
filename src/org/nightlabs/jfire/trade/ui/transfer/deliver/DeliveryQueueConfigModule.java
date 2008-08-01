package org.nightlabs.jfire.trade.ui.transfer.deliver;

import org.nightlabs.config.ConfigModule;
import org.nightlabs.jfire.store.deliver.id.DeliveryQueueID;

public class DeliveryQueueConfigModule extends ConfigModule {

	private static final long serialVersionUID = 1L;
	
	private long lastUsedDeliveryQueueID = -1;
	private String lastUsedDeliveryQueueOrganisationID = null;
	
	public DeliveryQueueConfigModule() {
	}
	
	public DeliveryQueueID getLastUsedDeliveryQueueId() {
		return DeliveryQueueID.create(lastUsedDeliveryQueueID, lastUsedDeliveryQueueOrganisationID);
	}

	public void setLastUsedDeliveryQueueId(DeliveryQueueID lastUsedDeliveryQueueId) {
		this.lastUsedDeliveryQueueID = lastUsedDeliveryQueueId.deliveryQueueID;
		this.lastUsedDeliveryQueueOrganisationID = lastUsedDeliveryQueueId.organisationID;
		setChanged();
	}

	public long getLastUsedDeliveryQueueID() {
		return lastUsedDeliveryQueueID;
	}

	public void setLastUsedDeliveryQueueID(long lastUsedDeliveryQueueID) {
		this.lastUsedDeliveryQueueID = lastUsedDeliveryQueueID;
	}

	public String getLastUsedDeliveryQueueOrganisationID() {
		return lastUsedDeliveryQueueOrganisationID;
	}

	public void setLastUsedDeliveryQueueOrganisationID(String lastUsedDeliveryQueueOrganisationID) {
		this.lastUsedDeliveryQueueOrganisationID = lastUsedDeliveryQueueOrganisationID;
	}
}
