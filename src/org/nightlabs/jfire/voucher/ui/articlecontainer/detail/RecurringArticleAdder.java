package org.nightlabs.jfire.voucher.ui.articlecontainer.detail;

import java.util.ArrayList;
import java.util.Collection;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.JFireEjbUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.SegmentID;
import org.nightlabs.jfire.voucher.VoucherManager;

public class RecurringArticleAdder extends ArticleAdder {

	@Override
	public Collection<Article> createArticles(SegmentID segmentID, OfferID offerID, ProductTypeID productTypeID, int qty)
	throws Exception {

		VoucherManager vm = JFireEjbUtil.getBean(VoucherManager.class, Login.getLogin().getInitialContextProperties());
		Collection<ProductTypeID> productTypeIDs = new ArrayList<ProductTypeID>(qty);
		for (int i = 0; i < qty; i++) {
			productTypeIDs.add(productTypeID);
		}
		return vm.createArticles(
				segmentID, offerID, productTypeIDs,
				getFetchGroups(), NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
	}

}
