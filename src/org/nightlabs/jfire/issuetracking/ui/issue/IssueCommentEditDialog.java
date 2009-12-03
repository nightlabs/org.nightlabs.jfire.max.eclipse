package org.nightlabs.jfire.issuetracking.ui.issue;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jfire.issue.IssueComment;
import org.nightlabs.jfire.issue.dao.IssueCommentDAO;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

public class IssueCommentEditDialog 
extends ResizableTitleAreaDialog
{
	private Text commentText;
	private IssueComment comment;
	public IssueCommentEditDialog(Shell shell, IssueComment comment) {
		super(shell, Messages.RESOURCE_BUNDLE);
		this.comment = comment;
	}

	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		Button button = super.createButton(parent, id, label, defaultButton);
		if (OK == id) {
			button.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueCommentEditDialog.okButton.text")); //$NON-NLS-1$
			button.setEnabled(false);
		}
		return button;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueCommentEditDialog.dialogTitle.text")); //$NON-NLS-1$
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueCommentEditDialog.title.text")); //$NON-NLS-1$

		XComposite area = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		area.getGridLayout().numColumns = 1;
		
		new Label(area, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueCommentEditDialog.commentLabel.text")); //$NON-NLS-1$
		
		commentText = new Text(area, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		commentText.setText(comment.getText());
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
		Job job = new Job(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueCommentEditDialog.job.storingComment.text")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				comment.setText(commentText.getText());
				IssueCommentDAO.sharedInstance().storeIssueComment(comment, false, null, 1, monitor);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
		super.okPressed();
	}
	
	public String getCommentString() {
		return commentText.getText();
	}
}