package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;


import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.recurring.RecurringOrder;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.HeaderComposite;

public class RecurringOrderHeaderComposite extends HeaderComposite{

	public RecurringOrderHeaderComposite(ArticleContainerEditComposite articleContainerEditComposite,
			RecurringOrder recurringOrder) {
		super(articleContainerEditComposite, articleContainerEditComposite, recurringOrder);
	}

}
