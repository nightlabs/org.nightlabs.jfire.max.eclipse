package org.nightlabs.jfire.trade.ui.transfer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.nightlabs.jfire.trade.Article;

/**
 * This class contains utility methods for handling transfers of articles.
 * 
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 */
public class TransferUtil
{
	/**
	 * Returns whether the given {@link Article} is deliverable.
	 * @param article The {@link Article} to be checked.
	 * @return whether the given {@link Article} is deliverable.
	 */
	public static boolean isDeliverable(Article article) {
		return article.isAllocated() && article.getDeliveryNoteID() != null;
	}
	
	/**
	 * Returns whether the given {@link Article} is payable.
	 * @param article The {@link Article} to be checked.
	 * @return whether the given {@link Article} is payable.
	 */
	public static boolean isPayable(Article article) {
		return article.isAllocated() && article.getInvoiceID() != null;
	}
	
	/**
	 * Filters the given collection of the articles by returning a set that
	 * contains only the articles that are payable.
	 * @param articles The articles to be filtered.
	 * @return a set that contains only the articles that are payable.
	 */
	public static Set<Article> getPayableArticles(Collection<Article> articles) {
		Set<Article> filteredArticles = new HashSet<Article>();
		for (Article article : articles)
			if (isPayable(article))
				filteredArticles.add(article);
		
		return filteredArticles;
	}
	
	/**
	 * Filters the given collection of the articles by returning a set that
	 * contains only the articles that are deliverable.
	 * @param articles The articles to be filtered.
	 * @return a set that contains only the articles that are payable.
	 */
	public static Set<Article> getDeliverableArticles(Collection<Article> articles) {
		Set<Article> filteredArticles = new HashSet<Article>();
		for (Article article : articles)
			if (isDeliverable(article))
				filteredArticles.add(article);
		
		return filteredArticles;
	}
}
