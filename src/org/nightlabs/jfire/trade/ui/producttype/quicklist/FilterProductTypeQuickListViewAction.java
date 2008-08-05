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

package org.nightlabs.jfire.trade.ui.producttype.quicklist;

import java.util.SortedSet;

import javax.jdo.FetchPlan;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.AbstractSearchQuery;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.overview.search.QueryFilterFactory;
import org.nightlabs.jfire.base.ui.overview.search.QueryFilterFactoryRegistry;
import org.nightlabs.jfire.base.ui.search.QueryFilterDialog;
import org.nightlabs.jfire.query.store.BaseQueryStore;
import org.nightlabs.jfire.query.store.dao.QueryStoreDAO;
import org.nightlabs.jfire.security.id.UserID;
import org.nightlabs.jfire.store.search.VendorDependentQuery;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Daniel Mazurek <daniel[AT]nightlabs[DOT]de>
 */
public class FilterProductTypeQuickListViewAction
implements IViewActionDelegate
{
	private static final Logger logger = Logger.getLogger(FilterProductTypeQuickListViewAction.class);

	private static String[] FETCH_GROUPS_QUERY_STORE_LOAD = new String[] {
		FetchPlan.DEFAULT,
		BaseQueryStore.FETCH_GROUP_NAME,
		BaseQueryStore.FETCH_GROUP_DESCRIPTION
	};

	private static String[] FETCH_GROUPS_QUERY_STORE_SAVE = new String[] {
		FetchPlan.DEFAULT,
		BaseQueryStore.FETCH_GROUP_NAME,
		BaseQueryStore.FETCH_GROUP_DESCRIPTION
	};

	private ProductTypeQuickListView view;

	/**
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		this.view = (ProductTypeQuickListView)view;
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		final IProductTypeQuickListFilter selectedFilter = view.getSelectedFilter();
		if (isFactoriesRegistered(selectedFilter))
		{
			Job loadQuery = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.producttype.quicklist.FilterProductTypeQuickListViewAction.job.openQueryDialog")) {			 //$NON-NLS-1$
				@Override
				protected IStatus run(ProgressMonitor monitor) throws Exception {
					final QueryCollection<VendorDependentQuery> queryCollection = selectedFilter.getQueryCollection(
							monitor);
					view.getViewSite().getShell().getDisplay().asyncExec(new Runnable(){
						@Override
						public void run() {
							final QueryFilterDialog dialog = new QueryFilterDialog(view.getSite().getShell(),
									getScope(), queryCollection);
							int returnCode = dialog.open();
							if (returnCode == Window.OK)
							{
								Job searchJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.producttype.quicklist.FilterProductTypeQuickListViewAction.job.search")) {				 //$NON-NLS-1$
									@Override
									protected IStatus run(ProgressMonitor monitor) throws Exception
									{
										QueryCollection<? extends AbstractSearchQuery> queryCollection = dialog.getQueryCollection();
										selectedFilter.setQueryCollection((QueryCollection<VendorDependentQuery>) queryCollection);
										selectedFilter.search(monitor, true);
										return Status.OK_STATUS;
									}
								};
								searchJob.schedule();

								Job saveJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.producttype.quicklist.FilterProductTypeQuickListViewAction.job.saveLastChanges")) { //$NON-NLS-1$
									@Override
									protected IStatus run(ProgressMonitor monitor) throws Exception
									{
										monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.ui.producttype.quicklist.FilterProductTypeQuickListViewAction.job.saveLastChanges"), 100); //$NON-NLS-1$
										UserID userID = Login.sharedInstance().getUserObjectID();
										BaseQueryStore defaultQueryStore = QueryStoreDAO.sharedInstance().getDefaultQueryStore(
												queryCollection.getResultClass(), userID,
												FETCH_GROUPS_QUERY_STORE_LOAD,
												NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
												new SubProgressMonitor(monitor, 50));
										defaultQueryStore.setQueryCollection(queryCollection);
										QueryStoreDAO.sharedInstance().storeQueryStore(
												defaultQueryStore,
												FETCH_GROUPS_QUERY_STORE_SAVE,
												NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
												false,
												new SubProgressMonitor(monitor, 50));
										monitor.done();
										return Status.OK_STATUS;
									}
								};
								saveJob.schedule();
							}
						}
					});
					monitor.done();
					return Status.OK_STATUS;
				}
			};
			loadQuery.schedule();
		}
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
	}

	protected String getScope() {
		return "global"; //$NON-NLS-1$
	}

	protected boolean isFactoriesRegistered(IProductTypeQuickListFilter selectedFilter)
	{
		if (selectedFilter != null) {
			SortedSet<QueryFilterFactory> factories = QueryFilterFactoryRegistry.sharedInstance().getQueryFilterCompositesFor(
					getScope(), selectedFilter.getQueryResultClass());
			return factories != null;
		}
		return false;
	}
}
