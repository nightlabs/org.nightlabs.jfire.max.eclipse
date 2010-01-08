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
extends JDOObjectLazyTreeNode<ObjectID, Object, PersonRelationTreeController> {
//	// -------------------------------------------------------------------------------------------------- FARK-MARK ------>>
//	// Attempt #2: Preemptive stop to the unending iteration.
//	//             Perhaps this can be done from the server-side.
//	@Override
//	public synchronized void setChildNodes(List<JDOObjectLazyTreeNode<ObjectID, Object, PersonRelationTreeController>> childNodes) {
//		// Checks to see if any of the children's PropertySetID (or PersonRelationID) already appears once on the path
//		// from this node to the parent. If so, we dont add the child to its collection.
//		List<PropertySetID> propertySetIDsToRoot = getPropertySetIDsToRoot();
//
//		// Check somethings here.
//		showPropertySetIDs("[@SET] propertySetIDsToRoot", propertySetIDsToRoot);
//		showPropertySetIDs("         ------> childNodes", childNodes);
//		System.err.println();
//		List<JDOObjectLazyTreeNode<ObjectID, Object, PersonRelationTreeController>> children = new LinkedList<JDOObjectLazyTreeNode<ObjectID,Object,PersonRelationTreeController>>();
//		for (JDOObjectLazyTreeNode<ObjectID,Object,PersonRelationTreeController> node : childNodes) {
//			PropertySetID propertySetID = ((PersonRelationTreeNode) node).getPropertySetID();
//			if (!propertySetIDsToRoot.contains( propertySetID ))
//				children.add(node);
//		}
//
//		super.setChildNodes(children);
//	}
//
//	@Override
//	public synchronized boolean addChildNode(JDOObjectLazyTreeNode<ObjectID, Object, PersonRelationTreeController> childNode) {
//		List<PropertySetID> propertySetIDsToRoot = getPropertySetIDsToRoot();
//		showPropertySetIDs("[@ADD] propertySetIDsToRoot", propertySetIDsToRoot);
//		showPropertySetIDs("         ----->>> childNode", Collections.singletonList(childNode));
//		System.err.println();
//
//		return propertySetIDsToRoot.contains( ((PersonRelationTreeNode) childNode).getPropertySetID() ) ? true : super.addChildNode(childNode);
//	}
//
//
//	// For debugging...
//	private void showPropertySetIDs(String preamble, List<?> objects) {
//		String className = objects != null && !objects.isEmpty() ? objects.get(0).getClass().getSimpleName() : "";
//		System.err.print("++ " + preamble + " :: [" + objects.size() + "," + className + "] {");
//		for (Object object : objects) {
//			PropertySetID pid = null;
//
//			if (object instanceof PropertySetID)
//				pid = (PropertySetID) object;
//			else if (object instanceof JDOObjectLazyTreeNode)
//				pid = ((PersonRelationTreeNode)object).getPropertySetID();
//
//			if (pid != null) {
//				String[] segID = pid.toString().split("&");
//				System.err.print("[" + segID[1] + "]");
//			}
//			else
//				System.err.print("[null]");
//		}
//
//		System.err.print("}\n");
//	}
//	// -------------------------------------------------------------------------------------------------- FARK-MARK ------>>



	// Attempt #1: To stop the unending iteration. At least for use with Behr's descriptions. See notes.
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

//	/**
//	 * @return a List of PropertySetIDs ordered from this node all the way up to the root.
//	 */
//	public List<PropertySetID> getPropertySetIDsToRoot() {
//		if (propertySetIDsToRoot == null || propertySetIDsToRoot.isEmpty()) {
//			propertySetIDsToRoot = new LinkedList<PropertySetID>();
//			getPropertySetIDsToRoot(this);
//		}
//
//		return propertySetIDsToRoot;
//	}
//
//	private List<PropertySetID> propertySetIDsToRoot = null;
//	private void getPropertySetIDsToRoot(PersonRelationTreeNode node) {
//		// Base case.
//		if (node.getParent() == null)
//			return;
//
//		// Iterative case.
//		ObjectID jdoObjectID = node.getJdoObjectID();
//		if (jdoObjectID instanceof PropertySetID)
//			propertySetIDsToRoot.add((PropertySetID)jdoObjectID);
//		else {
//			Object jdoObject = node.getJdoObject();
//			if (jdoObject instanceof PersonRelation)
//				propertySetIDsToRoot.add(((PersonRelation)jdoObject).getToID());
//		}
//
//		getPropertySetIDsToRoot(node.getParent());
//	}

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
