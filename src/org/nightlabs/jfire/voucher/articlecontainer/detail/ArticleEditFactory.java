package org.nightlabs.jfire.voucher.articlecontainer.detail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.trade.ArticleCarrier;
import org.nightlabs.jfire.trade.ArticleProductTypeClassGroup;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractArticleEditFactory;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEdit;

public class ArticleEditFactory
		extends AbstractArticleEditFactory
{

	@Implement
	public Collection<? extends org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleEdit> createArticleEdits(
			SegmentEdit segmentEdit,
			ArticleProductTypeClassGroup articleProductTypeClassGroup,
			Collection<? extends ArticleCarrier> articleCarriers)
	{
		ArrayList<ArticleEdit> res = new ArrayList<ArticleEdit>(1);
		ArticleEdit edit = new ArticleEdit();
		edit.init(this, segmentEdit, articleProductTypeClassGroup, new HashSet<ArticleCarrier>(articleCarriers));
		res.add(edit);
		return res;
	}

}
