package org.nightlabs.jfire.trade.ui.transfer.pay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nightlabs.datastructure.Tuple;
import org.nightlabs.jfire.accounting.pay.AbstractPaymentController;
import org.nightlabs.jfire.accounting.pay.Payment;
import org.nightlabs.jfire.accounting.pay.PaymentData;
import org.nightlabs.jfire.accounting.pay.PaymentException;
import org.nightlabs.jfire.accounting.pay.PaymentResult;
import org.nightlabs.jfire.accounting.pay.id.PaymentID;
import org.nightlabs.jfire.idgenerator.IDGenerator;

/**
 * A controller for the different stages of the payment process as described in the
 * <a href="https://www.jfire.org/modules/phpwiki/index.php/WorkflowPaymentAndDelivery">JFire Wiki</a>.
 * 
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 */
public class PaymentControllerImpl extends AbstractPaymentController {
	
	private Map<PaymentData, ClientPaymentProcessor> paymentProcessorMap;

	/**
	 * Initialises a PaymentControllerImpl with the list of tuples of the type ({@link PaymentData}, {@link ClientPaymentProcessor}). The controller
	 * will process all given {@link PaymentData}s using the respective {@link ClientPaymentProcessor} in the client stages.
	 * @param paymentTuples A list of tuples of {@link PaymentData} and a corresponding {@link ClientPaymentProcessor}. 
	 */
	public PaymentControllerImpl(List<Tuple<PaymentData, ClientPaymentProcessor>> paymentTuples) {
		super(getPaymentDatas(paymentTuples));
		
		paymentProcessorMap = new HashMap<PaymentData, ClientPaymentProcessor>();
		for (Tuple<PaymentData, ClientPaymentProcessor> tuple : paymentTuples) {
			paymentProcessorMap.put(tuple.getFirst(), tuple.getSecond());
		}
	}
	
