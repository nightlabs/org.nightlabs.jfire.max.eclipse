package org.nightlabs.jfire.personrelation.issuetracking.trade.ui;

import javax.jdo.JDOHelper;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.IssueComment;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.id.IssueDescriptionID;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditor;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorInput;
import org.nightlabs.jfire.personrelation.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeNode;

/**
 * Opens the Issue perspective and displays the editor to show the selected Issue.
 *
 * @author khaireel
 */
public class OpenIssueEditorAction extends Action implements IViewActionDelegate {
	private IssueID issueID = null;
	private IViewPart view;

	public OpenIssueEditorAction() {
		setId(OpenIssueEditorAction.class.getName());
		setText(Messages.getString("org.nightlabs.jfire.personrelation.issuetracking.trade.ui.OpenIssueEditorAction.text")); //$NON-NLS-1$
	}

	@Override
	public void run() {
		if (issueID != null) {
			IssueEditorInput issueEditorInput = new IssueEditorInput(issueID);
			try {
				Editor2PerspectiveRegistry.sharedInstance().openEditor(issueEditorInput, IssueEditor.EDITOR_ID);
			} catch (Exception e1) {
				throw new RuntimeException(e1);
			}
		}
	}

	@Override
	public void init(IViewPart view) { this.view = view; }

	@Override
	public void run(IAction action) { run(); }

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		issueID = null;
		PersonRelationTreeNode node = PersonRelationTreeNode.getPersonRelationTreeNodeFromSelection(selection);
		if (node == null) {
			action.setEnabled(false);
			return;
		}

		Object jdoObject = node.getJdoObject();
		ObjectID objectID = node.getJdoObjectID();
		if (jdoObject  instanceof IssueLink) {
			IssueLink issueLink = (IssueLink) jdoObject;
			issueID = (IssueID) JDOHelper.getObjectId(issueLink.getIssue());
		}
		else if (objectID instanceof IssueDescriptionID) {
			IssueDescriptionID issueDescriptionID = (IssueDescriptionID)objectID;
			issueID = IssueID.create(issueDescriptionID.organisationID, issueDescriptionID.issueID);
		}
		else if (jdoObject instanceof IssueComment) {
			IssueComment issueComment = (IssueComment) jdoObject;
			issueID = issueComment.getIssueID();
		}

		action.setEnabled(issueID != null);
	}

}
