package org.nightlabs.jfire.personrelation.ui.tree;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.Set;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.util.CollectionUtil;

/**
 * General methods for interacting with the {@link PersonRelationTree}, its {@link PersonRelationTreeNode}s, and
 * to a certain extent, its {@link PersonRelationTreeController} too.
 *
 * @author khaireel at nightlabs dot de
 */
public final class PersonRelationTreeUtil {
	/**
	 * @return the {@link PersonRelationTreeNode} containing within it's JDOObjectID, the given objectID.
	 * Returns null if the node cannot be found.
	 */
	public static <N extends PersonRelationTreeNode> N getNodeFromObjectID(Set<N> parentNodes, ObjectID objectID) {
		if (parentNodes == null)
			return null;
		
		for (N node : parentNodes)
			if (node != null) {
				ObjectID jdoObjectID = node.getJdoObjectID();
				if (jdoObjectID != null && jdoObjectID.equals(objectID))
					return node;
			}
		
		return null;
	}
	
	
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	// Debug string helpers.
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	// I. Quick debug.
	public static String showDequePaths(String preamble, Deque<? extends ObjectID> path, boolean isReversed) {
		String str = "++ " + preamble + " :: {";
		Iterator<? extends ObjectID> iter = isReversed ? path.descendingIterator() : path.iterator();
		while (iter.hasNext())
			str += showObjectID(iter.next());

		return str + "}";
	}

	// II. Quick debug.
	public static String showObjectIDs(String preamble, Collection<? extends ObjectID> objIDs, int modLnCnt) {
		if (objIDs == null)
			return preamble + " :: NULL";

		int len = objIDs.size();
		String str = preamble + " (size: " + len + ") :: {" + (len > modLnCnt ? "\n     " : " ");
		int ctr = 0;
		for (ObjectID objectID : objIDs) {
			str += "(" + ctr + ")" + showObjectID(objectID, true) + " ";
			ctr++;

			if (ctr % modLnCnt == 0)
				str += "\n     ";
		}

		return str + (len > modLnCnt ? "\n   }" : "}");
	}
	
	// II.a 
	public static String showObjectIDs(String preamble, ObjectID[] objIDs, int modLnCnt) {
		return showObjectIDs(preamble, CollectionUtil.array2ArrayList(objIDs), modLnCnt);
	}

	// III. Quick debug.
	public static String showObjectID(ObjectID objectID) {
		return showObjectID(objectID, false);
	}

	// III.a Quick debug.
	public static String showObjectID(ObjectID objectID, boolean isShortened) {
		if (objectID == null)
			return "[null]";

		String[] segID = objectID.toString().split("&");
		String str = segID[1];
		
		if (isShortened) {
			str = str.replaceFirst("propertySetID", "pSid");
			str = str.replaceFirst("personRelationID", "pRid");
		}			
		
		return "[" + str + "]";
	}

	// IV. Quick debug.
	public static String showNodeObjectIDs(String preamble, Collection<? extends PersonRelationTreeNode> nodes, int modLnCnt, boolean isShowPropertySetID) {
		if (nodes == null)
			return preamble + " :: NULL";
		
		int len = nodes.size();
		String str = preamble + " (size: " + len + ") :: {" + (len > modLnCnt ? "\n     " : " ") + "Nodes ~~ ";
		int ctr = 0;
		for (PersonRelationTreeNode node : nodes) {
			str += "(" + ctr + ")";
			if (node == null) str += "[Node:null] ";
			else {
				str += showObjectID(isShowPropertySetID ? node.getPropertySetID() : node.getJdoObjectID(), true);
				str += "[" + node.getChildNodeCount() + "]";
				str += "<Obj:" + (node.getJdoObject() == null ? "null" : "Y") + ">";
				str += " ";
			}			
			
			ctr++;
			if (ctr % modLnCnt == 0)
				str += "\n     ";
		}

		return str + (len > modLnCnt ? "\n   }" : "}");
	}

	// IV.a.
	public static <T extends PersonRelationTreeNode> String showNodeObjectIDs(String preamble, T[] nodes, int modLnCnt, boolean isShowPropertySetID) {
		return showNodeObjectIDs(preamble, CollectionUtil.array2ArrayList(nodes), modLnCnt, isShowPropertySetID);
	}

	// V. Quick debug.
	public static String showQuickNodeInfo(PersonRelationTreeNode node) {
		ObjectID jdoObjectID = node.getJdoObjectID();
		PropertySetID propertySetID = node.getPropertySetID();
		
		String str = jdoObjectID == null ? "[ObjectID:null]" : showObjectID(jdoObjectID);
		str += ", " + (propertySetID == null ? "[PropertySetID:null]" : showObjectID(propertySetID));
		
		return str;
	}
}
