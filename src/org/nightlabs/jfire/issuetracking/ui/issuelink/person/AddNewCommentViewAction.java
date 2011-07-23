package org.nightlabs.jfire.issuetracking.ui.issuelink.person;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueComment;
import org.nightlabs.jfire.issue.dao.IssueCommentDAO;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Fitas Amine - fitas at nightlabs dot de
 *
 */
public class AddNewCommentViewAction extends Action implements IViewActionDelegate
{
	public static final String ID = AddNewCommentViewAction.class.getName();

	public AddNewCommentViewAction() {
		super();
		setId(ID);
	}

	private IPersonIssueLinkView view;

	/**
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view)
	{
		if (view instanceof IPersonIssueLinkView) {
			this.view = (IPersonIssueLinkView) view;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction arg0) {
		run();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
		// do nothing
	}

	@Override
	public void run() {

		if (view == null || view.getSelectedIssueLink() == null)
			return;

		final InputDialog dlg = new InputDialog(RCPUtil.getActiveShell(),
				Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.person.AddNewCommentViewAction.inputDialog.title"), //$NON-NLS-1$
				Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.person.AddNewCommentViewAction.inputDialog.description"), "", null); //$NON-NLS-1$ //$NON-NLS-2$
		if (dlg.open() == Window.OK) {

			final String text = dlg.getValue();
			final Issue issue = view.getSelectedIssueLink().getIssue();
			Job job = new Job(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.person.AddNewCommentViewAction.job.savingComment")) { //$NON-NLS-1$
				@Override
				protected IStatus run(ProgressMonitor monitor)
				{
					monitor.beginTask(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.person.AddNewCommentViewAction.task.savingComment"), 100); //$NON-NLS-1$
					IssueComment comment = new IssueComment(issue.getOrganisationID(),
							IDGenerator.nextID(IssueComment.class),
							issue,
							text,
							Login.sharedInstance().getUser(
									new String[]{ FetchPlan.DEFAULT },
									NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
									new SubProgressMonitor(monitor, 40))
							);

					monitor.worked(30);
					IssueCommentDAO.sharedInstance().storeIssueComment(comment, false, null, // FETCH_GROUP_ISSUE_COMMENT,
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 60)
					);

					monitor.done();
					return Status.OK_STATUS;
				}
			};

			job.setPriority(Job.SHORT);
			job.schedule();
		}
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return SharedImages.EDIT_16x16;
	}

	@Override
	public String getText() {
		return Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.person.AddNewCommentViewAction.action.text"); //$NON-NLS-1$
	}

	@Override
	public String getToolTipText() {
		return Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.person.AddNewCommentViewAction.action.tooltip");	 //$NON-NLS-1$
	}
}
