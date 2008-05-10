package org.nightlabs.jfire.simpletrade.admin.ui.producttype.subscribe;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;

public class SubscribeAction
implements IWorkbenchWindowActionDelegate
{
	@SuppressWarnings("unused")
	private IWorkbenchWindow window;

	public void init(IWorkbenchWindow window)
	{
		this.window = window;
	}

	public void run(IAction action)
	{
		SubscribeWizard wizard = new SubscribeWizard();
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		dialog.open();
	}

	public void selectionChanged(IAction action, ISelection selection)
	{
	}

	public void dispose()
	{
	}

}
