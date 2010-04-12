package org.nightlabs.jfire.personrelation.trade.ui.compact;

import java.util.List;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.ui.tree.AbstractPersonRelationTreeLabelProviderDelegate;
import org.nightlabs.jfire.prop.id.PropertySetID;

/**
 * Handles the display of root {@link CompactedPersonRelationTreeNode}s.
 *
 * @author khaireel at nightlabs dot de
 */
public class CompactedPropertySetTreeLabelProviderDelegate extends AbstractPersonRelationTreeLabelProviderDelegate {
	private CompactedPersonRelationTreeController cprtController = null;
	
	/**
	 * Creates a new instance of the CompactedPropertySetTreeLabelProviderDelegate.
	 * It becomes imperative that we have access to the {@link CompactedPersonRelationTreeController} in order to get
	 * the information we want to display on the tree.
	 */
	public CompactedPropertySetTreeLabelProviderDelegate(CompactedPersonRelationTreeController cprtController) {
		this.cprtController = cprtController;
	}
	
	@Override
	public String getJDOObjectText(ObjectID jdoObjectID, Object jdoObject, int spanColIndex) {
		if (jdoObject != null && spanColIndex == 0 && cprtController != null) {
			// Go get the (root) node. There is a possibility that it contains several nodes in its internal tucked-path.
			// Caution, the node is lazy.
			List<CompactedPersonRelationTreeNode> treeNodeList = cprtController.getTreeNodeList(jdoObjectID);
			CompactedPersonRelationTreeNode compactedTuckedNode = getCorrectNodeFromList(treeNodeList);
			if (compactedTuckedNode == null)
				return "Loading... we are still loading... tralalalalalalalalalaaaaallaaaaaa";
			
			// At this point, the node we want is loaded.
			// If this is a COLLECTIVE node, we need to check if its collective elements in the tucked-path are also loaded.
			CompactedPersonRelationTreeNode[] tuckedPathNodes = compactedTuckedNode.getTuckedPathNodes();
			String displayText = "";
			if (tuckedPathNodes != null) {
				// AHA! We have a COLLECTIVE node.
				int pathLen = tuckedPathNodes.length;
				for (int i=0; i<pathLen; i++) {					
					displayText += getDisplayTextForNode(tuckedPathNodes[i]);
					if (compactedTuckedNode.tuckedPathNodeStatuses[i].equals(CompactedNodeStatus.COMPACTED)) {
						long diffVal = compactedTuckedNode.tuckedPathNodesActualChildCount[i] - compactedTuckedNode.tuckedPathNodesTuckedChildCount[i];
						displayText += " (... + " + diffVal + ")";
					}
					
					if (i < pathLen-1)
						displayText += " -> "; // displayText += " " + '\u2192' + " " + person.getDisplayName(); //" → " + person.getDisplayName();
				}
			}
			else
				displayText = getDisplayTextForNode(compactedTuckedNode);

			return displayText;
		}
		
		return null;
	}
	
	/**
	 * @return the COLLECTIVE node if there are several nodes mapped to given {@link PropertySetID}.
	 */
	private CompactedPersonRelationTreeNode getCorrectNodeFromList(List<CompactedPersonRelationTreeNode> treeNodeList) {
		if (treeNodeList == null || treeNodeList.isEmpty())
			return null;
		
		if (treeNodeList.size() == 1)
			return treeNodeList.get(0);
		
		for (CompactedPersonRelationTreeNode treeNode : treeNodeList)
			if (treeNode.tuckedPathDosier != null)
				return treeNode;
		
		return null;
	}
	
	/**
	 * @return the String representation for display on this label provider, provided the node and object are not null.
	 */
	private String getDisplayTextForNode(CompactedPersonRelationTreeNode node) {
		String displayText = ""; // node.getNodeStatus().equals(CompactedNodeStatus.COMPACTED) ? " (... + " + node.getCompactedChildCountDifference() + ")" : "";
		Object jdoObject = node.getJdoObject();
		if (jdoObject instanceof Person)
			displayText = String.format("%s%s", ((Person) jdoObject).getDisplayName(), displayText);
		else if (jdoObject instanceof PersonRelation)
			displayText = String.format("%s%s", ((PersonRelation) jdoObject).getTo().getDisplayName(), displayText);
		
		return displayText;
	}

	
	
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// [Section] Information on when to activate this label-provider delegate.
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	@Override
	public Class<?> getJDOObjectClass() { return Person.class; }

	@Override
	public Class<? extends ObjectID> getJDOObjectIDClass() { return PropertySetID.class; }
	
	@Override
	public int[][] getJDOObjectColumnSpan(ObjectID jdoObjectID, Object jdoObject) { return null; }
}
