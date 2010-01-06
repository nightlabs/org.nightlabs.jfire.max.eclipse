package org.nightlabs.jfire.personrelation.ui;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOObjectLazyTreeNode;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.id.PersonRelationID;
import org.nightlabs.jfire.prop.id.PropertySetID;

public class PersonRelationTreeNode
extends JDOObjectLazyTreeNode<ObjectID, Object, PersonRelationTreeController>
{
	// Attempt to stop the unending iteration. At least for use with Behr's descriptions. See notes.
	/**
	 * Based on the PropertySetID represented by this node, check to see if the same PropertySetID
	 * appears anywhere on the path from this node to the root.
	 * @return true if this PropertySetID on the path to the root is unique (and thus allowing for
	 * its children to continue to be loaded, whenever necessary). Returning false signifies that this
	 * node is a 'repeat' element, suggesting the beginnig of the next repeated-iterative bunch of
	 * craps, which have already been loaded and displayed.
	 */
	public boolean isContinueToLoadChildren() {
		List<PropertySetID> propSetIDsToRoot = getPropertySetIDsToRoot();
		if (propSetIDsToRoot.size() > 1) {
			PropertySetID psID = propSetIDsToRoot.remove(0);
			return !propSetIDsToRoot.contains(psID);
		}

		return true;
	}

	@Override
	public long getChildNodeCount() {
		// The related (lazy) tree-controller calls this node's method to determine how many children
		// the node has. But if the PropertySetID represented by this node has already once appeared
		// on the path to the root, then we force the child count to zero.
		return isContinueToLoadChildren() ? super.getChildNodeCount() : 0L;
	}

	@Override
	public PersonRelationTreeNode getParent() {
		return (PersonRelationTreeNode)super.getParent();
	}

	/**
	 * @return the PropertySetID represented by this node. Returns null if node does not present a Person-related object.
	 */
	public PropertySetID getPropertySetID() {
		ObjectID jdoObjectID = getJdoObjectID();
		if (jdoObjectID instanceof PropertySetID)
			return (PropertySetID) jdoObjectID;

		else {
			Object jdoObject = getJdoObject();
			if (jdoObject instanceof PersonRelation)
				return ((PersonRelation)jdoObject).getToID();
		}

		return null;
	}

	/**
	 * @return a List of PropertySetIDs ordered from this node all the way up to the root.
	 */
	public List<PropertySetID> getPropertySetIDsToRoot() {
		return getPropertySetIDsToRoot(this, new LinkedList<PropertySetID>());
	}

	private List<PropertySetID> getPropertySetIDsToRoot(PersonRelationTreeNode node, List<PropertySetID> propSetIDs) {
		// Base case.
		if (node.getParent() == null)
			return propSetIDs;

		// Iterative case.
		ObjectID jdoObjectID = node.getJdoObjectID();
		if (jdoObjectID instanceof PropertySetID)
			propSetIDs.add((PropertySetID)jdoObjectID);
		else {
			Object jdoObject = node.getJdoObject();
			if (jdoObject instanceof PersonRelation)
				propSetIDs.add(((PersonRelation)jdoObject).getToID());
		}

		return getPropertySetIDsToRoot(node.getParent(), propSetIDs);
	}

	/**
	 * Use this mainly for comparison with a given path-for-expansion.
	 * @return the ObjectIDs, containing PersonRelationIDs and/or PropertySetIDs from this node to the root.
	 */
	public Deque<ObjectID> getObjectIDsToRoot() {
		return getObjectIDsToRoot(this, new LinkedList<ObjectID>());
	}

	private Deque<ObjectID> getObjectIDsToRoot(PersonRelationTreeNode node, Deque<ObjectID> objectIDs) {
		// Base case.
		if (node.getParent() == null)
			return objectIDs;

		// Iterative case.
		ObjectID jdoObjectID = node.getJdoObjectID();
		if (jdoObjectID instanceof PropertySetID || jdoObjectID instanceof PersonRelationID)
			objectIDs.push(jdoObjectID);

		return getObjectIDsToRoot(node.getParent(), objectIDs);
	}
}
