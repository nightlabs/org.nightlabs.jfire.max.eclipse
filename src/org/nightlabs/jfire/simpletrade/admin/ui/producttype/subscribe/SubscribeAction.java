package org.nightlabs.jfire.simpletrade.admin.ui.producttype.subscribe;

import org.eclipse.jface.action.IAction;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.base.ui.login.action.LSDWorkbenchWindowActionDelegate;

public class SubscribeAction
extends LSDWorkbenchWindowActionDelegate
{
	public void run(IAction action)
	{
		SubscribeWizard wizard = new SubscribeWizard();
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		dialog.open();
	}
}
