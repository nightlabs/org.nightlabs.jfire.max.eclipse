/**
 *
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse;

/**
 * Event Object used for {@link IReverseProductListener}s.
 * @see IReverseProductListener
 * @see ReverseProductComposite
 * @author daniel[at]nightlabs[dot]de
 */
public class ReverseProductEvent
{
	private boolean reverseAll;
	private boolean reverseArticle;
	private boolean reversePaymentAndDelivery;
	private boolean releaseArticles;

	/**
	 * @param reverseAll
	 * @param reverseArticle
	 * @param reversePaymentAndDelivery
	 * @param releaseArticles
	 */
	public ReverseProductEvent(boolean reverseAll, boolean reverseArticle,
			boolean reversePaymentAndDelivery, boolean releaseArticles) {
		super();
		this.reverseAll = reverseAll;
		this.reverseArticle = reverseArticle;
		this.reversePaymentAndDelivery = reversePaymentAndDelivery;
		this.releaseArticles = releaseArticles;
	}

	/**
	 * Returns the reverseAll.
	 * @return the reverseAll
	 */
	public boolean isReverseAll() {
		return reverseAll;
	}

	/**
	 * Returns the reverseArticle.
	 * @return the reverseArticle
	 */
	public boolean isReverseArticle() {
		return reverseArticle;
	}

	/**
	 * Returns the reversePaymentAndDelivery.
	 * @return the reversePaymentAndDelivery
	 */
	public boolean isReversePaymentAndDelivery() {
		return reversePaymentAndDelivery;
	}

	/**
	 * Returns the releaseArticles.
	 * @return the releaseArticles
	 */
	public boolean isReleaseArticles() {
		return releaseArticles;
	}

}
