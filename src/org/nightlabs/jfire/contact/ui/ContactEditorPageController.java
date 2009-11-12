package org.nightlabs.jfire.contact.ui;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.dao.PropertySetDAO;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class ContactEditorPageController
extends ActiveEntityEditorPageController<Person>
{
	private static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT, PropertySet.FETCH_GROUP_FULL_DATA
	};

	public ContactEditorPageController(EntityEditor editor)
	{
		super(editor);
	}

	@Override
	protected String[] getEntityFetchGroups() {
		return FETCH_GROUPS;
	}

	@Override
	protected Person retrieveEntity(ProgressMonitor monitor) {
		ContactEditorInput input = (ContactEditorInput) getEntityEditor().getEditorInput();
		Person person = (Person)PropertySetDAO.sharedInstance().getPropertySet(input.getJDOObjectID(), getEntityFetchGroups(), getEntityMaxFetchDepth(), new SubProgressMonitor(monitor, 70));
		return person;
	}

	@Override
	protected Person storeEntity(Person controllerObject,
			ProgressMonitor monitor) {
		monitor.beginTask("Storing...", 100);
		try {
			controllerObject.deflate();
			controllerObject = (Person) PropertySetDAO.sharedInstance().storeJDOObject(
					controllerObject, true, FETCH_GROUPS, 1,
					new SubProgressMonitor(monitor, 100)
			);
			controllerObject.deflate();
		} finally {
			monitor.done();
		}
		return controllerObject;
	}
}
