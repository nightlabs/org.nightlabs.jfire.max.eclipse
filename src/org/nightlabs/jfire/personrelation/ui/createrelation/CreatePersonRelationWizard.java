package org.nightlabs.jfire.personrelation.ui.createrelation;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.person.search.PersonSearchWizardPage;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.personrelation.PersonRelationType;
import org.nightlabs.jfire.personrelation.dao.PersonRelationDAO;
import org.nightlabs.jfire.personrelation.id.PersonRelationTypeID;
import org.nightlabs.jfire.personrelation.ui.resource.Messages;
import org.nightlabs.jfire.prop.dao.PropertySetDAO;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

public class CreatePersonRelationWizard
extends DynamicPathWizard
{
	private PropertySetID fromPersonID;

	private SelectPersonRelationTypePage selectPersonRelationTypePage;
	private PersonSearchWizardPage personSearchWizardPage;

	public CreatePersonRelationWizard(PropertySetID fromPersonID) {
		if (fromPersonID == null)
			throw new IllegalArgumentException("fromPersonID must not be null!"); //$NON-NLS-1$

		this.fromPersonID = fromPersonID;
	}

	@Override
	public void addPages() {
		selectPersonRelationTypePage = new SelectPersonRelationTypePage();
		addPage(selectPersonRelationTypePage);

		personSearchWizardPage = new PersonSearchWizardPage(null); // TODO null supported? Or do I have to pass ""?!
		addPage(personSearchWizardPage);
	}

	@Override
	public boolean performFinish()
	{
		Collection<PersonRelationType> selectedPersonRelationTypes = selectPersonRelationTypePage.getSelectedElements();
		final Collection<PersonRelationTypeID> selectedPersonRelationTypeIDs = NLJDOHelper.getObjectIDList(selectedPersonRelationTypes);
		final Person selectedPerson = personSearchWizardPage.getSelectedPerson();

		try {
			getContainer().run(true, false, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor i_monitor) throws InvocationTargetException, InterruptedException
				{
					ProgressMonitor monitor = new ProgressMonitorWrapper(i_monitor);
					monitor.beginTask(Messages.getString("org.nightlabs.jfire.personrelation.ui.createrelation.CreatePersonRelationWizard.task.creatingPersonRelation.name"), 2 + selectedPersonRelationTypeIDs.size()); //$NON-NLS-1$
					try {
						PropertySetID toPersonID = null;
						if (!JDOHelper.isDetached(selectedPerson)) {
							// The person is new (not yet persisted), we need to first store the person
							selectedPerson.deflate();
							Person person = (Person) PropertySetDAO.sharedInstance().storeJDOObject(
									selectedPerson, true, null, 1,
									new SubProgressMonitor(monitor, 2)
							);
							toPersonID = (PropertySetID) JDOHelper.getObjectId(person);
						} else {
							toPersonID = (PropertySetID) JDOHelper.getObjectId(selectedPerson);
						}


						for (PersonRelationTypeID personRelationTypeID : selectedPersonRelationTypeIDs) {
							PersonRelationDAO.sharedInstance().createPersonRelation(
									personRelationTypeID, fromPersonID, toPersonID,
									new SubProgressMonitor(monitor, 1)
							);
						}
					} finally {
						monitor.done();
					}
				}
			});
		} catch (Exception x) {
			throw new RuntimeException(x);
		}

		return true;
	}

}
