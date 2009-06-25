package org.nightlabs.jfire.reporting.trade.ui.overview.action.deliverynote;

import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.reporting.trade.ui.overview.action.AbstractShowArticleContainerAction;
import org.nightlabs.jfire.reporting.trade.ui.resource.Messages;

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
		setText(Messages.getString("org.nightlabs.jfire.reporting.trade.ui.overview.action.deliverynote.ShowDeliveryNoteAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.jfire.reporting.trade.ui.overview.action.deliverynote.ShowDeliveryNoteAction.tooltipText")); //$NON-NLS-1$
	}

	@Override
	protected String getReportRegistryItemType() {
		return ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_DELIVERY_NOTE;
	}

}
