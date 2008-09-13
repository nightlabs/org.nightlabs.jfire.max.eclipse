/**
 * 
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

/**
 * {@link ArticleContainerEditFactory}s are registered as extensions
 * to the point <code>org.nightlabs.jfire.trade.ui.articleContainerEditFactory</code>
 * and create implementations of {@link ArticleContainerEdit}s.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public interface ArticleContainerEditFactory {

	/**
	 * @return Create a new instance of the implementation of {@link ArticleContainerEdit}
	 *         this factory was registered for.
	 */
	ArticleContainerEdit createArticleContainerEdit();
}
