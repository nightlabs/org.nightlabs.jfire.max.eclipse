package org.nightlabs.jfire.simpletrade.ui.articlecontainer.detail;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.CreateException;
import javax.naming.NamingException;
import javax.security.auth.login.LoginException;

import org.nightlabs.ModuleException;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.id.TariffID;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.simpletrade.SimpleTradeManager;
import org.nightlabs.jfire.simpletrade.SimpleTradeManagerUtil;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.SegmentID;


public class RecurringArticleAdder extends ArticleAdder {
	
	@Override
	public Collection<Article> createArticles(SegmentID segmentID,
			OfferID offerID, ProductTypeID productTypeID, int quantity,
			TariffID tariffID)
			throws ModuleException, RemoteException, LoginException,
			CreateException, NamingException {
		
		Collection<ProductTypeID> productTypeIDs = new ArrayList<ProductTypeID>( quantity);
		for (int i = 0; i <  quantity; i++) {
			productTypeIDs.add(productTypeID);
		}
		
		SimpleTradeManager stm = SimpleTradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
		Collection<Article> articles = stm.createArticles(
				segmentID, offerID,
				productTypeIDs, tariffID,
				getFetchGroups(), NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
		return articles;
	}

}
