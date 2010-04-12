/**
 *
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.openrelated;

import java.util.Set;

import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEdit;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.util.Util;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class OpenRelatedOfferAction extends OpenRelatedAction {

	@Override
	protected boolean calculateEnabledWithArticles(Set<Article> articles) {
		ArticleContainerEdit edit = getArticleEditActionRegistry().getActiveArticleContainerEdit();
//		setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.openrelated.OpenRelatedOfferAction.action.text.disabled")); //$NON-NLS-1$
		OfferID offerID = getCommonOfferID(articles);
		setText(getText(offerID));
//		if (offerID != null) {
//			setText(
//					String.format(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.openrelated.OpenRelatedOfferAction.action.text.enabled"), //$NON-NLS-1$
//					offerID.offerIDPrefix, ObjectIDUtil.longObjectIDFieldToString(offerID.offerID)
//				)
//			);
//		}
		return offerID != null && !(edit.getArticleContainerID() instanceof OfferID);
	}

	/**
	 * Extracts the OfferID common to all given articles or <code>null</code>.
	 * @param articles The articles to check.
	 */
	protected OfferID getCommonOfferID(Set<Article> articles) {
		OfferID offerID = null;
		boolean first = true;
		for (Article article : articles) {
			if (first) {
				offerID = article.getOfferID();
				first = false;
				continue;
			}
			if (!Util.equals(offerID, article.getOfferID()))
				return null;
		}
		return offerID;
	}

	@Override
	public void run() {
		OfferID offerID = getCommonOfferID(getArticles());
		if (offerID == null)
			return;
		try {
			RCPUtil.openEditor(new ArticleContainerEditorInput(offerID), ArticleContainerEditor.ID_EDITOR);
		} catch (PartInitException e) {
			throw new RuntimeException(e);
		}
	}

}
