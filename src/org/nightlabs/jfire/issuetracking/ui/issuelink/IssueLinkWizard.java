package org.nightlabs.jfire.issuetracking.ui.issuelink;

import java.util.Collection;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;

public class IssueLinkWizard 
extends DynamicPathWizard
{
	private Collection<String> issueLinkObjectID;

	private IssueLinkWizardContainer parent;
	public IssueLinkWizard(IssueLinkWizardContainer parent) {
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
		parent.setIssueLinkObjectIds(issueLinkObjectID);
		return true;
	}

	@Override
	public boolean canFinish() {
		if(issueLinkObjectID != null && issueLinkObjectID.size() != 0) {
			return true;
		}

		return false;
	}

	public void setIssueLinkObjectID(Collection<String> issueLinkObjectID) {
		this.issueLinkObjectID = issueLinkObjectID;
	}
}
