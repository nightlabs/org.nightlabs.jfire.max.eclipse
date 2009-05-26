package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;

import java.rmi.RemoteException;

import javax.jdo.FetchPlan;
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
import org.nightlabs.jfire.dynamictrade.dao.DynamicProductTypeDAO;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.store.id.UnitID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.FetchGroupsTrade;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.SegmentID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractArticleAdder;
import org.nightlabs.progress.ProgressMonitor;

public class ArticleAdder
extends AbstractArticleAdder
{
	private DynamicProductType dynamicProductType;

	@Override
	protected Composite _createComposite(Composite parent)
	{
		return new ArticleAdderComposite(parent, this);
	}

//	public static final String[] FETCH_GROUPS_VOUCHER_TYPE = {
//		FetchPlan.DEFAULT, ProductType.FETCH_GROUP_NAME
//	};

	@Override
	public void setProductTypeID(ProductTypeID productTypeID, ProgressMonitor monitor)
	{
//		this.dynamicProductType = DynamicProductTypeDAO.sharedInstance().getDynamicProductType(
//				productTypeID, FETCH_GROUPS_VOUCHER_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		this.dynamicProductType = DynamicProductTypeDAO.sharedInstance().getDynamicProductType(
				productTypeID, getFetchGroups(), NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}

	@Override
	public ProductType getProductType()
	{
		return dynamicProductType;
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

		DynamicTradeManagerRemote dm = JFireEjb3Factory.getRemoteBean(DynamicTradeManagerRemote.class, Login.getLogin().getInitialContextProperties());

		return dm.createArticle(
				segmentID, offerID, productTypeID, quantity, unitID, tariffID, productName, singlePrice, true, false,
				getFetchGroups(), NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
	}




}
