package org.nightlabs.jfire.reporting.trade.ui.overview.action.deliverynote;

import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.reporting.trade.ui.overview.action.AbstractPrintArticleContainerAction;

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
	public PrintDeliveryNoteAction() {
		super();
		setId(ID);
		setText("Print DeliveryNote");
		setToolTipText("Print DeliveryNote");
	}

	@Override
	protected String getReportRegistryItemType() {
		return ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_DELIVERY_NOTE;
	}
}
