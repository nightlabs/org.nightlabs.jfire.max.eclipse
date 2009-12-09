package org.nightlabs.jfire.issuetracking.ui.issue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.jfire.security.User;

public class IssueCommentAddDialog 
extends ResizableTitleAreaDialog
{
	private Text commentText;
	private Issue issue;
	private User user;
	public IssueCommentAddDialog(Shell shell, Issue issue, User user) {
		super(shell, Messages.RESOURCE_BUNDLE);
		this.issue = issue;
		this.user = user;
	}

	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		Button button = super.createButton(parent, id, label, defaultButton);
		if (OK == id) {
			button.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueCommentAddDialog.addButton.text")); //$NON-NLS-1$
			button.setEnabled(false);
		}
		return button;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueCommentAddDialog.shell.text")); //$NON-NLS-1$
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueCommentAddDialog.title.text")); //$NON-NLS-1$

		XComposite area = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		area.getGridLayout().numColumns = 1;
		
		new Label(area, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueCommentAddDialog.commentLabel.text")); //$NON-NLS-1$
		
		commentText = new Text(area, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		commentText.setLayoutData(new GridData(GridData.FILL_BOTH));
		commentText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				getButton(OK).setEnabled(!commentText.getText().isEmpty());
			}
		});
		
		return area;
	}

	@Override
	protected void okPressed() {
		commentString = commentText.getText();
		super.okPressed();
	}
	
	private String commentString;
	public String getCommentString() {
		return commentString;
	}
}