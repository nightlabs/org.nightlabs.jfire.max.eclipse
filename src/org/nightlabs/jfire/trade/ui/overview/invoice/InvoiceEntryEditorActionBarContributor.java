package org.nightlabs.jfire.trade.ui.overview.invoice;

import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleContainerEditorActionBarContributor;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerActionBarContributor;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractPrintArticleContainerAction;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractShowArticleContainerAction;
import org.nightlabs.jfire.trade.ui.overview.invoice.action.PrintInvoiceAction;
import org.nightlabs.jfire.trade.ui.overview.invoice.action.ShowInvoiceAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @deprecated I don't find any references to this. It seems, this class is not used anywhere. @Daniel: Is it really used?
 *		Is it planned to be used? Doesn't it conflict with the {@link ArticleContainerEditorActionBarContributor}? I don't think
 *		that you can use 2 ActionBarContributors to the same editor. Marco.
 */
@Deprecated
public class InvoiceEntryEditorActionBarContributor
//extends OverviewEntryEditorActionBarContributor
extends AbstractArticleContainerActionBarContributor
{

	public InvoiceEntryEditorActionBarContributor() {
		super();
	}

	@Override
	protected AbstractPrintArticleContainerAction createPrintAction() {
//		return new PrintInvoiceAction(getEditor());
		return new PrintInvoiceAction();
	}

	@Override
	protected AbstractShowArticleContainerAction createShowAction() {
//		return new ShowInvoiceAction(getEditor());
		return new ShowInvoiceAction();
	}

}
