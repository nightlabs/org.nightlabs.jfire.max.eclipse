package org.nightlabs.jfire.trade.ui.overview.deliverynote.action;

import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractShowArticleContainerAction;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ShowDeliveryNoteAction
extends AbstractShowArticleContainerAction
{
	public static final String ID = ShowDeliveryNoteAction.class.getName();
	
//	public ShowDeliveryNoteAction(OverviewEntryEditor editor) {
//		super(editor);
	public ShowDeliveryNoteAction() {
		super();
		setId(ID);
		setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.deliverynote.action.ShowDeliveryNoteAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.deliverynote.action.ShowDeliveryNoteAction.toolTipText"));		 //$NON-NLS-1$
	}

	@Override
	protected String getReportRegistryItemType() {
		return ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_DELIVERY_NOTE;
	}

//	@Override
//	protected void prepareParams(Map<String, Object> params) {
//		DeliveryNoteID deliveryNoteID = (DeliveryNoteID) getArticleContainerID();
//		params.put("deliveryNoteID", deliveryNoteID);
//	}

}
