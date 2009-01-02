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

package org.nightlabs.jfire.simpletrade.ui.articlecontainer.detail;

import java.util.Collection;

import javax.ejb.CreateException;
import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.naming.NamingException;
import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.MessageComposite;
import org.nightlabs.base.ui.composite.MessageComposite.MessageType;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.gridpriceconfig.TariffPricePair;
import org.nightlabs.jfire.accounting.id.CurrencyID;
import org.nightlabs.jfire.accounting.id.TariffID;
import org.nightlabs.jfire.base.JFireEjbUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.simpletrade.SimpleTradeManager;
import org.nightlabs.jfire.simpletrade.dao.SimpleProductTypeDAO;
import org.nightlabs.jfire.simpletrade.dao.TariffPricePairDAO;
import org.nightlabs.jfire.simpletrade.ui.resource.Messages;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.CustomerGroup;
import org.nightlabs.jfire.trade.FetchGroupsTrade;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.dao.OfferDAO;
import org.nightlabs.jfire.trade.dao.OrderDAO;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.id.CustomerGroupID;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.id.SegmentID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractArticleAdder;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;
import org.nightlabs.util.NLLocale;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ArticleAdder extends AbstractArticleAdder
{
	public static final String[] FETCH_GROUPS_ORDER = new String[] {
		FetchPlan.DEFAULT,
		Order.FETCH_GROUP_CUSTOMER_GROUP,
		Order.FETCH_GROUP_CURRENCY
	};

	public static final String[] FETCH_GROUPS_OFFER = new String[] {
		FetchPlan.DEFAULT,
		Offer.FETCH_GROUP_CURRENCY,
		Offer.FETCH_GROUP_ORDER,
		Order.FETCH_GROUP_CUSTOMER_GROUP
	};

	private ArticleAdderComposite articleAdderComposite = null;
	private Collection<TariffPricePair> tariffPricePairs = null;
	private ProductType productType = null;

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractArticleAdder#_createComposite(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Composite _createComposite(Composite parent)
	{
		articleAdderComposite = new ArticleAdderComposite(parent, this, tariffPricePairs);
		return articleAdderComposite;
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleAdder#getComposite()
	 */
	@Override
	public Composite getComposite()
	{
		return articleAdderComposite;
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleAdder#setProductTypeID(ProductTypeID, IProgressMonitor)
	 */
	public void setProductTypeID(ProductTypeID productTypeID, ProgressMonitor monitor)
	{
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.simpletrade.ui.articlecontainer.detail.ArticleAdder.loadProductTypeJob.name") + productTypeID, 3); //$NON-NLS-1$
		ArticleContainerID articleContainerID = getSegmentEdit().getArticleContainerID();
		CustomerGroup customerGroup;
		Currency currency;
		if (articleContainerID instanceof OrderID) {
			this.productType = SimpleProductTypeDAO.sharedInstance().getSimpleProductType(
					productTypeID,
					new String[]{
							FetchPlan.DEFAULT,
							FetchGroupsTrade.FETCH_GROUP_ARTICLE_IN_ORDER_EDITOR,
							ProductType.FETCH_GROUP_PACKAGE_PRICE_CONFIG}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							new SubProgressMonitor(monitor, 1)
			);
			Order order = OrderDAO.sharedInstance().getOrder((OrderID) articleContainerID, FETCH_GROUPS_ORDER, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 1));
			customerGroup = order.getCustomerGroup();
			currency = order.getCurrency();
		} else if (articleContainerID instanceof OfferID) {
			this.productType = SimpleProductTypeDAO.sharedInstance().getSimpleProductType(
					productTypeID,
					new String[]{
							FetchPlan.DEFAULT,
							FetchGroupsTrade.FETCH_GROUP_ARTICLE_IN_OFFER_EDITOR,
							ProductType.FETCH_GROUP_PACKAGE_PRICE_CONFIG}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							new SubProgressMonitor(monitor, 1)
			);
			Offer offer = OfferDAO.sharedInstance().getOffer(
					(OfferID) articleContainerID, FETCH_GROUPS_OFFER, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 1));
			customerGroup = offer.getOrder().getCustomerGroup();
			currency = offer.getCurrency();
		} else
			throw new IllegalStateException("ArticleContainerID is neither OrderID nor OfferID, but " + (articleContainerID == null ? "null" : articleContainerID.getClass().getName()) + "!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		tariffPricePairs = null;
		try {
			if (productType != null && productType.getPackagePriceConfig() != null && customerGroup != null && currency != null) {
				tariffPricePairs = TariffPricePairDAO.sharedInstance().getTariffPricePairs(
						productTypeID,
						(CustomerGroupID)JDOHelper.getObjectId(customerGroup),
						(CurrencyID)JDOHelper.getObjectId(currency),
						new SubProgressMonitor(monitor, 1)
				);
			}
		} catch (Exception e) {
			monitor.setCanceled(true);
			throw new RuntimeException(e);
		}
		monitor.done();
	}

	@Override
	public ProductType getProductType()
	{
		return productType;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractArticleAdder#createRequirementsNotFulFilledComposite(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Composite createRequirementsNotFulfilledComposite(Composite parent)
	{
		if (tariffPricePairs == null) {
			String message = String.format("No TariffPricePairs are available for the ProductType %s", getProductType().getName().getText(NLLocale.getDefault()));
			return new MessageComposite(parent, SWT.NONE, message, MessageType.WARNING);
		}

		return super.createRequirementsNotFulfilledComposite(parent);
	}

	public Collection<Article> createArticles(
			SegmentID segmentID, OfferID offerID,
			ProductTypeID productTypeID, int quantity,
			TariffID tariffID)
	throws org.nightlabs.ModuleException, java.rmi.RemoteException, LoginException, CreateException, NamingException
	{

		SimpleTradeManager stm = JFireEjbUtil.getBean(SimpleTradeManager.class, Login.getLogin().getInitialContextProperties());
		return stm.createArticles(
				segmentID, offerID, productTypeID, quantity, tariffID, true, false,
				getFetchGroups(), NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
	}

	protected String[] getFetchGroups() {
		Class<?> articleContainerClass = getSegmentEdit().getArticleContainerClass();
		String fetchGroupTrade_article;
		if (Order.class.isAssignableFrom(articleContainerClass)) {
			fetchGroupTrade_article = FetchGroupsTrade.FETCH_GROUP_ARTICLE_IN_ORDER_EDITOR;
		}
		else if (Offer.class.isAssignableFrom(articleContainerClass)) {
			fetchGroupTrade_article = FetchGroupsTrade.FETCH_GROUP_ARTICLE_IN_OFFER_EDITOR;
		}
		else
			throw new IllegalStateException("Why is this ArticleAdder in an unknown segment context? articleContainerClass=" + articleContainerClass); //$NON-NLS-1$

		return new String[] {
				fetchGroupTrade_article,
				FetchPlan.DEFAULT};

	}

}
