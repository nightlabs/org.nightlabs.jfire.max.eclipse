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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.nightlabs.base.ui.wizard.IWizardHopPage;
import org.nightlabs.jfire.accounting.pay.PaymentData;
import org.nightlabs.jfire.accounting.pay.PaymentException;
import org.nightlabs.jfire.accounting.pay.PaymentResult;
import org.nightlabs.jfire.accounting.pay.ServerPaymentProcessorCreditCardDummyForClientPayment;
import org.nightlabs.jfire.accounting.pay.id.ServerPaymentProcessorID;
import org.nightlabs.jfire.organisation.Organisation;

/**
 * This payment processor gathers data and performs the payment with any backend (i.e.
 * server sided) payment module.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ClientPaymentProcessorECTerminal extends AbstractClientPaymentProcessor
{
	public ClientPaymentProcessorECTerminal()
	{
	}

	private static Set includedServerPaymentProcessorIDs = null;

	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.pay.AbstractClientPaymentProcessor#getIncludedServerPaymentProcessorIDs()
	 */
	@Override
	public Set getIncludedServerPaymentProcessorIDs()
	{
		if (includedServerPaymentProcessorIDs == null) {
			HashSet set = new HashSet();
			set.add(
					ServerPaymentProcessorID.create(
							Organisation.DEV_ORGANISATION_ID,
							ServerPaymentProcessorCreditCardDummyForClientPayment.class.getName()));

			includedServerPaymentProcessorIDs = Collections.unmodifiableSet(set);
		}

		return includedServerPaymentProcessorIDs;
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.pay.ClientPaymentProcessor#createPaymentWizardPage()
	 */
	public IWizardHopPage createPaymentWizardPage()
	{
		return null;
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.pay.ClientPaymentProcessor#payBegin()
	 */
	public PaymentResult payBegin() throws PaymentException
	{
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
