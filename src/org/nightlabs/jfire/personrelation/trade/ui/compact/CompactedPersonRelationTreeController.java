package org.nightlabs.jfire.personrelation.trade.ui.compact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.personrelation.PersonRelationManagerRemote.TuckedQueryCount;
import org.nightlabs.jfire.personrelation.dao.PersonRelationDAO;
import org.nightlabs.jfire.personrelation.id.PersonRelationID;
import org.nightlabs.jfire.personrelation.trade.ui.tucked.TuckedPersonRelationTreeController;
import org.nightlabs.jfire.personrelation.trade.ui.tucked.TuckedPersonRelationTreeNode;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeController;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeUtil;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.CollectionUtil;

/**
 * Another specialised {@link PersonRelationTreeController}. This time tailored to handle the more complicated {@link CompactedPersonRelationTree}
 * and its {@link CompactedPersonRelationTreeNode}s.
 *
 * ASSUMPTIONS to be LIFTED:
 *  1. All roots provided in the tuckedPath are unique; i.e. no forking paths allowed.
 *  
 *  
 * ON LIFTING the above assumptions:
 *  1. Only on initiation: When determining which of the paths to instantiate, paths with same ancestor roots (or sub-paths to roots)
 *     can be checked against the known nodes that are already created. In other words, these instantiated root nodes will already have
 *     within them one of the (multiple, forking) tucked-paths. And so we select the one that has not previously used.
 *
 * @author khaireel at nightlabs dot de
 */
public class CompactedPersonRelationTreeController extends PersonRelationTreeController<CompactedPersonRelationTreeNode> {
	private static final Logger logger = Logger.getLogger(CompactedPersonRelationTreeController.class);
	private int sequenceCtr = 0;
	
	// Maintain some sort of synchronicity between the two paths; and one that will provide constant time reference in
	// retrieving relatable information between a PRID and its corresponding PSID (and vice-versa).
	private Map<Integer, Deque<ObjectID>> tuckedPRIDPathMaps = null; // <-- mixed PropertySetID & PersonRelationID.
	private Map<Integer, Deque<ObjectID>> tuckedPSIDPathMaps = null; // <-- PropertySetID only.
	

	/**
	 * Set the controller's internal information manipulator for the original tuckedPaths.
	 * @return the {@link PropertySetID}s of the roots of all the known tucked-paths from this controller.
	 * @see PersonRelationDAO.getRelationRootNodes().
	 */
	public Set<PropertySetID> setTuckedPaths(Map<Class<? extends ObjectID>, List<Deque<ObjectID>>> tuckedPaths) {
		List<Deque<ObjectID>> pathsToRoot_PRID = tuckedPaths.get(PersonRelationID.class); // <-- mixed PropertySetID & PersonRelationID.
		List<Deque<ObjectID>> pathsToRoot_PSID = tuckedPaths.get(PropertySetID.class);    // <-- PropertySetID only.

		// Initialise the path-expansion trackers.
		tuckedPRIDPathMaps = new HashMap<Integer, Deque<ObjectID>>(pathsToRoot_PRID.size());
		tuckedPSIDPathMaps = new HashMap<Integer, Deque<ObjectID>>(pathsToRoot_PRID.size());
		
		Iterator<Deque<ObjectID>> iterPaths_PRID = pathsToRoot_PRID.iterator();
		Iterator<Deque<ObjectID>> iterPaths_PSID = pathsToRoot_PSID.iterator();
		int index = 0;
		while (iterPaths_PSID.hasNext()) {
			tuckedPRIDPathMaps.put(index, iterPaths_PRID.next());
			tuckedPSIDPathMaps.put(index, iterPaths_PSID.next());
			index++;
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("==============================================================================================");
			logger.debug("@setTuckedPaths :: paths received = " + index);
			logger.debug("==============================================================================================");
			
			for(int i=0; i<index; i++)
				logger.debug(PersonRelationTreeUtil.showObjectIDs("PSID-path @ref=" + i, tuckedPSIDPathMaps.get(i), 10));
			
			logger.debug("..............................................................................................");

			for(int i=0; i<index; i++)
				logger.debug(PersonRelationTreeUtil.showObjectIDs("PRID-path @ref=" + i, tuckedPRIDPathMaps.get(i), 10));

			logger.debug("==============================================================================================");
		}
		
		return getTuckedPathsRootIDs();
	}

