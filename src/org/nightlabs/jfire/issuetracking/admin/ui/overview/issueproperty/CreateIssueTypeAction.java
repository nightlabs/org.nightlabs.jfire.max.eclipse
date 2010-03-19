package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;

/**
 * @author Daniel Mazurek
 *
 */
public class CreateIssueTypeAction extends Action 
{
	public static final String ID = CreateIssueTypeAction.class.getName();
	
	private Shell shell;
	
	public CreateIssueTypeAction(Shell shell) {
		setText("Create Issue Type");
		setToolTipText("Create Issue Type");
		setId(ID);
		this.shell = shell;
	}

	@Override
	public void run() 
	{
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(shell, new CreateIssueTypeWizard());
		dialog.open();
	}
}
