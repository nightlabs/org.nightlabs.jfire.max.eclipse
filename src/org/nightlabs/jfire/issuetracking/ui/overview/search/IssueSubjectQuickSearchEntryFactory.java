package org.nightlabs.jfire.issuetracking.ui.overview.search;

import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntryFactory;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;

public class IssueSubjectQuickSearchEntryFactory 
extends AbstractQuickSearchEntryFactory
{
	@Override
	public QuickSearchEntry createQuickSearchEntry() {
		return new IssueSubjectQuickSearchEntry(this);
	}

	@Override
	public String getName() {
		return "Subject";
	}
}
