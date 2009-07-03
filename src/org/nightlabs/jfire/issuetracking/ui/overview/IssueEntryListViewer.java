package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.search.JDOQuerySearchEntryViewer;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueTable;
import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 *
 * @author Chairat Kongarayawetchakun
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class IssueEntryListViewer extends JDOQuerySearchEntryViewer<Issue, IssueQuery> {
	private QueryCollection<? extends IssueQuery> previousSavedQuery;


	public IssueEntryListViewer(Entry entry) {
		super(entry);
	}

	private IssueTable issueTable;

	@Override
	public AbstractTableComposite<Issue> createListComposite(Composite parent) {
//		TODO we should pass the QueryMap obtained via this.getQueryMap() to the IssueTable so that it can filter new Issues agains it.
		issueTable = new IssueTable(parent, SWT.NONE);

		// [Observation; 02.07.2009]
		// It seems a good idea to have a NotificationListener here, to note whether an
		// Issue has been deleted, so that we can remove it from the list of table.
		// We dont have to make this 'completely' active, since its showing the results of the current search... right??
		// --> Let's give this a go first, and then decide later on. Kai.
		previousSavedQuery = null;
		JDOLifecycleManager.sharedInstance().addNotificationListener(Issue.class, issueChangeNotificationListener);
		issueTable.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				JDOLifecycleManager.sharedInstance().removeNotificationListener(Issue.class, issueChangeNotificationListener);
			}
		});


		return issueTable;
	}

	/**
	 * An implicit listener to handle the refreshing of the table's entry (or entries), in the case
	 * of a currently displayed Issue changed its fields or was deleted.
	 */
	private NotificationListener issueChangeNotificationListener = new NotificationAdapterJob() {
		@Override
		public void notify(final NotificationEvent event) {
			final ProgressMonitor monitor = getProgressMonitor();
			monitor.beginTask("Updating issues...", 100);
			try {
				if (previousSavedQuery != null && !getComposite().isDisposed()) {
					getComposite().getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							Collection<Issue> issues = issueTable.getElements();
							if (issues.isEmpty())	return;

							Collection<IssueID> issueIDs = NLJDOHelper.getObjectIDList(issues);
							for (Object obj : event.getSubjects()) {
								if (obj == null) continue;

								// In both DIRTY and DELETED cases, where the dirtyObjectID matches any of our table contents,
								// we want to refresh it again -- making sure that the refreshed contents adhere to the
								// constraints of the previous query. Kai.
								DirtyObjectID dirtyObjectID = (DirtyObjectID) obj;
								if ( issueIDs.contains(dirtyObjectID.getObjectID()) ) {
									issues = doSearch(previousSavedQuery, new SubProgressMonitor(monitor, 90));
									issueTable.setInput(issues);
									break;
								}
							}
						}
					});
				}

			} finally {
				monitor.done();
			}

		}
	};



	@Override
	protected void addResultTableListeners(AbstractTableComposite<Issue> tableComposite) {
		super.addResultTableListeners(tableComposite);
	}

	public IssueTable getIssueTable() {
		return issueTable;
	}

	@Override
	protected Collection<Issue> doSearch
	(QueryCollection<? extends IssueQuery> queryMap, ProgressMonitor monitor) {
		// We save the previous query; used later in the notification listener, in case we need
		// to refresh the table entries, given the query constraints.
		previousSavedQuery = queryMap;

		return IssueDAO.sharedInstance().getIssuesForQueries(
			queryMap,
			IssueTable.FETCH_GROUPS_ISSUE,
			NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
			monitor);
	}

	@Override
	public Class<Issue> getTargetType()
	{
		return Issue.class;
	}

	/**
	 * The ID for the Quick search registry.
	 */
	public static final String QUICK_SEARCH_REGISTRY_ID = IssueEntryListViewer.class.getName();

	@Override
	protected String getQuickSearchRegistryID()
	{
		return QUICK_SEARCH_REGISTRY_ID;
	}
}