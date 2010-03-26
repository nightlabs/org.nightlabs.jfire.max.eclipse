package org.nightlabs.jfire.personrelation.trade.ui.tucked;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.nightlabs.jdo.ObjectID;
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
	// The tuckedPath of ObjectIDs represented by this tuckedNode.
	private Deque<ObjectID> tuckedPath = null; // <-- mixed PropertySetID & PersonRelationID.

	// Keeps track of the actual child-count for each of the ObjectID in the tuckedPath represented by this node.
	private Map<ObjectID, Long> objectID2actualChildCount = new HashMap<ObjectID, Long>();

	// Keeps track of the tucked-child-count for each of the ObjectID in the tuckedPath represented by this node.
	private Map<ObjectID, Long> objectID2tuckedChildCount = new HashMap<ObjectID, Long>();

	// Keeps track of the tuckStatus for each of the ObjectID in the tuckedPath.
	private Map<ObjectID, TuckedNodeStatus> objectID2tuckedStatus = new HashMap<ObjectID, TuckedNodeStatus>();


	@Override
	public void setJdoObjectID(ObjectID jdoObjectID) {
		super.setJdoObjectID(jdoObjectID);

		TuckedPersonRelationTreeController tprtController = (TuckedPersonRelationTreeController) getActiveJDOObjectLazyTreeController();
		initTuckedPath(tprtController.getSubPathUpUntilCurrentID(jdoObjectID));
	}

	@Override
	public long getChildNodeCount() {
		if (!isNodeSet())
			return super.getChildNodeCount();
		
		return objectID2tuckedStatus.get(getJdoObjectID()).equals(TuckedNodeStatus.TUCKED) ? objectID2tuckedChildCount.get(getJdoObjectID()) : objectID2actualChildCount.get(getJdoObjectID());
	}
	
	/**
	 * Sets the tuckedPath of {@link ObjectID}s represented by this tuckedNode.
	 * This will also initialise the internal childCount and tuckedStatus mappings.
	 */
	protected void initTuckedPath(Deque<ObjectID> tuckedPath) {
		objectID2actualChildCount.clear();
		objectID2tuckedStatus.clear();
		if (tuckedPath == null)
			return;

		this.tuckedPath = tuckedPath;

//		// Will revert to these once I've managed to fix some bugs :/
//		for (ObjectID objectID : this.tuckedPath) {
//			objectID2actualChildCount.put(objectID, -1L);
//			objectID2tuckedChildCount.put(objectID, -1L);
//
//			objectID2tuckedStatus.put(objectID, true);
//		}

		objectID2actualChildCount.put(getJdoObjectID(), -1L);
		objectID2tuckedChildCount.put(getJdoObjectID(), -1L);
		objectID2tuckedStatus.put(getJdoObjectID(), TuckedNodeStatus.NORMAL);
	}


	/**
	 * Sets the actual child-count for the tuckedNode representing the given objectID.
	 * @return false if the objectID is not recognised, and hence nothing is set.
	 */
	public boolean setActualChildCountByObjectID(ObjectID objectID, long actualChildCount) {
		if (objectID2actualChildCount.get(objectID) == null)
			return false;

		objectID2actualChildCount.put(objectID, actualChildCount);
		return true;
	}

	/**
	 * Sets the tucked-child-count for the tuckedNode representing the given objectID.
	 * @return false if the objectID is not recognised, and hence nothing is set.
	 */
	public boolean setTuckedChildCountByObjectID(ObjectID objectID, long tuckedChildCount) {
		if (objectID2tuckedChildCount.get(objectID) == null)
			return false;

		objectID2tuckedChildCount.put(objectID, tuckedChildCount);
		return true;
	}

	/**
	 * Sets the tuckStatus for the tuckedNode representing the given objectID.
	 * @return false if the objectID is not recognised, and hence nothing is set.
	 */
	public boolean setTuckedStatusByObjectID(ObjectID objectID, TuckedNodeStatus tuckStatus) {
		if (objectID2tuckedStatus.get(objectID) == null)
			return false;

		objectID2tuckedStatus.put(objectID, tuckStatus);
		return true;
	}
	
	
	/**
	 * @return the {@link TuckedNodeStatus} of this node.
	 * TODO Generalise this for approach III.
	 */
	public TuckedNodeStatus getTuckedStatus() {
		return objectID2tuckedStatus.get(getJdoObjectID());
	}
	
	/**
	 * Sets the {@link TuckedNodeStatus} of this node.
	 * TODO Generalise this for approach III.
	 */
	public void setTuckedNodeStatus(TuckedNodeStatus tuckedNodeStatus) {
		objectID2tuckedStatus.put(getJdoObjectID(), tuckedNodeStatus);
	}

	public boolean isNodeSet() {
		Long val = objectID2actualChildCount.get(getJdoObjectID());
		return val != null && val != -1L;		
	}

	/**
	 * Shows the childCounts and statuses to each element in the tuckedPath represented by this node.
	 * @return a debug-string representation for this {@link TuckedPersonRelationTreeNode}.
	 */
	public String toDebugString() {
		String str = "\n" + this.getClass().getSimpleName() + "@" + PersonRelationTree.showObjectID(getJdoObjectID());
//		for (ObjectID objectID : tuckedPath) {
		ObjectID objectID = getJdoObjectID();
		if (objectID2tuckedChildCount.get(objectID) == null) return str;
			str += "\n  " + PersonRelationTree.showObjectID(objectID) + ": [" + objectID2tuckedChildCount.get(objectID) + " (of ";
			str += objectID2actualChildCount.get(objectID) + ")], [status: \"" + objectID2tuckedStatus.get(objectID) + "\"]";
//		}

		return str;
	}



	/**
	 * @return the "tucked" information status of this node, used for display with an appropriate label provider.
	 */
	public String getTuckedInfoStatus(ObjectID objectID) {
		// This TuckedNode should behave like a normal PersonRelationTreeNode if it has been "NORMAL"-ised.
		TuckedNodeStatus nodeStatus = objectID2tuckedStatus.get(objectID);
		if (nodeStatus == null || nodeStatus.equals(TuckedNodeStatus.NORMAL))
			return "";
		
		if (nodeStatus.equals(TuckedNodeStatus.TUCKED)) {
			long actualChildCount = objectID2actualChildCount.get(objectID);
			if (actualChildCount != -1)
				return String.format("... (+ %s tucked element(s))", actualChildCount-objectID2tuckedChildCount.get(objectID));
		}
		
		return "";
	}
	

