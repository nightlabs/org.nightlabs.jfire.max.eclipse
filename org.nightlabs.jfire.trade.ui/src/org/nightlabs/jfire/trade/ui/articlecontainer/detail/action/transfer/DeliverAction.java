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

import java.util.Set;

import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEdit;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.GenericArticleEditAction;
import org.nightlabs.jfire.trade.ui.transfer.TransferUtil;
import org.nightlabs.jfire.trade.ui.transfer.wizard.AbstractCombiTransferWizard;
import org.nightlabs.jfire.trade.ui.transfer.wizard.CombiTransferArticlesWizard;

public class DeliverAction
extends GenericArticleEditAction
{
	@Override
	public boolean calculateVisible()
	{
		ArticleContainerEdit edit = getArticleContainerEdit();
		
		// a deliveryNote can only be delivered as a whole with deliverAll - this is a restriction of the GUI
		// (the backend is more flexible), but IMHO this restriction makes understanding easier and prevents that
		// a customer receives a DeliveryNote with things he didn't receive (yet).
		return edit != null && !(edit.getArticleContainerID() instanceof DeliveryNoteID);
	}

	@Override
	protected boolean excludeArticle(Article article)
	{
		return !TransferUtil.canAddToDeliveryNote(article);
	}

	@Override
	public void run()
	{
		Set<ArticleID> articleIDs = NLJDOHelper.getObjectIDSet(getArticles());
		CombiTransferArticlesWizard wizard = new CombiTransferArticlesWizard(
				articleIDs,
				AbstractCombiTransferWizard.TRANSFER_MODE_DELIVERY);
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		dialog.open();
	}
}
