package org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueComment;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.dao.IssueCommentDAO;
import org.nightlabs.jfire.issuetracking.trade.ui.IssueTrackingTradePlugin;
import org.nightlabs.jfire.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Fitas Amine - fitas at nightlabs dot de
 *
 */
public class AddNewCommentViewAction  extends Action{


//	private static String[] FETCH_GROUP_ISSUE_COMMENT = new String[]{
//		FetchPlan.DEFAULT,
//		IssueComment.FETCH_GROUP_TEXT,
//		IssueComment.FETCH_GROUP_USER
//	};

	/**
	 *
	 */
	public AddNewCommentViewAction() {
		super();
	}

	private LegalEntityPersonIssueLinkTreeView view;
	/**
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(LegalEntityPersonIssueLinkTreeView view) {
		this.view = view;
	}


	@Override
	public void run() {

		if(view.getSelectedNode() == null ||!(view.getSelectedNode() instanceof IssueLink))
			return;

		final InputDialog dlg = new InputDialog(RCPUtil.getActiveShell(),
				Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person.AddNewCommentViewAction.inputDialog.title"), Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person.AddNewCommentViewAction.inputDialog.description"), "", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (dlg.open() == Window.OK) {

			final String text = dlg.getValue();

			Job job = new Job(Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person.AddNewCommentViewAction.job.savingComment")) { //$NON-NLS-1$
				@Override
				protected IStatus run(ProgressMonitor monitor)
				{
					monitor.beginTask(Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person.AddNewCommentViewAction.task.savingComment"), 100); //$NON-NLS-1$
					Issue issue = ((IssueLink)view.getSelectedNode()).getIssue();
					IssueComment comment = new IssueComment(issue.getOrganisationID(),
							IDGenerator.nextID(IssueComment.class),
							issue,
							text,
							Login.sharedInstance().getUser(
									new String[]{ FetchPlan.DEFAULT },
									NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
									new org.eclipse.core.runtime.NullProgressMonitor())
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
		return SharedImages.getSharedImageDescriptor(IssueTrackingTradePlugin.getDefault(),
				this.getClass(),"Add");//$NON-NLS-1$
	}

	@Override
	public String getText() {
		return Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person.AddNewCommentViewAction.action.text"); //$NON-NLS-1$
	}

	@Override
	public String getToolTipText() {
		return Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person.AddNewCommentViewAction.action.tooltip");	 //$NON-NLS-1$
	}
}
