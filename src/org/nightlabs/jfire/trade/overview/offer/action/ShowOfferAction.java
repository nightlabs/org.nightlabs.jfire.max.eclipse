package org.nightlabs.jfire.trade.overview.offer.action;

import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.trade.overview.action.AbstractShowArticleContainerAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ShowOfferAction 
extends AbstractShowArticleContainerAction 
{
	public static final String ID = ShowOfferAction.class.getName();
	
//	public ShowOfferAction(OverviewEntryEditor editor) {
//		super(editor);
//		setId(ID);
//	}
	public ShowOfferAction() {
		super();
		setId(ID);
	}

	@Override
	protected String getReportRegistryItemType() {
		return ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_OFFER;
	}

//	@Override
//	protected void prepareParams(Map<String, Object> params) {
//		OfferID offerID = (OfferID) getArticleContainerID();
//		params.put("offerID", offerID);		
//	}

}
