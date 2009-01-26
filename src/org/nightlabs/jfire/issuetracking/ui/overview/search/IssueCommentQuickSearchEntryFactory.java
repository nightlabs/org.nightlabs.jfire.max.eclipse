package org.nightlabs.jfire.issuetracking.ui.overview.search;

import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntryFactory;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

public class IssueCommentQuickSearchEntryFactory 
	extends AbstractQuickSearchEntryFactory<IssueQuery>
{
	@Override
	public QuickSearchEntry<IssueQuery> createQuickSearchEntry()
	{
		return new IssueCommentQuickSearchEntry(this);
	}

	@Override
	public String getName() {
		return "Comment"; //$NON-NLS-1$
	}

	@Override
	public Class<? extends IssueQuery> getQueryType()
	{
		return IssueQuery.class;
	}
}
