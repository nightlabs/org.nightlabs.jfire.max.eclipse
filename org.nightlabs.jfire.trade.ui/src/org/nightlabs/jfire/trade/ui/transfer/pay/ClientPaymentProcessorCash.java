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

package org.nightlabs.jfire.trade.ui.transfer.pay;

import org.nightlabs.base.ui.wizard.IWizardHopPage;
import org.nightlabs.jfire.accounting.pay.PaymentData;
import org.nightlabs.jfire.accounting.pay.PaymentException;
import org.nightlabs.jfire.accounting.pay.PaymentResult;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ClientPaymentProcessorCash extends AbstractClientPaymentProcessor
{

	public ClientPaymentProcessorCash()
	{
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.pay.ClientPaymentProcessor#createPaymentWizardPage()
	 */
	public IWizardHopPage createPaymentWizardPage()
	{
		// for a cash payment, we don't need any specific data.
		return null;
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.pay.ClientPaymentProcessor#payBegin()
	 */
	public PaymentResult payBegin() throws PaymentException
	{
//		return new PaymentResult(
//				Login.sharedInstance().getOrganisationID(),
//				PaymentResult.CODE_POSTPONED,
//				"", null);
		return null;
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.pay.ClientPaymentProcessor#payDoWork()
	 */
	public PaymentResult payDoWork() throws PaymentException
	{
		return null;
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.pay.ClientPaymentProcessor#payEnd()
	 */
	public PaymentResult payEnd() throws PaymentException
	{
//		throw new PaymentException(new PaymentResult(
//				Login.sharedInstance().getOrganisationID(), PaymentResult.CODE_FAILED, "TEST-Failure in Client-Cash-Module!", null));
		return null;
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.pay.ClientPaymentProcessor#getPaymentData()
	 */
	public PaymentData getPaymentData()
	{
		return null;
	}
}
