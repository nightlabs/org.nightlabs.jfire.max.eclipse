package org.nightlabs.jfire.personrelation.issuetracking.trade.ui;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.ui.PersonRelationTreeNode;
import org.nightlabs.jfire.prop.id.PropertySetID;

public class CreateOrLinkIssueAction implements IViewActionDelegate
{
	@Override
	public void init(IViewPart view) { }

	@Override
	public void run(IAction action) {

	}

	private PropertySetID selectedPersonID = null;

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		selectedPersonID = null;

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
		if (node.getJdoObjectID() instanceof PropertySetID)
			selectedPersonID = (PropertySetID) node.getJdoObjectID();
		else if (node.getJdoObject() instanceof PersonRelation) {
			PersonRelation pr = (PersonRelation) node.getJdoObject();
			selectedPersonID = pr.getToID();
		}
		action.setEnabled(selectedPersonID != null);
	}
}
