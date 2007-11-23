/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.query.JDOQuery;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.overview.search.JDOQuerySearchEntryViewer;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueTable;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun 
 *
 */
public class IssueEntryListViewer 
extends JDOQuerySearchEntryViewer {

	public IssueEntryListViewer(Entry entry) {
		super(entry);
	}

	private IssueTable issueTable;
	
	@Override
	public AbstractQueryFilterComposite createFilterComposite(Composite parent) {
		return new IssueFilterComposite(parent, SWT.NONE);
	}

	@Override
	public AbstractTableComposite<Issue> createListComposite(Composite parent) {
		issueTable = new IssueTable(parent, SWT.NONE);
		return issueTable;
	}

	@Override
	protected Object getQueryResult(Collection<JDOQuery> queries,
			ProgressMonitor monitor) {
//		try {
//			return IssueDAO.sharedInstance().getAccountsForQueries(
//					queries,
//					FETCH_GROUPS_ACCOUNTS, 
//					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
//					monitor);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
		return null;
	}
}
