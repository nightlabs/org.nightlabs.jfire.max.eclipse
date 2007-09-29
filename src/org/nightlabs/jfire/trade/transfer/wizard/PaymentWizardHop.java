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

package org.nightlabs.jfire.trade.transfer.wizard;

import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.jfire.accounting.pay.Payment;
import org.nightlabs.jfire.accounting.pay.PaymentData;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class PaymentWizardHop extends WizardHop
{
	private Payment payment;

	/**
	 * @param entryPage
	 */
	public PaymentWizardHop(PaymentEntryPage entryPage, Payment payment)
	{
		super(entryPage);

		if (payment == null)
			throw new NullPointerException("payment"); //$NON-NLS-1$

		this.payment = payment;
		payment.setCurrency(((PaymentWizard)getWizard()).getCurrency());
	}

	public PaymentEntryPage getPaymentEntryPage()
	{
		return (PaymentEntryPage) getEntryPage();
	}

	/**
	 * @return Returns the payment.
	 */
	public Payment getPayment()
	{
		return payment;
	}

	private PaymentData paymentData;

	/**
	 * @return Returns the paymentData.
	 */
	public PaymentData getPaymentData()
	{
		return paymentData;
	}
	/**
	 * @param paymentData The paymentData to set.
	 */
	public void setPaymentData(PaymentData paymentData)
	{
		this.paymentData = paymentData;
	}

	private long maxAmount = 0;

	/**
	 * @return Returns the maximum amount that can be paid by this payment.
	 */
	public long getMaxAmount()
	{
		return maxAmount;
	}
	/**
	 * @param maxAmount maxAmount to set. This is the maximum amount that can be paid
	 *		by this payment (this PaymentWizardHop). If <tt>amount > maxAmount</tt>,
	 *		<tt>amount</tt> will be set to <tt>maxAmount</tt>. 
	 */
	public void setMaxAmount(long maxAmount)
	{
		if (maxAmount < 0)
			throw new IllegalArgumentException("maxAmount = "+maxAmount+" < 0! Must be >= 0"); //$NON-NLS-1$ //$NON-NLS-2$

		this.maxAmount = maxAmount;
//		if (payment.getAmount() > maxAmount)
//			payment.setAmount(maxAmount);
	}

	/**
	 * @return Returns the amount to be paid by this payment. The range for amount is
	 *		<tt>0 &lt;= amount &lt;= maxAmount</tt>.
	 */
	public long getAmount()
	{
		return payment.getAmount();
	}
	/**
	 * @param amount The amount to set. Must be in the range
	 *		<tt>0 &lt;= amount &lt;= maxAmount</tt>.
	 */
	public void setAmount(long amount)
	{
		if (amount < 0)
			throw new IllegalArgumentException("amount = "+amount+" < 0! Must be >= 0"); //$NON-NLS-1$ //$NON-NLS-2$

//		if (amount > maxAmount)
//			throw new IllegalArgumentException("amount = "+amount+" too big! Must be amount <= maxAmount = "+maxAmount+"!");

		payment.setAmount(amount);
	}
}
