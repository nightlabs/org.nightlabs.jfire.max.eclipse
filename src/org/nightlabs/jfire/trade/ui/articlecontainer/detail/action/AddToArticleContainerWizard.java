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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action;

import java.util.Collection;

import javax.jdo.FetchPlan;
import javax.jdo.JDODetachedFieldAccessException;
import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.AccountingManager;
import org.nightlabs.jfire.base.JFireEjbUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.store.StoreManager;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.dao.ArticleDAO;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.NullProgressMonitor;

public abstract class AddToArticleContainerWizard extends DynamicPathWizard
{
//	private ArticleContainer articleContainer;
	private Collection<Article> articles;

//	/**
//	 * This constructor calls {@link #AddToDeliveryNoteWizard(ArticleContainer, Collection)} with
//	 * <code>articles = null</code>. It is meant to be extended in child classes and then used
//	 * in {@link org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleContainerAction}s.
//	 */
//	public AddToArticleContainerWizard(ArticleContainer articleContainer)
//	{
//		this(articleContainer, null);
//	}

	/**
	 * This constructor calls {@link #AddToDeliveryNoteWizard(ArticleContainer, Collection)} with
	 * <code>articleContainer = null</code>. It is meant to be extended in child classes and then used
	 * in {@link org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleEditAction}s.
	 */
	public AddToArticleContainerWizard(Collection<Article> articles)
	{
//		this(null, articles);
		this.articles = articles;
	}

//	/**
//	 * @param articleContainer Must be either <code>null</code> or an instance of
//	 *		{@link org.nightlabs.jfire.trade.ui.Order}, {@link org.nightlabs.jfire.trade.ui.Offer}
//	 *		or {@link org.nightlabs.jfire.store.DeliveryNote}.
//	 * @param articles In case not a whole <code>articleContainer</code> shall be put into
//	 *		an {@link org.nightlabs.jfire.accounting.Invoice} but only a few {@link org.nightlabs.jfire.trade.ui.Article}s,
//	 *		they can be provided instead of an <code>articleContainer</code>. The only requirement is that
//	 *		all {@link org.nightlabs.jfire.trade.ui.Article}s have the same vendor, the same customer and the same
//	 *		currency.
//	 */
//	protected AddToArticleContainerWizard(ArticleContainer articleContainer, Collection articles)
//	{
//		if (articleContainer == null && articles == null)
//			throw new IllegalArgumentException("Both, articleContainer and articles, are null! One of them must be defined!");
//
//		if (articleContainer != null && articles != null)
//			throw new IllegalArgumentException("Both, articleContainer and articles, are defined! One of them must be null!");
//
//		if (articleContainer != null) {
//			if (!(articleContainer instanceof Order) &&
//					!(articleContainer instanceof Offer) &&
//					!(articleContainer instanceof DeliveryNote))
//					throw new IllegalArgumentException("articleContainer is an instance of " + articleContainer.getClass().getName() + ", but must be either Order, Offer or DeliveryNote!");
//		}
//
//		if (articles != null) {
//			if (articles.isEmpty())
//				throw new IllegalArgumentException("articles is empty! Is specifying articles, they must not be empty!");
//		}
//
//		this.articleContainer = articleContainer;
//		this.articles = articles;
//	}

	private AnchorID vendorID = null;
	private AnchorID customerID = null;

