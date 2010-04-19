package org.nightlabs.jfire.personrelation.trade.ui.tucked.compact;

import java.util.Deque;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.personrelation.trade.ui.tucked.TuckedNodeStatus;
import org.nightlabs.jfire.personrelation.trade.ui.tucked.TuckedPersonRelationTreeNode;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeNode;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeUtil;
import org.nightlabs.jfire.prop.id.PropertySetID;

/**
 * An extension of the original {@link PersonRelationTreeNode}, this particular {@link CompactedPersonRelationTreeNode} is 
 * an even more compacted version of our earlier {@link TuckedPersonRelationTreeNode}.
 * While a normal {@link TuckedPersonRelationTreeNode} holds 'local' tucked-information, the {@link CompactedPersonRelationTreeNode} holds
 * a more 'global' compacted-information, of entire tucked-paths in a single node. Its main deployment is intended for UIs where
 * vertical space is sparse.
 * 
 * 
 * Properties / definitions:
 * 1. The JDOObjectID of this node refers to the ObjectID of the first element in the tucked-path.
 * 
 * 2. (Similarly) The JDOObject of this node refers to the Object represented by the (id of the) first element in the tucked-path.
 * 
 * 3. The node, if not {@link CompactedNodeStatus}.NORMAL or {@link CompactedNodeStatus}.UNSET, contains the entire tucked-path
 *    it represents based on its (standing) position in the tree.
 *    
 * 4. The node, if not NORMAL or UNSET, contains the corresponding {@link CompactedPersonRelationTreeNode}-references to the
 *    tucked-path it represents. 
 *     
 * 5. The children of this node are the children of the last element in the tucked-path.
 * 
 * 6. For each element in the tucked-path represented by this node, we maintain the same information as we have done
 *    with the {@link TuckedPersonRelationTreeNode}:
 *        I. actualChildCount;       <-- } i.e. the childCounts for THIS node are the child count representations of the LAST element in the tucked-path.
 *       II. tuckedChildCount        <-- }
 *           and
 *      III. tuckedStatus;           <-- i.e. the tuckedStatus for THIS node is the tuckedStatus representation of the FIRST element in the tucked-path.
 *      
 * 7. We continue to maintain a cache of loaded children (upon untucking/uncompacting, and then subsequent re-tucking/re-compacting)
 *    of the related last element in the tucked-path. The loaded-children cache refers to point 5.; i.e. the children of the last
 *    element in the tucked-path.
 * 
 *
 * @author khaireel at nightlabs dot de
 */
public class CompactedPersonRelationTreeNode extends PersonRelationTreeNode {
	public static int NODE_ID_CTR = 0;
	
	// :::::::::::: :::::::::::: :::::::::::: The Collective Information :: When this node is part of a known tucked-path :::::::::::: :::::::::::: :::::::::::: >>
	// The tucked path represented by this CompactedTuckedNode.
	protected TuckedPathDosier tuckedPathDosier = null;                           // ::[Defn. 3]:: <-- mixed PropertySetID & PersonRelationID. Ordered left-to-right: From root (this node) to end-child.
	private int pathLen = -1;

	// Children count-information of the tucked/compacted nodes that this node represented in its tucked-path.
	protected long[] tuckedPathNodesActualChildCount = null;                      // ::[Defn. 6.I]::
	protected long[] tuckedPathNodesTuckedChildCount = null;                      // ::[Defn. 6.II]::
	
	// Status information of the tucked/compacted nodes that this node represents in its tucked-path.
	protected TuckedNodeStatus[] tuckedPathNodeStatuses = null;                   // ::[Defn. 6.III]::
	
	// Internal nodes, instantiated (and shall not be duplicated), corresponding to the known tucked-path.
	private CompactedPersonRelationTreeNode[] tuckedPathNodes = null;             // ::[Defn. 4]::
	// :::::::::::: :::::::::::: :::::::::::: The Collective Information :: When this node is part of a known tucked-path :::::::::::: :::::::::::: :::::::::::: <<
	
	
	// ~~~~~~~~~~~ ~~~~~~~~~~~  The SELF-information that all CompactedTuckedNodes have ~~~~~~~~~~~ ~~~~~~~~~~~ >>
	// The childCounts of this CompactedTuckedNode. These have to be always in sync with the values whenever this node is part of a collective.
	private long actualChildCount = -1L;
	private long tuckedChildCount = -1L;
	
	// The nodeStatus of this CompactedTuckedNode. This shall take SECOND precedence when returning the status of this node.
	private TuckedNodeStatus nodeStatus = TuckedNodeStatus.UNSET;
	
