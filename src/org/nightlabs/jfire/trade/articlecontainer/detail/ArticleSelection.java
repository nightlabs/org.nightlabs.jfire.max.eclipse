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

package org.nightlabs.jfire.trade.articlecontainer.detail;

import java.util.Set;

import org.nightlabs.jfire.trade.Article;

public class ArticleSelection
{
	private ArticleEdit articleEdit;
	private Set<? extends Article> selectedArticles;

	public ArticleSelection(ArticleEdit articleEdit, Set<? extends Article> selectedArticles)
	{
		if (articleEdit == null)
			throw new IllegalArgumentException("articleEdit == null!!!"); //$NON-NLS-1$

		if (selectedArticles == null)
			throw new IllegalArgumentException("selectedArticles == null!!!"); //$NON-NLS-1$

		this.articleEdit = articleEdit;
		this.selectedArticles = selectedArticles;
	}

	/**
	 * @return Returns the <code>ArticleEdit</code> - never returns <code>null</code>.
	 */
	public ArticleEdit getArticleEdit()
	{
		return articleEdit;
	}
	/**
	 * @return Returns a <code>Set</code> of {@link org.nightlabs.jfire.trade.Article} - never returns <code>null</code>.
	 */
	public Set<? extends Article> getSelectedArticles()
	{
		return selectedArticles;
	}
}
