package org.nightlabs.jfire.issuetracking.ui.issue.create;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

public class CreateIssueQuickDialog 
extends ResizableTitleAreaDialog
{
	public CreateIssueQuickDialog(Shell shell) {
		super(shell, Messages.RESOURCE_BUNDLE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Create an Issue");
		setMessage("Create an Issue");

		Composite wrapper = new XComposite(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER);
		return wrapper;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Title");
	}
}