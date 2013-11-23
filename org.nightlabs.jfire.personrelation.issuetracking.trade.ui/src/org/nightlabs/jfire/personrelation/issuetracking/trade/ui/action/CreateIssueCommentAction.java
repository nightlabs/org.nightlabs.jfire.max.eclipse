package org.nightlabs.jfire.personrelation.issuetracking.trade.ui.action;

import javax.jdo.JDOHelper;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.IssueComment;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.id.IssueDescriptionID;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.personrelation.issuetracking.trade.ui.CreateIssueCommentDialog;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeNode;

public class CreateIssueCommentAction implements IViewActionDelegate
{
	private IViewPart view;

	@Override
	public void init(IViewPart view) {
		this.view = view;
	}

	@Override
	public void run(IAction action) {
		if (selectedIssueID == null)
			return;

		new CreateIssueCommentDialog(view.getSite().getShell(), selectedIssueID).open();
	}

	private IssueID selectedIssueID;

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		selectedIssueID = null;
		PersonRelationTreeNode node = PersonRelationTreeNode.getPersonRelationTreeNodeFromSelection(selection);
		if (node == null) {
			action.setEnabled(false);
			return;
		}

		Object jdoObject = node.getJdoObject();
		ObjectID objectID = node.getJdoObjectID();
		if (jdoObject  instanceof IssueLink) {
			IssueLink issueLink = (IssueLink) jdoObject;
			selectedIssueID = (IssueID) JDOHelper.getObjectId(issueLink.getIssue());
		}
		else if (objectID instanceof IssueDescriptionID) {
			IssueDescriptionID issueDescriptionID = (IssueDescriptionID)objectID;
			selectedIssueID = IssueID.create(issueDescriptionID.organisationID, issueDescriptionID.issueID);
		}
		else if (jdoObject instanceof IssueComment) {
			IssueComment issueComment = (IssueComment) jdoObject;
			selectedIssueID = issueComment.getIssueID();
		}

		action.setEnabled(selectedIssueID != null);
	}

}
