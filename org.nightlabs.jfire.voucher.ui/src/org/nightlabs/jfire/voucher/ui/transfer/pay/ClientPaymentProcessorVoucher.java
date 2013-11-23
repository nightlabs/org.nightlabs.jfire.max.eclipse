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

package org.nightlabs.jfire.voucher.ui.transfer.pay;

import org.nightlabs.base.ui.wizard.IWizardHopPage;
import org.nightlabs.jfire.accounting.pay.PaymentData;
import org.nightlabs.jfire.accounting.pay.PaymentException;
import org.nightlabs.jfire.accounting.pay.PaymentResult;
import org.nightlabs.jfire.trade.ui.transfer.pay.AbstractClientPaymentProcessor;
import org.nightlabs.jfire.voucher.accounting.pay.PaymentDataVoucher;
import org.nightlabs.jfire.voucher.ui.transfer.pay.wizard.VoucherKeyWizardPage;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ClientPaymentProcessorVoucher
extends AbstractClientPaymentProcessor
{
	private PaymentDataVoucher paymentDataVoucher;

	@Override
	public PaymentData getPaymentData()
	{
		if (paymentDataVoucher == null)
			paymentDataVoucher = new PaymentDataVoucher(getPayment());

		return paymentDataVoucher;
	}

	@Override
	public IWizardHopPage createPaymentWizardPage()
	{
		return new VoucherKeyWizardPage(this);
	}

	@Override
	public PaymentResult payBegin() throws PaymentException
	{
		return null;
	}

	@Override
	public PaymentResult payDoWork() throws PaymentException
	{
		return null;
	}

	@Override
	public PaymentResult payEnd() throws PaymentException
	{
		return null;
	}
}
