package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import java.util.Collection;
import java.util.EventObject;

import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleCarrier;

public class ArticleCreateEvent
extends EventObject
{
	private static final long serialVersionUID = 1L;

	private Collection<Article> articles;
	private Collection<ArticleCarrier> articleCarriers;
//	private JDOLifecycleEvent jdoLifecycleEvent;

	public ArticleCreateEvent(Object source, Collection<Article> articles, Collection<ArticleCarrier> articleCarriers)
	{
		super(source);
		this.articles = articles;
		this.articleCarriers = articleCarriers;
//		this.jdoLifecycleEvent = jdoLifecycleEvent;
	}

	public Collection<Article> getArticles()
	{
		return articles;
	}

	public Collection<ArticleCarrier> getArticleCarriers()
	{
		return articleCarriers;
	}

//	public JDOLifecycleEvent getJdoLifecycleEvent()
//	{
//		return jdoLifecycleEvent;
//	}
}
