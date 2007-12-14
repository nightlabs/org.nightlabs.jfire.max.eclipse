package org.nightlabs.jfire.issuetracking.ui.issue.action;

import org.nightlabs.base.ui.action.WorkbenchPartSelectionAction;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueNewWizard;

public class AddIssueAction 
extends WorkbenchPartSelectionAction
{
	public boolean calculateEnabled() {
		return true;
	}

	public boolean calculateVisible() {
		return true;
	}

	@Override
	public void run() {
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(new IssueNewWizard(null));
		dialog.open();
	}
}
