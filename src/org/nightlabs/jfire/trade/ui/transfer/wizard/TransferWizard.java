package org.nightlabs.jfire.trade.ui.transfer.wizard;

import org.eclipse.jface.wizard.IWizard;
import org.nightlabs.jfire.transfer.id.AnchorID;

public interface TransferWizard extends IWizard
{
	public static byte TRANSFER_MODE_PAYMENT = 1;
	public static byte TRANSFER_MODE_DELIVERY = 2;
	public static byte TRANSFER_MODE_BOTH = (byte)(TRANSFER_MODE_PAYMENT | TRANSFER_MODE_DELIVERY);

	public enum Side {
		Vendor,
		Customer;
	}

	/**
	 * This wizard can be either used on the vendor or on the customer side. If it is used
	 * on the vendor side, it means that the local organisation is the vendor. The customer side
	 * specifies that the local organisation is the customer. This is e.g. the case when the
	 * local organisation has received an invoice and wants to book the payment (or even
	 * DO the payment via electronic money transfer).
	 *
	 * @return Returns either {@link #SIDE_VENDOR} or {@link #SIDE_CUSTOMER}.
	 */
	Side getSide();

	/**
	 * @return Returns an <tt>AnchorID</tt> which references a {@link org.nightlabs.jfire.trade.ui.LegalEntity}.
	 */
	AnchorID getPartnerID();

	/**
	 *
	 * @return the set {@link IErrorHandler}
	 */
	IErrorHandler getErrorHandler();

	/**
	 *
	 * @param errorHandler the {@link IErrorHandler} to set.
	 */
	void setErrorHandler(IErrorHandler errorHandler);

	/**
	 *
	 * @return the transfer mode either {@link #TRANSFER_MODE_BOTH} or {@link #TRANSFER_MODE_DELIVERY} or {@link #TRANSFER_MODE_PAYMENT}.
	 */
	public byte getTransferMode();
}