	private void initVendorIDAndCustomerID()
	{
		try {
//			if (articleContainer != null) {
//				if (articleContainer instanceof Order) {
//					vendorID = ((Order)articleContainer).getVendorID();
//					customerID = ((Order)articleContainer).getCustomerID();
//				}
//				else if (articleContainer instanceof Offer) {
//					vendorID = ((Offer)articleContainer).getVendorID();
//					customerID = ((Offer)articleContainer).getCustomerID();
//				}
//				else if  (articleContainer instanceof DeliveryNote) {
//					vendorID = ((DeliveryNote)articleContainer).getVendorID();
//					customerID = ((DeliveryNote)articleContainer).getCustomerID();
//				}
//				else
//					throw new IllegalArgumentException("articleContainer is an instance of " + articleContainer.getClass().getName() + ", but must be either Order, Offer or DeliveryNote!");
//			}
//			else {
				Article article = articles.iterator().next();
				vendorID = article.getVendorID();
				customerID = article.getCustomerID();
//			}

			return;
		} catch (JDODetachedFieldAccessException x) {
			// ignore and load data from server
			Logger.getLogger(this.getClass()).warn("Necessary data was not detached! I will query it now, but this is EXPENSIVE! You should optimize this!", x); //$NON-NLS-1$
		}

		try {
//			if (articleContainer != null) {
//				if (articleContainer instanceof Order) {
//					Order order = getTradeManager().getOrder((OrderID)JDOHelper.getObjectId(articleContainer), FETCH_GROUPS_ORDER_FOR_VENDOR_ID_AND_CUSTOMER_ID);
//					vendorID = order.getVendorID();
//					customerID = order.getCustomerID();
//				}
//				else if (articleContainer instanceof Offer) {
//					Offer offer = getTradeManager().getOffer((OfferID)JDOHelper.getObjectId(articleContainer), FETCH_GROUPS_OFFER_FOR_VENDOR_ID_AND_CUSTOMER_ID);
//					vendorID = offer.getVendorID();
//					customerID = offer.getCustomerID();
//				}
//				else if  (articleContainer instanceof DeliveryNote) {
//					DeliveryNote deliveryNote = getStoreManager().getDeliveryNote((DeliveryNoteID)JDOHelper.getObjectId(articleContainer), FETCH_GROUPS_DELIVERY_NOTE_FOR_VENDOR_ID_AND_CUSTOMER_ID);
//					vendorID = deliveryNote.getVendorID();
//					customerID = deliveryNote.getCustomerID();
//				}
//				else
//					throw new IllegalArgumentException("articleContainer is an instance of " + articleContainer.getClass().getName() + ", but must be either Order, Offer or DeliveryNote!");
//			}
//			else {
				Article article = articles.iterator().next();
				// TODO should be asynchronous!
				article = ArticleDAO.sharedInstance().getArticle((ArticleID)JDOHelper.getObjectId(article), FETCH_GROUPS_ARTICLE_FOR_VENDOR_ID_AND_CUSTOMER_ID, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
				vendorID = article.getVendorID();
				customerID = article.getCustomerID();
//			}
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

//	public static final String[] FETCH_GROUPS_ORDER_FOR_VENDOR_ID_AND_CUSTOMER_ID = new String[] {
//		FetchPlan.DEFAULT,
//		Order.FETCH_GROUP_VENDOR_ID,
//		Order.FETCH_GROUP_CUSTOMER_ID
//	};
//
//	public static final String[] FETCH_GROUPS_OFFER_FOR_VENDOR_ID_AND_CUSTOMER_ID = new String[] {
//		FetchPlan.DEFAULT,
//		Offer.FETCH_GROUP_VENDOR_ID,
//		Offer.FETCH_GROUP_CUSTOMER_ID
//	};
//
//	public static final String[] FETCH_GROUPS_INVOICE_FOR_VENDOR_ID_AND_CUSTOMER_ID = new String[] {
//		FetchPlan.DEFAULT,
//		Invoice.FETCH_GROUP_VENDOR_ID,
//		Invoice.FETCH_GROUP_CUSTOMER_ID
//	};
//
//	public static final String[] FETCH_GROUPS_DELIVERY_NOTE_FOR_VENDOR_ID_AND_CUSTOMER_ID = new String[] {
//		FetchPlan.DEFAULT,
//		DeliveryNote.FETCH_GROUP_VENDOR_ID,
//		DeliveryNote.FETCH_GROUP_CUSTOMER_ID
//	};

	public static final String[] FETCH_GROUPS_ARTICLE_FOR_VENDOR_ID_AND_CUSTOMER_ID = new String[] {
		FetchPlan.DEFAULT,
		Article.FETCH_GROUP_VENDOR_ID,
		Article.FETCH_GROUP_CUSTOMER_ID
	};

	private TradeManager tradeManager = null;
	protected TradeManager getTradeManager()
	{
		try {
			if (tradeManager == null)
				tradeManager = JFireEjbUtil.getBean(TradeManager.class, Login.getLogin().getInitialContextProperties());

			return tradeManager;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private AccountingManager accountingManager = null;
	protected AccountingManager getAccountingManager()
	{
		try {
			if (accountingManager == null)
				accountingManager = JFireEjbUtil.getBean(AccountingManager.class, Login.getLogin().getInitialContextProperties());

			return accountingManager;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private StoreManager storeManager = null;
	protected StoreManager getStoreManager()
	{
		try {
			if (storeManager == null)
				storeManager = JFireEjbUtil.getBean(StoreManager.class, Login.getLogin().getInitialContextProperties());

			return storeManager;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public AnchorID getVendorID()
	{
		if (vendorID == null)
			initVendorIDAndCustomerID();

		return vendorID;
	}

	public AnchorID getCustomerID()
	{
		if (customerID == null)
			initVendorIDAndCustomerID();

		return customerID;
	}

//	/**
//	 * @return Returns all those {@link Article}s that need to be added to the invoice. This is either the
//	 *		articles passed to the constructor or the ones from the articleContainer which are not yet assigned
//	 *		to an invoice.
//	 */
//	protected Collection getArticlesToAdd()
//	{
//		if (articles != null)
//			return articles;
//		else {
//			Collection res = new ArrayList(articleContainer.getArticles().size());
//			for (Iterator it = articleContainer.getArticles().iterator(); it.hasNext(); ) {
//				Article article = (Article) it.next();
//				if (article.getInvoiceID() == null)
//					res.add(article);
//			}
//			this.articles = res;
//			return res;
//		}
//	}
//
//	/**
//	 * @return Returns <code>null</code>, if a <code>Collection</code> of {@link org.nightlabs.jfire.trade.ui.Article}s
//	 *		has been specified. Otherwise it returns an {@link org.nightlabs.jfire.trade.ui.Order},
//	 *		{@link org.nightlabs.jfire.trade.ui.Offer} or {@link org.nightlabs.jfire.store.DeliveryNote}.
//	 */
//	public ArticleContainer getArticleContainer()
//	{
//		return articleContainer;
//	}
	/**
	 * @return Returns <code>null</code>, if an {@link ArticleContainer} has been specified. Otherwise
	 *		a <code>Collection</code> of {@link org.nightlabs.jfire.trade.ui.Article}.
	 *
	 * @see #getArticleContainer()
	 */
	public Collection<Article> getArticles()
	{
		return articles;
	}

}
