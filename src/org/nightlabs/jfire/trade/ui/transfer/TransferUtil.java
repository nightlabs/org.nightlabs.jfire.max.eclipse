package org.nightlabs.jfire.trade.ui.transfer;

import javax.jdo.FetchPlan;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ui.articlecontainer.ArticleProvider;

/**
 * This class contains utility methods for handling transfers of articles.
 * 
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 */
public class TransferUtil
{
	/**
	 * Returns whether the given {@link Article} can be added to a delivery note, i.e. whether it is
	 * allocated and not yet contained in a delivery note and not reversed.
	 * @param article The {@link Article} to be checked.
	 * @return whether the given {@link Article} can be added to a delivery note.
	 */
	public static boolean canAddToDeliveryNote(Article article) {
		if (!article.isAllocated() || article.isReversed() || article.getDeliveryNoteID() != null)
			return false;

		// If the article is reversing, it can be added to an delivery note if the reversed article is in an delivery note, too
		if (article.isReversing()) {
			Article reversedArticle = ArticleProvider.sharedInstance().getArticle(
					article.getReversedArticleID(), new String[] { FetchPlan.DEFAULT, Article.FETCH_GROUP_DELIVERY_NOTE_ID}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
			
			if (reversedArticle.getDeliveryNoteID() == null)
				return false;
		}
		
		return true;
	}
	
	/**
	 * Returns whether the given {@link Article} can be added to an invoice, i.e. whether it is allocated
	 * and not yet contained in a delivery note and not reversed.
	 * @param article The {@link Article} to be checked.
	 * @return whether the given {@link Article} can be added to an invoice.
	 */
	public static boolean canAddToInvoice(Article article) {
		if (!article.isAllocated() || article.isReversed() || article.getInvoiceID() != null)
			return false;

		// If the article is reversing, it can be added to an invoice if the reversed article is in an invoice, too
		if (article.isReversing()) {
			Article reversedArticle = ArticleProvider.sharedInstance().getArticle(
					article.getReversedArticleID(), new String[] { FetchPlan.DEFAULT, Article.FETCH_GROUP_INVOICE_ID}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
			
			if (reversedArticle.getInvoiceID() == null)
				return false;
		}
		
		return true;
	}
}
