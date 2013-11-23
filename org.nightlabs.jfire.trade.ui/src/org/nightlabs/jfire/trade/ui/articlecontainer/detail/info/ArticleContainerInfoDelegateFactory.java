package org.nightlabs.jfire.trade.ui.articlecontainer.detail.info;

import org.nightlabs.jfire.trade.ArticleContainer;

/**
 * Interface which provides a factory pattern for creating new instances of specific implementations of {@link ArticleContainerInfoDelegate}.
 * Implementations of this interface can be registered via the extension point {@link ArticleContainerInfoDelegateRegistry#EXTENSION_POINT_ID}
 * and obtained via {@link ArticleContainerInfoDelegateRegistry#getArticleContainerInfoDelegateFactory(Class)}
 *
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 */
public interface ArticleContainerInfoDelegateFactory
{
	/**
	 * Returns the implementation of {@link ArticleContainerInfoDelegate} for this factory.
	 * @return the implementation of {@link ArticleContainerInfoDelegate} for this factory
	 */
	ArticleContainerInfoDelegate createArticleContainerInfoDelegate();

//	/**
//	 * Returns the index hint for this factory.
//	 * This information can be provided by the extension and the implementation with the lowest index will be used.
//	 *
//	 * @return the index hint for this factory
//	 */
//	int getIndexHint();

	/**
	 *
	 * @return the Class of {@link ArticleContainer} which can be handeled by the implementation of {@link ArticleContainerInfoDelegate}
	 * returned from {@link #createArticleContainerInfoDelegate()}
	 */
	Class<? extends ArticleContainer> getArticleContainerClass();
}