	/**
	 * @return the {@link PropertySetID}s of the roots of all the known tucked-paths from this controller.
	 */
	protected Set<PropertySetID> getTuckedPathsRootIDs() {
		if (tuckedPRIDPathMaps == null)
			return null;
		
		Set<PropertySetID> rootIDs = new HashSet<PropertySetID>(tuckedPSIDPathMaps.size());
		for (Deque<ObjectID> tuckedPSIDPath : tuckedPSIDPathMaps.values())
			rootIDs.add((PropertySetID) tuckedPSIDPath.peekFirst());
		
		return rootIDs; // Consider having this somehow sorted?
	}
	
	/**
	 * @return the tucked-path of {@link PropertySetID}s, in which the {@link ObjectID} represented in the given node is within
	 * the corresponding tucked-path of {@link ObjectID}s. Returns null otherwise.
	 */
	protected Deque<ObjectID> getTuckedPath(CompactedPersonRelationTreeNode node) {
		List<Integer> indexReferences = getPathIndexReferences(node);
		if (!indexReferences.isEmpty())
			return tuckedPSIDPathMaps.get(indexReferences.get(0)); // TODO See ASSUMPTION 1. Also see notes on how to lift ASSUMPTION 1 cleanly.
		
		return null;
	}
	
	/**
	 * @return the {@link TuckedPathDosier} in which the {@link ObjectID} represented in the given node is within
	 * the returned tucked-path. Returns null otherwise.
	 */
	protected TuckedPathDosier getTuckedPathDosier(CompactedPersonRelationTreeNode node) {
		List<Integer> indexReferences = getPathIndexReferences(node);
		if (!indexReferences.isEmpty()) {
			TuckedPathDosier tpDosier = new TuckedPathDosier();
			tpDosier.controllerIndexRef = indexReferences.get(0); // TODO See ASSUMPTION 1. Also see notes on how to lift ASSUMPTION 1 cleanly.
			tpDosier.pathPSID = CollectionUtil.collection2TypedArray(tuckedPSIDPathMaps.get(tpDosier.controllerIndexRef), ObjectID.class);
			tpDosier.pathPRID = CollectionUtil.collection2TypedArray(tuckedPRIDPathMaps.get(tpDosier.controllerIndexRef), ObjectID.class);
			
			return tpDosier;
		}
		
		return null;
	}

	
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// [Section] Tucked-path management
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// These manage the the correlated correspondence between the tuckedPSIDPaths and tuckedPRIDPaths. 
	// Notes on the multiple forked paths:
	// Let P1, P2, ..., Pn be the n unique paths in PSIDPaths.
	// Let P' \subset PSIDPaths so that P' is the set of unique paths containing an element e.
	// Then the following is true: 
	//   (i) Each subPath from the element e to the root, for all paths in P', is unique; and
	//  (ii) Each subPath from the element e to the final-child, for all paths in P', is the same.
	//
	// This means that we can correctly identify the correct path P \elementOf PSIDPaths, given an element e, if and only if we
	// we have the subPath from the element e to the root.
	/**
	 * @return the reference indexes to all the paths containing the mathing subPath-to-the-root based on the given tuckedNode.
	 * Returns an empty list if no reference(s) to the path can be found.
	 */
	private List<Integer> getPathIndexReferences(CompactedPersonRelationTreeNode node) {
		LinkedList<Integer> indexRefs = new LinkedList<Integer>();
		
		// For the correct identification of the path's index, the given tuckedNode must have already been duly and correctly initialised.
		LinkedList<ObjectID> objectIDsToRoot = (LinkedList<ObjectID>) node.getJDOObjectIDsToRoot(); // The root reference is at the beginning of this List.
		
		for (int index = 0; index < tuckedPRIDPathMaps.size(); index++) {
			Deque<ObjectID> tuckedPRIDPath = tuckedPRIDPathMaps.get(index);  // [Note. B.] The root reference is at the END of this Deque. Yeah, I know, how stupidly annoying... Kai :/
			Iterator<ObjectID> iterPRIDPath = tuckedPRIDPath.iterator();
			
			// This loop terminates on three conditions:
			//   1. CLEANLY. When both paths we are comparing are the same length, and have exactly the same elements.
			//   2. CLEANLY. When objectIDsToRoot is exactly a subPath of iterPRIDPath. --> In which case, we simply keep the index value at where we stopped.
			//   3. When we find disequality between elements.
			boolean isSubPath = true;
			for(Iterator<ObjectID> iterObjectID = objectIDsToRoot.descendingIterator(); iterObjectID.hasNext(); ) { // See reason in [Note. B.] above.
				ObjectID objectID = iterObjectID.next();
				if (!iterPRIDPath.hasNext()) // [Case 2.] If we survive up till here, which means we have exhausted the elements in objectIDsToRoot, which means we have found the correct index reference.
					break;
				
				if (!iterPRIDPath.next().equals(objectID)) { // [Case 3.]
					isSubPath = false;
					break;
				}
			}
			
			if (isSubPath) // [Case 1.]
				indexRefs.add(index);
		}
		
		return indexRefs;
	}

