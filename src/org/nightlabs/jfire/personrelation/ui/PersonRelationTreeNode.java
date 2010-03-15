package org.nightlabs.jfire.personrelation.ui;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOObjectLazyTreeNode;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.prop.id.PropertySetID;

/**
 * @author Marco Schulze
 * @author khaireel (at) nightlabs (dot) de
 */
public class PersonRelationTreeNode
extends JDOObjectLazyTreeNode<ObjectID, Object, PersonRelationTreeController> {
//	// Attempt #1: To stop the unending iteration. At least for use with Behr's descriptions. See notes.
//	/**
//	 * Based on the PropertySetID represented by this node, check to see if the same PropertySetID
//	 * appears anywhere on the path from this node to the root.
//	 * @return true if this PropertySetID on the path to the root is unique (and thus allowing for
//	 * its children to continue to be loaded, whenever necessary). Returning false signifies that this
//	 * node is a 'repeat' element, suggesting the beginnig of the next repeated-iterative bunch of
//	 * craps, which have already been loaded and displayed.
//	 */
//	public boolean isContinueToLoadChildren() {
//		List<PropertySetID> propSetIDsToRoot = getPropertySetIDsToRoot();
//		if (propSetIDsToRoot.size() > 1) {
//			PropertySetID psID = propSetIDsToRoot.remove(0);
//			return !propSetIDsToRoot.contains(psID);
//		}
//
//		return true;
//	}
//
//	@Override
//	public long getChildNodeCount() {
//		// The related (lazy) tree-controller calls this node's method to determine how many children
//		// the node has. But if the PropertySetID represented by this node has already once appeared
//		// on the path to the root, then we force the child count to zero.
//		return isContinueToLoadChildren() ? super.getChildNodeCount() : 0L;
//	}

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
	 * Checks the contents in a selection and returns a {@link PersonRelationTreeNode} if a valid one exists
	 * in the given {@link ISelection}. Assumes here that the selection contains at most one node, and that
	 * the given selection is an instance of the {@link IStructuredSelection}.
	 *
	 * Used very often:
	 * 1. In applications (eg. from a SelectionListener): We often need to know if an ISelection
	 *    contains an instance of the PersonRelationTreeNode. Currently, there are at least 5
	 *    Actions having the exact same codes.
	 *
	 * 2. Usually this is called where we already have access to a {@link PersonRelationTreeNode}.
	 *
	 * @return null if the selection does not contain a {@link PersonRelationTreeNode}.
	 */
	public static PersonRelationTreeNode getPersonRelationTreeNodeFromSelection(ISelection selection) {
		if (selection.isEmpty() || !(selection instanceof IStructuredSelection))
			return null;

		IStructuredSelection sel = (IStructuredSelection) selection;
		if (sel.size() != 1 || sel.getFirstElement() == null)
			return null;

		Object selObject = sel.getFirstElement();
		if (!(selObject instanceof PersonRelationTreeNode))
			return null;

		return (PersonRelationTreeNode) selObject;
	}
}
