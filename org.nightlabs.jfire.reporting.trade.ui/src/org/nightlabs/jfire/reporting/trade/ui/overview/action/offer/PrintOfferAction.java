package org.nightlabs.jfire.reporting.trade.ui.overview.action.offer;

import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.reporting.trade.ui.overview.action.AbstractPrintArticleContainerAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class PrintOfferAction
extends AbstractPrintArticleContainerAction
{
	public static final String ID = PrintOfferAction.class.getName();
	
	public PrintOfferAction() {
		super();
		setId(ID);
	}

	@Override
	protected String getReportRegistryItemType() {
		return ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_OFFER;
	}
}
