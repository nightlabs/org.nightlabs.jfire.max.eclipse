/**
 * 
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.openrelated;

import java.util.Set;

import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.GeneralEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.deliverynote.GeneralEditorInputDeliveryNote;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.util.Util;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class OpenRelatedDeliveryNoteAction extends OpenRelatedAction {

	@Override
	protected boolean calculateEnabledWithArticles(Set<Article> articles) {
		setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.openrelated.OpenRelatedDeliveryNoteAction.action.text.disabled")); //$NON-NLS-1$
		DeliveryNoteID deliveryNoteID = getCommonDeliveryNoteID(articles);
		if (deliveryNoteID != null) {
			setText(
					String.format(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.openrelated.OpenRelatedDeliveryNoteAction.action.text.enabled"), //$NON-NLS-1$
					deliveryNoteID.deliveryNoteIDPrefix, ObjectIDUtil.longObjectIDFieldToString(deliveryNoteID.deliveryNoteID)
				)
			);
		}
		return deliveryNoteID != null && !(getActiveGeneralEditorInput() instanceof GeneralEditorInputDeliveryNote);
	}
	
	/**
	 * Extracts the DeliveryNoteID common to all given articles or <code>null</code>.
	 * @param articles The articles to check.
	 */
	protected DeliveryNoteID getCommonDeliveryNoteID(Set<Article> articles) {
		DeliveryNoteID deliveryNoteID = null;
		boolean first = true;
		for (Article article : articles) {
			if (first) {
				deliveryNoteID = article.getDeliveryNoteID();
				first = false;
				continue;
			}
			if (!Util.equals(deliveryNoteID, article.getDeliveryNoteID()))
				return null;
		}
		return deliveryNoteID;
	}
	
	@Override
	public void run() {
		DeliveryNoteID deliveryNoteID = getCommonDeliveryNoteID(getArticles());
		if (deliveryNoteID == null)
			return;
		try {
			RCPUtil.openEditor(new GeneralEditorInputDeliveryNote(deliveryNoteID), GeneralEditor.ID_EDITOR);
		} catch (PartInitException e) {
			throw new RuntimeException(e);
		}
	}
	
}
