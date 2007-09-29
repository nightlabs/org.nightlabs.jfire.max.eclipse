package org.nightlabs.jfire.trade.overview.invoice.action;

import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.trade.overview.action.AbstractShowArticleContainerAction;
import org.nightlabs.jfire.trade.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ShowInvoiceAction 
extends AbstractShowArticleContainerAction 
{
	public static final String ID = ShowInvoiceAction.class.getName();
	
//	public ShowInvoiceAction(OverviewEntryEditor editor) {
//		super(editor);
	public ShowInvoiceAction() {
		super();	
		setId(ID);
		setText(Messages.getString("org.nightlabs.jfire.trade.overview.invoice.action.ShowInvoiceAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.jfire.trade.overview.invoice.action.ShowInvoiceAction.toolTipText")); //$NON-NLS-1$
	}

	@Override
	protected String getReportRegistryItemType() {
		return ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_INVOICE;
	}

//	@Override
//	protected void prepareParams(Map<String, Object> params) 
//	{
//		InvoiceID invoiceID = (InvoiceID) getArticleContainerID();
//		params.put("invoiceID", invoiceID);		
//	}	
	
}
