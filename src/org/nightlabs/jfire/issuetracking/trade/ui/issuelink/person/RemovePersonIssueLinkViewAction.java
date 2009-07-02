package org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.trade.ui.IssueTrackingTradePlugin;
import org.nightlabs.jfire.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Fitas Amine - fitas at nightlabs dot de
 *
 */
public class RemovePersonIssueLinkViewAction extends Action{

	private static String[] FETCH_GROUP_ISSUE = new String[]{
		FetchPlan.DEFAULT,
		Issue.FETCH_GROUP_ISSUE_LINKS
	};


	public RemovePersonIssueLinkViewAction() {
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
		final IssueLink issueLink = (IssueLink)view.getSelectedNode();
		final Issue issue = issueLink.getIssue();

		boolean result = MessageDialog.openConfirm(
				view.getSite().getShell(),
				"Remove Person/Issue Link",
				"Are you sure you wants to remove the Link Person/Issue"
				+ "(ID:" + ObjectIDUtil.longObjectIDFieldToString(issue.getIssueID()) + ") "
				+ "Subject:\"" + issue.getSubject().getText() + "\""
				+ "?");

		if(!result)
			return;

		Job job = new Job(Messages.getString("Deleting the Issue Link")) {
			@Override
			protected IStatus run(ProgressMonitor monitor)
			{
				monitor.beginTask("Deleting the Issue Link", 100);

				Issue _issue = IssueDAO.sharedInstance().getIssue(
						(IssueID)JDOHelper.getObjectId(issue), FETCH_GROUP_ISSUE,
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 30));

				_issue.removeIssueLink(issueLink);
				monitor.worked(30);
				Issue storedIssue = IssueDAO.sharedInstance().storeIssue(_issue,false,FETCH_GROUP_ISSUE,
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 70));
				monitor.done();
				return Status.OK_STATUS;
			}
		};

		job.setPriority(Job.SHORT);
		job.schedule();
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return SharedImages.getSharedImageDescriptor(IssueTrackingTradePlugin.getDefault(),
				this.getClass(),"Delete");//$NON-NLS-1$
	}

	@Override
	public String getText() {
		return "Removes the Link Person/Issue";
	}

	@Override
	public String getToolTipText() {
		return "Removes the Link between Person/Issue";
	}


}
