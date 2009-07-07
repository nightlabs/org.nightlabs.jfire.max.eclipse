package org.nightlabs.jfire.personrelation.issuetracking.trade.ui;

import javax.jdo.JDOHelper;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueComment;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.id.IssueDescriptionID;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.personrelation.ui.PersonRelationTreeNode;

public class CreateIssueCommentAction implements IViewActionDelegate {

	@Override
	public void init(IViewPart view) { }

	@Override
	public void run(IAction action) {
	}

	private IssueID selectedIssueID;

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		selectedIssueID = null;

		if (selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
			action.setEnabled(false);
			return;
		}

		IStructuredSelection sel = (IStructuredSelection) selection;
		if (sel.size() != 1 || sel.getFirstElement() == null) {
			action.setEnabled(false);
			return;
		}

		Object object = sel.getFirstElement();
		if (!(object instanceof PersonRelationTreeNode)) {
			action.setEnabled(false);
			return;
		}

		PersonRelationTreeNode node = (PersonRelationTreeNode) object;
		if (node.getJdoObject() instanceof IssueLink) {
			Issue issue = ((IssueLink) node.getJdoObject()).getIssue();
			selectedIssueID = (IssueID) JDOHelper.getObjectId(issue);
		}
		else if (node.getJdoObjectID() instanceof IssueDescriptionID) {
			IssueDescriptionID issueDescriptionID = (IssueDescriptionID) node.getJdoObjectID();
			selectedIssueID = IssueID.create(issueDescriptionID.organisationID, issueDescriptionID.issueID);
		}
		else if (node.getJdoObject() instanceof IssueComment) {
			IssueComment issueComment = (IssueComment) node.getJdoObject();
			selectedIssueID = issueComment.getIssueID();
		}
		action.setEnabled(selectedIssueID != null);
	}

}
