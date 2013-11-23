package org.nightlabs.jfire.personrelation.issuetracking.trade.ui.extended;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.person.PersonStruct;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.PersonRelationType;
import org.nightlabs.jfire.personrelation.id.PersonRelationID;
import org.nightlabs.jfire.personrelation.issuetracking.trade.ui.Activator;
import org.nightlabs.jfire.personrelation.ui.tree.AbstractPersonRelationTreeLabelProviderDelegate;
import org.nightlabs.jfire.personrelation.ui.tree.IPersonRelationTreeLabelProviderDelegate;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTree;
import org.nightlabs.jfire.prop.DataField;
import org.nightlabs.jfire.prop.datafield.SelectionDataField;
import org.nightlabs.jfire.prop.datafield.TextDataField;
import org.nightlabs.jfire.prop.id.StructFieldID;

/**
 * The default extended PersonRelationTreeLabelProviderDelegate that should be plugged into the {@link PersonRelationTree}'s collection
 * {@link IPersonRelationTreeLabelProviderDelegate}, which specifically handles the {@link LabelProvider}s for displaying JFire's
 * known default {@link PersonRelationType}s.
 *
 * @author khaireel (at) nightlabs (dot) de
 */
public class PersonRelationTreeLabelProviderDelegate extends AbstractPersonRelationTreeLabelProviderDelegate {
	@Override
	public Class<?> getJDOObjectClass() { return PersonRelation.class; }

	@Override
	public Class<? extends ObjectID> getJDOObjectIDClass() { return PersonRelationID.class; }

	@Override
	public int[][] getJDOObjectColumnSpan(ObjectID jdoObjectID, Object jdoObject) { return null; }

	@Override
	public String getJDOObjectText(ObjectID jdoObjectID, Object jdoObject, int spanColIndex) {
		if (jdoObject != null && spanColIndex == 1) {
			PersonRelation personRelation = (PersonRelation) jdoObject;
			String personRelationTypeID = personRelation.getPersonRelationType().getPersonRelationTypeID();

			// Display the city field for companyGroup and subsidiary relations, if available.
			if (personRelationTypeID.equals(PersonRelationType.PredefinedRelationTypes.companyGroup.personRelationTypeID)
					|| personRelationTypeID.equals(PersonRelationType.PredefinedRelationTypes.subsidiary.personRelationTypeID)) {

				Person person = personRelation.getTo();
				String cityField = getFieldInfo(person, PersonStruct.POSTADDRESS_CITY);
				if (cityField != null && !cityField.isEmpty()) cityField = String.format(", (%s)", cityField);

				return String.format("%s%s", person.getDisplayName(), cityField);
			}
		}

		return null; // Let the default LabelProvider in the PersonRelationTree handle this.
	}

	@Override
	public Image getJDOObjectImage(ObjectID jdoObjectID, Object jdoObject, int spanColIndex) {
		if (jdoObject != null && spanColIndex == 0) {
			PersonRelation personRelation = (PersonRelation) jdoObject;
			String personRelationTypeID = personRelation.getPersonRelationType().getReversePersonRelationTypeID().personRelationTypeID; // Somehow, it's the reverse we want to dislay. Kai.

			if (hasImageForPersonRelationTypeID(personRelationTypeID))
				return SharedImages.getSharedImage(Activator.getDefault(), PersonRelationTreeLabelProviderDelegate.class, personRelationTypeID);
		}

		return null;
	}

	/**
	 * @return true if we have a specialised image for the given personRelationTypeID.
	 */
	private boolean hasImageForPersonRelationTypeID(String personRelationTypeID) {
		// There are special icons for the PersonRelationTypes:
		return personRelationTypeID.equals(PersonRelationType.PredefinedRelationTypes.companyGroup.personRelationTypeID)   // (since 2010.03.15)
				|| personRelationTypeID.equals(PersonRelationType.PredefinedRelationTypes.subsidiary.personRelationTypeID) // (since 2010.03.15)
				|| personRelationTypeID.equals(PersonRelationType.PredefinedRelationTypes.employed.personRelationTypeID)   // (ever since)
				|| personRelationTypeID.equals(PersonRelationType.PredefinedRelationTypes.employing.personRelationTypeID); // (ever since)
	}

	/**
	 * @return the information tagged to the given person by the identifier structField.
	 * Returns an empty String if no such information found.
	 */
	private String getFieldInfo(Person person, StructFieldID structFieldID) {
		try {
			DataField dataField = person.getDataField(structFieldID);
			if (dataField instanceof TextDataField)
				return ((TextDataField) dataField).getText();

			if (dataField instanceof SelectionDataField)
				return ((SelectionDataField) dataField).getI18nText().getText();

			return ""; //$NON-NLS-1$

		} catch (Exception e) {
			return ""; //$NON-NLS-1$
		}
	}
}
