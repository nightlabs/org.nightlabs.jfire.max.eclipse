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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.print;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.ui.config.ReportConfigUtil;
import org.nightlabs.jfire.reporting.ui.layout.action.view.AbstractViewReportLayoutAction;
import org.nightlabs.jfire.trade.id.ArticleContainerID;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[ÐOT]de>
 *
 */
public class PrintPreviewAction extends ArticleContainerReportAction
{

	public boolean calculateVisible()
	{
//		return (getArticleContainerActionRegistry().getActiveArticleContainerEditorActionBarContributor()
//				.getActiveArticleContainerEditor().getArticleContainerEditorComposite().getArticleContainerID() instanceof InvoiceID);
		return true;
	}

	@Override
	public boolean calculateEnabled()
	{
		return true;
	}

	protected AbstractViewReportLayoutAction showReportAction = new AbstractViewReportLayoutAction() {
		@Override
		protected String getReportUseCaseID() {
			// Use null to force lookup by reportLayoutType
			return null;
		}
	};
	
	@Override
	public void run()
	{
		ArticleContainerID articleContainerID = getArticleContainerID();
		
		Map <String, Object> params = new HashMap<String,Object>();
//		params.put("invoiceOrganisationID", invoiceID.organisationID);
//		params.put("invoiceIDPrefix", invoiceID.invoiceIDPrefix);
//		params.put("invoiceInvoiceID", new BigDecimal(invoiceID.invoiceID));
//		params.put("languageID", NLLocale.getDefault().getLanguage());
		params.put("articleContainerID", articleContainerID); //$NON-NLS-1$
//		EditorReportViewer viewer = new EditorReportViewer();
		
		ReportRegistryItemID selectedItemID = ReportConfigUtil.getReportLayoutID(getReportRegistryItemType());
		if (selectedItemID == null) {
			// the user canceled, abort
			return;
		}

		Set<ReportRegistryItemID> itemIDs = new HashSet<ReportRegistryItemID>();
		itemIDs.add(selectedItemID);
		showReportAction.setNextRunParams(params);
		showReportAction.runWithRegistryItemIDs(itemIDs);
	}
}