	/**
	 * @return the next {@link PropertySetID}s on the related tuckedPSIDpath, based on the {@link ObjectID} from the given compactedTuckedNode.
	 * Returns null, if no match is found.
	 */
	private Set<PropertySetID> getNextRelatedPropertySetIDOnPath(CompactedPersonRelationTreeNode compactedTuckedNode) {
		List<Integer> indexes = getPathIndexReferences(compactedTuckedNode);
		if (indexes.isEmpty())
			return null;
		
		Set<PropertySetID> propertySetIDs = null;
		for (Integer index : indexes) {
			Deque<ObjectID> tuckedPRIDPath = tuckedPRIDPathMaps.get(index);
			Deque<ObjectID> tuckedPSIDPath = tuckedPSIDPathMaps.get(index);
			Iterator<ObjectID> iterPRID = tuckedPRIDPath.iterator();
			Iterator<ObjectID> iterPSID = tuckedPSIDPath.iterator();
			
			while (iterPRID.hasNext()) {
				iterPSID.next();
				if (iterPRID.next().equals(compactedTuckedNode.getJdoObjectID()) && iterPSID.hasNext()) {
					// return (PropertySetID) (iterPSID.hasNext() ? iterPSID.next() : null); // <-- Original: Handles the singular form.
					if (propertySetIDs == null)
						propertySetIDs = new HashSet<PropertySetID>();
					
					propertySetIDs.add((PropertySetID) iterPSID.next());
					break;
				}
			}
		}

		return propertySetIDs;
	}
	
	/**
	 * @return the next {@link ObjectID}s on the related tuckedPRIDPath, based on the {@link ObjectID} from the given compactedTuckedNode.
	 * Returns null, if not match is found.
	 */
	private Set<ObjectID> getNextObjectIDOnPath(CompactedPersonRelationTreeNode compactedTuckedNode) {
		List<Integer> indexes = getPathIndexReferences(compactedTuckedNode);
		if (indexes.isEmpty())
			return null;
		
		Set<ObjectID> objectIDs = null;
		for (Integer index : indexes) {
			Deque<ObjectID> tuckedPRIDPath = tuckedPRIDPathMaps.get(index);
			Iterator<ObjectID> iterPRID = tuckedPRIDPath.iterator();
			while (iterPRID.hasNext()) {
				if (iterPRID.next().equals(compactedTuckedNode.getJdoObjectID()) && iterPRID.hasNext()) {
					// return iterPRID.hasNext() ? iterPRID.next() : null; // <-- Original: Handles the 'singular' form.
					if (objectIDs == null)
						objectIDs = new HashSet<ObjectID>();
					
					objectIDs.add(iterPRID.next());
					break;
				}
			}
		}		

		return objectIDs;
	}
	
