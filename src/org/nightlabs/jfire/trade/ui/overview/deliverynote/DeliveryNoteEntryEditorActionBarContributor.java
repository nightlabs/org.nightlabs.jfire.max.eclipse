package org.nightlabs.jfire.trade.ui.overview.deliverynote;

import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleContainerEditorActionBarContributor;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerActionBarContributor;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractPrintArticleContainerAction;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractShowArticleContainerAction;
import org.nightlabs.jfire.trade.ui.overview.deliverynote.action.PrintDeliveryNoteAction;
import org.nightlabs.jfire.trade.ui.overview.deliverynote.action.ShowDeliveryNoteAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @deprecated I don't find any references to this. It seems, this class is not used anywhere. @Daniel: Is it really used?
 *		Is it planned to be used? Doesn't it conflict with the {@link ArticleContainerEditorActionBarContributor}? I don't think
 *		that you can use 2 ActionBarContributors to the same editor. Marco.
 */
@Deprecated
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
