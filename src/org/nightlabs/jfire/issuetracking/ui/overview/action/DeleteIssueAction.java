package org.nightlabs.jfire.issuetracking.ui.overview.action;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.progress.NullProgressMonitor;

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
		for (IssueID issueID : getSelectedIssueIDs()) {
			boolean result = MessageDialog.openConfirm(getActivePart().getSite().getShell(), "Confirm Delete", "Are you sure to delete this issue?");
			if (result == true) {
				IssueDAO.sharedInstance().deleteIssue(issueID, new NullProgressMonitor());
			}
		}
	}
}