	/**
	 * @return the corresponding {@link PropertySetID}, based on the {@link ObjectID} from the given compactedTuckedNode.
	 * Returns null, if no match is found.
	 */
	private PropertySetID getCorrespondingPSID(CompactedPersonRelationTreeNode compactedTuckedNode) {
		List<Integer> indexes = getPathIndexReferences(compactedTuckedNode);
		if (indexes.isEmpty())
			return null;
		
		Deque<ObjectID> tuckedPRIDPath = tuckedPRIDPathMaps.get(indexes.get(0));
		Deque<ObjectID> tuckedPSIDPath = tuckedPSIDPathMaps.get(indexes.get(0));

		Iterator<ObjectID> iterPRID = tuckedPRIDPath.iterator();
		Iterator<ObjectID> iterPSID = tuckedPSIDPath.iterator();
		while (iterPRID.hasNext()) {
			ObjectID psID = iterPSID.next();
			if (iterPRID.next().equals(compactedTuckedNode.getJdoObjectID()))
				return (PropertySetID) psID;
		}

		return null;
	}
	
	
	
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// [Section] Preparation of the related CompactedPersonRelationTreeNode.
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	@Override
	protected CompactedPersonRelationTreeNode createNode() {
		CompactedPersonRelationTreeNode node = new CompactedPersonRelationTreeNode();
		logger.debug(" ~~~ sequenceCtr: " + (sequenceCtr++) + " ~~~ ********* @createNode() :: meNodeID = " + node.meNodeID);		
		return node;
		
//		return new CompactedPersonRelationTreeNode();
	}
	


	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// [Section] Handles the retrieval of information, based on specific compacted-tucked situations.
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	/**
	 * Consolidated. Idea from original {@link TuckedPersonRelationTreeController}.
	 * Given a proper {@link TuckedPersonRelationTreeNode}, we check to see if the {@link ObjectID} it carries lie on the
	 * known tuckedPath. If so, we retrieve the childCounts for both actual and tucked. This, however, shall NOT set the
	 * status of the tuckedNode.
	 * @return true if the given tuckedNode has had both its childCounts updated for a nodeStatus other than NORMAL.
	 */
	protected boolean updateTuckedNodeChildCounts(CompactedPersonRelationTreeNode compactedTuckedNode, ProgressMonitor monitor) {
		Set<PropertySetID> propertySetIDsToRoot = CollectionUtil.createHashSetFromCollection(compactedTuckedNode.getPropertySetIDsToRoot()); // <-- We can use this here, no problems! See notes to figure out why, or simply ask Kai.
		PropertySetID nodePropertySetID = compactedTuckedNode.getPropertySetID();
		if (nodePropertySetID == null) // <-- It is possible that parentNode.getPropertySetID() returns null. But at this point, we know that the next element on the tuckedPath exists!
			nodePropertySetID = getCorrespondingPSID(compactedTuckedNode);
				
		// [Note. A.] When our tuckedPath ends, we should get nextIDOnPath == null. In this case, the personIDs we come across should not be affect 
		// with our tucked-node methodologies. We gather these IDs and then later on relegate them back to the super class's method. 
		Set<PropertySetID> nextIDsOnPath = getNextRelatedPropertySetIDOnPath(compactedTuckedNode);
		TuckedQueryCount tqCount = PersonRelationDAO.sharedInstance().getTuckedPersonRelationCount(
				null, nodePropertySetID, propertySetIDsToRoot, nextIDsOnPath,
				monitor);

		compactedTuckedNode.setActualChildCount(tqCount.actualChildCount);		
		compactedTuckedNode.setTuckedChildCount(tqCount.tuckedChildCount);
		
		// Special NORMAL case.
		if (nextIDsOnPath == null) {
			// In our implementation, this would mean that the compactedTuckedNode in the parameter is a NORMAL node.
			// So we simply take the value of the actualChildCount, and take that as the node's official childCount.
			if (logger.isDebugEnabled()) {
				logger.debug(" !!! ~~~ @updateTuckedNodeChildCounts, where nextIDsOnPath == null: " + PersonRelationTreeUtil.showQuickNodeInfo(compactedTuckedNode) + ", meNodeID=" + compactedTuckedNode.meNodeID);
				logger.debug(PersonRelationTreeUtil.showObjectIDs(" !!! ~~~ propertySetIDsToRoot", propertySetIDsToRoot, 10));
				logger.debug(" !!! ~~~ actualChildCount: " + tqCount.actualChildCount + ", tuckedChildCount: " + tqCount.tuckedChildCount);
			}
			
			compactedTuckedNode.setChildNodeCount(tqCount.actualChildCount);
			return false;
		}
		
		return true;
	}
	
