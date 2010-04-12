package org.nightlabs.jfire.personrelation.trade.ui.tucked;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOObjectLazyTreeNode;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeController;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeNode;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeUtil;
import org.nightlabs.jfire.prop.id.PropertySetID;

/**
 * The {@link TuckedPersonRelationTreeNode} is initialised upon calling the super class's method setJdoObjectID(), where
 * we shall assume that up till that stage, any other references needed by the node are readily available and can be queried directly
 * from the related controller; i.e. in our case, the {@link TuckedPersonRelationTreeController}.
 *
 * @author khaireel (at) nightlabs (dot) de
 */
public class TuckedPersonRelationTreeNode extends PersonRelationTreeNode {
	private final static Logger logger = Logger.getLogger(TuckedPersonRelationTreeNode.class);
	
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// [Section I] Handling the TUCKED and UNTUCKED statuses, with respect to the node's childCount.
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// Keeps track of the actual child-count for each of the ObjectID in the tuckedPath represented by this node.
	private long actualChildCount = -1L;

	// Keeps track of the tucked-child-count for each of the ObjectID in the tuckedPath represented by this node.
	private long tuckedChildCount = -1L;

	// Keeps track of the tuckStatus for each of the ObjectID in the tuckedPath.
	private TuckedNodeStatus tuckedStatus = TuckedNodeStatus.NORMAL;
	
	// An indication whether or not this TuckedNode is part of the tuckedPath. The value is set only once, and upon initialisation.
	private boolean isPartOfTuckedPath = false;
	

	@Override
	public void setJdoObjectID(ObjectID jdoObjectID) {
		super.setJdoObjectID(jdoObjectID);

		TuckedPersonRelationTreeController tprtController = (TuckedPersonRelationTreeController) getActiveJDOObjectLazyTreeController();
		Deque<ObjectID> subPath = tprtController.getSubPathUpUntilCurrentID(this);
		
		initTuckedNode(subPath);
	}

	/**
	 * Initialises this TuckedNode, given a tuckedPath of {@link ObjectID}s.
	 */
	protected void initTuckedNode(Deque<ObjectID> tuckedPath) {
		actualChildCount = -1L;
		tuckedChildCount = -1L;
		tuckedStatus = TuckedNodeStatus.NORMAL;
		
		// Used in operational transition. See notes, and see Section III.
		statusToChangeTo = TuckedNodeStatus.UNSET;

		// Tucked-node-operational variables.
		ObjectID objectID = getJdoObjectID();
		if (tuckedPath != null)
			isPartOfTuckedPath = tuckedPath.contains(objectID);
		
		if (logger.isDebugEnabled()) {
			logger.debug("---->> objectID: " + PersonRelationTreeUtil.showObjectID(objectID));
			logger.debug(PersonRelationTreeUtil.showObjectIDs("---->> Deque.tuckedPath", tuckedPath, 10));
		}
		
		// On initialisation, a tuckedNode is 'expanded' (i.e. '!isCollapsed') if it is part of the tuckedPath BUT NOT the last item on the tuckedPath.
		// In other words, the node isCollapsed if it is NOT part of the tuckedPath OR if it is the last item on the tuckedPath.
		isExpanded = objectID instanceof PropertySetID || isPartOfTuckedPath && !tuckedPath.getFirst().equals(objectID); // The Deque is reversed: The root is the last element in the Deque.
	}


	/**
	 * Sets the actual child-count for this TuckedNode.
	 */
	public void setActualChildCount(long actualChildCount) { this.actualChildCount = actualChildCount; }

	/**
	 * Sets the tucked-child-count for this TuckedNode.
	 */
	public void setTuckedChildCount(long tuckedChildCount) { this.tuckedChildCount = tuckedChildCount; }

	@Override
	public long getChildNodeCount() {
		if (!isNodeSet())
			return super.getChildNodeCount();
		
		return tuckedStatus.equals(TuckedNodeStatus.TUCKED) ? tuckedChildCount : actualChildCount; 
	}
	
	/**
	 * Sets the {@link TuckedNodeStatus} for this TuckedNode.
	 */
	public void setTuckedStatus(TuckedNodeStatus tuckedStatus) { this.tuckedStatus = tuckedStatus; }
	
	/**
	 * @return the {@link TuckedNodeStatus} of this node.
	 */
	public TuckedNodeStatus getTuckedStatus() { return tuckedStatus; }
	
	
	/**
	 * @return true if this TuckedNode is part of the tuckedPath (kept in the related controller). The value is set only once, and upon initialisation.
	 */
	public boolean isNodePartOfTuckedPath() { return isPartOfTuckedPath; }

	/**
	 * @return true if this TuckedNode has gotten its childCounts set.
	 */
	public boolean isNodeSet() { return actualChildCount != -1L && tuckedChildCount != -1L; }
	
	
	
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// [Section II] Handling the node's cached-children; for use in frequent tuck-untuck situations.
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// When we UNTUCK a node, we load the rest of its children. Then if we decide to TUCK it back, we dont want to have
	// to delete them away, since we may UNTUCK them at a later time. So, in that case, we keep them here in loadedTuckedChildren. 
	// Like an internal-nodal-cache. These are affected/consulted at everytime the node stores new children.
	private Deque<TuckedPersonRelationTreeNode> loadedTuckedChildren = null;
	
