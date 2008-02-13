/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.trade.ui.transfer.wizard;

import java.util.Collection;
import java.util.List;

import org.nightlabs.base.ui.wizard.IDynamicPathWizard;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.trade.id.CustomerGroupID;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface PaymentWizard
extends IDynamicPathWizard, TransferWizard
{
//	public static final String SIDE_VENDOR = "vendor"; //$NON-NLS-1$
//	public static final String SIDE_CUSTOMER = "customer"; //$NON-NLS-1$
//
//	/**
//	 * This wizard can be either used on the vendor or on the customer side. If it is used
//	 * on the vendor side, it means that the local organisation is the vendor. The customer side
//	 * specifies that the local organisation is the customer. This is e.g. the case when the
//	 * local organisation has received an invoice and wants to book the payment (or even
//	 * DO the payment via electronic money transfer).
//	 *
//	 * @return Returns either {@link #SIDE_VENDOR} or {@link #SIDE_CUSTOMER}.
//	 */
//	String getSide();
//
//	/**
//	 * @return Returns an <tt>AnchorID</tt> which references a {@link org.nightlabs.jfire.trade.ui.LegalEntity}.
//	 */
//	AnchorID getPartnerID();

	/**
	 * If your wizard is for outgoing payments, you should return <tt>null</tt>. If
	 * you want to process incoming payments, you should return all <tt>CustomerGroup</tt>s
	 * available to your customer. This will control which
	 * {@link org.nightlabs.jfire.accounting.pay.ModeOfPaymentFlavour}s are available.
	 * If you return <tt>null</tt>, ALL flavours will be shown.
	 *
	 * @return Returns instances of {@link org.nightlabs.jfire.accounting.id.CustomerGroupID}
	 */
	Collection<CustomerGroupID> getCustomerGroupIDs();

	Currency getCurrency();
	
	/**
	 * @return Returns the total amount to be paid. This is always the sum of all
	 *		payments (in case there is a split payment with multiple mode of payment
	 *		flavours). Note, that this might be negative.
	 *
	 * @see #setTotalAmount(long)
	 */
	long getTotalAmount();

	/**
	 * @param totalAmount The total amount to pay with all payments together. This might be
	 *		less than 0. If it is negative, the paymentDirection will be turned around and
	 *		the {@link PaymentEntryPage} will receive the positive amount.
	 */
	void setTotalAmount(long totalAmount);

	/**
	 * @return Returns instances of {@link PaymentEntryPage}.
	 */
	List<PaymentEntryPage> getPaymentEntryPages();

//	/**
//	 * If you implement PaymentWizard and do NOT extend {@link AbstractCombiTransferWizard},
//	 * you must override
//	 * {@link IDynamicPathWizard#addDynamicWizardPage(IDynamicPathWizardPage)}
//	 * and {@link IDynamicPathWizard#addDynamicWizardPage(int, IDynamicPathWizardPage)}
//	 * and call this method if the passed page is an instance of
//	 * {@link PaymentEntryPage}.
//	 *
//	 * @param paymentEntryPage
//	 */
//	void addPaymentEntryPage(PaymentEntryPage paymentEntryPage);

//	Payment createPayment();
//
//	/**
//	 * @return Returns instances of {@link Payment} which have been previously created
//	 *		by {@link #createPayment()}.
//	 */
//	List getPayments();
//
//	/**
//	 * This method adds a newly created {@link PaymentData} and stores it to a previously created
//	 * {@link Payment} object.
//	 *
//	 * @see #createPayment()
//	 */
//	void addPaymentData(PaymentData paymentData);
//
//	PaymentData getPaymentData(Payment payment);


//	long getAmount();
//	/**
//	 * This method allows to initiate a multi-part-payment by reducing the amount.
//	 *
//	 * @param newAmount The new amount to be paid.
//	 */
//	void setAmount(long newAmount);
//
//	/**
//	 * @return Returns the maxAmount.
//	 */
//	long getMaxAmount();
//	/**
//	 * @param maxAmount The maxAmount to set.
//	 */
//	void setMaxAmount(long maxAmount);

//	/**
//	 * This method is called by the {@link PaymentEntryPage} after initialization
//	 * of the {@link ClientPaymentProcessor} and the result of
//	 * {@link ClientPaymentProcessor#getPaymentData()} is passed.
//	 *
//	 * @param paymentData
//	 */
//	void setPaymentData(PaymentData paymentData);
//
//	Payment getPayment();
//
//	/**
//	 * @return Returns the {@link PaymentData} which has been previously passed
//	 *		to {@link #setPaymentData(PaymentData)}.
//	 */
//	PaymentData getPaymentData();

//	ModeOfPaymentFlavour getModeOfPaymentFlavour();

//	ModeOfPaymentFlavourID getModeOfPaymentFlavourID();
//
//	ClientPaymentProcessor getClientPaymentProcessor();
//
//	ServerPaymentProcessorID getServerPaymentProcessorID();

	/**
	 * This method can of course only be called after you created the invoices in
	 * {@link org.eclipse.jface.wizard.IWizard#performFinish()}. It is
	 * called by {@link TransferWizardUtil#payAndDeliver(CombiTransferWizard, PaymentWizard, DeliveryWizard, byte)}
	 *
	 * @return Your implementation should return instances of
	 *		{@link org.nightlabs.jfire.accounting.id.InvoiceID}.
	 */
	Collection<InvoiceID> getInvoiceIDs();

	// PaymentEntryPage getPaymentEntryPage();

	boolean isPaymentEnabled();
}
