package org.nightlabs.jfire.trade.ui.overview.deliverynote;

import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerActionBarContributor;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractPrintArticleContainerAction;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractShowArticleContainerAction;
import org.nightlabs.jfire.trade.ui.overview.deliverynote.action.PrintDeliveryNoteAction;
import org.nightlabs.jfire.trade.ui.overview.deliverynote.action.ShowDeliveryNoteAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DeliveryNoteEntryEditorActionBarContributor 
//extends OverviewEntryEditorActionBarContributor 
extends AbstractArticleContainerActionBarContributor
{

	public DeliveryNoteEntryEditorActionBarContributor() {
		super();
	}

	@Override
	protected AbstractPrintArticleContainerAction createPrintAction() {
//		return new PrintDeliveryNoteAction(getEditor());
		return new PrintDeliveryNoteAction();
	}

	@Override
	protected AbstractShowArticleContainerAction createShowAction() {
//		return new ShowDeliveryNoteAction(getEditor());
		return new ShowDeliveryNoteAction();
	}

	
}
