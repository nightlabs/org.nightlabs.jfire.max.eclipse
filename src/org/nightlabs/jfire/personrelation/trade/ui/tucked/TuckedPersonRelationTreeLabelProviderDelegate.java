package org.nightlabs.jfire.personrelation.trade.ui.tucked;

import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.person.PersonStruct;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.PersonRelationType;
import org.nightlabs.jfire.personrelation.id.PersonRelationID;
import org.nightlabs.jfire.personrelation.trade.ui.Activator;
import org.nightlabs.jfire.personrelation.ui.tree.AbstractPersonRelationTreeLabelProviderDelegate;
import org.nightlabs.jfire.personrelation.ui.tree.IPersonRelationTreeLabelProviderDelegate;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTree;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeController;
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
public class TuckedPersonRelationTreeLabelProviderDelegate extends AbstractPersonRelationTreeLabelProviderDelegate {
	private PersonRelationTreeController<TuckedPersonRelationTreeNode> personRelationTreeController;

	/**
	 * Creates a new instance of the TuckedPersonRelationTreeLabelProviderDelegate with a specific reference
	 * to the PersonRelationTreeController.
	 */
	public TuckedPersonRelationTreeLabelProviderDelegate(PersonRelationTreeController<TuckedPersonRelationTreeNode> personRelationTreeController) {
		this.personRelationTreeController = personRelationTreeController;
	}

	@Override
	public Class<?> getJDOObjectClass() { return PersonRelation.class; }

	@Override
	public Class<? extends ObjectID> getJDOObjectIDClass() { return PersonRelationID.class; }

	@Override
	public int[][] getJDOObjectColumnSpan(ObjectID jdoObjectID, Object jdoObject) { return null; }

	@Override
	public String getJDOObjectText(ObjectID jdoObjectID, Object jdoObject, int spanColIndex) {
		if (jdoObject != null && spanColIndex == 0) {
			// 1. Display the usual content of the node represented by the given jdoObjectID.
			// 2. Also display the "tucked" information of this node, whenever the information becomes available.
			
			// From 1.
			String defaultText = getDefaultJDOObjectText((PersonRelation) jdoObject);
			
			// From 2.
			// Cautiously work to access the data of the LAZY tree.
			// See notes on progress-steps.
			// Lazy-progress: Step 1.
			List<TuckedPersonRelationTreeNode> treeNodeList = personRelationTreeController.getTreeNodeList(jdoObjectID);
			if (treeNodeList == null || treeNodeList.isEmpty())
				return defaultText;
			
			// Lazy progress: Step 2.
			TuckedPersonRelationTreeNode tuckedNode = treeNodeList.get(0);
			if (tuckedNode == null)
				return defaultText;
			
			// Now we can append the "tucked" information of the node. 
			// TODO Figure out some sort of standard.
			return String.format("%s %s", defaultText, tuckedNode.getTuckedInfoStatus(jdoObjectID));
		}

		return null; // Let the default LabelProvider in the PersonRelationTree handle this.
	}
	
	private String getDefaultJDOObjectText(PersonRelation personRelation) {
		String personRelationTypeID = personRelation.getPersonRelationType().getPersonRelationTypeID();

		// Display the city field for companyGroup and subsidiary relations, if available.
		Person person = personRelation.getTo();
		if (personRelationTypeID.equals(PersonRelationType.PredefinedRelationTypes.companyGroup.personRelationTypeID)
				|| personRelationTypeID.equals(PersonRelationType.PredefinedRelationTypes.subsidiary.personRelationTypeID)) {

			String cityField = getFieldInfo(person, PersonStruct.POSTADDRESS_CITY);
			if (cityField != null && !cityField.isEmpty()) cityField = String.format(", (%s)", cityField);

			return String.format("%s%s", person.getDisplayName(), cityField);
		}

		return person.getDisplayName();
	}

	@Override
	public Image getJDOObjectImage(ObjectID jdoObjectID, Object jdoObject, int spanColIndex) {
		if (jdoObject != null && spanColIndex == 0) {
			PersonRelation personRelation = (PersonRelation) jdoObject;
			String personRelationTypeID = personRelation.getPersonRelationType().getReversePersonRelationTypeID().personRelationTypeID; // Somehow, it's the reverse we want to dislay. Kai.

			if (hasImageForPersonRelationTypeID(personRelationTypeID))
				return SharedImages.getSharedImage(Activator.getDefault(), TuckedPersonRelationTreeLabelProviderDelegate.class, personRelationTypeID);
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
