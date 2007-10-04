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

package org.nightlabs.jfire.simpletrade.articlecontainer.detail;

import java.util.Collection;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.id.CurrencyID;
import org.nightlabs.jfire.accounting.priceconfig.id.PriceConfigID;
import org.nightlabs.jfire.simpletrade.dao.SimpleProductTypeDAO;
import org.nightlabs.jfire.simpletrade.dao.TariffPricePairDAO;
import org.nightlabs.jfire.simpletrade.resource.Messages;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.CustomerGroup;
import org.nightlabs.jfire.trade.FetchGroupsTrade;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.id.CustomerGroupID;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.articlecontainer.OfferDAO;
import org.nightlabs.jfire.trade.ui.articlecontainer.OrderDAO;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractArticleAdder;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ArticleAdder extends AbstractArticleAdder
{
	private ArticleAdderComposite articleAdderComposite = null;

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractArticleAdder#_createComposite(org.eclipse.swt.widgets.Composite)
	 */
	public Composite _createComposite(Composite parent)
	{
		articleAdderComposite = new ArticleAdderComposite(parent, this, tariffPricePairs);
		return articleAdderComposite;
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleAdder#getComposite()
	 */
	public Composite getComposite()
	{
		return articleAdderComposite;
	}

//	/**
//	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleAdder#dispose()
//	 */
//	public void dispose()
//	{
//		articleAdderComposite.dispose();
//	}

	private Collection tariffPricePairs = null;
	
	private ProductType productType = null;

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

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleAdder#setProductTypeID(ProductTypeID, IProgressMonitor)
	 */
	public void setProductTypeID(ProductTypeID productTypeID, ProgressMonitor monitor)
	{
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.simpletrade.articlecontainer.detail.ArticleAdder.loadProductTypeJob.name") + productTypeID, 3); //$NON-NLS-1$
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

		try {
			tariffPricePairs = TariffPricePairDAO.sharedInstance().getTariffPricePairs(
					(PriceConfigID) JDOHelper.getObjectId(productType.getPackagePriceConfig()),
					(CustomerGroupID)JDOHelper.getObjectId(customerGroup),
					(CurrencyID)JDOHelper.getObjectId(currency),
					new SubProgressMonitor(monitor, 1)
			);
		} catch (Exception e) {
			monitor.setCanceled(true);
			throw new RuntimeException(e);
		}
		monitor.done();
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleAdder#getProductType()
	 */
	public ProductType getProductType()
	{
		return productType;
	}

}
