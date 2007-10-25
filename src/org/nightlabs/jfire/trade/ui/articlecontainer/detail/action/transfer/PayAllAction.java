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

import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleContainerAction;
import org.nightlabs.jfire.trade.ui.transfer.wizard.AbstractCombiTransferWizard;
import org.nightlabs.jfire.trade.ui.transfer.wizard.CombiTransferArticleContainerWizard;
import org.nightlabs.jfire.trade.ui.transfer.wizard.TransferWizard;

public class PayAllAction extends ArticleContainerAction
{
	public boolean calculateVisible()
	{
		return true;
	}

	@Override
	protected boolean excludeArticle(Article article) {
		ArticleContainerID articleContainerID = getArticleContainerActionRegistry().getActiveGeneralEditorActionBarContributor()
			.getActiveGeneralEditor().getGeneralEditorComposite().getArticleContainerID();
		if (articleContainerID instanceof InvoiceID)
			return false;

		return article.getInvoiceID() != null;
	}

	@Override
	public boolean calculateEnabled() {
		if (!super.calculateEnabled())
			return false;

		ArticleContainer articleContainer = getArticleContainerActionRegistry().getActiveGeneralEditorActionBarContributor()
			.getActiveGeneralEditor().getGeneralEditorComposite().getArticleContainer();

		if (!(articleContainer instanceof Invoice))
			return true;

		return ((Invoice)articleContainer).getInvoiceLocal().getAmountToPay() != 0;
	}

//	@Override
//	public boolean calculateEnabled() {
//		if (super.calculateEnabled()) {
//			boolean allPaid = true;
//			for (Article article : getArticles()) {
//				allPaid &= article.getInvoiceID() != null;
//			}
//			return !allPaid;
//		}
//		return false;		
//	}

	@Override
	public void run()
	{
		ArticleContainerID articleContainerID = getArticleContainerActionRegistry().getActiveGeneralEditorActionBarContributor()
		.getActiveGeneralEditor().getGeneralEditorComposite().getArticleContainerID();

		CombiTransferArticleContainerWizard wizard = new CombiTransferArticleContainerWizard(
				articleContainerID,
				AbstractCombiTransferWizard.TRANSFER_MODE_PAYMENT,
				TransferWizard.Side.Vendor); // TODO the side must be calculated correctly! It's not always "vendor"!

		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		dialog.open();
	}
}
