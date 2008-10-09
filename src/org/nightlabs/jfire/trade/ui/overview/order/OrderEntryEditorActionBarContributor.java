package org.nightlabs.jfire.trade.ui.overview.order;

import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleContainerEditorActionBarContributor;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerActionBarContributor;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractPrintArticleContainerAction;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractShowArticleContainerAction;
import org.nightlabs.jfire.trade.ui.overview.order.action.PrintOrderAction;
import org.nightlabs.jfire.trade.ui.overview.order.action.ShowOrderAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @deprecated I don't find any references to this. It seems, this class is not used anywhere. @Daniel: Is it really used?
 *		Is it planned to be used? Doesn't it conflict with the {@link ArticleContainerEditorActionBarContributor}? I don't think
 *		that you can use 2 ActionBarContributors to the same editor. Marco.
 */
@Deprecated
public class OrderEntryEditorActionBarContributor
extends AbstractArticleContainerActionBarContributor
{

	public OrderEntryEditorActionBarContributor() {
		super();
	}

	@Override
	protected AbstractPrintArticleContainerAction createPrintAction() {
//		return new PrintOrderAction(getEditor());
		return new PrintOrderAction();
	}

	@Override
	protected AbstractShowArticleContainerAction createShowAction() {
//		return new ShowOrderAction(getEditor());
		return new ShowOrderAction();
	}

}
