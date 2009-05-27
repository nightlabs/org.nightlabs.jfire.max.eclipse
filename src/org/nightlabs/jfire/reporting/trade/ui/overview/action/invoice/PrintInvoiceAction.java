package org.nightlabs.jfire.reporting.trade.ui.overview.action.invoice;

import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.reporting.trade.ui.overview.action.AbstractPrintArticleContainerAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class PrintInvoiceAction
extends AbstractPrintArticleContainerAction
{
	public static final String ID = PrintInvoiceAction.class.getName();
	
	public PrintInvoiceAction() {
		super();
		setId(ID);
	}

	@Override
	protected String getReportRegistryItemType() {
		return ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_INVOICE;
	}
}
