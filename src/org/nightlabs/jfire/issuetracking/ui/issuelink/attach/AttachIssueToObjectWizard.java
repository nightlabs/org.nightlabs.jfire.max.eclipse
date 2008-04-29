package org.nightlabs.jfire.issuetracking.ui.issuelink.attach;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.ObjectID;

public class AttachIssueToObjectWizard 
extends DynamicPathWizard
{
	private ObjectID objectID;
	private SelectIssueAndIssueLinkTypePage selectIssueAndIssueLinkTypePage;
	
	public AttachIssueToObjectWizard(ObjectID objectID) {
		this.objectID = objectID;
		setWindowTitle("Attach Issue(s) to Objects");
	}

	@Override
	public void addPages() {
		selectIssueAndIssueLinkTypePage = new SelectIssueAndIssueLinkTypePage();
		addPage(selectIssueAndIssueLinkTypePage);
	}

	@Override
	public boolean performFinish() {
		return true;
	}
}
