package org.nightlabs.jfire.voucher.ui.articlecontainer.detail;

import java.util.Collection;

import javax.jdo.FetchPlan;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.MessageComposite;
import org.nightlabs.base.ui.composite.MessageComposite.MessageType;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.FetchGroupsTrade;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.SegmentID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractArticleAdder;
import org.nightlabs.jfire.voucher.VoucherManagerRemote;
import org.nightlabs.jfire.voucher.accounting.VoucherPriceConfig;
import org.nightlabs.jfire.voucher.dao.VoucherTypeDAO;
import org.nightlabs.jfire.voucher.store.VoucherType;
import org.nightlabs.jfire.voucher.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

public class ArticleAdder
extends AbstractArticleAdder
{
	private VoucherType voucherType;

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
//		this.voucherType = VoucherTypeDAO.sharedInstance().getVoucherType(
//				productTypeID, FETCH_GROUPS_VOUCHER_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		this.voucherType = VoucherTypeDAO.sharedInstance().getVoucherType(
				productTypeID, getFetchGroups(), NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}

	@Override
	public ProductType getProductType()
	{
		return voucherType;
	}

	@SuppressWarnings("unchecked")
	public Collection<Article> createArticles(SegmentID segmentID, OfferID offerID, ProductTypeID productTypeID, int qty)
	throws Exception
	{
		VoucherManagerRemote vm = JFireEjb3Factory.getRemoteBean(VoucherManagerRemote.class, Login.getLogin().getInitialContextProperties());

		return (Collection<Article>) vm.createArticles(
				segmentID, offerID, productTypeID, qty,
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
				FetchPlan.DEFAULT,
				ProductType.FETCH_GROUP_PACKAGE_PRICE_CONFIG,
				VoucherPriceConfig.FETCH_GROUP_CURRENCIES
		};
	}

	@Override
	protected Composite createRequirementsNotFulfilledComposite(Composite parent) {
		Composite result = super.createRequirementsNotFulfilledComposite(parent);
		if (result != null)
			return result;

		Currency currency;
		Class<?> articleContainerClass = getSegmentEdit().getArticleContainerClass();
		if (Order.class.isAssignableFrom(articleContainerClass)) {
			currency = ((Order)getSegmentEdit().getArticleContainer()).getCurrency();
		}
		else if (Offer.class.isAssignableFrom(articleContainerClass)) {
			currency = ((Offer)getSegmentEdit().getArticleContainer()).getCurrency();
		}
		else
			throw new IllegalStateException("Why is this ArticleAdder in an unknown segment context? articleContainerClass=" + articleContainerClass); //$NON-NLS-1$

		if (!getProductType().getPackagePriceConfig().containsCurrency(currency))
			return new MessageComposite(
					parent, SWT.NONE,
					String.format(Messages.getString("org.nightlabs.jfire.voucher.ui.articlecontainer.detail.ArticleAdder.priceConfigDoesNotContainCurrencyMessage"), getProductType().getName().getText(), currency.getCurrencySymbol(), currency.getCurrencyID()), //$NON-NLS-1$
					MessageType.WARNING
			);

		return null;
	}
}