	@Override
	protected Set<CompactedPersonRelationTreeNode> fillUpNodeCounts
	(Map<ObjectID, Long> parentObjectID2NodeCount, Map<ObjectID, List<CompactedPersonRelationTreeNode>> parentObjectID2ParentTreeNodeList, ProgressMonitor monitor) {
		// Note: This method should return those nodes that needs to be refreshed.
		// When filling up the node's childCount, we have to be careful whenever we encounter COLLECTIVE nodes.
		if (logger.isDebugEnabled()) {
			logger.debug(" ~~~ sequenceCtr: " + (sequenceCtr++) + " ~~~ ********* @fillUpNodeCounts() ### ******* " + PersonRelationTreeUtil.showObjectIDs("NodeOIDs", parentObjectID2NodeCount.keySet(), 10));
		}
		
		Set<CompactedPersonRelationTreeNode> nodesToBeRefreshed = super.fillUpNodeCounts(parentObjectID2NodeCount, parentObjectID2ParentTreeNodeList, monitor);
		for (CompactedPersonRelationTreeNode treeNode : nodesToBeRefreshed) {
			// Check to see if the treeNode is a COLLECTIVE node. If so, we amend its childCounts appropriately through the data
			// that should already be available in its node-representative.
			CompactedPersonRelationTreeNode nodeRepresentative = treeNode.getNodeRepresentative();
			if (nodeRepresentative != null) {
				treeNode.setChildNodeCount(nodeRepresentative.getChildNodeCount());
				treeNode.setNodeStatus(nodeRepresentative.getNodeStatus());
				
				if (logger.isDebugEnabled())
					logger.debug(" ### ##### FOUND a COLLECTIVE node while updating child-counts. Node identified as COLLECTIVE: meNodeID = " + treeNode.meNodeID + ", nodeRepresentative.meNodeID = " + nodeRepresentative.meNodeID);
			}
		}
		
		return nodesToBeRefreshed;
	}
	
