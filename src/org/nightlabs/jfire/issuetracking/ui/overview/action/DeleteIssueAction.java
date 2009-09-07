package org.nightlabs.jfire.issuetracking.ui.overview.action;

import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueTable;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

public class DeleteIssueAction extends AbstractIssueAction {
	/**
	 *
	 */
	public DeleteIssueAction() {
	}

	/**
	 * @param activePart
	 */
	public DeleteIssueAction(IWorkbenchPart activePart) {
		super(activePart);
	}

	/**
	 * @param text
	 */
	public DeleteIssueAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param image
	 */
	public DeleteIssueAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * @param text
	 * @param style
	 */
	public DeleteIssueAction(String text, int style) {
		super(text, style);
	}

	@Override
	public boolean calculateEnabled() {
		return getSelectedIssueIDs().size() > 0;
	}

	private static final String[] FETCH_GROUP = new String[] { Issue.FETCH_GROUP_SUBJECT };

	@Override
	public void run() {
//		IWorkbenchPart part = getActivePart();
//		IssueTable issueTable = ((IssueEntryListViewer) ((IssueEntryListEditor)part).getEntryViewer()).getIssueTable(); // <-- Uughhh...

		Collection<IssueID> issueIDs = NLJDOHelper.getObjectIDList(getSelectedIssueIDs());
		for (IssueID issueID : issueIDs) {
			// Retrieve useful information to display for the user, indicating the related Issue to be deleted.
			Issue issueToBeDeleted =
				IssueDAO.sharedInstance().getIssue(issueID, FETCH_GROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
			boolean result = MessageDialog.openConfirm(
					RCPUtil.getActiveShell(),
					Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.action.DeleteIssueAction.dialog.confirmDelete.title.text"), //$NON-NLS-1$
					Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.action.DeleteIssueAction.dialog.confirmDelete.description.text") //$NON-NLS-1$
					+ "(ID:" + ObjectIDUtil.longObjectIDFieldToString(issueID.issueID) + ") " //$NON-NLS-1$ //$NON-NLS-2$
					+ "\"" + issueToBeDeleted.getSubject().getText() + "\"" //$NON-NLS-1$ //$NON-NLS-2$
					+ "?"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			if (result) {
				final IssueID issueIDToDelete = issueID;
				Job deleteIssueJob = new Job(Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.action.DeleteIssueAction.job.deleteIssue") + issueIDToDelete) { //$NON-NLS-1$
					@Override
					protected IStatus run(ProgressMonitor monitor) {
						IssueDAO.sharedInstance().deleteIssue(issueIDToDelete, monitor);
						return Status.OK_STATUS;
					}
				};

				deleteIssueJob.schedule();
			}

		}

//		DeleteIssueAction.executeDeleteIssues(issueTable, getSelectedIssueIDs(), part.getSite().getShell());
	}

	// -----------------------------------------------------------------------------------------------------------------------------------|
	// ---[ The general routine for deleting selected Issues from the IssueTable
	// ]--------------------------------------------------------|
	// -----------------------------------------------------------------------------------------------------------------------------------|
	@Deprecated
	// We should hide this method, because someone could call this from outside
	// ;-) , Chairat
	/**
	 * In most cases, the execution of the deletion of a Set of {@link Issue}, based on their {@link IssueID}s,
	 * are directly corresponding to the {@link IssueTable} itself. Thus, given the parameters, we can execute
	 * the deletion, with UI-interfaces.
	 * @param issueTable the {@link IssueTable} from which the selected {@link Issue}s have been marked for deletion.
	 * @param issueIDs the corresponding {@link IssueID}s of the selected {@link Issue} marked for deletion.
	 *
	 */
	public static void executeDeleteIssues(IssueTable issueTable,
			Collection<IssueID> issueIDs, Shell shell) {
		for (IssueID issueID : issueIDs) {
			// Retrieve useful information to display for the user, indicating
			// the related Issue to be deleted.
			Issue issueToBeDeleted = issueTable.getElementByID(issueID);
			boolean result = MessageDialog
					.openConfirm(
							shell,
							Messages
									.getString("org.nightlabs.jfire.issuetracking.ui.overview.action.DeleteIssueAction.dialog.confirmDelete.title.text"), //$NON-NLS-1$
							Messages
									.getString("org.nightlabs.jfire.issuetracking.ui.overview.action.DeleteIssueAction.dialog.confirmDelete.description.text") //$NON-NLS-1$
									+ "(ID:" + ObjectIDUtil.longObjectIDFieldToString(issueID.issueID) + ") " //$NON-NLS-1$ //$NON-NLS-2$
									+ "\"" + issueToBeDeleted.getSubject().getText() + "\"" //$NON-NLS-1$ //$NON-NLS-2$
									+ "?"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			if (result) {
				final IssueID issueIDToDelete = issueID;
				Job deleteIssueJob = new Job(
						Messages
								.getString("org.nightlabs.jfire.issuetracking.ui.overview.action.DeleteIssueAction.job.deleteIssue") + issueIDToDelete) { //$NON-NLS-1$
					@Override
					protected IStatus run(ProgressMonitor monitor) {
						IssueDAO.sharedInstance().deleteIssue(issueIDToDelete,
								monitor);
						return Status.OK_STATUS;
					}
				};

				deleteIssueJob.schedule();
			}

		}
	}

}
