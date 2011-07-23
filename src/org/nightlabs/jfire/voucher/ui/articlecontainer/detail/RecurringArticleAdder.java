package org.nightlabs.jfire.voucher.ui.articlecontainer.detail;

import java.util.ArrayList;
import java.util.Collection;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.SegmentID;
import org.nightlabs.jfire.voucher.VoucherManagerRemote;

public class RecurringArticleAdder extends ArticleAdder {

	@Override
	public Collection<Article> createArticles(SegmentID segmentID, OfferID offerID, ProductTypeID productTypeID, int qty)
	throws Exception {

		VoucherManagerRemote vm = JFireEjb3Factory.getRemoteBean(VoucherManagerRemote.class, Login.getLogin().getInitialContextProperties());
		Collection<ProductTypeID> productTypeIDs = new ArrayList<ProductTypeID>(qty);
		for (int i = 0; i < qty; i++) {
			productTypeIDs.add(productTypeID);
		}
		return (Collection<Article>) vm.createArticles(
				segmentID, offerID, productTypeIDs,
				getFetchGroups(), NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
	}

}
