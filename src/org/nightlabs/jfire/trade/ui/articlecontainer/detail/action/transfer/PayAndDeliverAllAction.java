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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.transfer;

import java.util.List;

import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleContainerAction;
import org.nightlabs.jfire.trade.ui.transfer.TransferUtil;
import org.nightlabs.jfire.trade.ui.transfer.wizard.AbstractCombiTransferWizard;
import org.nightlabs.jfire.trade.ui.transfer.wizard.CombiTransferArticleContainerWizard;

public class PayAndDeliverAllAction extends ArticleContainerAction
{

	public boolean calculateVisible()
	{
		return true;
	}
	
	@Override
	protected boolean excludeArticle(Article article) {
//		return article.getDeliveryNoteID() != null && article.getInvoiceID() != null;
		return ! (TransferUtil.canAddToDeliveryNote(article) && TransferUtil.canAddToInvoice(article));
	}

	@Override
	public boolean calculateEnabled() {
		super.calculateEnabled(); // makes articles available via getArticles()
		
		boolean allPaid = true, allDelivered = true;
		List<Article> articles = getArticles();
		if (articles == null)
			return false;
		
		for (Article article : articles) {
			allDelivered &= article.getDeliveryNoteID() != null;
			allPaid &= article.getInvoiceID() != null;
		}
		return !allPaid && !allDelivered;
	}

	@Override
	public void run()
	{
		ArticleContainerID articleContainerID = getArticleContainerActionRegistry().getActiveArticleContainerEditorActionBarContributor()
			.getActiveArticleContainerEditor().getGeneralEditorComposite().getArticleContainerID();

		CombiTransferArticleContainerWizard wizard = new CombiTransferArticleContainerWizard(
				articleContainerID,
				AbstractCombiTransferWizard.TRANSFER_MODE_BOTH);

		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		dialog.open();
	}

}
