package org.nightlabs.jfire.contact.email.ui;

import java.awt.Desktop;
import java.net.URI;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.nightlabs.jfire.contact.ui.ContactDetailView;
import org.nightlabs.jfire.person.PersonStruct;
import org.nightlabs.jfire.prop.DataField;
import org.nightlabs.jfire.prop.PropertySet;

/**
 * @author Daniel Mazurek <daniel at nightlabs dot de> 
 *
 */
public class ContactDetailEMailAction implements IViewActionDelegate 
{
	private ContactDetailView view;
	
	@Override
	public void init(IViewPart view) {
		this.view = (ContactDetailView) view;
	}

	@Override
	public void run(IAction action) {
		final PropertySet selectedPerson = view.getPerson();
		if (selectedPerson == null || !Desktop.isDesktopSupported())
			return;

		try {
			DataField emailField = selectedPerson.getDataField(PersonStruct.INTERNET_EMAIL);
			String email = (String) emailField.getData();
			URI mailtoURI = new URI("mailto:"+email);
			Desktop.getDesktop().mail(mailtoURI);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing
	}

}