//	// ------- FARK-Tests ---------------------------------------------------------------------------------------||----->>
//	protected String tuckedPathOfDisplayNames = null;
//
//	/**
//	 * @return the 'tucked' path of displayNames, from the root, down to this node.
//	 */
//	public String getTuckedPathOfDisplayNames() {
//		if (tuckedPathOfDisplayNames == null) {
//			TuckedPersonRelationTreeNode parentNode = (TuckedPersonRelationTreeNode) getParent();
//			Object jdoObject = getJdoObject();
//
//			// Recursive method to retrieve the path of displayNames.
//			// Base case.
//			if (parentNode == null || jdoObject == null)
//				return "";
//
//			// Iterative-case.
//			Person person = null;
//			if (jdoObject instanceof PersonRelation)
//				person = ((PersonRelation) jdoObject).getTo();
//			else if (jdoObject instanceof Person)
//				person = (Person) jdoObject;
//
//			// Guard.
//			if (person == null)
//				return "";
//
//			String displayNamesFromParent = parentNode.getTuckedPathOfDisplayNames();
//			if (!displayNamesFromParent.equals("")) displayNamesFromParent = String.format("%s;  ", displayNamesFromParent);
//
//			tuckedPathOfDisplayNames = String.format("%s%s", displayNamesFromParent, person.getDisplayName());
//		}
//
//		return tuckedPathOfDisplayNames;
//	}
//	// ------- FARK-Tests ---------------------------------------------------------------------------------------||----->>
}