	@Override
	protected Collection<Object> retrieveJDOObjectsByPropertySetIDs(Collection<Object> result, Set<PropertySetID> personIDs, ProgressMonitor monitor, int tix) {
		if (logger.isDebugEnabled()) {
			logger.debug(" ~~~ sequenceCtr: " + (sequenceCtr++) + " ~~~ ********* @retrieveJDOObjectsByPropertySetIDs() -- [I]");
			logger.debug(" ###                                          ######################## Called! ### :: retrieveJDOObjectsByPropertySetIDs()");
			logger.debug(PersonRelationTreeUtil.showObjectIDs(" ### @retrieveJDOObjectsByPropertySetIDs: personIDs", personIDs, 10));
		}
		
		Collection<Object> results = super.retrieveJDOObjectsByPropertySetIDs(result, personIDs, monitor, tix);
		
		// Note: This is where we do the FIRST initialisation of the root CompactedPersonRelationTreeNodes.
		//       i.e. All compacted-tucked root nodes first appearance, should be loaded through here.
		//
		// If the above note is true, then we WILL be able to have access to COLLECTIVE shell of the CompactedPersonRelationTreeNodes,
		// based on the personID. This shell MUST be empty, an completely uninitialised.
		for (PropertySetID personID : personIDs) {
			List<CompactedPersonRelationTreeNode> treeNodeList = getTreeNodeList(personID);
			
			// If there are multiple elements in the returned list, then this means that the COLLECTIVE shell we were after
			// has already been initialised, and thus, if this happens, then we skip it.
			if (treeNodeList != null && treeNodeList.size() == 1) {
				CompactedPersonRelationTreeNode rootShellNode = treeNodeList.get(0); // Also, if we now check its nodeStatus, it should tell us that it is UNSET.
				
				if (logger.isDebugEnabled()) {
					logger.debug(" ~~~ sequenceCtr: " + (sequenceCtr++) + " ~~~ ********* @retrieveJDOObjectsByPropertySetIDs() -- [II]");
					logger.debug(" ### Found SHELL node @personID: " + PersonRelationTreeUtil.showObjectID(personID));
					logger.debug(" ### Before init: " + rootShellNode.toDebugString());
				}
				
				// So, now we go about our business of setting up the UNSET node.
				// A. Handle its internal nodes that are part of the tucked-path. These are already arranged in order of path traversal from root to the end-child element.
				int pathLen = rootShellNode.tuckedPathDosier.pathPSID.length;
				Set<PersonRelationID> personRelationIDsInPathToLoad = new HashSet<PersonRelationID>(pathLen-1);
				
				for (int i=0; i<pathLen; i++) {
					// B. Create a new node to represent its related JDOObjectID, an initialise it as we know it up until now. 
					CompactedPersonRelationTreeNode tuckedNodeInPath = createNode();
					tuckedNodeInPath.setActiveJDOObjectLazyTreeController(CompactedPersonRelationTreeController.this);
					tuckedNodeInPath.setPropertySetID((PropertySetID) rootShellNode.tuckedPathDosier.pathPSID[i]);
					tuckedNodeInPath.setJdoObjectID(rootShellNode.tuckedPathDosier.pathPRID[i]);
					tuckedNodeInPath.setNodeStatus(i < pathLen-1 ? CompactedNodeStatus.COMPACTED : CompactedNodeStatus.NORMAL);
					
					// C. Set the new tuckedNodeInPath into rootShellNode. The following will take care of the parentage of the node.
					rootShellNode.setTuckedNodeInPath(tuckedNodeInPath);
					
					// D. We should let the framework of the super class to take care of filling up the reference into the node, 
					//    whenever there are future events that may cause the information within the node to change.
					addTreeNode(tuckedNodeInPath);
					
					// E. For all other node except the root, we assemble their JDOObjectIDs and load the JDOObjects appropriately.
					//    i.e. In this case,  (i) the JDOObjectID = PersonRelationID, and
					//                       (ii) the JDOObject = PersonRelation.
					if (i > 0)
						personRelationIDsInPathToLoad.add((PersonRelationID) tuckedNodeInPath.getJdoObjectID());
					
					// F. Perform the specialised actual and tucked childCounts.
					boolean isCountSuccessful = updateTuckedNodeChildCounts(tuckedNodeInPath, monitor);
					if (isCountSuccessful) {
						rootShellNode.setCollectiveChildCounts(tuckedNodeInPath);
					}
					else {
						// This is the case where the tuckedNodeInPath is the last element in the known tucked-path; i.e. its
						// nodeStatus would have been set to NORMAL. In this case, this node would be handled 'normally', and its correct childCount
						// appropriately set.
						logger.debug("!!! Node counting reverted for NORMAL node. " + PersonRelationTreeUtil.showObjectID(tuckedNodeInPath.getPropertySetID()));

						// H. Set the COLLECTIVE shell-node's own sync-ed information.
						//    This tuckedNodeInPath now corresponds to the last element in the tucked-path of the collective node, and thus this node
						//    is the "node-representative" of the rootShellNode.
						rootShellNode.setChildNodeCount(tuckedNodeInPath.getChildNodeCount());
						rootShellNode.setNodeStatus(tuckedNodeInPath.getNodeStatus());
					}
				}
				
				// G. Load the JDOObjects, and place the references in the correct nodes.
				if (!personRelationIDsInPathToLoad.isEmpty()) {
					Collection<Object> resultInPath = new ArrayList<Object>(personRelationIDsInPathToLoad.size());
					resultInPath = super.retrieveJDOObjectsByPersonRelationIDs(resultInPath, personRelationIDsInPathToLoad, monitor, tix);
					
					for (Object jdoObject : resultInPath) {
						ObjectID objectID = (ObjectID) JDOHelper.getObjectId(jdoObject);
						List<CompactedPersonRelationTreeNode> treeNodes = getTreeNodeList(objectID);
						
						if (treeNodes == null)
							continue;
						
						for (CompactedPersonRelationTreeNode treeNode : treeNodes) 
							treeNode.setJdoObject(jdoObject);
					}
				}
				
				// Done.
				if (logger.isDebugEnabled()) {
					logger.debug(" ~~~ sequenceCtr: " + (sequenceCtr++) + " ~~~ ********* @retrieveJDOObjectsByPropertySetIDs() -- [III]");
					logger.debug(" ### After init: " + rootShellNode.toDebugString());
				}
			}
		}		
		
		return results;
	}
	
