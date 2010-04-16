package org.nightlabs.jfire.personrelation.trade.ui.compact;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.id.PersonRelationID;
import org.nightlabs.jfire.personrelation.ui.tree.AbstractPersonRelationTreeLabelProviderDelegate;

/**
 * Another label provider delegate to hand {@link PersonRelation} occurrences in the tree.
 *
 * @author khaireel at nightlabs dot de
 */
public class CompactedPersonRelationTreeLabelProviderDelegate extends AbstractPersonRelationTreeLabelProviderDelegate {

	@Override
	public Class<?> getJDOObjectClass() { return PersonRelation.class; }

	@Override
	public Class<? extends ObjectID> getJDOObjectIDClass() { return PersonRelationID.class; }
	
	@Override
	public int[][] getJDOObjectColumnSpan(ObjectID jdoObjectID, Object jdoObject) { return null; }

	@Override
	public String getJDOObjectText(ObjectID jdoObjectID, Object jdoObject, int spanColIndex) {
		if (jdoObject != null && spanColIndex == 0) {
			// FIXME For a more appropriate operational display.
			PersonRelation personRelation = (PersonRelation) jdoObject;
			Person person = personRelation.getTo();
			
			return person.getDisplayName();
		}
		
		return null;
	}
}