	// When we UNTUCK a node, we load the rest of its children. Then if we decide to TUCK it back, we dont want to have
	// to delete them away, since we may UNTUCK them at a later time. So, in that case, we keep them here in loadedTuckedChildren. 
	// Like an internal-nodal-cache. These are affected/consulted at everytime the node stores new children.
	private Deque<CompactedPersonRelationTreeNode> loadedTuckedChildren = null;   // ::[Defn. 7]::
	// ~~~~~~~~~~~ ~~~~~~~~~~~  The SELF-information that all CompactedTuckedNodes have ~~~~~~~~~~~ ~~~~~~~~~~~ << 
	
	
	
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// [Section] On initialising this node.
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	@Override
	public void setJdoObjectID(ObjectID jdoObjectID) {
		super.setJdoObjectID(jdoObjectID);
		
		// Get from the controller, the (sub-)tuckedPath, from this node's position in the tree to the end-child. Null means this node is not part of the tuckedPath.
		// Upon initialisation, we set up only one node per tucked-path. 
		// And this only happens when the jdoObjectID we receive here is of the type PropertySetID. 
		if (jdoObjectID instanceof PropertySetID)
			initCompactedTuckedNode( ((CompactedPersonRelationTreeController)getActiveJDOObjectLazyTreeController()).getTuckedPathDosier(this) );
	}
	
