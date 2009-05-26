package org.nightlabs.jfire.trade.ui.transfer;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import javax.naming.NamingException;
import javax.security.auth.login.LoginException;

import org.nightlabs.ModuleException;
import org.nightlabs.datastructure.Pair;
import org.nightlabs.jfire.accounting.pay.PaymentController;
import org.nightlabs.jfire.accounting.pay.PaymentData;
import org.nightlabs.jfire.store.deliver.DeliveryController;
import org.nightlabs.jfire.store.deliver.DeliveryData;
import org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessor;
import org.nightlabs.jfire.trade.ui.transfer.deliver.DeliveryControllerImpl;
import org.nightlabs.jfire.trade.ui.transfer.pay.ClientPaymentProcessor;
import org.nightlabs.jfire.trade.ui.transfer.pay.PaymentControllerImpl;
import org.nightlabs.jfire.trade.ui.transfer.wizard.TransferWizardUtil;
import org.nightlabs.jfire.transfer.TransferController;
import org.nightlabs.jfire.transfer.TransferData;

public class TransferCoordinator {
	private List<PaymentData> paymentDatas;
	private List<DeliveryData> deliveryDatas;

	public TransferCoordinator() {

	}

	public boolean payAndDeliver(
			List<Pair<PaymentData, ClientPaymentProcessor>> paymentTuples,
			List<Pair<DeliveryData, ClientDeliveryProcessor>> deliveryTuples)
	throws RemoteException, LoginException, NamingException, ModuleException
	{
		PaymentController paymentController = null;
		DeliveryController deliveryController = null;

		// prepare PaymentControllerImpl
		if (paymentTuples != null)
			paymentController = new PaymentControllerImpl(paymentTuples);
		else
			// if no payment is to be done, assign a noop instance to avoid endless if (paymentController == null) checks
			paymentController = new DummyPaymentController();

		// prepare DeliveryControllerImpl
		if (deliveryTuples != null)
			deliveryController = new DeliveryControllerImpl(deliveryTuples);
		else
			// if no delivery is to be done, assign a noop instance to avoid endless if (deliveryController == null) checks
			deliveryController = new DummyDeliveryController();

		performStages(deliveryController, paymentController);

		this.paymentDatas = paymentController.getTransferDatas();
		this.deliveryDatas = deliveryController.getTransferDatas();

//		if (paymentWizard != null) {
//			if (payment.isPostponed())
//				payment.clearPending();
//
//			if (payment.isPending() || payment.isFailed())
//				return false;
//		}

//	 TODO check both: delivery (only if deliveryWizard != null)
// TODO in case of an error, a detailed status message showing all payments/deliveries should be shown

//		if (paymentWizard != null) {
//			// If we come here, payment was successful
//
//			if (bookInvoiceMode == BOOK_INVOICE_MODE_AFTER_SUCCESSFUL_PAYMENT) {
//				try {
//					accountingManager.bookInvoices(paymentWizard.getInvoiceIDs(), true, true);
//				} catch (Throwable t) {
//					throw new ModuleException("Payment was successful, but book failed!", t);
//				}
//			}
//		} // if (paymentWizard != null) {

//		return true;
		// Daniel: moved failed check from ErrorDialog here
		boolean paymentsFailed = TransferWizardUtil.isPaymentsFailed(paymentDatas);
		boolean deliveriesFailed = TransferWizardUtil.isDeliveriesFailed(deliveryDatas);

		return !(paymentsFailed || deliveriesFailed);
	}

	public void performStages(DeliveryController deliveryController, PaymentController paymentController) throws LoginException {
		boolean skipServerPayment = false;
		boolean skipServerDelivery = false;

		///////////
		// BEGIN //
		///////////

		// if the client approve failed on ALL client payments, we don't do anything
		// in the server, but call the client's payEnd to allow clean-up.
		skipServerPayment = !paymentController.clientBegin();

		// if the client approve failed for ALL deliveries, we don't do anything
		// in the server, but call the client's deliverEnd to allow clean-up.
		skipServerDelivery = !deliveryController.clientBegin();

		if (skipServerDelivery)
			deliveryController.skipServerStages();
		if (skipServerPayment)
			paymentController.skipServerStages();


//	TODO perform Server-Payment and -Delivery in one step if both must be done

		deliveryController.serverBegin();
		paymentController.serverBegin();

		if (paymentController.isRollbackRequired() || deliveryController.isRollbackRequired()) {
			paymentController.forceRollback();
			deliveryController.forceRollback();
		}

		////////////
		// DOWORK //
		////////////

		deliveryController.clientDoWork();
		paymentController.clientDoWork();

		deliveryController.serverDoWork();
		paymentController.serverDoWork();

		if (paymentController.isRollbackRequired() || deliveryController.isRollbackRequired()) {
			paymentController.forceRollback();
			deliveryController.forceRollback();
		}

		/////////
		// END //
		/////////

		deliveryController.clientEnd();
		paymentController.clientEnd();

		deliveryController.serverEnd();
		paymentController.serverEnd();

		////////////
		// VERIFY //
		////////////

		paymentController.verifyData();
		deliveryController.verifyData();
	}

	public List<PaymentData> getPaymentDatas() {
		return paymentDatas;
	}

	public List<DeliveryData> getDeliveryDatas() {
		return deliveryDatas;
	}
}

/**
 * A {@link DeliveryController} that does absolutely nothing.
 *
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 */
class DummyController<D extends TransferData> implements TransferController<D> {
	public boolean clientBegin() { return true; }
	public void clientDoWork() {}
	public void clientEnd() {}
	public boolean isRollbackRequired() {
		return rollbackRequired;
	}
	public void serverBegin() {}
	public void serverDoWork() {}
	public void serverEnd() {}
	public void verifyData() {}

	private boolean rollbackRequired = false;

	public void forceRollback() {
		rollbackRequired = true;
	}
	public List<D> getTransferDatas() { return new LinkedList<D>(); }
	public void skipServerStages() {}
}

class DummyDeliveryController extends DummyController<DeliveryData> implements DeliveryController {
}

class DummyPaymentController extends DummyController<PaymentData> implements PaymentController {
}