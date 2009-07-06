package org.nightlabs.jfire.issuetracking.ui.overview.action;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueTable;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListEditor;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListViewer;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

public class DeleteIssueAction
extends AbstractIssueAction
{
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

	@Override
	public void run() {
		// It's possible that multiple Issues have been selected to be deleted.
		for (IssueID issueID : getSelectedIssueIDs()) {
			// Perform the delete only upon the confirmation from the UI.
			// Erh... how the heck do I get access to the freaking table???
			IWorkbenchPart part = getActivePart();
			final IssueTable issueTable = ((IssueEntryListViewer) ((IssueEntryListEditor)part).getEntryViewer()).getIssueTable(); // <-- Uughhh :(

			// Won't it be more user-friendly if we ALSO show the Subject of the Issue to be deleted?
			// i.e. Just the IssueID seem very rigid inside a User dialog. Kai.
			Issue issueToBeDeleted = issueTable.getElementByID(issueID);
			boolean result = MessageDialog.openConfirm(
					getActivePart().getSite().getShell(),
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
						IssueDAO.sharedInstance().deleteIssue(issueIDToDelete, monitor); // new SubProgressMonitor(monitor, 50));

						// Shouldn't we refresh the table here? Otherwise, it makes no sense that any deletion has occured.
						// i.e. At least remove the deleted entry from the UI.
						// --> Alright, let's give it a try... Kai.
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								if (issueTable.removeElementByID(issueIDToDelete) != null)
									issueTable.refresh();
							}
						});

						return Status.OK_STATUS;
					}
				};

				deleteIssueJob.schedule();
			}
		}
	}
}
