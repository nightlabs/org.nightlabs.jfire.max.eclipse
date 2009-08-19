package org.nightlabs.jfire.personrelation.ui.createrelation;

import java.util.Collection;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.personrelation.PersonRelationType;
import org.nightlabs.jfire.personrelation.ui.PersonRelationTypeList;
import org.nightlabs.jfire.personrelation.ui.resource.Messages;

public class SelectPersonRelationTypePage
extends WizardHopPage
{
	private PersonRelationTypeList personRelationTypeList;

	public SelectPersonRelationTypePage() {
		super(SelectPersonRelationTypePage.class.getName(), Messages.getString("org.nightlabs.jfire.personrelation.ui.createrelation.SelectPersonRelationTypePage.page.title")); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.personrelation.ui.createrelation.SelectPersonRelationTypePage.page.description")); //$NON-NLS-1$
	}

	@Override
	public Control createPageContents(Composite parent) {
		personRelationTypeList = new PersonRelationTypeList(parent);
		personRelationTypeList.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateStatus(null);
			}
		});
		return personRelationTypeList;
	}

	@Override
	public boolean isPageComplete() {
		if (personRelationTypeList == null)
			return false;

		return !personRelationTypeList.getSelection().isEmpty();
	}

	public Collection<PersonRelationType> getSelectedElements() {
		return personRelationTypeList.getSelectedElements();
	}
}
