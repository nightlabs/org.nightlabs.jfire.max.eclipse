package org.nightlabs.jfire.trade.transfer.wizard;

import org.nightlabs.jfire.transfer.id.AnchorID;

public interface TransferWizard
{
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
	 * @return Returns an <tt>AnchorID</tt> which references a {@link org.nightlabs.jfire.trade.LegalEntity}.
	 */
	AnchorID getPartnerID();
}
