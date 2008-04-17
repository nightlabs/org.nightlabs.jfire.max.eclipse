package org.nightlabs.jfire.issuetracking.ui.issuelink;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkAdderComposite;

public class IssueLinkWizard 
extends DynamicPathWizard
{
	private Issue issue;
	
	private IssueLinkAdderComposite linkAdderComposite;
	
	public IssueLinkWizard(IssueLinkAdderComposite linkAdderComposite, Issue issue) {
		this.linkAdderComposite = linkAdderComposite;
		this.issue = issue;
		setWindowTitle("Link Objects to Issue");
	}

	@Override
	public void addPages() {
		IssueLinkWizardCategoryPage categoryPage = new IssueLinkWizardCategoryPage(this);
		addPage(categoryPage);
	}

	/**
	 * This method is used for adding issueLink to IssueLinkAdderComposite.
	 */
	@Override
	public boolean performFinish() {
		return true;
	}

	@Override
	public boolean canFinish() {
		return true;
	}

	public Issue getIssue() {
		return issue;
	}
	
	public IssueLinkAdderComposite getLinkAdderComposite() {
		return linkAdderComposite;
	}
}