	/**
	 * @return the previously loaded node representing given {@link ObjectID}.
	 * Returns null if no node containing such objectID is found.
	 */
	protected TuckedPersonRelationTreeNode getLoadedTuckedChildByObjectID(ObjectID objectID) {
		if (loadedTuckedChildren != null && !loadedTuckedChildren.isEmpty()) {
			for (TuckedPersonRelationTreeNode loadedChild : loadedTuckedChildren)
				if (loadedChild.getJdoObjectID().equals(objectID))
					return loadedChild;
		}
		
		return null;
	}
	
	/**
	 * @return true if we have successfully kept the loaded-tucked-child.
	 */
	protected boolean storeLoadedTuckedChild(TuckedPersonRelationTreeNode loadedChildNode) {
		if (loadedTuckedChildren == null)
			loadedTuckedChildren = new LinkedList<TuckedPersonRelationTreeNode>();
		
		if (loadedTuckedChildren.contains(loadedChildNode))
			return false;
		
		
		return loadedTuckedChildren.add(loadedChildNode);
	}
	
	/**
	 * @return true if we have successfully removed the loaded-tucked-child.
	 */
	protected boolean removeLoadedTuckedChild(TuckedPersonRelationTreeNode loadedChildNode) {
		if (loadedTuckedChildren != null && !loadedTuckedChildren.isEmpty())
			return loadedTuckedChildren.remove(loadedChildNode);		
		
		return false;
	}

	@Override
	public synchronized boolean addChildNode(JDOObjectLazyTreeNode<ObjectID, Object, PersonRelationTreeController<? extends PersonRelationTreeNode>> childNode) {
		boolean isAddSucceeded = super.addChildNode(childNode);
		storeLoadedTuckedChild((TuckedPersonRelationTreeNode) childNode);
		
		return isAddSucceeded;
	}
	
	@Override
	public synchronized void setChildNodes(List<JDOObjectLazyTreeNode<ObjectID, Object, PersonRelationTreeController<? extends PersonRelationTreeNode>>> childNodes) {
		super.setChildNodes(childNodes);
		
		for (JDOObjectLazyTreeNode<ObjectID, Object, PersonRelationTreeController<? extends PersonRelationTreeNode>> childNode : childNodes)
			storeLoadedTuckedChild((TuckedPersonRelationTreeNode) childNode);
	}
	
	@Override
	public synchronized boolean removeChildNode(JDOObjectLazyTreeNode<ObjectID, Object, PersonRelationTreeController<? extends PersonRelationTreeNode>> childNode) {
		boolean isRemoveSucceeded = super.removeChildNode(childNode);
		removeLoadedTuckedChild((TuckedPersonRelationTreeNode) childNode);
		
		return isRemoveSucceeded;
	}
	
	
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// [Section III] Handling transitions from UNTUCK to TUCKED, and vice-versa.
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// We refer to this variable during node-status transitions, in order to coordinate the operational changes
	// in the nodes through the system of listeners that already handled by the controller.
	private TuckedNodeStatus statusToChangeTo = TuckedNodeStatus.UNSET;

	/**
	 * Sets the impending new status that this TuckedNode will take after. For example, we set this value from the client's context-menu
	 * to indicate the new {@link TuckedNodeStatus} this node should have.
	 */
	protected void setStatusToChangeTo(TuckedNodeStatus statusToChangeTo) { this.statusToChangeTo = statusToChangeTo; }
	
	/**
	 * @return the impending new status that this TuckedNode will take after. When the value read {@link TuckedNodeStatus}.UNSET, then
	 * nothing should be done to change this node's status.
	 */
	protected TuckedNodeStatus getStatusToChangeTo() { return statusToChangeTo; }
	
	
	
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// [Section IV] Maintaining operational collapse states.
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	private boolean isExpanded = false;
	public boolean isNodeExpanded() { return isExpanded; }
	public void toggleNodeExpandedState() { isExpanded = !isExpanded; }
	
	
	
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// [Section ??] Miscellaneous and debuggings.
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	/**
	 * @return the "tucked" information status of this node, used for for appending the display with an appropriate label provider.
	 * TODO Move this to the appropriate Label Provider!
	 */
	public String getTuckedInfoStatus(ObjectID objectID) {
		// This TuckedNode should behave like a normal PersonRelationTreeNode if it has been "NORMAL"-ised.
		return tuckedStatus.equals(TuckedNodeStatus.TUCKED) && isNodeSet() ? String.format("... (+ %s tucked element(s))", actualChildCount-tuckedChildCount) : "";
	}
	
	/**
	 * Shows the childCounts and statuses and other shits, pertaining to this {@link TuckedPersonRelationTreeNode}.
	 */
	public String toDebugString() {
		String str = this.getClass().getSimpleName() + "@" + PersonRelationTreeUtil.showObjectID(getJdoObjectID());
		if (!isNodeSet())
			return str + " --------->> [UN-set]";
		
		str += "\n  " + PersonRelationTreeUtil.showObjectID(getPropertySetID()) + ": [# tucked: " + tuckedChildCount + "]";
		str += ", [# actual: " + actualChildCount + "], [status: \"" + tuckedStatus + "\"]";
		str += ", [getChildNodeCount(): " + getChildNodeCount() + "]";
		str += ", [loadedTuckedChildren.size(): " + (loadedTuckedChildren == null ? "null" : loadedTuckedChildren.size()) + "]";
		str += ", [isExpanded: " + (isExpanded ? "True" : "False") + "]";

		return str;
	}

}
