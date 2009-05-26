package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;


import java.rmi.RemoteException;

import javax.naming.NamingException;
import javax.security.auth.login.LoginException;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.ModuleException;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Price;
import org.nightlabs.jfire.accounting.id.TariffID;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.dynamictrade.DynamicTradeManagerRemote;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.store.id.UnitID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.SegmentID;


/**
 * @author Fitas Amine - fitas at nightlabs dot de
 */
public class RecurringArticleAdder extends ArticleAdder {

//	@Override
//	protected Composite createRequirementsNotFulfilledComposite(Composite parent)
//	{
//		ArticleContainer ac = getSegmentEdit().getArticleContainer();
//		String message = String.format(
//				"Recurring Trade is currently not supported on Dynamic Trade",
//				TradePlugin.getArticleContainerTypeString(ac.getClass(), false), TradePlugin.getArticleContainerTypeString(ac.getClass(), true),
//				ArticleContainerUtil.getArticleContainerID(ac)
//		);
//		return new MessageComposite(parent, SWT.NONE, message, MessageType.INFO);
//	}
	@Override
	protected Composite _createComposite(Composite parent)
	{
		return new RecurringArticleAdderComposite(parent, this);
	}

	@Override
	public Article createArticle(
			SegmentID segmentID,
			OfferID offerID,
			ProductTypeID productTypeID,
			long quantity,
			UnitID unitID,
			TariffID tariffID,
			I18nText productName,
			Price singlePrice,
			boolean allocate,
			boolean allocateSynchronously) throws RemoteException, LoginException, NamingException, ModuleException
	{
		DynamicTradeManagerRemote dtm = JFireEjb3Factory.getRemoteBean(DynamicTradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
		return dtm.createRecurringArticle(segmentID, offerID, productTypeID, quantity, unitID, tariffID,
				productName, singlePrice, getFetchGroups(), NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//
	}




}
