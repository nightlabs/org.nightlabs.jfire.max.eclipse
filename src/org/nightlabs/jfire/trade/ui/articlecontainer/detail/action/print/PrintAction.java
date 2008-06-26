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
import java.util.Map;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.reporting.config.ReportLayoutConfigModule;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.reporting.ui.layout.action.print.PrintReportLayoutUtil;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

public class PrintAction extends ArticleContainerReportAction
{

	public boolean calculateVisible()
	{
		return true;
	}

	@Override
	public boolean calculateEnabled()
	{
		return true;
	}

	@Override
	public void run()
	{
		Job printJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.print.PrintAction.printJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) {
				try {
					ArticleContainerID articleContainerID = getArticleContainerActionRegistry().getActiveArticleContainerEditorActionBarContributor()
					.getActiveArticleContainerEditor().getArticleContainerEditorComposite().getArticleContainerID();
					
//					InvoiceID invoiceID = (InvoiceID)articleContainerID;
					
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("articleContainerID", articleContainerID); //$NON-NLS-1$
					
					ReportLayoutConfigModule cfMod = (ReportLayoutConfigModule)ConfigUtil.getUserCfMod(ReportLayoutConfigModule.class, new String[] {FetchPlan.ALL}, 3, new NullProgressMonitor());
					ReportRegistryItemID defLayoutID = cfMod.getDefaultAvailEntry(getReportRegistryItemType());
					if (defLayoutID == null)
						throw new IllegalStateException("No default ReportLayout was set for the category type "+ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_INVOICE); //$NON-NLS-1$
					PrintReportLayoutUtil.printReportLayout(
							defLayoutID,
							params,
							monitor
						);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				return Status.OK_STATUS;
			}
		};
		printJob.schedule();
	}
}
