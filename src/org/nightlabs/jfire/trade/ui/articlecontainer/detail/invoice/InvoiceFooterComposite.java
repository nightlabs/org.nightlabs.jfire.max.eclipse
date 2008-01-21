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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice;

import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.FooterComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.GeneralEditorComposite;
import org.nightlabs.l10n.NumberFormatter;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class InvoiceFooterComposite
extends FooterComposite
{
	private Invoice invoice;

	public InvoiceFooterComposite(GeneralEditorComposite generalEditorComposite)
	{
		super(generalEditorComposite, generalEditorComposite);
		this.invoice = invoice;
//		refresh(invoice.getArticles());
	}

//	public void refresh() 
//	{
//		String price = NumberFormatter.formatCurrency(invoice.getPrice().getAmount(), invoice.getCurrency());		
//		setFooterText(TradePlugin.getResourceString("FooterComposite.totalPrice")+ " " + price);				
//	}
	@Override
	public void refresh()
	{
		String amountPaid = NumberFormatter.formatCurrency(invoice.getInvoiceLocal().getAmountPaid(), invoice.getCurrency());
		String amountToPay = NumberFormatter.formatCurrency(invoice.getInvoiceLocal().getAmountToPay(), invoice.getCurrency());
		String totalPrice = NumberFormatter.formatCurrency(invoice.getPrice().getAmount(), invoice.getCurrency());

		if (invoice.getInvoiceLocal().getAmountPaid() == 0)
			setFooterText(String.format("Total price: %3$s", amountPaid, amountToPay, totalPrice));
		else
			setFooterText(String.format("Already paid: %1$s  To pay: %2$s  Total price: %3$s", amountPaid, amountToPay, totalPrice));
	}
}
