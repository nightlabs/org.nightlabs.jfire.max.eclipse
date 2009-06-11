package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.search.JDOQuerySearchEntryViewer;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueTable;
import org.nightlabs.progress.ProgressMonitor;

/**
 *
 * @author Chairat Kongarayawetchakun
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class IssueEntryListViewer
	extends JDOQuerySearchEntryViewer<Issue, IssueQuery>
{
	public IssueEntryListViewer(Entry entry) {
		super(entry);
	}

	private IssueTable issueTable;

	@Override
	public AbstractTableComposite<Issue> createListComposite(Composite parent) {
//		TODO we should pass the QueryMap obtained via this.getQueryMap() to the IssueTable so that it can filter new Issues agains it.
		issueTable = new IssueTable(parent, SWT.NONE);
		return issueTable;
	}

	@Override
	protected void addResultTableListeners(AbstractTableComposite<Issue> tableComposite) {
		super.addResultTableListeners(tableComposite);
	}

	public IssueTable getIssueTable() {
		return issueTable;
	}

	@Override
	protected Collection<Issue> doSearch(QueryCollection<? extends IssueQuery> queryMap,
		ProgressMonitor monitor)
	{
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