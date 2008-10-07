package org.nightlabs.jfire.voucher.ui.articlecontainer.detail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.SegmentID;
import org.nightlabs.jfire.voucher.VoucherManager;
import org.nightlabs.jfire.voucher.VoucherManagerUtil;

public class RecurringArticleAdder extends ArticleAdder {

	@Override
	public Collection<Article> createArticles(SegmentID segmentID, OfferID offerID, ProductTypeID productTypeID, int qty)
	throws Exception {

		VoucherManager vm = VoucherManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
		return vm.createArticles(
				segmentID, offerID, Collections.singleton(productTypeID),
				getFetchGroups(), NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
	}

}
