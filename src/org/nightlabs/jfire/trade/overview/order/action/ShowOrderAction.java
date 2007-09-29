package org.nightlabs.jfire.trade.overview.order.action;

import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.trade.overview.action.AbstractShowArticleContainerAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ShowOrderAction 
extends AbstractShowArticleContainerAction 
{
	public static final String ID = ShowOrderAction.class.getName();
	
//	public ShowOrderAction(OverviewEntryEditor editor) {
//		super(editor);
//		setId(ID);
//	}
	public ShowOrderAction() {
		super();
		setId(ID);
	}

	@Override
	protected String getReportRegistryItemType() {
		return ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_ORDER;
	}

//	@Override
//	protected void prepareParams(Map<String, Object> params) {
//		OrderID orderID = (OrderID) getArticleContainerID();
//		params.put("orderID", orderID);	
//	}

}
