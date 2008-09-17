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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.jdo.JDOHelper;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeComposite;

public class ReverseWizard extends DynamicPathWizard
{
	private OrderID orderID = null;
	private SelectOfferPage selectOfferPage;
	private Collection<Article> articles;
	private Collection<ArticleID> articleIDs;

	public ReverseWizard(Collection<Article> _articles)
	{
		this.articles = _articles;
		articleIDs = new ArrayList<ArticleID>(articles.size());
		for (Iterator<Article> it = articles.iterator(); it.hasNext(); ) {
			Article article = it.next();
			articleIDs.add((ArticleID)JDOHelper.getObjectId(article));

			if (orderID == null)
				orderID = article.getOrderID();
			else if (!orderID.equals(article.getOrderID()))
				throw new IllegalArgumentException("Not all Articles are in the same Order!"); //$NON-NLS-1$
		}
	}

	@Override
	public void addPages()
	{
		selectOfferPage = new SelectOfferPage(orderID);
		addPage(selectOfferPage);
	}

	private TradeManager tradeManager = null;
	protected TradeManager getTradeManager()
	{
		if (tradeManager == null) {
			try {
				tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			} catch (Exception x) {
				throw new RuntimeException(x);
			}
		}

		return tradeManager;
	}

	@Override
	public boolean performFinish()
	{
		try {
			OfferID offerID;
			switch (selectOfferPage.getAction()) {
				case SelectOfferPage.ACTION_CREATE:
//				 FIXME IDPREFIX (next line) should be asked from user if necessary!
					Offer offer = getTradeManager().createReverseOffer(articleIDs, null, true, null, 1);
					offerID = (OfferID) JDOHelper.getObjectId(offer);
					break;
				case SelectOfferPage.ACTION_SELECT:
					offerID = selectOfferPage.getSelectedOfferID();
					getTradeManager().reverseArticles(offerID, articleIDs, false, null, 1);
					break;
				default:
					throw new IllegalStateException("selectOfferPage.getAction() returned unknown action!"); //$NON-NLS-1$
			}
			HeaderTreeComposite.openEditor(new ArticleContainerEditorInput(offerID));
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public OrderID getOrderID()
	{
		return orderID;
	}
}