	@Override
	protected Collection<ObjectID> retrieveChildObjectIDsByPropertySetIDs(CompactedPersonRelationTreeNode parentNode, ProgressMonitor monitor) {
		CompactedPersonRelationTreeNode nodeRepresentative = parentNode.getNodeRepresentative();
		if (nodeRepresentative != null) {
			if (logger.isDebugEnabled()) {
				logger.debug(" ### ##### FOUND a COLLECTIVE node while retrieveChildObjectIDsByPropertySetIDs() !!! +++++++++ <Poss. 1>");
				logger.debug(" ### ##### Node identified as COLLECTIVE: meNodeID = " + parentNode.meNodeID + ", nodeRepresentative.meNodeID = " + nodeRepresentative.meNodeID);
			}
			
			return super.retrieveChildObjectIDsByPersonRelationIDs(nodeRepresentative, monitor);
		}
		
		return super.retrieveChildObjectIDsByPropertySetIDs(parentNode, monitor);
	}
	
	@Override
	protected Collection<ObjectID> retrieveChildObjectIDsByPersonRelationIDs(CompactedPersonRelationTreeNode parentNode, ProgressMonitor monitor) {
		if (logger.isDebugEnabled()) {
			CompactedPersonRelationTreeNode nodeRepresentative = parentNode.getNodeRepresentative();
			if (nodeRepresentative != null) {
				logger.debug(" ### ##### FOUND a COLLECTIVE node while retrieveChildObjectIDsByPersonRelationIDs() !!! +++++++++ <Poss. 2>");
				logger.debug(" ### ##### Node identified as COLLECTIVE: meNodeID = " + parentNode.meNodeID + ", nodeRepresentative.meNodeID = " + nodeRepresentative.meNodeID);
			}
		}
		
		return super.retrieveChildObjectIDsByPersonRelationIDs(parentNode, monitor);
	}
	
}
