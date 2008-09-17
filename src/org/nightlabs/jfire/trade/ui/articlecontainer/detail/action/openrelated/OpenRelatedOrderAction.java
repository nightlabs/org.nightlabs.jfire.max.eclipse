/**
 * 
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.openrelated;

import java.util.Set;

import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEdit;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.util.Util;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class OpenRelatedOrderAction extends OpenRelatedAction {

	@Override
	protected boolean calculateEnabledWithArticles(Set<Article> articles) {
		ArticleContainerEdit edit = getArticleEditActionRegistry().getActiveArticleContainerEdit();
		setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.openrelated.OpenRelatedOrderAction.action.text.disabled")); //$NON-NLS-1$
		OrderID orderID = getCommonOrderID(articles);
		if (orderID != null) {
			setText(
					String.format(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.openrelated.OpenRelatedOrderAction.action.text.enabled"), //$NON-NLS-1$
					orderID.orderIDPrefix, ObjectIDUtil.longObjectIDFieldToString(orderID.orderID)
				)
			);
		}
		return orderID != null && !(edit.getArticleContainerID() instanceof OrderID);
	}
	
	/**
	 * Extracts the OrderID common to all given articles or <code>null</code>.
	 * @param articles The articles to check.
	 */
	protected OrderID getCommonOrderID(Set<Article> articles) {
		OrderID orderID = null;
		boolean first = true;
		for (Article article : articles) {
			if (first) {
				orderID = article.getOrderID();
				first = false;
				continue;
			}
			if (!Util.equals(orderID, article.getOrderID()))
				return null;
		}
		return orderID;
	}
	
	@Override
	public void run() {
		OrderID orderID = getCommonOrderID(getArticles());
		if (orderID == null)
			return;
		try {
			RCPUtil.openEditor(new ArticleContainerEditorInput(orderID), ArticleContainerEditor.ID_EDITOR);
		} catch (PartInitException e) {
			throw new RuntimeException(e);
		}
	}
	
}
