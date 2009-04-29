package org.nightlabs.jfire.simpletrade.ui.articlecontainer.detail;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.CreateException;
import javax.naming.NamingException;
import javax.security.auth.login.LoginException;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.id.TariffID;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.simpletrade.SimpleTradeManagerRemote;
import org.nightlabs.jfire.store.NotAvailableException;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.SegmentID;


public class RecurringArticleAdder extends ArticleAdder {

	@Override
	public Collection<? extends Article> createArticles(SegmentID segmentID,
			OfferID offerID, ProductTypeID productTypeID, int quantity,
			TariffID tariffID)
			throws NotAvailableException, RemoteException, LoginException,
			CreateException, NamingException {

		Collection<ProductTypeID> productTypeIDs = new ArrayList<ProductTypeID>( quantity);
		for (int i = 0; i <  quantity; i++) {
			productTypeIDs.add(productTypeID);
		}

		SimpleTradeManagerRemote stm = JFireEjb3Factory.getRemoteBean(SimpleTradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
		Collection<? extends Article> articles = stm.createArticles(
				segmentID, offerID,
				productTypeIDs, tariffID,
				getFetchGroups(), NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
		return articles;
	}

}
