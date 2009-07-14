package org.nightlabs.jfire.issuetracking.trade.ui.issuelink;

import java.util.Collection;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPart;
import org.nightlabs.base.ui.action.WorkbenchPartAction;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueTable;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * This provides the general unified Action to remove selected {@link Issue}(s) that are linked to
 * the arbitrary object, listed in the {@link IssueTable}.
 *
 * @author Khaireel Mohamed - khaireel at nightlabs dot de
 */
public class IssueLinkActionRemove extends WorkbenchPartAction { //Action {
	// This is intended to replace the private classes defined in the following classes:
	//    (i) ShowLinkedIssueSection;  <-- Action button; top right menu-bar.
	//   (ii) ShowLinkedIssuePage;     <-- Context-menu, right-click on the IssueTable.
	//
	// And a similar situation is also adopted in the Issue-to-Person link.

	// Important references.
	private IssueTable issueTable;
	private ShowLinkedIssuePageController controller; // <-- Currently, I leave it specifically to this ShowLinkedIssuePageController; it has
	                                                  //     all the necessary FETCH_GROUPS and specific methods to handle the link relations
	                                                  //     between Issues, the related IssueLinks, and the arbitrary object (in this case,
	                                                  //     this refers to the ArticleContainer).

	/**
	 * Creates a new instance of the IssueLinkActionRemove.
	 */
	public IssueLinkActionRemove(IssueTable issueTable, ShowLinkedIssuePageController controller, IWorkbenchPart activePart) {
		super(activePart);
		setId(IssueLinkActionRemove.class.getName());
		setImageDescriptor(SharedImages.DELETE_16x16);
		setToolTipText("Remove link to selected issue");
		setText("Remove selected issue link(s)");

		this.issueTable = issueTable;
		this.controller = controller;
	}

	@Override
	public void run() {
		// Pick out those Issues selected from the IssueTable.
		Collection<Issue> selectedIssues = issueTable.getSelectedElements();
		if (selectedIssues == null || selectedIssues.isEmpty()) return;

		// Scroll through every single selected Issue, and ask for delete-confirmation.
		for (Issue selectedIssue : selectedIssues) {
			boolean result = MessageDialog.openConfirm(
					getActivePart().getSite().getShell(),
					Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.ShowLinkedIssueSection.dialog.removeIssueLink.title"), //$NON-NLS-1$
					Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.ShowLinkedIssueSection.dialog.removeIssueLink.message") //$NON-NLS-1$
					+ "(ID:" + ObjectIDUtil.longObjectIDFieldToString(selectedIssue.getIssueID()) + ") " //$NON-NLS-1$ //$NON-NLS-2$
					+ "\"" + selectedIssue.getSubject().getText() + "\"?"); //$NON-NLS-1$ //$NON-NLS-2$

			if (result) {
				// Setup the pre-delete sequence.
				// Look for that related IssueLink to be deleted.
				final Issue issue = selectedIssue;
				final IssueLink issueLink = controller.removeRelatedIssueLink(selectedIssue);

				Job job = new Job(Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.ShowLinkedIssueSection.job.removeIssueLink")) { //$NON-NLS-1$
					@Override
					protected IStatus run(ProgressMonitor monitor) {
						try {
							Issue _issue = IssueDAO.sharedInstance().getIssue(
									(IssueID)JDOHelper.getObjectId(issue), new String[] {FetchPlan.DEFAULT, Issue.FETCH_GROUP_ISSUE_LINKS},
									NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 50));

							_issue.removeIssueLink(issueLink);
							IssueDAO.sharedInstance().storeIssue(
									_issue, false, new String[] {FetchPlan.DEFAULT, Issue.FETCH_GROUP_ISSUE_LINKS},
									NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 50));

							return Status.OK_STATUS;
						} finally {
							monitor.done();
						}
					}
				};

				job.setPriority(Job.SHORT);
				job.schedule();
			}
		}

	}

	@Override
	public boolean calculateEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean calculateVisible() {
		// TODO Auto-generated method stub
		return false;
	}
}
