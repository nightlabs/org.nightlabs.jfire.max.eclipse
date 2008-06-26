package org.nightlabs.jfire.trade.ui.overview.offer.action;

import org.eclipse.ui.IEditorInput;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.GeneralEditorInputOffer;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractEditArticleContainerAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class EditOfferAction
extends AbstractEditArticleContainerAction
{

	public EditOfferAction() {
	}

	public String getEditorID() {
		return ArticleContainerEditor.ID_EDITOR;
	}

	public IEditorInput getEditorInput() {
		OfferID offerID = (OfferID) getArticleContainerID();
		return new GeneralEditorInputOffer(offerID);
	}

}
