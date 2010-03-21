package org.nightlabs.jfire.personrelation.trade.ui.tucked;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTree;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeNode;

/**
 * An extended version of the original {@link PersonRelationTreeNode}. This 'tucked' node contains an ordered path of {@link PersonRelationTreeNode}s,
 * collectively displaying the 'tucked path' in place of expanded path children.
 *
 * See notes on "Search by Association".
 *
 * @author khaireel (at) nightlabs (dot) de
 */
public class TuckedPersonRelationTreeNode extends PersonRelationTreeNode {
	// ------- FARK-Tests ---------------------------------------------------------------------------------------||----->>
	protected String tuckedPathOfDisplayNames = null;

	/**
	 * @return the 'tucked' path of displayNames, from the root, down to this node.
	 */
	public String getTuckedPathOfDisplayNames() {
		if (tuckedPathOfDisplayNames == null) {
			TuckedPersonRelationTreeNode parentNode = (TuckedPersonRelationTreeNode) getParent();
			Object jdoObject = getJdoObject();

			// Recursive method to retrieve the path of displayNames.
			// Base case.
			if (parentNode == null || jdoObject == null)
				return "";

			// Iterative-case.
			Person person = null;
			if (jdoObject instanceof PersonRelation)
				person = ((PersonRelation) jdoObject).getTo();
			else if (jdoObject instanceof Person)
				person = (Person) jdoObject;

			// Guard.
			if (person == null)
				return "";

			String displayNamesFromParent = parentNode.getTuckedPathOfDisplayNames();
			if (!displayNamesFromParent.equals("")) displayNamesFromParent = String.format("%s;  ", displayNamesFromParent);

			tuckedPathOfDisplayNames = String.format("%s%s", displayNamesFromParent, person.getDisplayName());
		}

		return tuckedPathOfDisplayNames;
	}


	public void setActualChildCount(ObjectID objectID, long actualChildCount) {
		System.err.println("~~~~~~~~~ Actual child count [" + PersonRelationTree.showObjectID(objectID) + "]: " + actualChildCount);
	}
	// ------- FARK-Tests ---------------------------------------------------------------------------------------||----->>
}
