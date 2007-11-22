/**
 * 
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.openrelated;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorInput;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleSelection;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.IGeneralEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleEditActionRegistry;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.IArticleEditAction;

/**
 * Base Action for all 'Open related' actions.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public abstract class OpenRelatedAction extends Action implements IArticleEditAction {

	private Set<Article> articles = null;
	private ArticleEditActionRegistry articleEditActionRegistry;

	public boolean calculateEnabled(Set<ArticleSelection> articleSelections) {
		this.articles = null;
		Set<Article> articles = new HashSet<Article>();
		for (ArticleSelection articleSelection : articleSelections) {
			for (Article article : articleSelection.getSelectedArticles()) {
				articles.add(article);
			}
		}
		this.articles = articles;
		return calculateEnabledWithArticles(articles);
	}

	protected abstract boolean calculateEnabledWithArticles(Set<Article> articles);

	public boolean calculateVisible() {
		return true;
	}
	
	public Set<Article> getArticles() {
		return articles;
	}
	
	public ArticleEditActionRegistry getArticleEditActionRegistry() {
		return articleEditActionRegistry;
	}

	public void init(ArticleEditActionRegistry articleEditActionRegistry) {
		this.articleEditActionRegistry = articleEditActionRegistry;
	}

	public IEditorInput getActiveGeneralEditorInput() {
		IGeneralEditor editor = getArticleEditActionRegistry().getActiveGeneralEditorActionBarContributor().getActiveGeneralEditor();
		if (editor == null)
			return null;
		return editor.getEditorInput();
	}
}
