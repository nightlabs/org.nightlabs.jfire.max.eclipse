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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.addtodeliverynote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.JDOHelper;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.AddToArticleContainerWizard;
import org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;

public class AddToDeliveryNoteWizard extends AddToArticleContainerWizard
{
	private SelectDeliveryNotePage selectDeliveryNotePage;

	public AddToDeliveryNoteWizard(Collection<Article> articles)
	{
		super(articles);
		setWindowTitle(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.addtodeliverynote.AddToDeliveryNoteWizard.windowTitle")); //$NON-NLS-1$
	}
	
	@Override
	public void addPages()
	{
		selectDeliveryNotePage = new SelectDeliveryNotePage();
		addPage(selectDeliveryNotePage);
	}

	private Collection<Article> articlesToAdd = null;

	/**
	 * @return Returns all those {@link Article}s that need to be added to the invoice. This is either the
	 *		articles passed to the constructor or the ones from the articleContainer which are not yet assigned
	 *		to an invoice.
	 */
	protected Collection<Article> getArticlesToAdd()
	{
		if (articlesToAdd != null)
			return articlesToAdd;
		else {
			Collection<Article> articles = getArticles();
			Collection<Article> res = new ArrayList<Article>(articles.size());
			for (Iterator<Article> it = articles.iterator(); it.hasNext(); ) {
				Article article = it.next();
				if (article.getDeliveryNoteID() == null)
					res.add(article);
			}
			this.articlesToAdd = res;
			return res;
		}
	}

	@Override
	public boolean performFinish()
	{
		try {
			// get ArticleIDs from Articles
			List<ArticleID> articleIDs = new LinkedList<ArticleID>();
			for (Iterator<Article> it = getArticlesToAdd().iterator(); it.hasNext(); )
				articleIDs.add((ArticleID)JDOHelper.getObjectId(it.next()));

			// either create a new delivery note or add the articles to an existing.
			DeliveryNoteID deliveryNoteID;
			switch (selectDeliveryNotePage.getAction()) {
				case SelectDeliveryNotePage.ACTION_CREATE:
					// FIXME IDPREFIX (next line) should be asked from user if necessary!
					DeliveryNote deliveryNote = getStoreManager().createDeliveryNote(articleIDs, null, true, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
					deliveryNoteID = (DeliveryNoteID) JDOHelper.getObjectId(deliveryNote);

//					HeaderTreeView headerTreeView = (HeaderTreeView) RCPUtil.findView(HeaderTreeView.ID_VIEW);
//					if (headerTreeView != null) {
//						headerTreeView.getHeaderTreeComposite().addDeliveryNote(deliveryNoteID);
//					}
					break;
				case SelectDeliveryNotePage.ACTION_SELECT:
					deliveryNoteID = selectDeliveryNotePage.getSelectedDeliveryNoteID();
					getStoreManager().addArticlesToDeliveryNote(deliveryNoteID, articleIDs, true, false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
					break;
				default:
					throw new IllegalStateException("selectDeliveryNotePage.getAction() returned unknown action!"); //$NON-NLS-1$
			}

			HeaderTreeComposite.openEditor(new ArticleContainerEditorInput(deliveryNoteID));
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
