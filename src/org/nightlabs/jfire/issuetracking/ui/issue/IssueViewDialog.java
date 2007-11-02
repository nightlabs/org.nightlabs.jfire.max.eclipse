package org.nightlabs.jfire.issuetracking.ui.issue;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.dialog.CenteredDialog;
import org.nightlabs.jfire.issue.Issue;

public class IssueViewDialog extends CenteredDialog{
	private static final Logger logger = Logger.getLogger(IssueViewDialog.class);

	private Issue issue;  

	public IssueViewDialog(Shell parentShell, Issue issue) 
	{
		super(parentShell);
		this.issue = issue;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	protected Control createDialogArea(Composite parent) 
	{
		getShell().setText("Title");
		parent.setLayout(new GridLayout(1, false));
		
		XComposite c = new XComposite(parent, SWT.NONE);
		c.getGridLayout().numColumns = 5;
		
		Button editButton = new Button(c, SWT.PUSH);
		editButton.setText("Edit");
		
		Button replyButton = new Button(c, SWT.PUSH);
		replyButton.setText("Reply");
		
		Button notifyButton = new Button(c, SWT.PUSH);
		notifyButton.setText("Notify");
		
		Button deleteButton = new Button(c, SWT.PUSH);
		deleteButton.setText("Delete");
		
		Button reassignButton = new Button(c, SWT.PUSH);
		reassignButton.setText("Reassign");
		
		IssueViewComposite issueViewComposite = new IssueViewComposite(issue, parent, SWT.NONE);
		return issueViewComposite;
	}  

	@Override
	protected Control createContents(Composite parent) {
		Control ctrl = super.createContents(parent);
		getButton(Dialog.OK).setEnabled(false);
		return ctrl;
	}
}