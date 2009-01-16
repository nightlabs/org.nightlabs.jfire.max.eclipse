package org.nightlabs.jfire.issuetracking.ui.overview.action;

import org.nightlabs.base.ui.action.WorkbenchPartSelectionAction;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.issue.remind.RemindIssueWizard;

public class RemindIssueAction 
extends WorkbenchPartSelectionAction
{
	public boolean calculateEnabled() {
		return !getSelectedObjects().isEmpty();
	}

	public boolean calculateVisible() {
		return true;
	}

	@Override
	public void run() {
		Issue issue = (Issue)(getSelectedObjects().isEmpty() ? null : getSelectedObjects().get(0));
		
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(new RemindIssueWizard(issue));
		dialog.open();
	}
}