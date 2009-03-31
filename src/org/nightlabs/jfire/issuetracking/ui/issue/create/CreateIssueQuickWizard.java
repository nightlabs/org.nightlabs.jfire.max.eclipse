package org.nightlabs.jfire.issuetracking.ui.issue.create;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;

public class CreateIssueQuickWizard 
extends DynamicPathWizard
implements INewWizard {

	@Override
	public boolean performFinish() {
		return false;
	}

	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {
		
	}
}
