package org.nightlabs.jfire.personrelation.ui;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.PersonRelationType;
import org.nightlabs.jfire.personrelation.id.PersonRelationID;

/**
 * Specificity: Handles special icons to display "companyGroup" and "subsidiary" relations.
 *
 * @author khaireel
 */
public class CompanyPersonRelationTreeLabelProviderDelegate extends PersonRelationTreeLabelProviderDelegate {
	@Override
	public Class<?> getJDOObjectClass() {
		return PersonRelation.class;
	}

	@Override
	public Class<? extends ObjectID> getJDOObjectIDClass() {
		return PersonRelationID.class;
	}

	@Override
	public Image getJDOObjectImage(ObjectID jdoObjectID, Object jdoObject, int spanColIndex) {
		if (spanColIndex == 0 && jdoObject != null) {
			String prtString = ((PersonRelation) jdoObject).getPersonRelationType().getPersonRelationTypeID();
//			String prtString = ((PersonRelation) jdoObject).getPersonRelationType().getReversePersonRelationTypeID().personRelationTypeID;
			if (prtString.equals(PersonRelationType.PredefinedRelationTypes.companyGroup.personRelationTypeID) ||
				prtString.equals(PersonRelationType.PredefinedRelationTypes.subsidiary.personRelationTypeID)) {

				return SharedImages.getSharedImage(PersonRelationPlugin.getDefault(), CompanyPersonRelationTreeLabelProviderDelegate.class, prtString);
			}
		}

		return null;
	}

	@Override
	public int[][] getJDOObjectColumnSpan(ObjectID jdoObjectID, Object jdoObject) {
		return null; // null means each real column assigned to one visible column
	}

	@Override
	public String getJDOObjectText(ObjectID jdoObjectID, Object jdoObject, int spanColIndex) {
		return null; // Returning null will execute the default behaviour (in the original TreeLabelProvider) for displaying whatever text.
	}
}
