package org.nightlabs.jfire.trade.ui.overview.offer;

import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleContainerEditorActionBarContributor;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerActionBarContributor;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractPrintArticleContainerAction;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractShowArticleContainerAction;
import org.nightlabs.jfire.trade.ui.overview.offer.action.PrintOfferAction;
import org.nightlabs.jfire.trade.ui.overview.offer.action.ShowOfferAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @deprecated I don't find any references to this. It seems, this class is not used anywhere. @Daniel: Is it really used?
 *		Is it planned to be used? Doesn't it conflict with the {@link ArticleContainerEditorActionBarContributor}? I don't think
 *		that you can use 2 ActionBarContributors to the same editor. Marco.
 */
@Deprecated
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
