package org.nightlabs.jfire.contact.ui;

import javax.jdo.FetchPlan;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditor;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.person.PersonStruct;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.dao.PropertySetDAO;
import org.nightlabs.jfire.prop.datafield.TextDataField;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class ContactEditor
extends ActiveEntityEditor
{
	public static final String EDITOR_ID = ContactEditor.class.getName();

	private static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT, PropertySet.FETCH_GROUP_FULL_DATA
	};

	@Override
	protected String getEditorTitleFromEntity(Object entity) {
		if (entity instanceof Person) {
			Person person = (Person)entity;
			String titleString;
			try {
				titleString = ((TextDataField)person.getDataField(PersonStruct.PERSONALDATA_NAME)).getText();
			} catch (Exception ex) {
				titleString = "";
			}
			return "(ID:" + ObjectIDUtil.longObjectIDFieldToString(person.getPropertySetID()) + ") " + titleString;
		}

		return null;
	}

	@Override
	protected Object retrieveEntityForEditorTitle(ProgressMonitor monitor) {
		PropertySetID personID = ((ContactEditorInput)getEditorInput()).getJDOObjectID();
		assert personID != null;
		return PropertySetDAO.sharedInstance().getPropertySet(personID, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}

	@Override
	protected String getEditorTooltipFromEntity(Object entity) {
		if (entity instanceof Person) {
			Person person = (Person)entity;
			String tooltipString;
			try {
				tooltipString = String.format("%s %s - %s",  //$NON-NLS-1$
						((TextDataField)person.getDataField(PersonStruct.PERSONALDATA_NAME)).getText(),
						((TextDataField)person.getDataField(PersonStruct.PERSONALDATA_FIRSTNAME)).getText(),
						((TextDataField)person.getDataField(PersonStruct.PERSONALDATA_COMPANY)).getText());
				;
			} catch (Exception ex) {
				tooltipString = "";
			}
			return tooltipString;
		}

		return null;
	}
}
