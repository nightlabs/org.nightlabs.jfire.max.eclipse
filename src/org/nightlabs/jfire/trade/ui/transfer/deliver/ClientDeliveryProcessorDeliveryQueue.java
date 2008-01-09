package org.nightlabs.jfire.trade.ui.transfer.deliver;

import java.util.HashSet;
import java.util.Set;

import org.nightlabs.base.ui.wizard.IWizardHopPage;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.organisation.Organisation;
import org.nightlabs.jfire.store.deliver.DeliveryData;
import org.nightlabs.jfire.store.deliver.DeliveryDataDeliveryQueue;
import org.nightlabs.jfire.store.deliver.DeliveryException;
import org.nightlabs.jfire.store.deliver.DeliveryQueue;
import org.nightlabs.jfire.store.deliver.DeliveryQueueConfigModule;
import org.nightlabs.jfire.store.deliver.DeliveryQueueDAO;
import org.nightlabs.jfire.store.deliver.DeliveryResult;
import org.nightlabs.jfire.store.deliver.ServerDeliveryProcessorDeliveryQueue;
import org.nightlabs.jfire.store.deliver.id.DeliveryQueueID;
import org.nightlabs.jfire.store.deliver.id.ServerDeliveryProcessorID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * 
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 */
public class ClientDeliveryProcessorDeliveryQueue extends AbstractClientDeliveryProcessor {

	private SelectTargetDeliveryQueueWizardPage wizardPage;
	private DeliveryQueueID targetQueueId;
	private DeliveryQueue targetQueue;

	public static class Factory extends AbstractClientDeliveryProcessorFactory {
		public ClientDeliveryProcessor createClientDeliveryProcessor() {
			return new ClientDeliveryProcessorDeliveryQueue();
		}

		public void init() {
		}
	}

	public IWizardHopPage createDeliveryWizardPage() {
		DeliveryQueueConfigModule deliveryQueueConfigModule = getDeliveryQueueConfigModule();
		if (deliveryQueueConfigModule.getVisibleDeliveryQueues().size() == 1) {
			targetQueueId = deliveryQueueConfigModule.getVisibleDeliveryQueues().get(0).getObjectID();
		} else {
			wizardPage = new SelectTargetDeliveryQueueWizardPage(deliveryQueueConfigModule.getVisibleDeliveryQueues());
		}
		return wizardPage;
	}

	private DeliveryQueueConfigModule getDeliveryQueueConfigModule() {
		String[] fetchGroups = new String[] { DeliveryQueueConfigModule.FETCH_GROUP_VISIBLE_DELIVERY_QUEUES, DeliveryQueue.FETCH_GROUP_NAME };
		DeliveryQueueConfigModule printQueueConfigModule = (DeliveryQueueConfigModule) ConfigUtil.getUserCfMod(DeliveryQueueConfigModule.class,
				fetchGroups, -1, new NullProgressMonitor());

		return printQueueConfigModule;
	}

	public DeliveryResult deliverBegin() throws DeliveryException {
		if (targetQueueId == null)
			targetQueueId = wizardPage.getSelectedDeliveryQueue().getObjectID();

		DeliveryQueue targetQueue = DeliveryQueueDAO.sharedInstance().getDeliveryQueue(targetQueueId, new String[] { DeliveryQueue.FETCH_GROUP_NAME }, -1,
				new NullProgressMonitor());
		deliveryData.setTargetQueue(targetQueue);
		return null;
	}

	public DeliveryResult deliverDoWork() throws DeliveryException {
		return null;
	}

	public DeliveryResult deliverEnd() throws DeliveryException {
		return null;
	}

	private DeliveryDataDeliveryQueue deliveryData;

	public DeliveryData getDeliveryData() {
		if (deliveryData == null) {
			deliveryData = new DeliveryDataDeliveryQueue(getDelivery());
		}
		return deliveryData;
	}

	@Override
	public void init() {
	}

	@Override
	public Set getIncludedServerDeliveryProcessorIDs() {
		Set<ServerDeliveryProcessorID> ids = new HashSet<ServerDeliveryProcessorID>();
		ids.add(ServerDeliveryProcessorID.create(Organisation.DEV_ORGANISATION_ID, ServerDeliveryProcessorDeliveryQueue.class.getName()));
		return ids;
	}

}