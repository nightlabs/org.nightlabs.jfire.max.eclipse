package org.nightlabs.jfire.issuetracking.ui.issuelink;

import java.util.Set;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkAdderComposite;

public class IssueLinkWizard 
extends DynamicPathWizard
{
	private Set<ObjectID> issueLinkObjectID;

	private IssueLinkAdderComposite parent;
	public IssueLinkWizard(IssueLinkAdderComposite parent) {
		this.parent = parent;
		setWindowTitle("Link Objects to Issue");
	}

	@Override
	public void addPages() {
		IssueLinkWizardCategoryPage page = new IssueLinkWizardCategoryPage(this);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		parent.addItems(issueLinkObjectID);
		return true;
	}

	@Override
	public boolean canFinish() {
		if(issueLinkObjectID != null && issueLinkObjectID.size() != 0) {
			return true;
		}

		return false;
	}

	public void setIssueLinkObjectID(Set<ObjectID> issueLinkObjectID) {
		this.issueLinkObjectID = issueLinkObjectID;
	}
	
	public void setLinkRelation(String relationText) {
		
	}
}
