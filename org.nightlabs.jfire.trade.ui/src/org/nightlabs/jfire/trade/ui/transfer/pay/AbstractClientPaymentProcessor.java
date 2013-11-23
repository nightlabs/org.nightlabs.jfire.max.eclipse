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

import java.util.Set;

import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.pay.Payment;
import org.nightlabs.jfire.accounting.pay.id.ServerPaymentProcessorID;
import org.nightlabs.jfire.trade.ui.transfer.wizard.IPaymentEntryPage;
import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public abstract class AbstractClientPaymentProcessor
implements ClientPaymentProcessor
{
	// the following fields are set BEFORE the init() method is called. - The init method does not exist anymore! Marco.
	private ClientPaymentProcessorFactory clientPaymentProcessorFactory;
	private AnchorID customerID;
	private Currency currency;
	private long amount;

	private Payment payment;

	private IPaymentEntryPage paymentEntryPage;

//	// the following fields are set AFTER the init() method is called.
//	InvoiceID invoiceID = null;

	@Override
	public void setClientPaymentProcessorFactory(
			ClientPaymentProcessorFactory clientPaymentProcessorFactory)
	{
		this.clientPaymentProcessorFactory = clientPaymentProcessorFactory;
	}

	@Override
	public void setPartnerID(AnchorID customerID)
	{
		this.customerID = customerID;
	}

	@Override
	public void setCurrency(Currency currency)
	{
		this.currency = currency;
	}

	@Override
	public void setAmount(long amount)
	{
		this.amount = amount;
	}


	@Override
	public long getAmount()
	{
		return amount;
	}

	@Override
	public ClientPaymentProcessorFactory getClientPaymentProcessorFactory()
	{
		return clientPaymentProcessorFactory;
	}

	@Override
	public Currency getCurrency()
	{
		return currency;
	}

	@Override
	public AnchorID getPartnerID()
	{
		return customerID;
	}

	@Override
	public Payment getPayment()
	{
		return payment;
	}

	@Override
	public void setPayment(Payment payment)
	{
		this.payment = payment;
	}

	@Override
	public void setPaymentEntryPage(IPaymentEntryPage paymentEntryPage)
	{
		this.paymentEntryPage = paymentEntryPage;
	}
	
	@Override
	public IPaymentEntryPage getPaymentEntryPage()
	{
		return paymentEntryPage;
	}

	public void init()
	{
		// nothing to do yet - but this might change
	}

	public String getRequirementCheckKey()
	{
		return null;
	}

//	@Implement
//	public void setInvoiceID(InvoiceID invoiceID)
//	{
//		this.invoiceID = invoiceID;
//	}
//	@Implement
//	public InvoiceID getInvoiceID()
//	{
//		return invoiceID;
//	}

	@Override
	public Set<ServerPaymentProcessorID> getIncludedServerPaymentProcessorIDs()
	{
		return null;
	}
	
	@Override
	public Set<ServerPaymentProcessorID> getExcludedServerPaymentProcessorIDs()
	{
		return null;
	}

}
