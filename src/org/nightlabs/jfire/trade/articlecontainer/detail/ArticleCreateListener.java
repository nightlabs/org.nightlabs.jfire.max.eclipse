package org.nightlabs.jfire.trade.articlecontainer.detail;

public interface ArticleCreateListener
{
	/**
	 * This method is called on the SWT GUI thread! If you need to do expensive tasks here,
	 * do it asynchronously using a {@link org.eclipse.core.runtime.jobs.Job}!
	 */
	void articlesCreated(ArticleCreateEvent articleCreateEvent);
}
