package org.nightlabs.jfire.issuetracking.ui.issuelink.person;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 *
 * Removes the Issue Link between a Person(LegalEntity) and an Issue Link
 *
 * @author Fitas Amine - fitas at nightlabs dot de
 *
 */
public class RemovePersonIssueLinkViewAction extends Action implements IViewActionDelegate
{
	public static final String ID = RemovePersonIssueLinkViewAction.class.getName();

	private static String[] FETCH_GROUP_ISSUE = new String[]{
		FetchPlan.DEFAULT,
		Issue.FETCH_GROUP_ISSUE_LINKS
	};

	private IPersonIssueLinkView view;

	public RemovePersonIssueLinkViewAction() {
		super();
		setId(ID);
	}

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

		final IssueLink issueLink = view.getSelectedIssueLink();
		final Issue issue = issueLink.getIssue();

		boolean result = MessageDialog.openConfirm(
				view.getSite().getShell(),
				Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.person.RemovePersonIssueLinkViewAction.dialog.removePersonIssueLink.title"), //$NON-NLS-1$
				Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.person.RemovePersonIssueLinkViewAction.dialog.removePersonIssueLink.message1") //$NON-NLS-1$
				+ "(ID:" + ObjectIDUtil.longObjectIDFieldToString(issue.getIssueID()) + ") " //$NON-NLS-1$ //$NON-NLS-2$
				+ Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.person.RemovePersonIssueLinkViewAction.dialog.removePersonIssueLink.message2") + issue.getSubject().getText() + "\"" //$NON-NLS-1$ //$NON-NLS-2$
				+ "?"); //$NON-NLS-1$

		if(!result)
			return;

		Job job = new Job(Messages.getString(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.person.RemovePersonIssueLinkViewAction.job.deleteIssueLink"))) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor)
			{
				monitor.beginTask(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.person.RemovePersonIssueLinkViewAction.job.deleteIssueLink"), 100); //$NON-NLS-1$

				Issue _issue = IssueDAO.sharedInstance().getIssue(
						(IssueID)JDOHelper.getObjectId(issue), FETCH_GROUP_ISSUE,
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 30));

				_issue.removeIssueLink(issueLink);
				monitor.worked(30);
				_issue = IssueDAO.sharedInstance().storeIssue(_issue,false,FETCH_GROUP_ISSUE,
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
		return SharedImages.DELETE_16x16;
	}

	@Override
	public String getText() {
		return Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.person.RemovePersonIssueLinkViewAction.action.text"); //$NON-NLS-1$
	}

	@Override
	public String getToolTipText() {
		return Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.person.RemovePersonIssueLinkViewAction.action.tooltip"); //$NON-NLS-1$
	}

}
