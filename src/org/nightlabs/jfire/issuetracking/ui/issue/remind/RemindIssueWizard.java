package org.nightlabs.jfire.issuetracking.ui.issue.remind;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.issue.Issue;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class RemindIssueWizard 
extends DynamicPathWizard
{
	private RemindIssueUserWizardPage remindIssueUserWizardPage;
	private RemindIssueDetailWizardPage remindIssueDetailWizardPage;

	private Issue selectedIssue;
	
	public RemindIssueWizard(Issue selectedIssue)
	{
		setWindowTitle("Remind issue");
		this.selectedIssue = selectedIssue;
	}

	/**
	 * Adding the page to the wizard.
	 */
	@Override
	public void addPages() {
		remindIssueUserWizardPage = new RemindIssueUserWizardPage(selectedIssue);
		addPage(remindIssueUserWizardPage);
		
		remindIssueDetailWizardPage = new RemindIssueDetailWizardPage(selectedIssue);
		addPage(remindIssueDetailWizardPage);
	}

	@Override
	public boolean performFinish() {
		return true;
	}
}