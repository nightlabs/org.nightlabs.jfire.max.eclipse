package org.nightlabs.jfire.issuetracking.ui.issuelink;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;

public class IssueLinkObjectWizard 
extends DynamicPathWizard
{
	public IssueLinkObjectWizard() {
		setWindowTitle("Link Objects to Issue");
	}

	@Override
	public void addPages() {
		IssueLinkObjectWizardPage page = new IssueLinkObjectWizardPage();
		addPage(page);
	}
	
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean canFinish() {
		return true;
	}
}
