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

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.dao.ArticleDAO;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEdit;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.GenericArticleEditAction;
import org.nightlabs.jfire.trade.ui.transfer.TransferUtil;
import org.nightlabs.progress.NullProgressMonitor;

public class AddToDeliveryNoteAction
extends GenericArticleEditAction
{
	@Override
	public boolean calculateVisible()
	{
		ArticleContainerEdit edit = getArticleContainerEdit();
		return edit != null &&
				!(edit.getArticleContainerID() instanceof DeliveryNoteID);
	}
	
	@Override
	protected boolean excludeArticle(Article article)
	{
		// Exclude if the article is already in a delivery note
		if (!TransferUtil.canAddToDeliveryNote(article))
			return true;
		
		// Exclude if the article is a reversing article and the corresponding reversed article is not in a delivery note
		if (article.isReversing() && article.getReversedArticleID() != null) {
			Article reversedArticle = ArticleDAO.sharedInstance().getArticle(
					article.getReversedArticleID(), 
					new String[] { FetchPlan.DEFAULT, Article.FETCH_GROUP_DELIVERY_NOTE_ID }, 
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new NullProgressMonitor());
			if (reversedArticle.getDeliveryNoteID() == null)
				return true;
		}
		
		return false;
	}

	@Override
	public void run()
	{
		AddToDeliveryNoteWizard addToDeliveryNoteWizard = new AddToDeliveryNoteWizard(getArticles());
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(addToDeliveryNoteWizard);
		dialog.open();
	}
}
