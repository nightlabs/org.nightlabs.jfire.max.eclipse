package org.nightlabs.jfire.personrelation.issuetracking.trade.ui;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.personrelation.ui.createrelation.CreatePersonRelationWizard;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeNode;
import org.nightlabs.jfire.prop.id.PropertySetID;

public class CreatePersonRelationAction implements IViewActionDelegate
{
	private IViewPart view;

	@Override
	public void init(IViewPart view) {
		this.view = view;
	}

	@Override
	public void run(IAction action) {
		PropertySetID personID = this.selectedPersonID;
		if (personID == null)
			return;

		CreatePersonRelationWizard wizard = new CreatePersonRelationWizard(personID);
		if (view != null)
			new DynamicPathWizardDialog(view.getSite().getShell(), wizard).open(); // The safer way to instantiate the Wizard?
		else
			new DynamicPathWizardDialog(wizard).open();
	}

	private PropertySetID selectedPersonID = null;

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		selectedPersonID = null;
		PersonRelationTreeNode node = PersonRelationTreeNode.getPersonRelationTreeNodeFromSelection(selection);
		if (node == null) {
			action.setEnabled(false);
			return;
		}

		selectedPersonID = node.getPropertySetID();
		action.setEnabled(selectedPersonID != null);
	}
}
