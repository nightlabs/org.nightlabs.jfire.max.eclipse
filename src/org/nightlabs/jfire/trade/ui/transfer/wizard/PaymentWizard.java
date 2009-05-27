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

	/**
	 * This method can of course only be called after you created the invoices in
	 * {@link org.eclipse.jface.wizard.IWizard#performFinish()}. It is
	 * called by {@link TransferWizardUtil#payAndDeliver(CombiTransferWizard, PaymentWizard, DeliveryWizard, byte)}
	 *
	 * @return Your implementation should return instances of
	 *		{@link org.nightlabs.jfire.accounting.id.InvoiceID}.
	 */
	Collection<InvoiceID> getInvoiceIDs();

	boolean isPaymentEnabled();
}
