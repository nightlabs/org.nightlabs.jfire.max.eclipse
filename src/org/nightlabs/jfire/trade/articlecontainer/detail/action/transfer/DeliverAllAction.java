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

package org.nightlabs.jfire.trade.articlecontainer.detail.action.transfer;

import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.articlecontainer.detail.action.ArticleContainerAction;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.transfer.wizard.AbstractCombiTransferWizard;
import org.nightlabs.jfire.trade.transfer.wizard.CombiTransferArticleContainerWizard;
import org.nightlabs.jfire.trade.transfer.wizard.TransferWizard;

public class DeliverAllAction extends ArticleContainerAction
{

	public DeliverAllAction()
	{
	}

	public boolean calculateVisible()
	{
		return true;
	}
	
	@Override
	protected boolean excludeArticle(Article article) {
		ArticleContainerID articleContainerID = getArticleContainerActionRegistry().getActiveGeneralEditorActionBarContributor()
			.getActiveGeneralEditor().getGeneralEditorComposite().getArticleContainerID();
		if (articleContainerID instanceof DeliveryNoteID)
			return article.getArticleLocal().isDelivered();

		return article.getDeliveryNoteID() != null;
	}

//	@Override
//	public boolean calculateEnabled() {
//		if (super.calculateEnabled()) {
//			boolean allDelivered = true;
//			for (Article article : getArticles()) {
//				allDelivered &= article.getDeliveryNoteID() != null;
//			}
//			return !allDelivered;
//		}
//		return false;		
//	}

	public void run()
	{
		ArticleContainerID articleContainerID = getArticleContainerActionRegistry().getActiveGeneralEditorActionBarContributor()
		.getActiveGeneralEditor().getGeneralEditorComposite().getArticleContainerID();

		CombiTransferArticleContainerWizard wizard = new CombiTransferArticleContainerWizard(
				articleContainerID,
				AbstractCombiTransferWizard.TRANSFER_MODE_DELIVERY,
				TransferWizard.Side.Vendor); // TODO the side must be calculated correctly! It's not always "vendor"!

		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		dialog.open();
	}
}
