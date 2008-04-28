package org.nightlabs.jfire.issuetracking.ui.issuelink.attach;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTable;

public class AttachIssueToObjectWizard 
extends DynamicPathWizard
{
	public AttachIssueToObjectWizard(IssueLinkTable issueLinkTable, Issue issue) {
		setWindowTitle("Attach Issue(s) to Objects");
	}

	@Override
	public void addPages() {
	}

	@Override
	public boolean performFinish() {
		return true;
	}
}
