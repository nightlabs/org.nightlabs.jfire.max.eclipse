package org.nightlabs.jfire.simpletrade.ui.articlecontainer.detail;

import org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractArticleAdderFactory;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEdit;

public class RecurringArticleAdderFactory extends AbstractArticleAdderFactory{

	@Override
	public RecurringArticleAdder createArticleAdder(SegmentEdit segmentEdit) {
		RecurringArticleAdder articleAdder = new RecurringArticleAdder();
		articleAdder.init(segmentEdit);
		return articleAdder;

	}
}
