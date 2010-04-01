package org.nightlabs.jfire.personrelation.trade.ui.tucked;

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

import org.apache.log4j.Logger;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOObjectLazyTreeNode;
import org.nightlabs.jfire.personrelation.PersonRelationManagerRemote.TuckedQueryCount;
import org.nightlabs.jfire.personrelation.dao.PersonRelationDAO;
import org.nightlabs.jfire.personrelation.id.PersonRelationID;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTree;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeController;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeNode;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;
import org.nightlabs.util.CollectionUtil;

/**
 * A specialised {@link PersonRelationTreeController} to handle {@link TuckedPersonRelationTreeNode}.
 *
 * @author khaireel (at) nightlabs (dot) de
 */
public class TuckedPersonRelationTreeController extends PersonRelationTreeController<TuckedPersonRelationTreeNode> {
	private static final Logger logger = Logger.getLogger(TuckedPersonRelationTreeController.class);

	// Maintain some sort of synchronicity between the two paths; and one that will provide constant time reference in
	// retrieving relatable information between a PRID and its corresponding PSID (and vice-versa).
	private Map<Integer, Deque<ObjectID>> tuckedPRIDPathMaps = null; // <-- mixed PropertySetID & PersonRelationID.
	private Map<Integer, Deque<ObjectID>> tuckedPSIDPathMaps = null; // <-- PropertySetID only.
	

