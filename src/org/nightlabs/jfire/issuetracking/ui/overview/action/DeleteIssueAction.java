package org.nightlabs.jfire.issuetracking.ui.overview.action;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueTable;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListEditor;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListViewer;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

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
		// Something very fragile here... this handles the deletion of multiple Issues, selected from the IssueTable.
		for (IssueID issueID : getSelectedIssueIDs()) {
			boolean result = MessageDialog.openConfirm(
					getActivePart().getSite().getShell(),
					Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.action.DeleteIssueAction.dialog.confirmDelete.title.text"),
					Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.action.DeleteIssueAction.dialog.confirmDelete.description.text") + ObjectIDUtil.longObjectIDFieldToString(issueID.issueID) + "?"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			// Perform the delete only upon the confirmation from the UI.
			// Erh... how do I get access to the freaking table???
			IWorkbenchPart part = getActivePart();
			final IssueTable issueTable = ((IssueEntryListViewer) ((IssueEntryListEditor)part).getEntryViewer()).getIssueTable(); // <-- Uughhh :(
			final IssueID issueIDToDelete = issueID;

			if (result == true) {
				Job deleteIssueJob = new Job("Deleting issue with ID: " + issueIDToDelete) {
					@Override
					protected IStatus run(ProgressMonitor monitor) {
						IssueDAO.sharedInstance().deleteIssue(issueIDToDelete, new SubProgressMonitor(monitor, 50));

						// Shouldn't we refresh the table here?
						// --> i.e. at least remove the deleted entry from the UI.
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								if (issueTable.removeElementByID(issueIDToDelete))
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
