package org.nightlabs.jfire.issuetracking.ui.issuelink.attach;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.ObjectID;

public class AttachIssueToObjectWizard 
extends DynamicPathWizard
{
	private ObjectID objectID;
	private SelectIssueLinkTypePage selectIssueLinkTypePage;
	
	public AttachIssueToObjectWizard(ObjectID objectID) {
		this.objectID = objectID;
		setWindowTitle("Attach Issue(s) to Objects");
	}

	@Override
	public void addPages() {
		selectIssueLinkTypePage = new SelectIssueLinkTypePage();
		addPage(selectIssueLinkTypePage);
	}

	@Override
	public boolean performFinish() {
		return true;
	}
}
