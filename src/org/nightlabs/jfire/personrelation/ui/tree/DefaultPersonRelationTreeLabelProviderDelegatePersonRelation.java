package org.nightlabs.jfire.personrelation.ui.tree;

import java.io.ByteArrayInputStream;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.PersonRelationType;
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
			PersonRelation personRelation = (PersonRelation) jdoObject;
			if (spanColIndex == 0) {
				PersonRelationType personRelationType = personRelation.getPersonRelationType();
				String imageKey = "PersonRelationType-" + personRelationType.getPersonRelationTypeID() + ".16x16";
				ImageRegistry imageRegistry = PersonRelationPlugin.getDefault().getImageRegistry();
				Image image = imageRegistry.get(imageKey);
				if (image == null && personRelationType.getIcon16x16Data() != null) {
					try {
						image = new Image(null, new ImageData(new ByteArrayInputStream(personRelationType.getIcon16x16Data())));
						imageRegistry.put(imageKey, image);
					} catch (Exception e) {
						// rather display no image than having an error here...
						image = null;
					}
				}
				if (image != null)
					return image;
				return SharedImages.getSharedImage(PersonRelationPlugin.getDefault(), DefaultPersonRelationTreeLabelProviderDelegatePersonRelation.class);
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
