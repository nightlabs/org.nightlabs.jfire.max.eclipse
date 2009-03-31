package org.nightlabs.jfire.issuetracking.ui.issue.create;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class QuickCreateIssueAction 
implements IWorkbenchWindowActionDelegate 
{
	public static final String ID = QuickCreateIssueAction.class.getName();
	
	@Override
	public void dispose() {
	}

	private IWorkbenchWindow window;

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	@Override
	public void run(IAction action) {
		CreateIssueQuickDialog createIssueQuickDialog = new CreateIssueQuickDialog(window.getShell());
		createIssueQuickDialog.open();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}
}
