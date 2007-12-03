package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.nightlabs.jfire.base.ui.overview.AbstractEntryFactory;
import org.nightlabs.jfire.base.ui.overview.Entry;

public class IssueTypeEntryFactory extends AbstractEntryFactory {

	public IssueTypeEntryFactory(){
		
	}
	
	public Entry createEntry() {
		return new IssueTypeEntry(this);
	}

}
