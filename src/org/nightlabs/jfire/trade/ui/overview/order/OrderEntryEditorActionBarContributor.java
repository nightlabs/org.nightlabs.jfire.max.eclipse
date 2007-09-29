package org.nightlabs.jfire.trade.ui.overview.order;

import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerActionBarContributor;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractPrintArticleContainerAction;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractShowArticleContainerAction;
import org.nightlabs.jfire.trade.ui.overview.order.action.PrintOrderAction;
import org.nightlabs.jfire.trade.ui.overview.order.action.ShowOrderAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
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
