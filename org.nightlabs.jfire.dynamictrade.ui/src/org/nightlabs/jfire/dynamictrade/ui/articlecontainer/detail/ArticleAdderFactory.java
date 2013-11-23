package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;

import org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractArticleAdderFactory;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleAdder;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEdit;

public class ArticleAdderFactory
extends AbstractArticleAdderFactory
{

	public ArticleAdder createArticleAdder(SegmentEdit segmentEdit)
	{
		ArticleAdder articleAdder = new org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleAdder();
		articleAdder.init(segmentEdit);
		return articleAdder;
	}

}
