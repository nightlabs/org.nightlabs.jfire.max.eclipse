package org.nightlabs.jfire.issuetracking.ui.overview.search;

import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.issue.query.IssueQuery;

/**
 * @author chairat
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class IssueCommentQuickSearchEntry
	extends AbstractQuickSearchEntry<IssueQuery>
{
	public IssueCommentQuickSearchEntry(QuickSearchEntryFactory<IssueQuery> factory)
	{
		super(factory, IssueQuery.class);
	}

	@Override
	protected void doSetSearchConditionValue(IssueQuery query, String value)
	{
		query.setIssueComment(".*" + value + ".*");
	}

	@Override
	protected String getModifiedQueryFieldName()
	{
		return IssueQuery.FieldName.issueComment;
	}
}
