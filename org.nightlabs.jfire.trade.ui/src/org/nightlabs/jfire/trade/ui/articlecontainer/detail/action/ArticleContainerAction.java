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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEdit;

public abstract class ArticleContainerAction
extends Action implements IArticleContainerAction
{
	public ArticleContainerAction()
	{
//		super("", AS_PUSH_BUTTON); //$NON-NLS-1$
	}

	public ArticleContainerAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	public ArticleContainerAction(String text, int style) {
		super(text, style);
	}

	public ArticleContainerAction(String text) {
		super(text);
	}


	private ArticleContainerActionRegistry articleContainerActionRegistry;

	public void init(ArticleContainerActionRegistry articleContainerActionRegistry)
	{
		this.articleContainerActionRegistry = articleContainerActionRegistry;
	}

	public ArticleContainerActionRegistry getArticleContainerActionRegistry()
	{
		return articleContainerActionRegistry;
	}

	/**
	 * This method is called by {@link #calculateEnabled() } for every {@link Article}. If this
	 * method returns <code>false</code>, the current article will not be included in the targets
	 * for this action (see {@link #getArticles() }. If the resulting list is empty,
	 * {@link #calculateEnabled() } will return <code>false</code>.
	 *
	 * @param article
	 * @return
	 */
	protected boolean excludeArticle(Article article)
	{
		return false;
	}

	private List<Article> articles = null;

	/**
	 * @return Returns those instances of {@link Article} that are target of this action. This
	 *		is only available, after {@link #calculateEnabled() } has been called.
	 */
	protected List<Article> getArticles()
	{
		return articles;
	}

	/**
	 * Iterates all {@link Article}s and calls {@link #excludeArticle(Article) } in order to find out
	 * which articles will be processed by this action. If all articles are excluded, <code>false</code>
	 * will be returned.
	 */
	public boolean calculateEnabled()
	{
		this.articles = null;

		ArticleContainerEdit edit = getArticleContainerEdit();
		if (edit == null)
			return false;

		Collection<Article> articles = edit.getArticles();
		
		if (articles == null)
			return false;

		List<Article> filteredArticles = new ArrayList<Article>(articles.size());
		for (Article article : articles) {
			if (excludeArticle(article))
				continue;

			filteredArticles.add(article);
		}
		this.articles = filteredArticles;

		return !this.articles.isEmpty();
	}
	
	
	/**
	 * This is a convenience method for: 
	 * <pre>
	 * getArticleContainerActionRegistry().getActiveArticleContainerEdit()
	 * </pre>
	 * However you should always use this method.
	 * 
	 * @return The active {@link ArticleContainerEdit}. Note that this might be <code>null</code>.
	 */
	protected ArticleContainerEdit getArticleContainerEdit() {
		return getArticleContainerActionRegistry().getActiveArticleContainerEdit();
	}
	
	/**
	 * This method attempts to get the {@link ArticleContainer} from the active {@link ArticleContainerEdit}.
	 * The edit might be <code>null</code> and this method will also return <code>null</code> then.
	 *   
	 * @return The {@link ArticleContainer} of the {@link ArticleContainerEdit} this action associated with or null, if there is currently no edit active.
	 */
	protected ArticleContainer getArticleContainer() {
		ArticleContainerEdit articleContainerEdit = getArticleContainerEdit();
		if (articleContainerEdit == null)
			return null;

		return articleContainerEdit.getArticleContainer();
	}
	
	/**
	 * This method attempts to get the {@link ArticleContainerID} from the active {@link ArticleContainerEdit}.
	 * The edit might be <code>null</code> and this method will also return <code>null</code> then.
	 *   
	 * @return The {@link ArticleContainerID} of the {@link ArticleContainerEdit} this action associated with or null, if there is currently no edit active.
	 */
	protected ArticleContainerID getArticleContainerID() {
		ArticleContainerEdit articleContainerEdit = getArticleContainerEdit();
		if (articleContainerEdit == null)
			return null;
		
		return articleContainerEdit.getArticleContainerID();
	}
}
