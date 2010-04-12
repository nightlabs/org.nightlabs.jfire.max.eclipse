package org.nightlabs.jfire.personrelation.ui.tree;

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
extends JDOObjectLazyTreeNode<ObjectID, Object, PersonRelationTreeController<? extends PersonRelationTreeNode>> {
	@Override
	public PersonRelationTreeNode getParent() {
		return (PersonRelationTreeNode)super.getParent();
	}

	// A quick reference to this node's own PropertySetID. Because there is always a chance we can set it whenever
	// we are in possession of the tucked-path information.
	private PropertySetID propertySetID = null;

	/**
	 * Sets the {@link PropertySetID} for this node.
	 */
	public void setPropertySetID(PropertySetID propertySetID) {
		this.propertySetID = propertySetID;
	}
	
	/**
	 * @return the PropertySetID represented by this node. Returns null if node does not present a Person-related object.
	 */
	public PropertySetID getPropertySetID() {
		if (propertySetID == null) {
			ObjectID jdoObjectID = getJdoObjectID();
			if (jdoObjectID instanceof PropertySetID)
				propertySetID = (PropertySetID) jdoObjectID;
			
			else {
				Object jdoObject = getJdoObject();
				if (jdoObject instanceof PersonRelation)
					propertySetID = ((PersonRelation)jdoObject).getToID();
			}
		}

		return propertySetID;
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
		propSetIDs.add(node.getPropertySetID());
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
	@SuppressWarnings("unchecked")
	public static <N extends PersonRelationTreeNode> N getPersonRelationTreeNodeFromSelection(ISelection selection) {
		if (selection.isEmpty() || !(selection instanceof IStructuredSelection))
			return null;

		IStructuredSelection sel = (IStructuredSelection) selection;
		if (sel.size() != 1 || sel.getFirstElement() == null)
			return null;

		Object selObject = sel.getFirstElement();
		if (!(selObject instanceof PersonRelationTreeNode))
			return null;

		return (N) selObject;
	}
}
