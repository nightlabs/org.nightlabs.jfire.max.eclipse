package org.nightlabs.jfire.issuetracking.ui.issuelink.attach;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;

public class AttachIssueToObjectWizard 
extends DynamicPathWizard
{
	private Object attachedObject;
	private SelectIssueAndIssueLinkTypePage selectIssueAndIssueLinkTypePage;
	
	public AttachIssueToObjectWizard(Object attacedObject) {
		this.attachedObject = attacedObject;
		setWindowTitle("Attach Issue(s) to Objects");
	}

	@Override
	public void addPages() {
		selectIssueAndIssueLinkTypePage = new SelectIssueAndIssueLinkTypePage(attachedObject);
		addPage(selectIssueAndIssueLinkTypePage);
	}

	@Override
	public boolean performFinish() {
		return true;
	}
}
