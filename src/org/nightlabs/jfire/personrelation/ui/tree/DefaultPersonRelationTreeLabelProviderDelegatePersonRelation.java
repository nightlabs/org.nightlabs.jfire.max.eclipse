package org.nightlabs.jfire.personrelation.ui.tree;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.id.PersonRelationID;
import org.nightlabs.jfire.personrelation.ui.PersonRelationPlugin;
import org.nightlabs.util.NLLocale;

/**
 * Default {@link IPersonRelationTreeLabelProviderDelegate} to display {@link PersonRelation}-information.
 * 
 * @author Marco Schulze
 * @author khaireel
 * @author abieber
 */
public class DefaultPersonRelationTreeLabelProviderDelegatePersonRelation extends AbstractPersonRelationTreeLabelProviderDelegate {
	
	private String languageID = NLLocale.getDefault().getLanguage();

	public String getJDOObjectText(ObjectID jdoObjectID, Object jdoObject, int spanColIndex) {
		if (jdoObject == null) {
			if (jdoObjectID instanceof PersonRelationID) {
				PersonRelationID personRelationID = (PersonRelationID) jdoObjectID;

				switch (spanColIndex) {
					case 0:
						return personRelationID.organisationID + '/' + personRelationID.personRelationID;
					default:
						break;
				}
			}
			else {
				switch (spanColIndex) {
					case 0:
						return String.valueOf(jdoObjectID);
					default:
						break;
				}
			}
		}
		else {
			if (jdoObject instanceof PersonRelation) {
				PersonRelation personRelation = (PersonRelation) jdoObject;

				switch (spanColIndex) {
					case 0:
						return personRelation.getPersonRelationType().getName().getText(languageID);
					case 1:
						return personRelation.getTo().getDisplayName();
					default:
						break;
				}
			}
			else {
				switch (spanColIndex) {
					case 0:
						return String.valueOf(jdoObject);
					default:
						break;
				}
			}
		}

		return null;
	}

	public Image getJDOObjectImage(ObjectID jdoObjectID, Object jdoObject, int spanColIndex)
	{
		if (jdoObject instanceof PersonRelation) {
			if (spanColIndex == 0) {
				String suffix = jdoObject.getClass().getSimpleName();
				return SharedImages.getSharedImage(PersonRelationPlugin.getDefault(), DefaultPersonRelationTreeLabelProviderDelegatePersonRelation.class, suffix);
			}
			else
				return null;
		}

		return null;
	}


	@Override
	public Class<? extends ObjectID> getJDOObjectIDClass() {
		return PersonRelationID.class;
	}


	@Override
	public Class<?> getJDOObjectClass() {
		return PersonRelation.class;
	}


	@Override
	public int[][] getJDOObjectColumnSpan(ObjectID jdoObjectID, Object jdoObject) {
		if (jdoObjectID instanceof PersonRelationID)
			return null; // null means each real column assigned to one visible column
		return null;
	}

}
