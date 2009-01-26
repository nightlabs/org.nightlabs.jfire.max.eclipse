package org.nightlabs.jfire.issuetracking.ui.overview.search;

import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 *
 * @author chairat
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class IssueSubjectCommentQuickSearchEntry
	extends AbstractQuickSearchEntry<IssueQuery>
{
	public IssueSubjectCommentQuickSearchEntry(QuickSearchEntryFactory<IssueQuery> factory)
	{
		super(factory, IssueQuery.class);
	}

	@Override
	protected void doSetSearchConditionValue(IssueQuery query, String value)
	{
		query.setIssueSubjectNComment(".*" + value + ".*"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	protected String getModifiedQueryFieldName()
	{
		return IssueQuery.FieldName.issueSubjectNComment;
	}
}
