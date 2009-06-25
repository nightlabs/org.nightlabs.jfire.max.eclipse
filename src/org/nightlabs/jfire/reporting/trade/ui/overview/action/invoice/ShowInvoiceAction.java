package org.nightlabs.jfire.reporting.trade.ui.overview.action.invoice;

import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.reporting.trade.ui.overview.action.AbstractShowArticleContainerAction;
import org.nightlabs.jfire.reporting.trade.ui.resource.Messages;

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
		setText(Messages.getString("org.nightlabs.jfire.reporting.trade.ui.overview.action.invoice.ShowInvoiceAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.jfire.reporting.trade.ui.overview.action.invoice.ShowInvoiceAction.tooltipText")); //$NON-NLS-1$
	}

	@Override
	protected String getReportRegistryItemType() {
		return ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_INVOICE;
	}

}