	/**
	 * Set the controller's internal information manipulator for the original tuckedPaths.
	 * @see PersonRelationDAO.getRelationRootNodes().
	 */
	public void setTuckedPaths(Map<Class<? extends ObjectID>, List<Deque<ObjectID>>> tuckedPaths) {
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
			logger.debug("******--******--******--******--******--******--******--******--******--******--******--******");
			logger.debug(" @setTuckedPaths :: paths received = " + index);
			logger.debug("******--******--******--******--******--******--******--******--******--******--******--******");
			
			for(int i=0; i<index; i++)
				logger.debug(PersonRelationTree.showObjectIDs("PSID-path @ref=" + i, tuckedPSIDPathMaps.get(i), 10));
			
			logger.debug("..............................................................................................");

			for(int i=0; i<index; i++)
				logger.debug(PersonRelationTree.showObjectIDs("PRID-path @ref=" + i, tuckedPRIDPathMaps.get(i), 10));

			logger.debug("******--******--******--******--******--******--******--******--******--******--******--******");
		}
	}

	@Override
	protected TuckedPersonRelationTreeNode createNode() {
		return new TuckedPersonRelationTreeNode();
	}

	
	
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	// Handles the interaction to change a node's status from TUCKED to UNTUCKED, and vice-versa. 
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	/**
	 * Given a set of parentsToRefresh in the changedEvent, we specifically look into the node's getStatusToChangeTo() method
	 * and work to properly change it's status.
	 */
	protected void fireTuckChangedEvent(JDOLazyTreeNodesChangedEvent<ObjectID, TuckedPersonRelationTreeNode> changedEvent, ProgressMonitor monitor) {
		Set<TuckedPersonRelationTreeNode> parentsToRefresh = changedEvent.getParentsToRefresh();
		for (TuckedPersonRelationTreeNode parentNode : parentsToRefresh) {
			// Status change: From TUCKED to UNTUCKED.
			if (parentNode.getStatusToChangeTo().equals(TuckedNodeStatus.UNTUCKED)) {
				//  1. Retrieve the children.
				//  2. Create (a new node) + add only those children that are not already loaded.
				Collection<ObjectID> childObjectIDs = retrieveChildObjectIDs(parentNode, new SubProgressMonitor(monitor, 80));
				Set<ObjectID> nextObjectIDsOnPath = getNextObjectIDOnPath(parentNode); // <-- The node representing this ObjectID is ALWAYS loaded, whether the node is TUCKED or UNTUCKED.
				
				for (ObjectID objectID : childObjectIDs)
					if (!nextObjectIDsOnPath.contains(objectID))
						parentNode.addChildNode(createOrRetrieveChildNodeByObjectID(parentNode, objectID));
				
				parentNode.setTuckedStatus(TuckedNodeStatus.UNTUCKED);
				parentNode.setStatusToChangeTo(TuckedNodeStatus.UNSET);
			}
			
			// Status change: From UNTUCKED back to TUCKED.
			else if (parentNode.getStatusToChangeTo().equals(TuckedNodeStatus.TUCKED)) {
				// The set of TUCKED nodes are exactly from the same set of UNTUCKED nodes, which we have already created when
				// dealing with the status change form TUCKED to UNTUCKED. And also, we have kept a reference in the node's very
				// own loadedTuckedChildren, for use in future references.
				Collection<ObjectID> childObjectIDs = retrieveChildObjectIDs(parentNode, new SubProgressMonitor(monitor, 80));
				List<JDOObjectLazyTreeNode<ObjectID, Object, PersonRelationTreeController<? extends PersonRelationTreeNode>>> subListOfTuckedNodes = new ArrayList<JDOObjectLazyTreeNode<ObjectID,Object,PersonRelationTreeController<? extends PersonRelationTreeNode>>>(childObjectIDs.size());				

				for (ObjectID objectID : childObjectIDs)
					subListOfTuckedNodes.add(createOrRetrieveChildNodeByObjectID(parentNode, objectID));
				
				parentNode.setChildNodes(subListOfTuckedNodes);
				parentNode.setTuckedStatus(TuckedNodeStatus.TUCKED);
				parentNode.setStatusToChangeTo(TuckedNodeStatus.UNSET);
			}
		}

		// Done. Now react to the new changes.
		onJDOObjectsChanged(new JDOLazyTreeNodesChangedEvent<ObjectID, TuckedPersonRelationTreeNode>(changedEvent.getSource(), parentsToRefresh));		
	}

	/**
	 * Consults the parentNode's previously loaded child nodes on UNTUCKing (but which are not present due to a subsequent TUCK).
	 * @return the childNode representing the given objectID, if one is already existing. Otherwise, we create a new node, and
	 * have it initialised accordingly.
	 */
	private TuckedPersonRelationTreeNode createOrRetrieveChildNodeByObjectID(TuckedPersonRelationTreeNode parentNode, ObjectID objectID) {
		TuckedPersonRelationTreeNode childNode = parentNode.getLoadedTuckedChildByObjectID(objectID);
		if (childNode == null) {
			childNode = createNode();
			childNode.setActiveJDOObjectLazyTreeController(TuckedPersonRelationTreeController.this);
			childNode.setParent(parentNode);
			childNode.setJdoObjectID(objectID);
			
			addTreeNode(childNode); // <-- For the controller's internal map: objectID2TreeNodeList
		}
		
		return childNode;
	}
	
	@Override
	protected void onJDOObjectsChanged(JDOLazyTreeNodesChangedEvent<ObjectID, TuckedPersonRelationTreeNode> changedEvent) {
		// Tucked information (basically the child counting) may have changed and needs to be refreshed.
		Set<TuckedPersonRelationTreeNode> parentsToRefresh = changedEvent.getParentsToRefresh();
		if (!(changedEvent.getSource() instanceof TuckedPersonRelationTree) && parentsToRefresh != null && !parentsToRefresh.isEmpty()) {
			for (TuckedPersonRelationTreeNode parentNode : parentsToRefresh)
				if (parentNode != null)
					updateTuckedNodeChildCounts(parentNode, new NullProgressMonitor()); // FIXME Put a proper monitor here?
		}

	}
	
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	// Retrieving COUNTs of the children of a given SET of personIDs. 
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	/**
	 * Upon consolidation, we have made it possible to handle the 'general' case of retrieving {@link TuckedPersonRelationTreeNode}'s childCounts.
	 * The only exception here is that if we find cases where the objectIDs (or the nodes containing the objectIDs) are not in our tuckedPath,
	 * then we need to refer them to the super class's methods which should then handle the separate cases between {@link PropertySetID}s and
	 * {@link PersonRelationID}s. These 'unhandled' objectIDs are placed back in the parameter unhandledObjectIDs, and the method calling this
	 * general method should handle this case independently.
	 */
	protected <T extends ObjectID> Map<ObjectID, Long> retrieveTuckedNodeChildCount
	(Map<ObjectID, Long> result, Set<TuckedPersonRelationTreeNode> parentNodes, Set<T> objectIDs, Set<T> unhandledObjectIDs, ProgressMonitor monitor, int tix) {
		// [Note. A.] When our tuckedPath ends, we should get nextIDOnPath == null. In this case, personRelationIDs we come across should not be affect 
		// with our tucked-node methodologies. We gather these IDs and then later on relegate them back to the super class's method. 
		for (T objectID : objectIDs) {
			// ----------------------------------------------------------------------------------->> CONSOLIDATED for codes-optimality ------------>>---||
			// [I] Fetch our related tuckedNode.
			TuckedPersonRelationTreeNode tuckedNode = getTuckedNodeFromObjectID(parentNodes, objectID);
			if (tuckedNode == null) { // <-- When we dont recognise a tuckedNode from our tuckedPath, we simply relegate the ID back to the super class's method.
				unhandledObjectIDs.add(objectID);
				monitor.worked(1);
				continue;
			}
			
			// [II] Update its childCounts.
			boolean isChildCountsUpdated = updateTuckedNodeChildCounts(tuckedNode, new SubProgressMonitor(monitor, 10));
			if (!isChildCountsUpdated) { // <-- See [Note. A.]
				unhandledObjectIDs.add(objectID);
				monitor.worked(1);
				continue;
			}
			
			// [III] If we got all the way up to this point, then we have successfully gotten our tuckedNode 'set'.
			result.put(objectID, tuckedNode.getChildNodeCount());
			monitor.worked(1);
			// ----------------------------------------------------------------------------------->> CONSOLIDATED for codes-optimality ------------>>---||
			
			if (logger.isDebugEnabled())
				logger.debug("*** " + tuckedNode.toDebugString());
		}
		
		return result;
	}
	
	@Override
	protected Map<ObjectID, Long> retrieveChildCountByPersonRelationIDs
	(Map<ObjectID, Long> result, Set<TuckedPersonRelationTreeNode> parentNodes, Set<PersonRelationID> personRelationIDs, ProgressMonitor monitor, int tix) {
		ProgressMonitor subMonitor = new SubProgressMonitor(monitor, tix);
		subMonitor.beginTask("Retrieving child count...", personRelationIDs.size());
		
		if (logger.isDebugEnabled())
			logger.debug("~~~~~~~~~~~~~~ I'm here: @retrieve--[[ChildCount]]--ByPersonRelationIDs.");
		
		Set<PersonRelationID> unhandledPersonRelationIDs = new HashSet<PersonRelationID>();
		result = retrieveTuckedNodeChildCount(result, parentNodes, personRelationIDs, unhandledPersonRelationIDs, subMonitor, tix); // <-- Factorised. Since 2010.03.31.
				
		// If there exists any personRelationIDs that has not been handled for the tucked situation, we let the super class handle them.
		if (!unhandledPersonRelationIDs.isEmpty())
			result = super.retrieveChildCountByPersonRelationIDs(result, parentNodes, unhandledPersonRelationIDs, subMonitor, tix);
		
		subMonitor.done();
		return result;
	}

	@Override
	protected Map<ObjectID, Long> retrieveChildCountByPropertySetIDs
	(Map<ObjectID, Long> result, Set<TuckedPersonRelationTreeNode> parentNodes, Set<PropertySetID> personIDs, ProgressMonitor monitor, int tix) {
		// This should be about as similar as the previous method: retrieveChildCountByPersonRelationIDs().
		ProgressMonitor subMonitor = new SubProgressMonitor(monitor, tix);
		subMonitor.beginTask("Retrieving child count...", personIDs.size());

		if (logger.isDebugEnabled())
			logger.debug("@retrieve..ChildCount..By<<PropertySetID>>s");

		Set<PropertySetID> unhandledPersonIDs = new HashSet<PropertySetID>();
		result = retrieveTuckedNodeChildCount(result, parentNodes, personIDs, unhandledPersonIDs, subMonitor, tix); // <-- Factorised. Since 2010.03.31.
		
		// If there exists any personRelationIDs that has not been handled for the tucked situation, we let
		// the super class handle them.
		if (!unhandledPersonIDs.isEmpty())
			result = super.retrieveChildCountByPropertySetIDs(result, parentNodes, unhandledPersonIDs, subMonitor, tix);
		
		subMonitor.done();
		return result;
	}

	
	// ---------------------------------------------------------------------------------------------------------------------------------- ++ ------>>
	/**
	 * Given a proper {@link TuckedPersonRelationTreeNode}, we check to see if the {@link ObjectID} it carries lie on the
	 * known tuckedPath. If so, we retrieve the childCounts for both actual and tucked. This, however, shall NOT set the
	 * status of the tuckedNode.
	 * @return true if the given tuckedNode has had both its childCounts updated.
	 */
	protected boolean updateTuckedNodeChildCounts(TuckedPersonRelationTreeNode tuckedNode, ProgressMonitor monitor) {
		Set<PropertySetID> nextIDsOnPath = getNextRelatedPropertySetIDOnPath(tuckedNode);
		
		// [Note. A.] When our tuckedPath ends, we should get nextIDOnPath == null. In this case, the personIDs we come across should not be affect 
		// with our tucked-node methodologies. We gather these IDs and then later on relegate them back to the super class's method. 
		if (nextIDsOnPath == null) 
			return false;
		
		Set<PropertySetID> propertySetIDsToRoot = CollectionUtil.createHashSetFromCollection(tuckedNode.getPropertySetIDsToRoot());
		PropertySetID nodePropertySetID = tuckedNode.getPropertySetID();
		if (nodePropertySetID == null) // <-- It is possible that parentNode.getPropertySetID() returns null. But at this point, we know that the next element on the tuckedPath exists!
			nodePropertySetID = getCorrespondingPSID(tuckedNode);
		
		TuckedQueryCount tqCount = PersonRelationDAO.sharedInstance().getTuckedPersonRelationCount(
				null, nodePropertySetID, propertySetIDsToRoot, nextIDsOnPath,
				new SubProgressMonitor(monitor, 20));

		tuckedNode.setActualChildCount(tqCount.actualChildCount);
		tuckedNode.setTuckedChildCount(tqCount.tuckedChildCount);
		return true;
	}
	
	/**
	 * @return the {@link TuckedPersonRelationTreeNode} containing within it's JDOObjectID, the given objectID.
	 * Returns null if the node cannot be found.
	 */
	private TuckedPersonRelationTreeNode getTuckedNodeFromObjectID(Set<TuckedPersonRelationTreeNode> parentNodes, ObjectID objectID) {
		if (parentNodes == null)
			return null;
		
		for (TuckedPersonRelationTreeNode tuckedNode : parentNodes)
			if (tuckedNode != null) {
				ObjectID jdoObjectID = tuckedNode.getJdoObjectID();
				if (jdoObjectID != null && jdoObjectID.equals(objectID))
					return tuckedNode;
			}
		
		return null;
	}

	
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	// Retrieving ObjectIDs of the children of a given parentNode. 
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	@Override
	protected Collection<ObjectID> retrieveChildObjectIDsByPersonRelationIDs(TuckedPersonRelationTreeNode parentNode, ProgressMonitor monitor) {
		// We need some kind of coordination, in order to know what to load here. Done.
		// This depends on the information we can gather from the parentNode: check the status indicated by getStatusToChangeTo().
		if (logger.isDebugEnabled()) {
			logger.debug("~~~~~~~~~~~~~~ I'm here: @retrieveChild--((ObjectID))--sByPersonRelationIDs");
			logger.debug(" ::: parentNode: " + PersonRelationTree.showObjectID(parentNode.getPropertySetID()) + ",  statusToChangeTo: " + parentNode.getStatusToChangeTo());
		}
		
		
		// Our tucked-situational environment is rather unique:
		// The tucked-path is ALWAYs known before hand; i.e. upon initiation, we have the required tucked path, which is the basis for the entire TuckedNode concept.
		// Thus, at any node we encounter, we already know what the next PropertySetID is going to be, and so we don't need to fetch that ID again. This was
		// previously done by first fetching the PersonRelation and then through it we access the getToID(). Note that it is possible to have null, which simply 
		// carries two meanings: 1. we have reached the end of the line, or 2. ALL child nodes need to be fetched, since they don't belong to the tuckedPath.
		Collection<ObjectID> result = new ArrayList<ObjectID>();
		ObjectID parentID = parentNode.getJdoObjectID(); // <-- This parentID is a 'PersonRelationID'.
		Set<PropertySetID> nextIDsOnPath = getNextRelatedPropertySetIDOnPath(parentNode);
		PropertySetID correspondingPSID = getCorrespondingPSID(parentNode); // <-- This is the same as the original getToID(). And, only if the parentNode has been instantiated, then this is the same as parentNode.getPropertySetID().
		
		if (logger.isDebugEnabled()) {
			logger.debug("~~ CHECK I: correspondingPSID = " + PersonRelationTree.showObjectID(correspondingPSID));
			logger.debug(PersonRelationTree.showObjectIDs("            nextIDsOnPath ", nextIDsOnPath, 10));
		}
		
		
		// [Guard check 1] Here, if our nextIDOnPath is null, then this node should be treated as a normal node; i.e. and if we check its status, we should also see 'TuckedNodeStatus.NORMAL'.
		if (nextIDsOnPath == null)
			return parentID instanceof PersonRelationID ? super.retrieveChildObjectIDsByPersonRelationIDs(parentNode, monitor) : super.retrieveChildObjectIDsByPropertySetIDs(parentNode, monitor);
		
		// [Guard check 2] No need to handle a node that has already been handled.
		PropertySetID parentPSID = parentNode.getPropertySetID();
		TuckedNodeStatus statusToChangeTo = parentNode.getStatusToChangeTo();
		if (parentPSID != null && statusToChangeTo.equals(TuckedNodeStatus.UNSET))
			return result;
		
		
		// Load the ID's accordingly. These are the cases that we need to adhere to. (since 2010.03.28)
		//   [Case 1]:: Upon first load. Condition: parentNode.getPropertySetID() == null. Reaction: Default --> treat parentNode as a TuckedNode.
		//   [Case 2]:: Change of status from TUCKED to UNTUCKED. Condition: parentNode.getStatusToChangeTo() == UNTUCKED. Reaction: Untuck parentNode, and retrieve all childObjectIDs as per normal.
		//   [Case 3]:: Change of status from UNTUCKED to TUCKED. Condition: parentNode.getStatusToChangeTo() == TUCKED. Reaction: Default --> treat parentNode as a TuckedNode.
		// [General case]: Ensure that the TuckedNode internal information is current.
		Set<PropertySetID> propertySetIDsToRoot = CollectionUtil.createHashSetFromCollection(parentNode.getPropertySetIDsToRoot());
		TuckedQueryCount tqCount = PersonRelationDAO.sharedInstance().getTuckedPersonRelationCount(
				null, correspondingPSID, propertySetIDsToRoot, nextIDsOnPath, //CollectionUtil.createHashSet(nextIDOnPath), 
				new SubProgressMonitor(monitor, 80));
		
		parentNode.setActualChildCount(tqCount.actualChildCount);
		parentNode.setTuckedChildCount(tqCount.tuckedChildCount);


		// The filtered IDs that we want.
		Collection<PersonRelationID> filteredPersonRelationIDs = null;
		
		// These two conditions are NOT mutually exclusive.
		boolean isHandleTuckedRetrieval = parentPSID == null || statusToChangeTo.equals(TuckedNodeStatus.TUCKED);
		boolean isHandleUnTuckedRetrieval = statusToChangeTo.equals(TuckedNodeStatus.UNTUCKED);
		
		// Now we handle [Case 1] and [Case 3].
		if (isHandleTuckedRetrieval) {
			if (logger.isDebugEnabled())
				logger.debug(":::: Handling [Case 1] and [Case 3] :::: ::::::::::::::::::::");
			
			filteredPersonRelationIDs = PersonRelationDAO.sharedInstance().getInclusiveFilteredPersonRelationIDs(
					null, correspondingPSID, null,
					null, nextIDsOnPath,
					getPersonRelationComparator(),
					new SubProgressMonitor(monitor, 80));
			
			parentNode.setTuckedStatus(TuckedNodeStatus.TUCKED);
		}
		// Handle [Case 2].
		else if (isHandleUnTuckedRetrieval) {
			if (logger.isDebugEnabled())
				logger.debug(":::: Handling [Case 2] :::: :::::::::::::::::::::::::::::::::");
			
			filteredPersonRelationIDs = PersonRelationDAO.sharedInstance().getFilteredPersonRelationIDs(
					null, correspondingPSID, null,
					null, propertySetIDsToRoot,
					getPersonRelationComparator(),
					new SubProgressMonitor(monitor, 80));			
			
			parentNode.setTuckedStatus(TuckedNodeStatus.UNTUCKED);
		}
		
		if (logger.isDebugEnabled())
			logger.debug("++++++++++++++++++++++++ -------------->>>  " + parentNode.toDebugString());
		
		

		// Tidy up, and we're done.
		if (filteredPersonRelationIDs != null) {
			parentNode.setChildNodeCount(filteredPersonRelationIDs.size());			
			result.addAll(filteredPersonRelationIDs);
		}

		return result;
	}

	// ---------------------------------------------------------------------------------------------------------------------------------- ++ ------>>
	@Override
	protected Collection<ObjectID> retrieveChildObjectIDsByPropertySetIDs(TuckedPersonRelationTreeNode parentNode, ProgressMonitor monitor) {
		// Similar to the method retrieveChildObjectIDsByPersonRelationIDs, we need some kind of coordination.
		// And theoretically, we can handle them collectively! ... and so far, the theory is holding :)
		if (!parentNode.isNodeSet())
			parentNode.setStatusToChangeTo(TuckedNodeStatus.TUCKED); // <-- We just need to ensure that the node is TUCKED upon initialisation.
		
		if (logger.isDebugEnabled())
			logger.debug("~~~~~~~~~~~~~~ I'm here: @retrieveChild--((ObjectID))--sByPropertySetIDs");		
		
		return retrieveChildObjectIDsByPersonRelationIDs(parentNode, monitor); // <-- Factorised since 2010.03.30.
	}

	


	// -------------------------------------------------------------------------------------------------- ++ ------>>
	// These manage the the correlated correspondence between the tuckedPSIDPaths and tuckedPRIDPaths. 
	// -------------------------------------------------------------------------------------------------- ++ ------>>
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
	private List<Integer> getPathIndexReferences(TuckedPersonRelationTreeNode tuckedNode) {
		LinkedList<Integer> indexRefs = new LinkedList<Integer>();
		
		// For the correct identification of the path's index, the given tuckedNode must have already been duly and correctly initialised.
		LinkedList<ObjectID> objectIDsToRoot = (LinkedList<ObjectID>) tuckedNode.getJDOObjectIDsToRoot(); // The root reference is at the beginning of this List.
		
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
	 * @return the next {@link PropertySetID}s on the related tuckedPSIDpath, based on the {@link ObjectID} from the given tuckedNode.
	 * Returns null, if no match is found.
	 */
	private Set<PropertySetID> getNextRelatedPropertySetIDOnPath(TuckedPersonRelationTreeNode tuckedNode) {
		List<Integer> indexes = getPathIndexReferences(tuckedNode);
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
				if (iterPRID.next().equals(tuckedNode.getJdoObjectID()) && iterPSID.hasNext()) {
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
	 * @return the next {@link ObjectID}s on the related tuckedPRIDPath, based on the {@link ObjectID} from the given tuckedNode.
	 * Returns null, if not match is found.
	 */
	private Set<ObjectID> getNextObjectIDOnPath(TuckedPersonRelationTreeNode tuckedNode) {
		List<Integer> indexes = getPathIndexReferences(tuckedNode);
		if (indexes.isEmpty())
			return null;
		
		Set<ObjectID> objectIDs = null;
		for (Integer index : indexes) {
			Deque<ObjectID> tuckedPRIDPath = tuckedPRIDPathMaps.get(index);
			Iterator<ObjectID> iterPRID = tuckedPRIDPath.iterator();
			while (iterPRID.hasNext()) {
				if (iterPRID.next().equals(tuckedNode.getJdoObjectID()) && iterPRID.hasNext()) {
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
	 * @return the corresponding {@link PropertySetID}, based on the {@link ObjectID} from the given tuckedNode.
	 * Returns null, if no match is found.
	 */
	private PropertySetID getCorrespondingPSID(TuckedPersonRelationTreeNode tuckedNode) {
		List<Integer> indexes = getPathIndexReferences(tuckedNode);
		if (indexes.isEmpty())
			return null;
		
		Deque<ObjectID> tuckedPRIDPath = tuckedPRIDPathMaps.get(indexes.get(0));
		Deque<ObjectID> tuckedPSIDPath = tuckedPSIDPathMaps.get(indexes.get(0));

		Iterator<ObjectID> iterPRID = tuckedPRIDPath.iterator();
		Iterator<ObjectID> iterPSID = tuckedPSIDPath.iterator();
		while (iterPRID.hasNext()) {
			ObjectID psID = iterPSID.next();
			if (iterPRID.next().equals(tuckedNode.getJdoObjectID()))
				return (PropertySetID) psID;
		}

		return null;
	}

	/**
	 * @return the sub-path of the tuckedPRIDpath, up to and inclusive of the currentID in the given tuckedNode.
	 * Returns null if the given currentID is not found in any of the known tuckedPRIDpaths.
	 */
	protected Deque<ObjectID> getSubPathUpUntilCurrentID(TuckedPersonRelationTreeNode tuckedNode) {
		List<Integer> indexes = getPathIndexReferences(tuckedNode);
		if (indexes.isEmpty())
			return null;
		
		Deque<ObjectID> tuckedPRIDPath = tuckedPRIDPathMaps.get(indexes.get(0));
		Deque<ObjectID> subPath = new LinkedList<ObjectID>();
		for (ObjectID objectID : tuckedPRIDPath) {
			subPath.add(objectID);
			if (objectID.equals(tuckedNode.getJdoObjectID()))
				return subPath;
		}

		return null;
	}
}
