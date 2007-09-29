package org.nightlabs.jfire.trade.ui.overview.order.action;

import java.util.Map;

import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractPrintArticleContainerAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class PrintOrderAction 
extends AbstractPrintArticleContainerAction 
{
	public static final String ID = PrintOrderAction.class.getName();
	
//	public PrintOrderAction(OverviewEntryEditor editor) {
//		super(editor);
//		setId(ID);
//	}
	public PrintOrderAction() {
		super();
		setId(ID);
	}

	@Override
	protected String getReportRegistryItemType() {
		return ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_ORDER;
	}

	@Override
	protected void prepareParams(Map<String, Object> params) {
		OrderID orderID = (OrderID) getArticleContainerID();
		params.put("orderID", orderID);	 //$NON-NLS-1$
	}

}
