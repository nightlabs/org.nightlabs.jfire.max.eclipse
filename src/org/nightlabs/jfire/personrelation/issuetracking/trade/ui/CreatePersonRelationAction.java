package org.nightlabs.jfire.personrelation.issuetracking.trade.ui;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.personrelation.ui.createrelation.CreatePersonRelationWizard;
import org.nightlabs.jfire.prop.id.PropertySetID;

public class CreatePersonRelationAction implements IViewActionDelegate
{
	private PersonRelationIssueTreeView view;

	@Override
	public void init(IViewPart view) {
		this.view = (PersonRelationIssueTreeView) view;
	}

	@Override
	public void run(IAction action) {
		PropertySetID personID = view.getPersonRelationTree().getSelectedInputPersonID();
		if (personID == null)
			return;

		CreatePersonRelationWizard wizard = new CreatePersonRelationWizard(personID);
		new DynamicPathWizardDialog(wizard).open();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}
}
