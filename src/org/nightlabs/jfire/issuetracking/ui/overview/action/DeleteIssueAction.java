package org.nightlabs.jfire.issuetracking.ui.overview.action;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.issue.IssueManagerUtil;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditor;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorInput;
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
		return getSelectedIssueIDs().size() == 1;
	}

	@Override
	public void run() {
		boolean result = MessageDialog.openConfirm(getActivePart().getSite().getShell(), "Confirm Delete", "Are you sure to delete this issue?");
		if (result == true) {
			IssueDAO.sharedInstance().deleteIssue(getSelectedIssueIDs().iterator().next(), new NullProgressMonitor());
		}
	}
}
