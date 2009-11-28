package org.nightlabs.jfire.contact.pbx.ui.call.action;

import javax.jdo.JDOHelper;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.nightlabs.jfire.contact.ui.ContactDetailView;
import org.nightlabs.jfire.pbx.ui.call.CallHandlerRegistry;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.id.PropertySetID;

public class ContactDetailViewCallAction implements IViewActionDelegate {
	private ContactDetailView view;

	@Override
	public void init(IViewPart view) {
		this.view = (ContactDetailView) view;
	}

	@Override
	public void run(IAction action) {
		final PropertySet selectedPerson = view.getPerson();
		if (selectedPerson == null)
			return;

		PropertySetID personID = (PropertySetID) JDOHelper.getObjectId(selectedPerson);
		CallHandlerRegistry.sharedInstance().call(personID);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
