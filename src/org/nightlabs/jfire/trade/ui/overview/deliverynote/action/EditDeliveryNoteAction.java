package org.nightlabs.jfire.trade.ui.overview.deliverynote.action;

import org.eclipse.ui.IEditorInput;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.deliverynote.ArticleContainerEditorInputDeliveryNote;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractEditArticleContainerAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class EditDeliveryNoteAction
extends AbstractEditArticleContainerAction
{
	public String getEditorID() {
		return ArticleContainerEditor.ID_EDITOR;
	}

	public IEditorInput getEditorInput() {
		DeliveryNoteID deliveryNoteID = (DeliveryNoteID) getArticleContainerID();
		return new ArticleContainerEditorInputDeliveryNote(deliveryNoteID);
	}

}
