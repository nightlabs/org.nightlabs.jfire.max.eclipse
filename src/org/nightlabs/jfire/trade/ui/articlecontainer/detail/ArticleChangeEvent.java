/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import java.util.Collection;
import java.util.EventObject;

import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleCarrier;

public class ArticleChangeEvent extends EventObject
{
	private static final long serialVersionUID = 1L;
	private Collection<Article> dirtyArticles;
	private Collection<ArticleCarrier> dirtyArticleCarriers;
	private Collection<Article> deletedArticles;
	private Collection<ArticleCarrier> deletedArticleCarriers;
//	private NotificationEvent notificationEvent;

	/**
	 * @param source 
	 * @param dirtyArticles Instances of {@link Article}.
	 * @param dirtyArticleCarriers TODO
	 * @param deletedArticles TODO
	 * @param deletedArticleCarriers TODO
	 * @param changeEvent The original <code>ChangeEvent</code> as dispatched by the {@link org.nightlabs.jfire.base.jdo.notification.ChangeManager}.
	 */
	public ArticleChangeEvent(
			Object source,
			Collection<Article> dirtyArticles, Collection<ArticleCarrier> dirtyArticleCarriers,
			Collection<Article> deletedArticles, Collection<ArticleCarrier> deletedArticleCarriers)
	{
		super(source);
		this.dirtyArticles = dirtyArticles;
		this.dirtyArticleCarriers = dirtyArticleCarriers;
		this.deletedArticles = deletedArticles;
		this.deletedArticleCarriers = deletedArticleCarriers;
//		this.notificationEvent = notificationEvent;
	}

	/**
	 * @return Instances of {@link Article}. Note, that this <code>Collection</code>
	 *		does not have an <code>Article</code> for every <code>ArticleID</code> in the
	 *		<code>ChangeEvent</code> (see {@link #getChangeEvent()}), but only for those that
	 *		are managed by the instance of {@link org.nightlabs.jfire.trade.ui.ArticleSegmentGroups}.
	 */
	public Collection<Article> getDirtyArticles()
	{
		return dirtyArticles;
	}

	public Collection<ArticleCarrier> getDirtyArticleCarriers()
	{
		return dirtyArticleCarriers;
	}

	public Collection<Article> getDeletedArticles()
	{
		return deletedArticles;
	}

	public Collection<ArticleCarrier> getDeletedArticleCarriers()
	{
		return deletedArticleCarriers;
	}

//	/**
//	 * @return The original <code>NotificationEvent</code> as dispatched by the
//	 *		{@link org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager}.
//	 */
//	public NotificationEvent getNotificationEvent()
//	{
//		return notificationEvent;
//	}
//	/**
//	 * @return The original <code>ChangeEvent</code> as dispatched by the
//	 *		{@link org.nightlabs.jfire.base.jdo.notification.ChangeManager}.
//	 */
//	public ChangeEvent getChangeEvent()
//	{
//		return changeEvent;
//	}
}
