package org.nightlabs.jfire.issuetracking.ui.issuelink;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;

public class IssueLinkWizard 
extends DynamicPathWizard
{
	public IssueLinkWizard() {
		setWindowTitle("Link Objects to Issue");
	}

	@Override
	public void addPages() {
		IssueLinkWizardCategoryPage page = new IssueLinkWizardCategoryPage();
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
