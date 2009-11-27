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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.deliverynote;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.FooterComposite;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class DeliveryNoteFooterComposite
extends FooterComposite
{
	public DeliveryNoteFooterComposite(Composite parent, ArticleContainerEditComposite articleContainerEditComposite)
	{
		super(parent, articleContainerEditComposite);
	}

//	public void refresh()
//	{
//		long priceAmount = 0;
//		Currency currency = null;
//		for (Article article : deliveryNote.getArticles()) {
//			ArticlePrice articlePrice = article.getPrice();
//			priceAmount =+ articlePrice.getAmount();
//			currency = article.getCurrency();
//		}
//		String price = NumberFormatter.formatCurrency(priceAmount, currency);
//		setFooterText(TradePlugin.getResourceString("FooterComposite.totalPrice")+ " " + price);
//	}
}
