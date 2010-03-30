package org.nightlabs.jfire.personrelation.trade.ui.tucked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
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

	// A reference to the 'tucked'-path(s) handled by this controller.
	private List<Deque<ObjectID>>  tuckedPRIDPaths = null; // <-- mixed PropertySetID & PersonRelationID.
	private List<Deque<ObjectID>>  tuckedPSIDPaths = null; // <-- PropertySetID only.

	/**
	 * Set the controller's internal information manipulator for the original tuckedPaths.
	 * @see PersonRelationDAO.getRelationRootNodes().
	 */
	public void setTuckedPaths(Map<Class<? extends ObjectID>, List<Deque<ObjectID>>> tuckedPaths) {
		tuckedPRIDPaths = tuckedPaths.get(PersonRelationID.class);
		tuckedPSIDPaths = tuckedPaths.get(PropertySetID.class);
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
				ObjectID nextObjectIDOnPath = getNextObjectIDOnPath(parentNode.getJdoObjectID()); // <-- The node representing this ObjectID is ALWAYS loaded, whether the node is TUCKED or UNTUCKED.
				
				for (ObjectID objectID : childObjectIDs)
					if (!objectID.equals(nextObjectIDOnPath))
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
				if (parentNode != null) {
					updateTuckedNodeChildCounts(parentNode, new NullProgressMonitor()); // TEST
//					parentNode.setStatusToChangeTo(TuckedNodeStatus.TOREFRESH);
//					retrieveChildObjectIDs(parentNode, new NullProgressMonitor());
//					parentNode.setStatusToChangeTo(TuckedNodeStatus.UNSET);
				}
		}

	}
	
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	// Retrieving COUNTs of the children of a given SET of personIDs. 
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	@Override
	protected Map<ObjectID, Long> retrieveChildCountByPersonRelationIDs
	(Map<ObjectID, Long> result, Set<TuckedPersonRelationTreeNode> parentNodes, Set<PersonRelationID> personRelationIDs, ProgressMonitor monitor, int tix) {
		ProgressMonitor subMonitor = new SubProgressMonitor(monitor, tix);
		subMonitor.beginTask("Retrieving child count...", personRelationIDs.size());
		
		if (logger.isDebugEnabled())
			logger.debug("~~~~~~~~~~~~~~ I'm here: @retrieve--[[ChildCount]]--ByPersonRelationIDs.");
		
		// [Note. A.] When our tuckedPath ends, we should get nextIDOnPath == null. In this case, personRelationIDs we come across should not be affect 
		// with our tucked-node methodologies. We gather these IDs and then later on relegate them back to the super class's method. 
		Set<PersonRelationID> unhandledPersonRelationIDs = new HashSet<PersonRelationID>();		
		for (PersonRelationID personRelationID : personRelationIDs) {
			PropertySetID nextIDOnPath = getNextRelatedPropertySetIDOnPath(personRelationID);
			
			// See [Note. A.]
			if (nextIDOnPath == null) {
				unhandledPersonRelationIDs.add(personRelationID);
				subMonitor.worked(1);
				continue;
			}
						
			TuckedPersonRelationTreeNode tuckedNode = getTuckedNodeFromObjectID(parentNodes, personRelationID);
			if (logger.isDebugEnabled()) {
				logger.debug("@retrieveChildCountByPersonRelationIDs: Now handling " + PersonRelationTree.showObjectID(personRelationID));
				logger.debug(" ~~ nextIDOnPath: " + (nextIDOnPath == null ? "null" : PersonRelationTree.showObjectID(nextIDOnPath)));
				logger.debug(" ~~ tuckedNode: " + (tuckedNode == null ? "null" : tuckedNode.toDebugString()));
			}
			
			if (tuckedNode == null) {
				if (logger.isTraceEnabled())
					logger.warn("!!! WARNing !!! tuckedNode to " + PersonRelationTree.showObjectID(personRelationID) + " is NULL!");
				
				unhandledPersonRelationIDs.add(personRelationID); // result.put(personRelationID, 0L); // <-- Let the super class handle this.
				subMonitor.worked(1);
				continue;
			}
			
			PropertySetID nodePropertySetID = tuckedNode.getPropertySetID();
			if (nodePropertySetID == null) // It is possible that parentNode.getPropertySetID() returns null.					
				nodePropertySetID = getCorrespondingPSID(personRelationID);
			
			Set<PropertySetID> propertySetIDsToRoot = CollectionUtil.createHashSetFromCollection(tuckedNode.getPropertySetIDsToRoot());
			Set<PropertySetID> nextIDsOnPath = nextIDOnPath == null ? null : CollectionUtil.createHashSet(nextIDOnPath);
			TuckedQueryCount tqCount = PersonRelationDAO.sharedInstance().getTuckedPersonRelationCount(
					null, nodePropertySetID, propertySetIDsToRoot, nextIDsOnPath,
					new SubProgressMonitor(monitor, 20));

			tuckedNode.setActualChildCount(tqCount.actualChildCount);
			tuckedNode.setTuckedChildCount(tqCount.tuckedChildCount);
			tuckedNode.setTuckedStatus(nextIDOnPath == null ? TuckedNodeStatus.UNTUCKED : TuckedNodeStatus.TUCKED);
			if (logger.isDebugEnabled())
				logger.debug("*** " + tuckedNode.toDebugString());
			
			
			// See notes on setting the childCount for a TuckedNode.
			long childCount = nextIDOnPath == null || tuckedNode.getTuckedStatus().equals(TuckedNodeStatus.TUCKED) ? tqCount.tuckedChildCount : tqCount.actualChildCount-tqCount.tuckedChildCount;

			result.put(personRelationID, childCount);
			subMonitor.worked(1);
		}
		
		// If there exists any personRelationIDs that has not been handled for the tucked situation, we let
		// the super class handle them.
		if (!unhandledPersonRelationIDs.isEmpty())
			result = super.retrieveChildCountByPersonRelationIDs(result, parentNodes, unhandledPersonRelationIDs, subMonitor, tix);
		
		subMonitor.done();
		return result;
	}

	// -------------------------------------------------------------------------------------------------- ++ ------>>
	@Override
	protected Map<ObjectID, Long> retrieveChildCountByPropertySetIDs
	(Map<ObjectID, Long> result, Set<TuckedPersonRelationTreeNode> parentNodes, Set<PropertySetID> personIDs, ProgressMonitor monitor, int tix) {
		// This should be about as similar as the previous method: retrieveChildCountByPersonRelationIDs().
		ProgressMonitor subMonitor = new SubProgressMonitor(monitor, tix);
		subMonitor.beginTask("Retrieving child count...", personIDs.size());

		if (logger.isDebugEnabled())
			logger.debug("@retrieve..ChildCount..By<<PropertySetID>>s");
		
		// [Note. A.] When our tuckedPath ends, we should get nextIDOnPath == null. In this case, the personIDs we come across should not be affect 
		// with our tucked-node methodologies. We gather these IDs and then later on relegate them back to the super class's method. 
		Set<PropertySetID> unhandledPersonIDs = new HashSet<PropertySetID>();
		for (PropertySetID personID : personIDs) {
			PropertySetID nextIDOnPath = getNextRelatedPropertySetIDOnPath(personID);			
			if (nextIDOnPath == null) { // <-- See [Note. A.]
				unhandledPersonIDs.add(personID);
				subMonitor.worked(1);
				continue;
			}
			
			TuckedPersonRelationTreeNode tuckedNode = getTuckedNodeFromObjectID(parentNodes, personID);
			if (tuckedNode == null) {
				unhandledPersonIDs.add(personID);
				subMonitor.worked(1);
				continue;
			}

			Set<PropertySetID> propertySetIDsToRoot = CollectionUtil.createHashSetFromCollection(tuckedNode.getPropertySetIDsToRoot());
			Set<PropertySetID> nextIDsOnPath = nextIDOnPath == null ? null : CollectionUtil.createHashSet(nextIDOnPath);
			TuckedQueryCount tqCount = PersonRelationDAO.sharedInstance().getTuckedPersonRelationCount(
					null, personID, propertySetIDsToRoot, nextIDsOnPath,
					new SubProgressMonitor(monitor, 20));

			tuckedNode.setActualChildCount(tqCount.actualChildCount);
			tuckedNode.setTuckedChildCount(tqCount.tuckedChildCount);
			tuckedNode.setTuckedStatus(nextIDOnPath == null ? TuckedNodeStatus.UNTUCKED : TuckedNodeStatus.TUCKED);
			if (logger.isDebugEnabled())
				logger.debug("*** " + tuckedNode.toDebugString());
			
			
			// See notes on setting the childCount for a TuckedNode.
			long childCount = nextIDOnPath == null || tuckedNode.getTuckedStatus().equals(TuckedNodeStatus.TUCKED) ? tqCount.tuckedChildCount : tqCount.actualChildCount-tqCount.tuckedChildCount;

			result.put(personID, childCount);
			subMonitor.worked(1);
		}
		
		// If there exists any personRelationIDs that has not been handled for the tucked situation, we let
		// the super class handle them.
		if (!unhandledPersonIDs.isEmpty())
			result = super.retrieveChildCountByPropertySetIDs(result, parentNodes, unhandledPersonIDs, subMonitor, tix);
		
		subMonitor.done();
		return result;
		
//		for (PropertySetID personID : personIDs) {
//			// -------------------------------------------------------------------------------------------------- ++ ------>>
//			long personRelationCount = 0;
//			PropertySetID nextIDOnPath = getNextRelatedPropertySetIDOnPath(personID);
//
//			if (nextIDOnPath == null)
//				personRelationCount = PersonRelationDAO.sharedInstance().getPersonRelationCount(
//						null, personID, null, new NullProgressMonitor()
//				);
//
//			else {
//				Set<PropertySetID> toPropertySetIDsToInclude = CollectionUtil.createHashSet(nextIDOnPath);
//				personRelationCount = PersonRelationDAO.sharedInstance().getInclusiveFilteredPersonRelationCount(
//						null, personID, null,
//						null, toPropertySetIDsToInclude, new SubProgressMonitor(monitor, 80)
//				);
//			}
//			// -------------------------------------------------------------------------------------------------- ++ ------>>
//
//			if (logger.isDebugEnabled())
//				logger.debug("  -- --> personID: " + PersonRelationTree.showObjectID(personID) + ", personRelationCount: " + personRelationCount);
//
//			result.put(personID, personRelationCount);
//			subMonitor.worked(1);
//		}
//
//		subMonitor.done();
//		return result;
	}

	// Quickie: For singular access...
	protected void updateTuckedNodeChildCounts(TuckedPersonRelationTreeNode tuckedNode, ProgressMonitor monitor) {
		ObjectID objectID = tuckedNode.getJdoObjectID();
		PropertySetID nextIDOnPath = getNextRelatedPropertySetIDOnPath(objectID);
		
		if (nextIDOnPath == null) 
			return;
		
		Set<PropertySetID> propertySetIDsToRoot = CollectionUtil.createHashSetFromCollection(tuckedNode.getPropertySetIDsToRoot());
		Set<PropertySetID> nextIDsOnPath = nextIDOnPath == null ? null : CollectionUtil.createHashSet(nextIDOnPath);
		PropertySetID nodePropertySetID = tuckedNode.getPropertySetID();
		if (nodePropertySetID == null) // It is possible that parentNode.getPropertySetID() returns null.					
			nodePropertySetID = getCorrespondingPSID(objectID);
		
		TuckedQueryCount tqCount = PersonRelationDAO.sharedInstance().getTuckedPersonRelationCount(
				null, nodePropertySetID, propertySetIDsToRoot, nextIDsOnPath,
				new SubProgressMonitor(monitor, 20));

		tuckedNode.setActualChildCount(tqCount.actualChildCount);
		tuckedNode.setTuckedChildCount(tqCount.tuckedChildCount);
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
		PropertySetID nextIDOnPath = getNextRelatedPropertySetIDOnPath(parentID);
		PropertySetID correspondingPSID = getCorrespondingPSID(parentID); // <-- This is the same as the original getToID(). And, only if the parentNode has been instantiated, then this is the same as parentNode.getPropertySetID().
		
		if (logger.isDebugEnabled()) {
			logger.debug("~~ CHECK I: correspondingPSID = " + PersonRelationTree.showObjectID(correspondingPSID));
			logger.debug("            nextIDOnPath = " + PersonRelationTree.showObjectID(nextIDOnPath));
		}
		
		
		// [Guard check 1] Here, if our nextIDOnPath is null, then this node should be treated as a normal node.
		if (nextIDOnPath == null)
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
				null, correspondingPSID, propertySetIDsToRoot, CollectionUtil.createHashSet(nextIDOnPath), 
				new SubProgressMonitor(monitor, 80));
		
		parentNode.setActualChildCount(tqCount.actualChildCount);
		parentNode.setTuckedChildCount(tqCount.tuckedChildCount);


		// The filtered IDs that we want.
		Collection<PersonRelationID> filteredPersonRelationIDs = null;
		
		// These two conditions are NOT mutually exclusive.
		boolean isHandleTuckedRetrieval = parentPSID == null || statusToChangeTo.equals(TuckedNodeStatus.TUCKED) || statusToChangeTo.equals(TuckedNodeStatus.TOREFRESH) && parentNode.getTuckedStatus().equals(TuckedNodeStatus.TUCKED);
		boolean isHandleUnTuckedRetrieval = statusToChangeTo.equals(TuckedNodeStatus.UNTUCKED) || statusToChangeTo.equals(TuckedNodeStatus.TOREFRESH) && parentNode.getTuckedStatus().equals(TuckedNodeStatus.UNTUCKED);
		
		// Now we handle [Case 1] and [Case 3].
		if (isHandleTuckedRetrieval) { // (parentPSID == null || parentNode.getStatusToChangeTo().equals(TuckedNodeStatus.TUCKED)) {
			if (logger.isDebugEnabled())
				logger.debug(":::: Handling [Case 1] and [Case 3] :::: ::::::::::::::::::::");
			
			filteredPersonRelationIDs = PersonRelationDAO.sharedInstance().getInclusiveFilteredPersonRelationIDs(
					null, correspondingPSID, null,
					null, CollectionUtil.createHashSet(nextIDOnPath),
					getPersonRelationComparator(),
					new SubProgressMonitor(monitor, 80));
			
			parentNode.setTuckedStatus(TuckedNodeStatus.TUCKED);
		}
		// Handle [Case 2].
		else if (isHandleUnTuckedRetrieval) { // (parentNode.getStatusToChangeTo().equals(TuckedNodeStatus.UNTUCKED)) {
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
			logger.debug(parentNode.toDebugString());
		
		

		// Tidy up, and we're done.
		if (filteredPersonRelationIDs != null) {
			parentNode.setChildNodeCount(filteredPersonRelationIDs.size());			
			result.addAll(filteredPersonRelationIDs);
		}

		return result;
	}

	// -------------------------------------------------------------------------------------------------- ++ ------>>
	@Override
	protected Collection<ObjectID> retrieveChildObjectIDsByPropertySetIDs(TuckedPersonRelationTreeNode parentNode, ProgressMonitor monitor) {
		// Similar to the method retrieveChildObjectIDsByPersonRelationIDs, we need some kind of coordination.
		// And theoretically, we can handle them collectively!
		if (!parentNode.isNodeSet())
			parentNode.setStatusToChangeTo(TuckedNodeStatus.TUCKED); // <-- We just need to ensure that the node is TUCKED upon initialisation.
		
		if (logger.isDebugEnabled())
			logger.debug("~~~~~~~~~~~~~~ I'm here: @retrieveChild--((ObjectID))--sByPropertySetIDs");		
		
		return retrieveChildObjectIDsByPersonRelationIDs(parentNode, monitor);
	}

	


	// -------------------------------------------------------------------------------------------------- ++ ------>>
	// These manage the the correlated correspondence between the tuckedPSIDPaths and tuckedPRIDPaths. 
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	/**
	 * @return the next {@link PropertySetID} on the related tuckedPSIDpath, based on the given {@link ObjectID} from the tuckedPRIDpath.
	 * Returns null, if no match is found.
	 */
	private PropertySetID getNextRelatedPropertySetIDOnPath(ObjectID currentPRID) {
		Deque<ObjectID> tuckedPRIDPath = tuckedPRIDPaths.get(0); // } <-- From assumption 1. TODO Lift assumption 1 for all cases.
		Deque<ObjectID> tuckedPSIDPath = tuckedPSIDPaths.get(0); // }

		Iterator<ObjectID> iterPRID = tuckedPRIDPath.iterator();
		Iterator<ObjectID> iterPSID = tuckedPSIDPath.iterator();
		while (iterPRID.hasNext()) {
			iterPSID.next();
			if (iterPRID.next().equals(currentPRID))
				return (PropertySetID) (iterPSID.hasNext() ? iterPSID.next() : null);
		}

		return null;
	}
	
	/**
	 * @return the next {@link ObjectID} on the related tuckedPRIDPath, based on the given {@link ObjectID}.
	 * Returns null, if not match is found.
	 */
	private ObjectID getNextObjectIDOnPath(ObjectID currentPRID) {
		Deque<ObjectID> tuckedPRIDPath = tuckedPRIDPaths.get(0); // } <-- From assumption 1. TODO Lift assumption 1 for all cases.

		Iterator<ObjectID> iterPRID = tuckedPRIDPath.iterator();
		while (iterPRID.hasNext()) {
			if (iterPRID.next().equals(currentPRID))
				return iterPRID.hasNext() ? iterPRID.next() : null;
		}

		return null;
	}
	
	/**
	 * @return the corresponding {@link PropertySetID} from the given currentPRID.
	 * Returns null, if no match is found.
	 */
	private PropertySetID getCorrespondingPSID(ObjectID currentPRID) {
		Deque<ObjectID> tuckedPRIDPath = tuckedPRIDPaths.get(0); // } <-- From assumption 1. TODO Lift assumption 1 for all cases.
		Deque<ObjectID> tuckedPSIDPath = tuckedPSIDPaths.get(0); // }

		Iterator<ObjectID> iterPRID = tuckedPRIDPath.iterator();
		Iterator<ObjectID> iterPSID = tuckedPSIDPath.iterator();
		while (iterPRID.hasNext()) {
			ObjectID psID = iterPSID.next();
			if (iterPRID.next().equals(currentPRID))
				return (PropertySetID) psID;
		}

		return null;
	}

	/**
	 * @return the sub-path of the tuckedPRIDpath, up to and inclusive of the given currentID.
	 * Returns null if the given currentID is not found in any of the known tuckedPRIDpaths.
	 */
	protected Deque<ObjectID> getSubPathUpUntilCurrentID(ObjectID currentID) {
		Deque<ObjectID> tuckedPRIDPath = tuckedPRIDPaths.get(0); // } <-- From assumption 1. TODO Lift assumption 1 for all cases.

		Deque<ObjectID> subPath = new LinkedList<ObjectID>();
		for (ObjectID objectID : tuckedPRIDPath) {
			subPath.add(objectID);
			if (objectID.equals(currentID))
				return subPath;
		}

		return null;
	}
}
