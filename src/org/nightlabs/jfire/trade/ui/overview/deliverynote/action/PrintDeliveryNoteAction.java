package org.nightlabs.jfire.trade.ui.overview.deliverynote.action;

import java.util.Map;

import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractPrintArticleContainerAction;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class PrintDeliveryNoteAction 
extends AbstractPrintArticleContainerAction 
{
	public static final String ID = PrintDeliveryNoteAction.class.getName();
	
	/**
	 * @param editor
	 */
//	public PrintDeliveryNoteAction(OverviewEntryEditor editor) {
//		super(editor);
	public PrintDeliveryNoteAction() {
		super();	
		setId(ID);
		setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.deliverynote.action.PrintDeliveryNoteAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.deliverynote.action.PrintDeliveryNoteAction.toolTipText"));		 //$NON-NLS-1$
	}

	@Override
	protected String getReportRegistryItemType() {
		return ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_DELIVERY_NOTE;
	}

	@Override
	protected void prepareParams(Map<String, Object> params) {
		DeliveryNoteID deliveryNoteID = (DeliveryNoteID) getArticleContainerID();
		params.put("deliveryNoteID", deliveryNoteID);		 //$NON-NLS-1$
	}

}
