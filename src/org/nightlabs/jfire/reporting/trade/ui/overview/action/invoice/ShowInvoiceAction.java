package org.nightlabs.jfire.reporting.trade.ui.overview.action.invoice;

import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.reporting.trade.ui.overview.action.AbstractShowArticleContainerAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ShowInvoiceAction
extends AbstractShowArticleContainerAction
{
	public static final String ID = ShowInvoiceAction.class.getName();

	public ShowInvoiceAction() {
		super();
		setId(ID);
		setText("Show Invoice");
		setToolTipText("Show Invoice");
	}

	@Override
	protected String getReportRegistryItemType() {
		return ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_INVOICE;
	}

}
