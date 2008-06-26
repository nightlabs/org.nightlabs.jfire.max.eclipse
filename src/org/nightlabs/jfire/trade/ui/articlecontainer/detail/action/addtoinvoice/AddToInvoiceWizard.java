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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.addtoinvoice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.JDOHelper;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.AddToArticleContainerWizard;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice.ArticleContainerEditorInputInvoice;
import org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;

public class AddToInvoiceWizard extends AddToArticleContainerWizard
{
	private SelectInvoicePage selectInvoicePage;

//	/**
//	 * @see AddToArticleContainerWizard#AddToArticleContainerWizard(ArticleContainer)
//	 */
//	public AddToInvoiceWizard(ArticleContainer articleContainer)
//	{
//		super(articleContainer);
//	}

	/**
	 * @see AddToArticleContainerWizard#AddToArticleContainerWizard(Collection)
	 */
	public AddToInvoiceWizard(Collection<Article> articles)
	{
		super(articles);
		for (Iterator<Article> it = articles.iterator(); it.hasNext(); ) {
			Article article = it.next();
			if (article.getInvoiceID() != null)
				throw new IllegalArgumentException("At least one Article (" + article.getPrimaryKey() + ") is already in an invoice! An Article can only be in one invoice!"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		setWindowTitle(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.addtoinvoice.AddToInvoiceWizard.windowTitle")); //$NON-NLS-1$
	}

	@Override
	public void addPages()
	{
		selectInvoicePage = new SelectInvoicePage();
		addPage(selectInvoicePage);
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
				if (article.getInvoiceID() == null)
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
			List<ArticleID> articleIDs = new LinkedList<ArticleID>();
			for (Iterator<Article> it = getArticlesToAdd().iterator(); it.hasNext(); )
				articleIDs.add((ArticleID)JDOHelper.getObjectId(it.next()));

			InvoiceID invoiceID;
			switch (selectInvoicePage.getAction()) {
				case SelectInvoicePage.ACTION_CREATE:
					// FIXME IDPREFIX (next line) should be asked from user if necessary!
					Invoice invoice = getAccountingManager().createInvoice(articleIDs, null, true, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
					invoiceID = (InvoiceID) JDOHelper.getObjectId(invoice);

//					HeaderTreeView headerTreeView = (HeaderTreeView) RCPUtil.findView(HeaderTreeView.ID_VIEW);
//					if (headerTreeView != null) {
//						headerTreeView.getHeaderTreeComposite().addInvoice(invoiceID);
//					}
					break;
				case SelectInvoicePage.ACTION_SELECT:
					invoiceID = selectInvoicePage.getSelectedInvoiceID();
					getAccountingManager().addArticlesToInvoice(invoiceID, articleIDs, true, false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
					break;
				default:
					throw new IllegalStateException("selectInvoicePage.getAction() returned unknown action!"); //$NON-NLS-1$
			}
			HeaderTreeComposite.openEditor(new ArticleContainerEditorInputInvoice(invoiceID));
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
