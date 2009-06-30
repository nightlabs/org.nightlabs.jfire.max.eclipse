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
import org.nightlabs.jfire.issue.dao.IssueCommentDAO;
import org.nightlabs.jfire.issuetracking.trade.ui.IssueTrackingTradePlugin;
import org.nightlabs.jfire.security.User;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Fitas Amine - fitas at nightlabs dot de
 *
 */
public class AddNewCommentViewAction  extends Action{


	private static String[] FETCH_GROUP_ISSUE_COMMENT = new String[]{
		FetchPlan.DEFAULT,
		IssueComment.FETCH_GROUP_TEXT,
		IssueComment.FETCH_GROUP_USER
	};

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
		if(view.getSelectedIssueLink() == null)
			return;

		final InputDialog dlg = new InputDialog(RCPUtil.getActiveShell(),
				"Add new Comment", "Enter a New Comment", "", null);
		if (dlg.open() == Window.OK) {	

			final String text = dlg.getValue(); 

			Job job = new Job("Saving the newly Added Comment") {
				@Override
				protected IStatus run(ProgressMonitor monitor)
				{
					monitor.beginTask("Saving the newly Added Comment", 100);
					Issue issue = view.getSelectedIssueLink().getIssue();
					IssueComment comment = new IssueComment(issue.getOrganisationID(),
							IDGenerator.nextID(IssueComment.class),
							issue,
							text,
							Login.sharedInstance().getUser(new String[]{User.FETCH_GROUP_NAME},
									NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
									new org.eclipse.core.runtime.NullProgressMonitor() ));

					monitor.worked(30);
					IssueCommentDAO.sharedInstance().storeIssueComment(comment, FETCH_GROUP_ISSUE_COMMENT, 
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 60));

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
		return "Add a Comment";
	}

	@Override
	public String getToolTipText() {
		return "Add a Comment";	
	}
}
