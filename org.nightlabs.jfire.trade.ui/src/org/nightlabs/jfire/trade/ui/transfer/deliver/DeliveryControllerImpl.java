package org.nightlabs.jfire.trade.ui.transfer.deliver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nightlabs.datastructure.Pair;
import org.nightlabs.jfire.store.deliver.AbstractDeliveryController;
import org.nightlabs.jfire.store.deliver.Delivery;
import org.nightlabs.jfire.store.deliver.DeliveryData;
import org.nightlabs.jfire.store.deliver.DeliveryException;
import org.nightlabs.jfire.store.deliver.DeliveryResult;
import org.nightlabs.jfire.store.deliver.id.DeliveryID;
import org.nightlabs.jfire.transfer.TransferController;

/**
 * A controller for the different stages of the delivery process as described in the
 * <a href="https://www.jfire.org/modules/phpwiki/index.php/WorkflowPaymentAndDelivery">JFire Wiki</a>.
 *
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 * @see TransferController
 */
public class DeliveryControllerImpl extends AbstractDeliveryController {

	private Map<DeliveryData, ClientDeliveryProcessor> deliveryProcessorMap;

	/**
	 * Initialises a DeliveryControllerImpl with the list of tuples of the type ({@link DeliveryData}, {@link ClientDeliveryProcessor}). The controller
	 * will process all given {@link DeliveryData}s using the respective {@link ClientDeliveryProcessor} in the client stages.
	 * @param deliveryTuples A list of tuples of {@link DeliveryData} and a corresponding {@link ClientDeliveryProcessor}.
	 */
//	public DeliveryControllerImpl(List<Tuple<DeliveryData, ClientDeliveryProcessor>> deliveryTuples) {
//		super();
//		deliveryProcessorMap = new HashMap<DeliveryData, ClientDeliveryProcessor>();
//		List<DeliveryData> deliveryDatas = new LinkedList<DeliveryData>();
//		for (Tuple<DeliveryData, ClientDeliveryProcessor> tuple : deliveryTuples) {
//			deliveryDatas.add(tuple.getElement1());
//			deliveryProcessorMap.put(tuple.getElement1(), tuple.getElem2());
//		}
//		setTransferDatas(deliveryDatas);
//	}

	public DeliveryControllerImpl(List<Pair<DeliveryData, ClientDeliveryProcessor>> deliveryTuples) {
		super(getDeliveryDatas(deliveryTuples));

		deliveryProcessorMap = new HashMap<DeliveryData, ClientDeliveryProcessor>();
		for (Pair<DeliveryData, ClientDeliveryProcessor> tuple : deliveryTuples) {
			deliveryProcessorMap.put(tuple.getFirst(), tuple.getSecond());
		}
	}

