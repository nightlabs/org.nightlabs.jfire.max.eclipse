package org.nightlabs.jfire.issuetracking.dashboard.ui.internal;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.base.dashboard.ui.AbstractDashboardGadget;
import org.nightlabs.jfire.base.dashboard.ui.action.DashboardTableActionManager;
import org.nightlabs.jfire.dashboard.DashboardGadgetLayoutEntry;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.dashboard.IssueDashboardGadgetConfig;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueTable;
import org.nightlabs.jfire.query.store.BaseQueryStore;
import org.nightlabs.jfire.query.store.QueryStore;
import org.nightlabs.jfire.query.store.dao.QueryStoreDAO;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class IssueDashboardGadget extends AbstractDashboardGadget 
{
	private IssueTable issueTable;
	private DashboardTableActionManager<Issue> actionManager;

	public IssueDashboardGadget() {}

	@Override
	public Composite createControl(Composite parent) {
		XComposite invoiceGadget = createDefaultWrapper(parent);
		issueTable = new IssueTable(invoiceGadget, SWT.NONE);
		actionManager = new DashboardTableActionManager<Issue>(issueTable);
		return invoiceGadget;
	}
	
	@Override
	public void refresh() {
		getGadgetContainer().setTitle(getGadgetContainer().getLayoutEntry().getName());
		Job refreshJob = new RefreshGadgetJob("Loading Issues");
		refreshJob.schedule();	
	}
	
	class RefreshGadgetJob extends Job {
		
		private RefreshGadgetJob(String name) {
			super(name);
		}

		@Override
		protected IStatus run(ProgressMonitor monitor) {
			monitor.beginTask("Loading Issues", 100);
			try {
				displayLoadingMessage();
				DashboardGadgetLayoutEntry<?> layoutEntry = getGadgetContainer().getLayoutEntry();
				IssueDashboardGadgetConfig config = getConfig(layoutEntry);
				QueryCollection<? extends IssueQuery> queryCollection = getConfiguredQueryCollection(config, new SubProgressMonitor(monitor, 50));
				final Collection<Issue> issues = getIssuesForQueryCollection(queryCollection, new SubProgressMonitor(monitor, 50));
				if (!issueTable.getDisplay().isDisposed()) {
					issueTable.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							issueTable.setInput(issues);			
						}
					});					
				}
			} finally {
				monitor.done();
			}
			return Status.OK_STATUS;
		}

		private IssueDashboardGadgetConfig getConfig(
				DashboardGadgetLayoutEntry<?> layoutEntry) {
			return (IssueDashboardGadgetConfig) (layoutEntry.getConfig() != null ? layoutEntry.getConfig() : new IssueDashboardGadgetConfig());
		}

		@SuppressWarnings("unchecked")
		private QueryCollection<? extends IssueQuery> getConfiguredQueryCollection(IssueDashboardGadgetConfig config, ProgressMonitor monitor) {
			
			monitor.beginTask("Loading Issues", 1);
			try {
				QueryCollection<IssueQuery> queryCollection = null;
				if (config.getIssueQueryItemId() != null) {
					QueryStore queryStore = QueryStoreDAO.sharedInstance().getQueryStore(
									config.getIssueQueryItemId(),
									new String[] { BaseQueryStore.FETCH_GROUP_SERIALISED_QUERIES },
									NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
									monitor);
					queryCollection = (QueryCollection<IssueQuery>) queryStore.getQueryCollection();
				} else {
					queryCollection = new QueryCollection(Issue.class);
					queryCollection.add(new IssueQuery());
				}
				
				queryCollection.setFromInclude(0);
				queryCollection.setToExclude(config.getAmountOfIssues());
				
				return queryCollection;

			} finally {
				monitor.done();
			}
		}
		
		private Collection<Issue> getIssuesForQueryCollection(
				QueryCollection<? extends IssueQuery> queryCollection,
				ProgressMonitor monitor) {
			Collection<Issue> issues = IssueDAO.sharedInstance().getIssuesForQueries(
					queryCollection, 
					issueTable.getIssueTableFetchGroups(), 
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
					new SubProgressMonitor(monitor, 50));
			List<Issue> invoiceList = new LinkedList<Issue>(issues);
			monitor.done();
			return invoiceList;
		}

		private void displayLoadingMessage() {
			issueTable.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					issueTable.setLoadingMessage("Loading Issues");
				}
			});
		}
		
	}
}
