package org.nightlabs.jfire.personrelation.issuetracking.trade.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.eclipse.ui.dialog.ResizableTrayDialog;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueComment;
import org.nightlabs.jfire.issue.dao.IssueCommentDAO;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.personrelation.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.security.User;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

public class CreateIssueCommentDialog extends ResizableTrayDialog
{
	private IssueID issueID;
	private Text commentText;

	private void init(IssueID issueID)
	{
		this.issueID = issueID;
	}

	public CreateIssueCommentDialog(Shell shell, IssueID issueID) {
		super(shell, Messages.getBundle());
		init(issueID);
	}

	public CreateIssueCommentDialog(IShellProvider parentShell, IssueID issueID) {
		super(parentShell, Messages.getBundle());
		init(issueID);
	}

	public IssueID getIssueID() {
		return issueID;
	}

	@Override
	protected void okPressed() {
		final String newCommentText = commentText.getText();
		final Display display = commentText.getDisplay();
		Job job = new Job(Messages.getString("org.nightlabs.jfire.personrelation.issuetracking.trade.ui.CreateIssueCommentDialog.job.createNewComment.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				monitor.beginTask(Messages.getString("org.nightlabs.jfire.personrelation.issuetracking.trade.ui.CreateIssueCommentDialog.task.createNewComment.name"), 100); //$NON-NLS-1$
				try {
					Issue issue = IssueDAO.sharedInstance().getIssue(issueID, null, 1, new SubProgressMonitor(monitor, 20));
					User user = Login.getLogin().getUser(null, 1, new SubProgressMonitor(monitor, 20));
					IssueComment issueComment = new IssueComment(issue, newCommentText, user);
					IssueCommentDAO.sharedInstance().storeIssueComment(issueComment, false, null, 1, new SubProgressMonitor(monitor, 60));

					display.asyncExec(new Runnable() {
						public void run() {
							CreateIssueCommentDialog.super.okPressed();
						}
					});

					return Status.OK_STATUS;
				} finally {
					monitor.done();
				}
			}
		};
		job.setUser(true);
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText(Messages.getString("org.nightlabs.jfire.personrelation.issuetracking.trade.ui.CreateIssueCommentDialog.dialog.createIssueComment.title")); //$NON-NLS-1$
		Composite dialogArea = (Composite) super.createDialogArea(parent);
		commentText = new Text(dialogArea, SWT.BORDER | SWT.MULTI);
		commentText.setLayoutData(new GridData(GridData.FILL_BOTH));
		return dialogArea;
	}
}
