package org.nightlabs.jfire.personrelation.ui.tree;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.personrelation.ui.PersonRelationPlugin;
import org.nightlabs.jfire.prop.id.PropertySetID;

/**
 * Default {@link IPersonRelationTreeLabelProviderDelegate} to display {@link Person}-information.
 * 
 * @author Marco Schulze
 * @author khaireel
 * @author abieber
 */
public class DefaultPersonRelationTreeLabelProviderDelegatePerson extends AbstractPersonRelationTreeLabelProviderDelegate {

	public String getJDOObjectText(ObjectID jdoObjectID, Object jdoObject, int spanColIndex) {
		if (jdoObject == null) {
			if (jdoObjectID instanceof PropertySetID) {
				PropertySetID personID = (PropertySetID) jdoObjectID;

				switch (spanColIndex) {
					case 0:
						return personID.organisationID + '/' + personID.propertySetID;
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
			if (jdoObject instanceof Person) {
				Person person = (Person) jdoObject;

				switch (spanColIndex) {
					case 0:
						return person.getDisplayName(); // I have encountered cases where the displayName is not set; eg. when the check-box to auto-generate displayName is not selected. Any forthcoming solutions? Kai
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
		if (jdoObject instanceof Person) {
			return spanColIndex == 0
					? SharedImages.getSharedImage(PersonRelationPlugin.getDefault(), DefaultPersonRelationTreeLabelProviderDelegatePerson.class, jdoObject.getClass().getSimpleName())
					: null;
		}
		return null;
	}


	@Override
	public Class<? extends ObjectID> getJDOObjectIDClass() {
		return PropertySetID.class;
	}


	@Override
	public Class<?> getJDOObjectClass() {
		return Person.class;
	}


	@Override
	public int[][] getJDOObjectColumnSpan(ObjectID jdoObjectID, Object jdoObject) {
		if (jdoObjectID instanceof PropertySetID)
			return new int[][] { {0, 1} };

		return null;
	}

}
