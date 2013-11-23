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

import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEdit;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleContainerAction;
import org.nightlabs.jfire.trade.ui.transfer.TransferUtil;

public class AddAllToDeliveryNoteAction extends ArticleContainerAction
{
	public boolean calculateVisible()
	{
		ArticleContainerEdit edit = getArticleContainerEdit();
		
		return edit != null && !(edit.getArticleContainerID() instanceof DeliveryNote);
	}

	@Override
	public boolean calculateEnabled() {
		if (super.calculateEnabled()) {
			boolean allDelivered = true;
			for (Article article : getArticles()) {
				allDelivered &= article.getDeliveryNoteID() != null;
			}
			return !allDelivered;
		}
		return false;
	}

	@Override
	protected boolean excludeArticle(Article article)
	{
		return !TransferUtil.canAddToDeliveryNote(article);
		
//		if (article.getDeliveryNoteID() != null)
//			return true;
//
//		return false;
	}

	@Override
	public void run()
	{
		AddToDeliveryNoteWizard addToDeliveryNoteWizard = new AddToDeliveryNoteWizard(getArticleContainerEdit().getArticles());

		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(addToDeliveryNoteWizard);
		dialog.open();
	}
}