	private static List<PaymentData> getPaymentDatas(List<Tuple<PaymentData, ClientPaymentProcessor>> paymentTuples) {
		List<PaymentData> paymentDatas = new LinkedList<PaymentData>();
		for (Tuple<PaymentData, ClientPaymentProcessor> tuple : paymentTuples) {
			PaymentData data = tuple.getFirst();
			paymentDatas.add(data);
		}		
		return paymentDatas;
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.transfer.pay.PaymentController#clientBegin()
	 */
	@Override
	public boolean _clientBegin() {
		boolean allFailed = true;
		ClientPaymentProcessor clientPaymentProcessor = null;
		Payment payment = null;
		ArrayList<PaymentResult> payBeginClientResults = new ArrayList<PaymentResult>(getTransferDatas().size());
		
		for (PaymentData paymentData : getTransferDatas()) {
			clientPaymentProcessor = paymentProcessorMap.get(paymentData);
			payment = paymentData.getPayment();
			
			try {
				PaymentResult payBeginClientResult = clientPaymentProcessor.payBegin();
				if (payBeginClientResult == null)
					payBeginClientResult = new PaymentResult(
							PaymentResult.CODE_APPROVED_NO_EXTERNAL,
							(String)null,
							(Throwable)null);

				payment.setPayBeginClientResult(payBeginClientResult);

			} catch (PaymentException x) {
				payment.setPayBeginClientResult(x.getPaymentResult());
			} catch (Throwable t) {
				PaymentResult payBeginClientResult = new PaymentResult(IDGenerator.getOrganisationID(), t);

				payment.setPayBeginClientResult(payBeginClientResult);
			}
			payBeginClientResults.add(payment.getPayBeginClientResult());
			
			allFailed &= payment.isFailed();
		}
		
		setLastStageResults(payBeginClientResults);
		return !allFailed;
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.transfer.pay.PaymentController#clientDoWork()
	 */
	@Override
	public void _clientDoWork() {
		List<PaymentID> paymentIDs = new ArrayList<PaymentID>(getTransferDatas().size());
		ArrayList<PaymentResult> payDoWorkClientResults = new ArrayList<PaymentResult>(getTransferDatas().size());
		ClientPaymentProcessor clientPaymentProcessor = null;
		
		for (PaymentData paymentData : getTransferDatas()) {
			Payment payment = paymentData.getPayment();
			clientPaymentProcessor = paymentProcessorMap.get(paymentData);
			
			paymentIDs.add(PaymentID.create(payment.getOrganisationID(), payment.getPaymentID()));
			try {
				PaymentResult payDoWorkClientResult = clientPaymentProcessor.payDoWork();
				if (payDoWorkClientResult == null)
					payDoWorkClientResult = new PaymentResult(
							PaymentResult.CODE_PAID_NO_EXTERNAL,
							(String)null,
							(Throwable)null);
	
				payment.setPayDoWorkClientResult(payDoWorkClientResult);
			} catch (PaymentException x) {
				payment.setPayDoWorkClientResult(x.getPaymentResult());
			} catch (Throwable t) {
				PaymentResult payDoWorkClientResult = new PaymentResult(IDGenerator.getOrganisationID(), t);

				payment.setPayDoWorkClientResult(payDoWorkClientResult);
			}
			payDoWorkClientResults.add(payment.getPayDoWorkClientResult());
		}
		
//		setTransferIDs(paymentIDs);
		setLastStageResults(payDoWorkClientResults);
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.transfer.pay.PaymentController#clientEnd()
	 */
	@Override
	public void _clientEnd() {
		ClientPaymentProcessor clientPaymentProcessor = null;
		Payment payment = null;
		
		List<PaymentResult> payEndClientResults = new ArrayList<PaymentResult>(getTransferDatas().size());
		for (PaymentData paymentData : getTransferDatas()) {
			payment = paymentData.getPayment();
			clientPaymentProcessor = paymentProcessorMap.get(paymentData);
			
			try {
				PaymentResult payEndClientResult = clientPaymentProcessor.payEnd();
				if (payEndClientResult == null) {
					if (payment.isForceRollback() || payment.isFailed()) {
						payEndClientResult = new PaymentResult(
								PaymentResult.CODE_ROLLED_BACK_NO_EXTERNAL,
								(String)null,
								(Throwable)null);
					}
					else {
						payEndClientResult = new PaymentResult(
								PaymentResult.CODE_COMMITTED_NO_EXTERNAL,
								(String)null,
								(Throwable)null);
					}
				}

				payment.setPayEndClientResult(payEndClientResult);
			} catch (PaymentException x) {
				payment.setPayEndClientResult(x.getPaymentResult());
			} catch (Throwable t) {
				PaymentResult payEndClientResult = new PaymentResult(IDGenerator.getOrganisationID(), t);

				payment.setPayEndClientResult(payEndClientResult);
			}
			payEndClientResults.add(payment.getPayEndClientResult());
		}
		
		setLastStageResults(payEndClientResults);
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.transfer.pay.PaymentController#verifyData()
	 */
	@Override
	public void verifyData() {
		for (PaymentData paymentData : getTransferDatas()) {
			Payment payment = paymentData.getPayment();
			if ((payment.getPayBeginClientResult() != null && payment.getPayBeginClientResult().isRolledBack()) ||
					(payment.getPayBeginServerResult() != null && payment.getPayBeginServerResult().isRolledBack()) ||
					(payment.getPayDoWorkClientResult() != null && payment.getPayDoWorkClientResult().isRolledBack()) ||
					(payment.getPayDoWorkServerResult() != null && payment.getPayDoWorkServerResult().isRolledBack()) ||
					(payment.getPayEndClientResult() != null && payment.getPayEndClientResult().isRolledBack()) ||
					(payment.getPayEndServerResult() != null && payment.getPayEndServerResult().isRolledBack()))
				payment.setRollbackStatus(Payment.ROLLBACK_STATUS_DONE_NORMAL); // TODO this might differ from the value on the server - do we better download the data from the server?
		}
	}
}