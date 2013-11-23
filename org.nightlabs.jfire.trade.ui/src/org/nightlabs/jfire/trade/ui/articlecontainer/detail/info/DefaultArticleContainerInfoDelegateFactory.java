package org.nightlabs.jfire.trade.ui.articlecontainer.detail.info;

import org.nightlabs.jfire.trade.ArticleContainer;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class DefaultArticleContainerInfoDelegateFactory
implements ArticleContainerInfoDelegateFactory
{
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.info.ArticleContainerInfoDelegateFactory#createArticleContainerInfoDelegate()
	 */
	@Override
	public ArticleContainerInfoDelegate createArticleContainerInfoDelegate() {
		return new DefaultArticleContainerInfoDelegate();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.info.ArticleContainerInfoDelegateFactory#getArticleContainerClass()
	 */
	@Override
	public Class<? extends ArticleContainer> getArticleContainerClass() {
		return ArticleContainer.class;
	}

}
