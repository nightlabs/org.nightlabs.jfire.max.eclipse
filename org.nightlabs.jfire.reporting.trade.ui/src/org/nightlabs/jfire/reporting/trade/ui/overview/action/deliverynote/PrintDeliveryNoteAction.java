package org.nightlabs.jfire.reporting.trade.ui.overview.action.deliverynote;

import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.reporting.trade.ui.overview.action.AbstractPrintArticleContainerAction;
import org.nightlabs.jfire.reporting.trade.ui.resource.Messages;

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
		setText(Messages.getString("org.nightlabs.jfire.reporting.trade.ui.overview.action.deliverynote.PrintDeliveryNoteAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.jfire.reporting.trade.ui.overview.action.deliverynote.PrintDeliveryNoteAction.tooltipText")); //$NON-NLS-1$
	}

	@Override
	protected String getReportRegistryItemType() {
		return ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_DELIVERY_NOTE;
	}
}
