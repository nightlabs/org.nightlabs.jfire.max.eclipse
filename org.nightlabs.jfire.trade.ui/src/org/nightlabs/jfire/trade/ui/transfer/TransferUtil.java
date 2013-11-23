package org.nightlabs.jfire.trade.ui.transfer;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.dao.ArticleDAO;
import org.nightlabs.jfire.trade.dao.OfferDAO;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.progress.NullProgressMonitor;

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
			Article reversedArticle = ArticleDAO.sharedInstance().getArticle(
					article.getReversedArticleID(), 
					new String[] { FetchPlan.DEFAULT, Article.FETCH_GROUP_DELIVERY_NOTE_ID}, 
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new NullProgressMonitor());
			
			if (reversedArticle.getDeliveryNoteID() == null)
				return false;
		}
		
		if (!checkArticleOfferState(article)) {
			return false;
		}
		
		if (article.getDeliveryNoteID() != null)
			return false;
		
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
			Article reversedArticle = ArticleDAO.sharedInstance().getArticle(
					article.getReversedArticleID(), 
					new String[] { FetchPlan.DEFAULT, Article.FETCH_GROUP_INVOICE_ID}, 
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new NullProgressMonitor());
			
			if (reversedArticle.getInvoiceID() == null)
				return false;
		}

		if (!checkArticleOfferState(article)) {
			return false;
		}
		
		if (article.getInvoiceID() != null)
			return false;
		
		return true;
	}
	
	/**
	 * Checks whether the the articles Offer is in a state that allows for modifiying the Article, i.e. adding it to an Invoice or DeliveryNote.
	 * 
	 * @param article The article to check.
	 * @return <code>true</code> if the Offer is in a supported state. <code>false</code> otherwise.
	 */
	private static boolean checkArticleOfferState(Article article) {
		Article articleToCheck = ArticleDAO.sharedInstance().getArticle(
						(ArticleID) JDOHelper.getObjectId(article), new String[] { FetchPlan.DEFAULT,Article.FETCH_GROUP_OFFER },
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
		if (articleToCheck == null || articleToCheck.getOffer() == null)
			return false;
		if (articleToCheck.getOffer().isAborted()) {
			return false;
		}
		return true;
	}
	
	
}