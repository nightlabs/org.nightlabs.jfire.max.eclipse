package org.nightlabs.jfire.trade.admin.ui.overview;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;

public class UnitCreateWizard 
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