	/**
	 * Initialises this CompactedTuckedNode, given a tuckedPath of {@link ObjectID}s. See ::[Defn. 3]::
	 */
	protected void initCompactedTuckedNode(TuckedPathDosier tuckedPathDosier) {
		this.tuckedPathDosier = tuckedPathDosier;
		if (this.tuckedPathDosier != null) {			
			// The rudimentaries: Made to correspond to the ordered-entry of the elements in the tucked-path.
			pathLen = tuckedPathDosier.pathPSID.length;
			tuckedPathNodesActualChildCount = new long[pathLen];
			tuckedPathNodesTuckedChildCount = new long[pathLen];
			tuckedPathNodeStatuses = new TuckedNodeStatus[pathLen];
			
			for (int i=0; i<pathLen; i++) {
				tuckedPathNodesActualChildCount[i] = -1L;
				tuckedPathNodesTuckedChildCount[i] = -1L;
				tuckedPathNodeStatuses[i] = TuckedNodeStatus.UNSET;
			}
			
			// Initialise the rest of the corresponding nodal information with respect to the tucked-path.
			tuckedPathNodes = new CompactedPersonRelationTreeNode[pathLen]; // When initialising this, consider the PARENT of this node too.
		}
	}
	

	
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// [Section] Setting information into this node.
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	/**
	 * Sets a valid {@link CompactedPersonRelationTreeNode} whose {@link ObjectID} it represents belongs to this node's
	 * tucked-path. We shall assume that at least the 'SELF-information' of the given node's STATUS has been initialised.
	 * @return true if the node is properly set in its appropriate position.
	 */
	protected boolean setTuckedNodeInPath(CompactedPersonRelationTreeNode node) {
		// Locate the position and then insert.
		int posRef = getTuckedPathNodePosReference(node);
		if (posRef != -1) {
			tuckedPathNodes[posRef] = node;
			tuckedPathNodeStatuses[posRef] = node.nodeStatus;
			
			// Be careful here. There is the issue of hidden root nodes!
			tuckedPathNodes[posRef].setParent(posRef > 0 ? tuckedPathNodes[posRef-1] : this.getParent());
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Sets the actual SELF child-count for this TuckedNode.
	 */
	public void setActualChildCount(long actualChildCount) { 
		this.actualChildCount = actualChildCount;
	}

	/**
	 * Sets the tucked SELF child-count for this TuckedNode.
	 */
	public void setTuckedChildCount(long tuckedChildCount) {
		this.tuckedChildCount = tuckedChildCount; 
	}

	/**
	 * Sets the collective childCounts, if and only if this node is holding the collective compacted information.
	 * @return true if the childCounts have been correctly set.
	 */
	protected boolean setCollectiveChildCounts(CompactedPersonRelationTreeNode node) {
		int posRef = getTuckedPathNodePosReference(node);
		if (posRef != -1) {
			tuckedPathNodesActualChildCount[posRef] = node.actualChildCount;
			tuckedPathNodesTuckedChildCount[posRef] = node.tuckedChildCount;
			
			// Special case. See [Defn. 6.I] and [Defn. 6.II].
			if (posRef == pathLen-1) {
				actualChildCount = node.actualChildCount;
				tuckedChildCount = node.tuckedChildCount;
			}
		}
		
		return false;
	}
	
	/**
	 * @return the position reference of the {@link ObjectID} represented in the given node, with respect to the tucked-path.
	 * Returns -1 if no position reference can be found.
	 */
	private int getTuckedPathNodePosReference(CompactedPersonRelationTreeNode node) {
		if (tuckedPathDosier != null) {
			ObjectID objectID = node.getJdoObjectID();
			for (int i=0; i<pathLen; i++)
				if (objectID.equals(tuckedPathDosier.pathPRID[i]))
					return i;
		}
		
		return -1;
	}
	
	/**
	 * Sets the SELF nodeStatus for this node.
	 */
	public void setNodeStatus(TuckedNodeStatus nodeStatus) {
		this.nodeStatus = nodeStatus;
	}
	
	
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// [Section] Information about this node.
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	/**
	 * @return the {@link TuckedNodeStatus} of this node. See ::[Defn. 6.III]::
	 */
	public TuckedNodeStatus getNodeStatus() {
		return nodeStatus;
	}
	
	/**
	 * @return true if this CompactedTuckedNode is set; i.e. if and only if it has ALL its childCounts set.
	 */
	public boolean isNodeSet() {
		if (tuckedPathDosier == null) 
			return nodeStatus.equals(TuckedNodeStatus.NORMAL);
		
		for (TuckedNodeStatus nodeStatus : tuckedPathNodeStatuses)
			if (nodeStatus.equals(TuckedNodeStatus.UNSET))
				return false;
		
		return true;
	}
	
	/**
	 * @return true if this node is part of the tucked-path, identified in the controller.
	 */
	public boolean isNodePartOfTuckedPath() {
		return tuckedPathDosier != null;
	}
	
	@Override
	public long getChildNodeCount() {
		if (nodeStatus.equals(TuckedNodeStatus.UNSET) || nodeStatus.equals(TuckedNodeStatus.NORMAL))
			return super.getChildNodeCount();
		
		return getNodeStatus().equals(TuckedNodeStatus.TUCKED) ? tuckedChildCount : actualChildCount;
	}

	/**
	 * @return the nodes in the collective tucked-path represented by this node.
	 */
	protected CompactedPersonRelationTreeNode[] getTuckedPathNodes() {
		return tuckedPathNodes;
	}

	/**
	 * @return the difference in values between the actualChildCount and the tuckedChildCount.
	 */
	public long getCompactedChildCountDifference() {
		int posRef = getTuckedPathNodePosReference(this);
		return posRef != -1 ? tuckedPathNodesActualChildCount[posRef] - tuckedPathNodesTuckedChildCount[posRef] : actualChildCount - tuckedChildCount;
	}
	
	/**
	 * @return the node-representative of this COLLECTIVE node, but only if this node is of the COLLECTIVE type.
	 * Returns null otherwise.
	 */
	public CompactedPersonRelationTreeNode getNodeRepresentative() {
		if (tuckedPathDosier != null) {
			// The node-representative is the node-element at the end of the tucked-path.
			return tuckedPathNodes[pathLen-1];
		}
		
		return null;
	}
	
	
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// [Section] Miscellaneous and debuggings.
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	protected int meNodeID = -1;
	public CompactedPersonRelationTreeNode() { meNodeID = CompactedPersonRelationTreeNode.NODE_ID_CTR++; }
	
	public String toDebugString() {
		String str = this.getClass().getSimpleName() + " :: (@JDOObjectID:" + PersonRelationTreeUtil.showObjectID(getJdoObjectID(), true) + "), (@PropertySetID:";
		str += PersonRelationTreeUtil.showObjectID(getPropertySetID(), true) + "), [" + getNodeStatus() + "]";
		
		if (!isNodeSet())
			str += " --------->> [UN-set]";
		
		str += "\n  ~~ meNodeID: " + meNodeID;
		
		if (tuckedPathDosier != null) {
			// Display the COLLECTIVE information.
			str += tuckedPathDosier.toDebugString("  ++ ");
			
			int i = 0;
			str += "\n  ++ nodeStatuses :: { ";
			for (TuckedNodeStatus nodeStatus : tuckedPathNodeStatuses) {
				str += "(" + i + ")[" + nodeStatus + "] ";
				i++;
			}
			str += "}";
			
			str += "\n  ++ nodeCounts :: { ";
			for (i=0; i<pathLen; i++) 
				str += "(" + i + ")[Ac:" + tuckedPathNodesActualChildCount[i] + ", Tk:" + tuckedPathNodesTuckedChildCount[i] + "] ";
			str += "}";
			
			str += PersonRelationTreeUtil.showNodeObjectIDs("\n  ++ tuckedPathNodes-PRid", tuckedPathNodes, 10, false);
			str += PersonRelationTreeUtil.showNodeObjectIDs("\n  ++ tuckedPathNodes-PSid", tuckedPathNodes, 10, true);
		}
		
		// Now the SELF-information.
		str += "\n  +~ nodeStatus: " + nodeStatus;
		str += "\n  +~ childNodeCount: " + getChildNodeCount();
		
		return str;
	}
}
