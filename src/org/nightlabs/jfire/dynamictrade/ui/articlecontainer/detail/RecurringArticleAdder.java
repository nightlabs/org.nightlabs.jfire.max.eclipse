package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;


import javax.ejb.CreateException;
import javax.jdo.FetchPlan;
import javax.naming.NamingException;
import javax.security.auth.login.LoginException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.MessageComposite;
import org.nightlabs.base.ui.composite.MessageComposite.MessageType;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Price;
import org.nightlabs.jfire.accounting.id.TariffID;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.dynamictrade.DynamicTradeManager;
import org.nightlabs.jfire.dynamictrade.DynamicTradeManagerUtil;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.store.id.UnitID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ArticleContainerUtil;
import org.nightlabs.jfire.trade.FetchGroupsTrade;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.SegmentID;
import org.nightlabs.jfire.trade.ui.TradePlugin;


/**
 * @author Fitas Amine - fitas at nightlabs dot de
 */
public class RecurringArticleAdder extends ArticleAdder {

	@Override
	protected Composite createRequirementsNotFulfilledComposite(Composite parent) 
	{
		ArticleContainer ac = getSegmentEdit().getArticleContainer();
		String message = String.format(
				"Recurring Trade is currently not supported on Dynamic Trade", 
				TradePlugin.getArticleContainerTypeString(ac.getClass(), false), TradePlugin.getArticleContainerTypeString(ac.getClass(), true),
				ArticleContainerUtil.getArticleContainerID(ac)
		);
		return new MessageComposite(parent, SWT.NONE, message, MessageType.INFO);
	}

	public Article createArticles(
			SegmentID segmentID, OfferID offerID,
			ProductTypeID productTypeID, int quantity,
			TariffID tariffID,
			UnitID unitID,
			I18nText productName,
			Price singlePrice)
	throws org.nightlabs.ModuleException, java.rmi.RemoteException, LoginException, CreateException, NamingException
	{
		DynamicTradeManager dtm = DynamicTradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
		return dtm.createRecurringArticle(segmentID, offerID, productTypeID, quantity, unitID, tariffID,
				productName, singlePrice, getFetchGroups(), NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

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
