package org.nightlabs.jfire.issuetracking.ui.overview.search;

import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntryFactory;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;

public class IssueSubjectCommentQuickSearchEntryFactory 
extends AbstractQuickSearchEntryFactory
{
	@Override
	public QuickSearchEntry createQuickSearchEntry() {
		return new IssueSubjectCommentQuickSearchEntry(this);
	}

	@Override
	public String getName() {
		return "Subject And Comment";
	}
}
