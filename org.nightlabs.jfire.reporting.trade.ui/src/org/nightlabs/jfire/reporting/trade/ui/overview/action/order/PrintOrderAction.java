package org.nightlabs.jfire.reporting.trade.ui.overview.action.order;

import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.reporting.trade.ui.overview.action.AbstractPrintArticleContainerAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class PrintOrderAction
extends AbstractPrintArticleContainerAction
{
	public static final String ID = PrintOrderAction.class.getName();
	
	public PrintOrderAction() {
		super();
		setId(ID);
	}

	@Override
	protected String getReportRegistryItemType() {
		return ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_ORDER;
	}
}
