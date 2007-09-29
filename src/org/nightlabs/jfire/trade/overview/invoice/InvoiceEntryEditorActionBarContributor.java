package org.nightlabs.jfire.trade.overview.invoice;

import org.nightlabs.jfire.trade.overview.AbstractArticleContainerActionBarContributor;
import org.nightlabs.jfire.trade.overview.action.AbstractPrintArticleContainerAction;
import org.nightlabs.jfire.trade.overview.action.AbstractShowArticleContainerAction;
import org.nightlabs.jfire.trade.overview.invoice.action.PrintInvoiceAction;
import org.nightlabs.jfire.trade.overview.invoice.action.ShowInvoiceAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
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
