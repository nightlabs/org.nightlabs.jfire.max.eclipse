/**
 *
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse;

/**
 * Interface to listen for changes inside the {@link ReverseProductComposite}.
 * @see ReverseProductComposite#addReverseProductListener(IReverseProductListener)
 *
 * @author daniel[at]nightlabs[dot]de
 */
public interface IReverseProductListener
{
	/**
	 * Gets called whenever something related to the selection changes inside the {@link ReverseProductComposite}.
	 * @param event the ReverseProductEvent which contains information about the changes.
	 */
	void reverseProductChanged(ReverseProductEvent event);
}
