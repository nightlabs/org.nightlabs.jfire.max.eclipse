package org.nightlabs.jfire.issuetracking.ui.overview.search;

import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.query.IssueQuery;

/**
 * @author chairat 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class IssueCommentQuickSearchEntry 
	extends AbstractQuickSearchEntry<Issue, IssueQuery>
{
	public IssueCommentQuickSearchEntry(QuickSearchEntryFactory<Issue, IssueQuery> factory) {
		super(factory, IssueQuery.class);
	}

//	@Override
//	protected void doResetSearchCondition(IssueQuery query, String lastValue)
//	{
//		query.setIssueComment(lastValue);
//	}

	@Override
	protected void doSetSearchConditionValue(IssueQuery query, String value)
	{
		query.setIssueComment(value);
	}

	@Override
	protected void doUnsetSearchConditionValue(IssueQuery query)
	{
		query.setIssueComment(null);
	}
	
//	@Override
//	public Object search(ProgressMonitor monitor) {
//		Collection<AbstractJDOQuery> queries = new ArrayList<AbstractJDOQuery>();
//		IssueQuery query = new IssueQuery();
//		query.setIssueComment(getSearchText());
//		queries.add(query);
//		return IssueDAO.sharedInstance().getIssuesForQueries(queries, 
//				IssueTable.FETCH_GROUPS_ISSUE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
//	}
}
