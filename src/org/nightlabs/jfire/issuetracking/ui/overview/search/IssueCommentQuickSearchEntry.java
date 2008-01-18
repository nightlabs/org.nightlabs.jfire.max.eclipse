package org.nightlabs.jfire.issuetracking.ui.overview.search;

import java.util.ArrayList;
import java.util.Collection;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.JDOQuery;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueTable;
import org.nightlabs.progress.ProgressMonitor;

public class IssueCommentQuickSearchEntry 
extends AbstractQuickSearchEntry 
{
	public IssueCommentQuickSearchEntry(QuickSearchEntryFactory factory) {
		super(factory);
	}
	
	@Override
	public Object search(ProgressMonitor monitor) {
		Collection<JDOQuery> queries = new ArrayList<JDOQuery>();
		IssueQuery query = new IssueQuery();
		query.setIssueComment(getSearchText());
		queries.add(query);
		return IssueDAO.sharedInstance().getIssuesForQueries(queries, 
				IssueTable.FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}
}
