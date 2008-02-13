package org.nightlabs.jfire.trade.ui.overview.offer;

import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerActionBarContributor;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractPrintArticleContainerAction;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractShowArticleContainerAction;
import org.nightlabs.jfire.trade.ui.overview.offer.action.PrintOfferAction;
import org.nightlabs.jfire.trade.ui.overview.offer.action.ShowOfferAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class OfferEntryEditorActionBarContributor
extends AbstractArticleContainerActionBarContributor
{

	public OfferEntryEditorActionBarContributor() {
		super();
	}

	@Override
	protected AbstractPrintArticleContainerAction createPrintAction() {
//		return new PrintOfferAction(getEditor());
		return new PrintOfferAction();
	}

	@Override
	protected AbstractShowArticleContainerAction createShowAction() {
//		return new ShowOfferAction(getEditor());
		return new ShowOfferAction();
	}

}
