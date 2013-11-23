package org.nightlabs.jfire.reporting.trade.ui.overview.action.order;

import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.reporting.trade.ui.overview.action.AbstractShowArticleContainerAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ShowOrderAction
extends AbstractShowArticleContainerAction
{
	public static final String ID = ShowOrderAction.class.getName();

	public ShowOrderAction() {
		super();
		setId(ID);
	}

	@Override
	protected String getReportRegistryItemType() {
		return ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_ORDER;
	}

}