	private static List<DeliveryData> getDeliveryDatas(List<Pair<DeliveryData, ClientDeliveryProcessor>> deliveryTuples) {
		List<DeliveryData> deliveryDatas = new LinkedList<DeliveryData>();
		for (Pair<DeliveryData, ClientDeliveryProcessor> tuple : deliveryTuples) {
			DeliveryData data = tuple.getFirst();
			deliveryDatas.add(data);
		}
		return deliveryDatas;
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.transfer.TransferController#clientBegin()
	 */
	@Override
	public boolean _clientBegin() {
		boolean allFailed = true;
		ClientDeliveryProcessor clientDeliveryProcessor = null;
		Delivery delivery = null;
		ArrayList<DeliveryResult> deliverBeginClientResults = new ArrayList<DeliveryResult>(getTransferDatas().size());

		for (DeliveryData deliveryData : getTransferDatas()) {
			clientDeliveryProcessor = deliveryProcessorMap.get(deliveryData);
			delivery = deliveryData.getDelivery();

			try {
				DeliveryResult deliverBeginClientResult = clientDeliveryProcessor.deliverBegin();
				if (deliverBeginClientResult == null)
					deliverBeginClientResult = new DeliveryResult(
							DeliveryResult.CODE_APPROVED_NO_EXTERNAL,
							(String)null,
							(Throwable)null);

				delivery.setDeliverBeginClientResult(deliverBeginClientResult);
			} catch (DeliveryException x) {
				delivery.setDeliverBeginClientResult(x.getDeliveryResult());
			} catch (Throwable t) {
				DeliveryResult deliverBeginClientResult = new DeliveryResult(t);
				delivery.setDeliverBeginClientResult(deliverBeginClientResult);
			}
			deliverBeginClientResults.add(delivery.getDeliverBeginClientResult());

			allFailed &= delivery.isFailed();
		}
		setLastStageResults(deliverBeginClientResults);

		return !allFailed;
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.transfer.TransferController#clientDoWork()
	 */
	@Override
	public void _clientDoWork() {
		List<DeliveryID> deliveryIDs = new ArrayList<DeliveryID>(getTransferDatas().size());
		ArrayList<DeliveryResult> deliverDoWorkClientResults = new ArrayList<DeliveryResult>(getTransferDatas().size());
		ClientDeliveryProcessor clientDeliveryProcessor = null;

		for (DeliveryData deliveryData : getTransferDatas()) {
			Delivery delivery = deliveryData.getDelivery();
			clientDeliveryProcessor = deliveryProcessorMap.get(deliveryData);

			deliveryIDs.add(DeliveryID.create(delivery.getOrganisationID(), delivery.getDeliveryID()));
			try {
				DeliveryResult deliverDoWorkClientResult = clientDeliveryProcessor.deliverDoWork();
				if (deliverDoWorkClientResult == null)
					deliverDoWorkClientResult = new DeliveryResult(
							DeliveryResult.CODE_DELIVERED_NO_EXTERNAL,
							(String)null,
							(Throwable)null);

				delivery.setDeliverDoWorkClientResult(deliverDoWorkClientResult);
			} catch (DeliveryException x) {
				delivery.setDeliverDoWorkClientResult(x.getDeliveryResult());
			} catch (Throwable t) {
				DeliveryResult deliverDoWorkClientResult = new DeliveryResult(t);

				delivery.setDeliverDoWorkClientResult(deliverDoWorkClientResult);
			}

			deliverDoWorkClientResults.add(delivery.getDeliverDoWorkClientResult());
		}
		setLastStageResults(deliverDoWorkClientResults);
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.transfer.TransferController#clientEnd()
	 */
	@Override
	public void _clientEnd() {
		ClientDeliveryProcessor clientDeliveryProcessor = null;
		Delivery delivery = null;

		List<DeliveryResult> deliverEndClientResults = new ArrayList<DeliveryResult>(getTransferDatas().size());
		for (DeliveryData deliveryData : getTransferDatas()) {
			clientDeliveryProcessor = deliveryProcessorMap.get(deliveryData);
			delivery = deliveryData.getDelivery();

			try {
				DeliveryResult deliverEndClientResult = clientDeliveryProcessor.deliverEnd();
				if (deliverEndClientResult == null) {
					if (delivery.isForceRollback() || delivery.isFailed()) {
						deliverEndClientResult = new DeliveryResult(
								DeliveryResult.CODE_ROLLED_BACK_NO_EXTERNAL,
								(String)null,
								(Throwable)null);
					}
					else {
						deliverEndClientResult = new DeliveryResult(
								DeliveryResult.CODE_COMMITTED_NO_EXTERNAL,
								(String)null,
								(Throwable)null);
					}
				}

				delivery.setDeliverEndClientResult(deliverEndClientResult);
			} catch (DeliveryException x) {
				delivery.setDeliverEndClientResult(x.getDeliveryResult());
			} catch (Throwable t) {
				DeliveryResult deliverEndClientResult = new DeliveryResult(t);
				delivery.setDeliverEndClientResult(deliverEndClientResult);
			}

			deliverEndClientResults.add(delivery.getDeliverEndClientResult());
		}
		setLastStageResults(deliverEndClientResults);
	}

//	/*
//	 * (non-Javadoc)
//	 * @see org.nightlabs.jfire.trade.ui.transfer.TransferController#isRollbackRequired()
//	 */
//	@Override
//	public boolean isRollbackRequired() {
//		if (isForceRollback())
//			return true;
//
//		for (DeliveryData deliveryData : getTransferDatas()) {
//			Delivery delivery = deliveryData.getDelivery();
//			if (delivery.isFailed() || delivery.isForceRollback())
//				return true;
//		}
//
//		return false;
//	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.transfer.TransferController#verifyData()
	 */
	@Override
	public void verifyData() {
		for (DeliveryData deliveryData : getTransferDatas()) {
			Delivery delivery = deliveryData.getDelivery();
			if ((delivery.getDeliverBeginClientResult() != null && delivery.getDeliverBeginClientResult().isRolledBack()) ||
					(delivery.getDeliverBeginServerResult() != null && delivery.getDeliverBeginServerResult().isRolledBack()) ||
					(delivery.getDeliverDoWorkClientResult() != null && delivery.getDeliverDoWorkClientResult().isRolledBack()) ||
					(delivery.getDeliverDoWorkServerResult() != null && delivery.getDeliverDoWorkServerResult().isRolledBack()) ||
					(delivery.getDeliverEndClientResult() != null && delivery.getDeliverEndClientResult().isRolledBack()) ||
					(delivery.getDeliverEndServerResult() != null && delivery.getDeliverEndServerResult().isRolledBack()))
				delivery.setRollbackStatus(Delivery.ROLLBACK_STATUS_DONE_NORMAL); // TODO this might differ from the value on the server - do we better download the data from the server?
		}
	}
}
