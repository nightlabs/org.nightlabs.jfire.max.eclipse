package org.nightlabs.jfire.issuetracking.ui.issuelink;

import java.util.Set;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkAdderComposite;

public class IssueLinkWizard 
extends DynamicPathWizard
{
	private Set<IssueLink> issueLinks;

	private Issue issue;
	private IssueLinkAdderComposite parent;
	
	public IssueLinkWizard(IssueLinkAdderComposite parent, Issue issue) {
		this.parent = parent;
		this.issue = issue;
		setWindowTitle("Link Objects to Issue");
	}

	@Override
	public void addPages() {
		IssueLinkWizardCategoryPage page = new IssueLinkWizardCategoryPage(this);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		parent.addIssueLinks(issueLinks);
		return true;
	}

	@Override
	public boolean canFinish() {
		if(issueLinks != null && issueLinks.size() != 0) {
			return true;
		}

		return false;
	}

	public void setIssueLinks(Set<IssueLink> issueLinks) {
		this.issueLinks = issueLinks;
	}
	
	public Issue getIssue() {
		return issue;
	}
}
