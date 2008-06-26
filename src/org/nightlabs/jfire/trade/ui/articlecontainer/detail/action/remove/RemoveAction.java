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

import java.util.HashSet;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.AccountingManager;
import org.nightlabs.jfire.accounting.AccountingManagerUtil;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.store.StoreManager;
import org.nightlabs.jfire.store.StoreManagerUtil;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.articlecontainer.ArticleUtil;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleEdit;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleSelection;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEdit;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleEditAction;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.IArticleEditActionDelegate;
import org.nightlabs.progress.NullProgressMonitor;

public class RemoveAction extends ArticleEditAction
{
	@Override
	public boolean calculateVisible()
	{
//		return super.calculateVisible();
		return true;
	}

	@Override
	public boolean calculateEnabled(Set<ArticleSelection> articleSelections)
	{
		// The RemoveAction calculates enabled, already:
		//  * Removal from Offer/Order (=deletion of Article) is NOT possible, if
		//    - Article is NOT reversing (i.e. "normal") and it is currently allocated/allocationPending;
		//    - Offer is finalized.
		//  * Removal from Invoice is NOT possible, if
		//    - Invoice is finalized.
		//  * Removal from DeliveryNote is NOT possible, if
		//    - DeliveryNote is finalized.

		for (ArticleSelection articleSelection : articleSelections) {
			SegmentEdit segmentEdit = articleSelection.getArticleEdit().getSegmentEdit();
			String articleContainerClass = segmentEdit.getArticleContainerClass();

			for (Article article : articleSelection.getSelectedArticles()) {

				// Deletion is possible for allocated articles too since it 
				// releases allocated articles before.
				
//				if (Offer.class.getName().equals(articleContainerClass) ||
//						Order.class.getName().equals(articleContainerClass)) {
//					// removal here means deletion => must NOT be allocated/allocationPending
//					if (!article.isReversing()) {
//						if (article.isAllocated() || article.isAllocationPending())
//							return false;
//					}
//				}

				if (Offer.class.getName().equals(articleContainerClass)) {
					if (((Offer)segmentEdit.getArticleContainer()).isFinalized())
						return false;
				}
				else if (Order.class.getName().equals(articleContainerClass)) {
					if (ArticleUtil.isOfferFinalized(article, new NullProgressMonitor())) // TODO real progress monitor
						return false;
				}
				else if (Invoice.class.getName().equals(articleContainerClass)) {
					if (((Invoice)segmentEdit.getArticleContainer()).isFinalized())
						return false;
				}
				else if (DeliveryNote.class.getName().equals(articleContainerClass)) {
					if (((DeliveryNote)segmentEdit.getArticleContainer()).isFinalized())
						return false;
				}
				else
					throw new IllegalStateException("Unknown articleContainerClass: " + articleContainerClass); //$NON-NLS-1$

			}
		}

		return true;
//		return super.calculateEnabled(articleSelections);
	}

	@Override
	public void run()
	{
		// We remove the lines from the server - therefore find out first, what lines shall be handled here.
		SegmentEdit segmentEdit = getArticleEditActionRegistry().getActiveArticleContainerEditorActionBarContributor().getActiveSegmentEdit();
		Set<? extends ArticleSelection> articleSelections = segmentEdit.getArticleSelections();
		Set<ArticleID> articleIDs = new HashSet<ArticleID>();
		for (ArticleSelection articleSelection : articleSelections) {
			ArticleEdit articleEdit = articleSelection.getArticleEdit();

			IArticleEditActionDelegate delegate = articleEdit.getArticleEditFactory().getArticleEditActionDelegate(this.getId());
			if (delegate != null && !(delegate instanceof RemoveActionDelegate))
				throw new ClassCastException("The delegate of type \"" + delegate.getClass().getName()+"\" for articleEditActionID \"" + delegate.getArticleEditActionID() + "\" does not extend class \"" + RemoveActionDelegate.class.getName() + "\"!!!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

			RemoveActionDelegate removeActionDelegate = (RemoveActionDelegate) delegate;

			if (removeActionDelegate == null || !removeActionDelegate.isDelegateHandlingRemoteWork()) {
				for (Article article : articleSelection.getSelectedArticles()) {
					articleIDs.add((ArticleID) JDOHelper.getObjectId(article));
				}
			}
		}

		// Remove all articles on the server that are not handled later by the delegate.
		if (!articleIDs.isEmpty()) {
			try {
//				ArticleContainer articleContainer = segmentEdit.getArticleContainerID();
//				ArticleContainerID articleContainerID = (ArticleContainerID) JDOHelper.getObjectId(articleContainer);
				ArticleContainerID articleContainerID = segmentEdit.getArticleContainerID();

				if (articleContainerID instanceof OfferID || articleContainerID instanceof OrderID) {
					TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
					tradeManager.deleteArticles(articleIDs, true);
				}
				else if (articleContainerID instanceof InvoiceID) {
					AccountingManager accountingManager = AccountingManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
					accountingManager.removeArticlesFromInvoice((InvoiceID) articleContainerID, articleIDs, true, false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
				}
				else if (articleContainerID instanceof DeliveryNoteID) {
					StoreManager storeManager = StoreManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
					storeManager.removeArticlesFromDeliveryNote((DeliveryNoteID) articleContainerID, articleIDs, true, false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
				}
				else
					throw new IllegalStateException("ArticleContainerID is neither an instance of OfferID nor OrderID nor InvoiceID nor DeliveryNoteID! Unknown type: " + articleContainerID.getClass().getName()); //$NON-NLS-1$
			} catch (RuntimeException x) {
				throw x;
			} catch (Exception x) {
				throw new RuntimeException(x);
			}
		}

//		// Remove all selected articles from the local ArticleProductTypeClassGroup-s.
//		for (Iterator itS = articleSelections.iterator(); itS.hasNext(); ) {
//			ArticleSelection articleSelection = (ArticleSelection) itS.next();
//			ArticleEdit articleEdit = articleSelection.getArticleEdit();
//			RemoveActionDelegate removeActionDelegate = (RemoveActionDelegate) articleEdit.getArticleEditFactory().getArticleEditActionDelegate(this.getId());
//			removeActionDelegate.removeArticlesFromArticleProductTypeClassGroup(articleEdit, articleSelection.getSelectedArticles());
//		}

		// super.run() delegates to the ArticleContainerEditorActionBarContributor which calls the run method in
		// the ArticleEditActionDelegates. They'll remove the lines in the GUI.
		super.run();
	}
}
