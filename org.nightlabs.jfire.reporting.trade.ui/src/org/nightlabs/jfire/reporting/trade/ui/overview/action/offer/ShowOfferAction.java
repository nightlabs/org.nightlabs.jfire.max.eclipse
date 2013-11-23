package org.nightlabs.jfire.reporting.trade.ui.overview.action.offer;

import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.reporting.trade.ui.overview.action.AbstractShowArticleContainerAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ShowOfferAction
extends AbstractShowArticleContainerAction
{
	public static final String ID = ShowOfferAction.class.getName();

	public ShowOfferAction() {
		super();
		setId(ID);
	}

	@Override
	protected String getReportRegistryItemType() {
		return ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_OFFER;
	}

}
