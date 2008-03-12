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

package org.nightlabs.jfire.trade.ui.articlecontainer;

import java.util.Collection;
import java.util.Set;

import org.nightlabs.jfire.base.ui.jdo.JDOObjectProvider;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.ui.resource.Messages;

public class ArticleProvider extends JDOObjectProvider
{
	private static ArticleProvider _sharedInstance;
	public static ArticleProvider sharedInstance()
	{
		if (_sharedInstance == null)
			_sharedInstance = new ArticleProvider();

		return _sharedInstance;
	}

	public ArticleProvider() { }

	public Article getArticle(ArticleID orderID, String[] fetchGroups, int maxFetchDepth)
	{
		return getArticle(null, orderID, fetchGroups, maxFetchDepth);
	}

	private TradeManager tradeManager;

	public Article getArticle(TradeManager tradeManager, ArticleID articleID, String[] fetchGroups, int maxFetchDepth)
	{
		this.tradeManager = tradeManager;
		return (Article) getJDOObject(null, articleID, fetchGroups, maxFetchDepth);
	}

	@Override
	protected Object retrieveJDOObject(String scope, Object objectID, String[] fetchGroups, int maxFetchDepth)
	throws Exception
	{
		if (tradeManager == null)
			tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();

		try {
			return tradeManager.getArticle((ArticleID)objectID, fetchGroups, maxFetchDepth);
		} finally {
			tradeManager = null;
		}
	}

	public Collection<Article> getArticles(Collection<ArticleID> articleIDs, String[] fetchGroups, int maxFetchDepth)
	{
		return getArticles(null, articleIDs, fetchGroups, maxFetchDepth);
	}

	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public Collection<Article> getArticles(TradeManager tradeManager, Collection<ArticleID> articleIDs, String[] fetchGroups, int maxFetchDepth)
	{
		this.tradeManager = tradeManager;
		return getJDOObjects(null, articleIDs, fetchGroups, maxFetchDepth);
	}

	@Override
	protected Collection retrieveJDOObjects(String scope, Set articleIDs, String[] fetchGroups, int maxFetchDepth) throws Exception
	{
		if (tradeManager == null)
			tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();

		try {
			return tradeManager.getArticles(articleIDs, fetchGroups, maxFetchDepth);
		} finally {
			tradeManager = null;
		}
	}
}
