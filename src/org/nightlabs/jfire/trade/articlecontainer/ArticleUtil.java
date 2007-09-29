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

package org.nightlabs.jfire.trade.articlecontainer;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.progress.ProgressMonitor;

public class ArticleUtil
{
	protected ArticleUtil() { }

	public static final String[] FETCH_GROUPS_DEFAULT_ONLY = new String[] { FetchPlan.DEFAULT };
	public static boolean isOfferFinalized(Article article, ProgressMonitor monitor)
	{
		Offer offer = OfferDAO.sharedInstance().getOffer(
				article.getOfferID(), FETCH_GROUPS_DEFAULT_ONLY, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		return offer.isFinalized();
	}

	protected static final String[] FETCH_GROUPS_OFFER_SIMPLE = new String[] {
		FetchPlan.DEFAULT,
		Offer.FETCH_GROUP_OFFER_LOCAL,
	};
	public static boolean isOfferAccepted(Article article, ProgressMonitor monitor)
	{
		Offer offer = OfferDAO.sharedInstance().getOffer(
				article.getOfferID(), FETCH_GROUPS_OFFER_SIMPLE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		return offer.getOfferLocal().isAccepted();
	}

	protected static final String[] FETCH_GROUPS_DELIVERY_NOTE_SIMPLE = new String[] {
		FetchPlan.DEFAULT,
		DeliveryNote.FETCH_GROUP_DELIVERY_NOTE_LOCAL
	};
	public static boolean isDeliveryNoteBooked(Article article, ProgressMonitor monitor)
	{
		DeliveryNote deliveryNote = DeliveryNoteDAO.sharedInstance().getDeliveryNote(
				article.getDeliveryNoteID(), FETCH_GROUPS_DELIVERY_NOTE_SIMPLE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor);
		return deliveryNote.getDeliveryNoteLocal().isBooked();
	}
}
