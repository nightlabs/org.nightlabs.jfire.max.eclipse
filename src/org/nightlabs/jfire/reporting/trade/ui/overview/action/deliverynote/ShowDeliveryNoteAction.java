package org.nightlabs.jfire.reporting.trade.ui.overview.action.deliverynote;

import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.reporting.trade.ui.overview.action.AbstractShowArticleContainerAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ShowDeliveryNoteAction
extends AbstractShowArticleContainerAction
{
	public static final String ID = ShowDeliveryNoteAction.class.getName();

	public ShowDeliveryNoteAction() {
		super();
		setId(ID);
		setText("Show DeliveryNote");
		setToolTipText("Show DeliveryNote");
	}

	@Override
	protected String getReportRegistryItemType() {
		return ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_DELIVERY_NOTE;
	}

}
