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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.remove;

import java.util.Set;

import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleEdit;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleSelection;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleEditActionDelegate;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.IArticleEditAction;

/**
 * You must implement this class, when you want to remove {@link org.nightlabs.jfire.trade.ui.Article}s from an
 * {@link org.nightlabs.jfire.accounting.Invoice} or a {@link org.nightlabs.jfire.store.DeliveryNote}; or
 * when you want to delete the {@link org.nightlabs.jfire.trade.ui.Article}s (removing an <code>Article</code>from an
 * {@link org.nightlabs.jfire.trade.ui.Offer}/{@link org.nightlabs.jfire.trade.ui.Order} is equivalent to deleting
 * it from the datastore).
 * <p>
 * Normally, all you have to do is taking care about your GUI in the method
 * {@link org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.IArticleEditActionDelegate#run(IArticleEditAction, ArticleSelection)}.
 * The work on the server as well as updating the {@link org.nightlabs.jfire.trade.ui.ArticleProductTypeClassGroup}
 * is done for you (but you can intercept if you like to).
 * </p>
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public abstract class RemoveActionDelegate extends ArticleEditActionDelegate
{
//	/**
//	 * This method is called by the {@link RemoveAction} and removes the articles from the
//	 * {@link org.nightlabs.jfire.trade.ui.ArticleProductTypeClassGroup}. It is executed,
//	 * after the server removal has been done (if {@link #isDelegateHandlingRemoteWork()} == false)
//	 * and before {@link org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.IArticleEditActionDelegate#run(IArticleEditAction, ArticleSelection)}
//	 * is called.
//	 * <p>
//	 * You better should not overwrite this method.
//	 * </p>
//	 *
//	 * @param articleEdit The ArticleEdit from which to remove the articles.
//	 * @param articles The {@link org.nightlabs.jfire.trade.ui.Article}s to be removed.
//	 */
//	public void removeArticlesFromArticleProductTypeClassGroup(ArticleEdit articleEdit, Collection articles) {
//		articleEdit.getArticleProductTypeClassGroup().removeArticles(articles);
//	}

	/**
	 * The implementation of this method in {@link RemoveAction} calculates enabled, already:
	 * <ul>
	 *	<li>Removal from Offer/Order (=deletion of Article) is NOT possible, if
	 *    <ul>
	 *      <li>Article is NOT reversing (i.e. "normal") and it is currently allocated/allocationPending;</li>
	 *	    <li>Offer is finalized.</li>
	 *    </ul>
	 *  </li>
	 *  <li>Removal from Invoice is NOT possible, if
	 *    <ul>
	 *      <li>Invoice is finalized.</li>
	 *    </ul>
	 *  </li>
	 *  <li>Removal from DeliveryNote is NOT possible, if
	 *    <ul>
	 *      <li>DeliveryNote is finalized.</li>
	 *    </ul>
	 *  </li>
	 * </ul>
	 */
	public boolean calculateEnabled(ArticleSelection articleSelection, Set<ArticleSelection> articleSelections)
	{
		// The RemoveAction calculates enabled, already:
		//  * Removal from Offer/Order (=deletion of Article) is NOT possible, if
		//    - Article is NOT reversing (i.e. "normal") and it is currently allocated/allocationPending;
		//    - Offer is finalized.
		//  * Removal from Invoice is NOT possible, if
		//    - Invoice is finalized.
		//  * Removal from DeliveryNote is NOT possible, if
		//    - DeliveryNote is finalized.
		//
		// Here, we can only reduce the possibilities, but we don't.
		return true;
	}

	/**
	 * Overwrite this method if you want to handle the remote work (removing the
	 * article on the server).
	 *
	 * @return The default implementation of this method returns <code>false</code>,
	 *		which means the delegate handles ONLY the GUI work (and the {@link RemoveAction}
	 *		removes the article in a generic way remotely in the server). You can overwrite
	 *		this method, return <code>true</code> and handle the removal via an own bean in the
	 *		{@link org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.IArticleEditActionDelegate#run(IArticleEditAction, ArticleSelection)}
	 *		method.
	 */
	public boolean isDelegateHandlingRemoteWork() {
		return false;
	}

	/**
	 * This implementation removes the {@link org.nightlabs.jfire.trade.ui.Article}s specified by the {@link ArticleSelection}
	 * from the {@link org.nightlabs.jfire.trade.ui.ArticleProductTypeClassGroup} by calling {@link ArticleEdit#removeArticles(Set)}.
	 * This keeps the {@link org.nightlabs.jfire.trade.ui.ArticleSegmentGroups}
	 * up to date. You should update your GUI in {@link ArticleEdit#removeArticles(Set)}.
	 *
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.IArticleEditActionDelegate#run(org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.IArticleEditAction, org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleSelection)
	 */
	public void run(IArticleEditAction articleEditAction, ArticleSelection articleSelection)
	{
//		try {
//			ArticleEdit articleEdit = articleSelection.getArticleEdit();
//			articleEdit.removeArticles(articleSelection.getSelectedArticles());
////			articleEdit.getArticleProductTypeClassGroup().removeArticles(articleSelection.getSelectedArticles());
////			for (Iterator it = articleSelection.getSelectedArticles().iterator(); it.hasNext(); ) {
////				Article article = (Article) it.next();
////				articleEdit.getSegmentEdit().getArticleContainerID().removeArticle(article);
////			}
//		} catch (Exception x) {
//			throw new RuntimeException(x);
//		}
////		runDelegated(articleEditAction, articleSelection);
	}

//	/**
//	 * Implement this method and remove the {@link org.nightlabs.jfire.trade.ui.Article}s specified by
//	 * <code>articleSelection</code> from your GUI elements (and wherever you store the <code>Article</code>s elsewhere).
//	 *
//	 * @param articleEditAction The action which triggered the call to this method.
//	 * @param articleSelection The selected {@link org.nightlabs.jfire.trade.ui.Article}s.
//	 */
//	protected abstract void runDelegated(IArticleEditAction articleEditAction, ArticleSelection articleSelection);
}
