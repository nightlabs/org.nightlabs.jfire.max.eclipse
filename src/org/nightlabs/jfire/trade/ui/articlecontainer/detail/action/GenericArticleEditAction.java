package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleSelection;

/**
 * This implementation of {@link ArticleEditAction} doesn't delegate to {@link ArticleEditActionDelegate}s,
 * but instead expects to be extended (it's abstract). This means, it is the same action for all kinds
 * of {@link Article}s and thus it's named "generic".
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public abstract class GenericArticleEditAction
		extends ArticleEditAction
{
	/**
	 * This method is called by {@link #calculateEnabled() } for every {@link Article}. If this
	 * method returns <code>false</code> for at least one article, the action will be disabled.
	 *
	 * @param article
	 * @return
	 */
	protected abstract boolean excludeArticle(Article article);

	private Set<Article> articles = null;

	@Override
	public boolean calculateEnabled(Set<ArticleSelection> articleSelections)
	{
		this.articles = null;
		Set<Article> articles = new HashSet<Article>();
		for (Iterator<ArticleSelection> itAS = articleSelections.iterator(); itAS.hasNext(); ) {
			ArticleSelection articleSelection = itAS.next();
			for (Iterator<? extends Article> itSA = articleSelection.getSelectedArticles().iterator(); itSA.hasNext(); ) {
				Article article = itSA.next();
				if (excludeArticle(article))
					return false;

				articles.add(article);
			}
		}

		this.articles = articles;
		return true;
	}

	public Set<Article> getArticles()
	{
		return articles;
	}

}
