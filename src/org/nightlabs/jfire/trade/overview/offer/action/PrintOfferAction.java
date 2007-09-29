package org.nightlabs.jfire.trade.overview.offer.action;

import java.util.Map;

import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.overview.action.AbstractPrintArticleContainerAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class PrintOfferAction 
extends AbstractPrintArticleContainerAction 
{
	public static final String ID = PrintOfferAction.class.getName();
	
//	public PrintOfferAction(OverviewEntryEditor editor) {
//		super(editor);
//		setId(ID);
//	}
	public PrintOfferAction() {
		super();
		setId(ID);
	}

	@Override
	protected String getReportRegistryItemType() {
		return ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_OFFER;
	}

	@Override
	protected void prepareParams(Map<String, Object> params) {
		OfferID offerID = (OfferID) getArticleContainerID();
		params.put("offerID", offerID);		 //$NON-NLS-1$
	}

}
