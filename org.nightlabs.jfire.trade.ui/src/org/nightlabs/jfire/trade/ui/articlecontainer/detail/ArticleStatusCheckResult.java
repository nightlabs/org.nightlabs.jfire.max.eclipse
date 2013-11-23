/**
 * 
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.nightlabs.jfire.trade.Article;

/** 
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ArticleStatusCheckResult {

	private Set<Article> allocatedOrReversedArticles = new HashSet<Article>();
	private Set<Article> notAllocatedNorReversedArticles = new HashSet<Article>();

	public ArticleStatusCheckResult(Collection<Article> articles) {
		for (Article article : articles) {
			if (article.isAllocated() || article.isReversed()) {
				allocatedOrReversedArticles.add(article);
			}
			else {
				notAllocatedNorReversedArticles.add(article);
			}
		}
	}
	
//	public ArticleStatusCheckResult(Set<ArticleSelection> articleSelections) 
//	{			
//		for (ArticleSelection articleSelection : articleSelections) {
//			Set<? extends Article> articles = articleSelection.getSelectedArticles();
//			for (Article article : articles) {
//				if (article.isAllocated() || article.isReversed()) {
//					allocatedOrReversedArticles.add(article);
//				}
//				else {
//					notAllocatedNorReversedArticles.add(article);
//				}
//			}
//		}			
//	}
	
	/**
	 * Returns the allocatedOrReversedArticles.
	 * @return the allocatedOrReversedArticles
	 */
	public Set<Article> getAllocatedOrReversedArticles() {
		return allocatedOrReversedArticles;
	}

	/**
	 * Returns the notAllocatedNorReversedArticles.
	 * @return the notAllocatedNorReversedArticles
	 */
	public Set<Article> getNotAllocatedNorReversedArticles() {
		return notAllocatedNorReversedArticles;
	}
	
	/**
	 * Returns true if all given articles from the ArticleSelection
	 * have the status allocated or reversed and false if not.
	 * 
	 * @return true if all given articles from the ArticleSelection
	 * have the status allocated or reversed and false if not 
	 */
	public boolean isAllArticlesAllocatedOrReversed() {
		return getNotAllocatedNorReversedArticles().isEmpty();
	}
}
