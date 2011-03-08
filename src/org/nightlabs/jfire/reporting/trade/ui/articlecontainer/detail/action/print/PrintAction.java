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

package org.nightlabs.jfire.reporting.trade.ui.articlecontainer.detail.action.print;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.reporting.config.ReportLayoutConfigModule;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.layout.render.RenderReportRequest;
import org.nightlabs.jfire.reporting.trade.ui.resource.Messages;
import org.nightlabs.jfire.reporting.ui.layout.ReportRegistryItemListDialog;
import org.nightlabs.jfire.reporting.ui.layout.action.print.PrintReportLayoutUtil;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

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
		Job printJob = new Job(Messages.getString("org.nightlabs.jfire.reporting.trade.ui.articlecontainer.detail.action.print.PrintAction.jobName")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) {
				monitor.beginTask(Messages.getString("org.nightlabs.jfire.reporting.trade.ui.articlecontainer.detail.action.print.PrintAction.taskName"), 6); //$NON-NLS-1$
				try {
					final ArticleContainerID articleContainerID = getArticleContainerID();
//					InvoiceID invoiceID = (InvoiceID)articleContainerID;

					final Map<String, Object> params = new HashMap<String, Object>();
					params.put("articleContainerID", articleContainerID); //$NON-NLS-1$

					final ReportLayoutConfigModule cfMod = ConfigUtil.getUserCfMod(ReportLayoutConfigModule.class, new String[] {FetchPlan.ALL}, 3, new NullProgressMonitor());
					final ReportRegistryItemID defLayoutID = cfMod.getDefaultAvailEntry(getReportRegistryItemType());
					if (defLayoutID == null) {
//						throw new IllegalStateException("No default ReportLayout was set for the category type "+ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_INVOICE); //$NON-NLS-1$
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								String type = getReportRegistryItemType();
								Collection<ReportRegistryItemID> itemIDs = cfMod.getAvailEntries(type);
								final ReportRegistryItemListDialog dialog = new ReportRegistryItemListDialog(null, itemIDs);
								int returnType = dialog.open();
								if (returnType == Window.OK) {
									Job job = new Job(Messages.getString("Loading Layout...")) {
										@Override
										protected IStatus run(ProgressMonitor monitor) throws Exception {
											ReportRegistryItemID itemID = dialog.getSelectedReportRegistryItem();
											RenderReportRequest renderReportRequest = new RenderReportRequest(itemID, params);
											
											Locale locale = ArticleContainerReportActionHelper.getArticleContainerReportLocale(
													articleContainerID, defLayoutID, params,
													new SubProgressMonitor(monitor, 2));
											
											if (locale == null)
												locale = Locale.getDefault();
											renderReportRequest.setLocale(locale);
											
											PrintReportLayoutUtil.printReportLayout(
													renderReportRequest,
													new SubProgressMonitor(monitor, 4)
												);
											return Status.OK_STATUS;
										}
									};
									job.setPriority(Job.LONG);
									job.schedule();
								}
							}
						});
					}
					
				} catch (Exception e) {
					throw new RuntimeException(e);
				} finally {
					monitor.done();
				}
				return Status.OK_STATUS;
			}
		};
		printJob.schedule();
	}
}
