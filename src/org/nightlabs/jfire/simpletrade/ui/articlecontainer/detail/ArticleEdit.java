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

package org.nightlabs.jfire.simpletrade.ui.articlecontainer.detail;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleCarrier;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractArticleEdit;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ArticleEdit extends AbstractArticleEdit
{
	private ArticleEditComposite articleEditComposite = null;

	@Override
	public Composite _createComposite(Composite parent)
	{
		articleEditComposite = new ArticleEditComposite(parent, this);
		return articleEditComposite;
	}

	public Set<? extends ArticleCarrier> addArticles(Set<? extends ArticleCarrier> articleCarriers)
	{
		Class productTypeClass = getArticleProductTypeClassGroup().getProductTypeClass();

		Set<ArticleCarrier> accepted = null;
		for (Iterator<? extends ArticleCarrier> it = articleCarriers.iterator(); it.hasNext(); ) {
			ArticleCarrier articleCarrier = it.next();

			if (productTypeClass.equals(articleCarrier.getArticle().getProductType().getClass())) {
				if (accepted == null)
					accepted = new HashSet<ArticleCarrier>(articleCarriers.size());

				accepted.add(articleCarrier);
				it.remove();
			}
		}

		if (accepted != null) {
			_addArticleCarriers(accepted);
			articleEditComposite.refreshUI();
		}

		return articleCarriers; // we could return null here, if it's empty, but there's no advantage...it doesn't matter

////		String productTypePK = getArticleProductTypeGroup().getProductType().getPrimaryKey();
//		Class productTypeClass = getArticleProductTypeClassGroup().getProductTypeClass();
//		Article article = (Article) articleCarriers.iterator().next();
//
//		if (productTypeClass.equals(article.getProductType().getClass())) {
//			_addArticles(articleCarriers);
//
//
////			getArticles().addAll(articles);
//			// Because one ArticleAdder can only add Articles of one ProductType (the currently selected one),
//			// we rely on all Articles having the same ProductType here.
////			getArticleProductTypeClassGroup().addArticles(articles); // done by _addArticles(...)!
//			articleCarriers = null; // we processed all articles, so we don't return any
//			articleEditComposite.refreshUI();
//		}
//
//		return articleCarriers;
	}

	public Set<? extends Article> getSelectedArticles()
	{
		return articleEditComposite.getSelectedArticles();
	}

	public Set<? extends Article> setSelectedArticles(Set<? extends Article> articles)
	{
		return articleEditComposite.setSelectedArticles(articles);
	}

//	/**
//	 * This method is called by {@link RemoveActionDelegate#run(IArticleEditAction, ArticleSelection)}.
//	 *
//	 * @param articles The {@link Article}s to be removed.
//	 */
//	public void removeArticles(Set<? extends Article> articles)
//	{
//		_removeArticles(articles);
//		articleEditComposite.refreshUI();
//	}
}
