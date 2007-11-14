 package org.nightlabs.jfire.issuetracking.ui.issue;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.dialog.CenteredDialog;
import org.nightlabs.jfire.issue.Issue;

public class IssueEditDialog extends CenteredDialog{
	private static final Logger logger = Logger.getLogger(IssueEditDialog.class);

	private Issue issue;  

	public IssueEditDialog(Shell parentShell, Issue issue) 
	{
		super(parentShell);
		this.issue = issue;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	protected Control createDialogArea(Composite parent) 
	{
		getShell().setText("Title");
		
		IssueEditComposite issueEditComposite = new IssueEditComposite(issue, parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		issueEditComposite.setLayoutData(gridData);
		
		return parent;
	}  

	@Override
	protected Control createContents(Composite parent) {
		Control ctrl = super.createContents(parent);
		getButton(Dialog.OK).setEnabled(false);
		return ctrl;
	}
}