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


	@Override
	public long getNodeCount(TuckedPersonRelationTreeNode parent) {
		if (parent != null && parent.isNodeSet())
			return parent.getChildNodeCount();
		
		return super.getNodeCount(parent);
	}
	
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	// Retrieving ObjectIDs of the children of a given parentNode. 
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	@Override
	protected Map<ObjectID, Long> retrieveChildCountByPropertySetIDs
	(Map<ObjectID, Long> result, Set<TuckedPersonRelationTreeNode> parentNodes, Set<PropertySetID> personIDs, ProgressMonitor monitor, int tix) {
		if (logger.isDebugEnabled())
			logger.debug("@retrieve..ChildCount..By<<PropertySetID>>s");

		ProgressMonitor subMonitor = new SubProgressMonitor(monitor, tix);
		subMonitor.beginTask("Retrieving child count...", personIDs.size());

		for (PropertySetID personID : personIDs) {
			// -------------------------------------------------------------------------------------------------- ++ ------>>
			long personRelationCount = 0;
			PropertySetID nextIDOnPath = getNextRelatedPropertySetIDOnPath(personID);

			if (nextIDOnPath == null)
				personRelationCount = PersonRelationDAO.sharedInstance().getPersonRelationCount(
						null, personID, null, new NullProgressMonitor()
				);

			else {
				Set<PropertySetID> toPropertySetIDsToInclude = CollectionUtil.createHashSet(nextIDOnPath);
				personRelationCount = PersonRelationDAO.sharedInstance().getInclusiveFilteredPersonRelationCount(
						null, personID, null,
						null, toPropertySetIDsToInclude, new SubProgressMonitor(monitor, 80)
				);
			}
			// -------------------------------------------------------------------------------------------------- ++ ------>>

			if (logger.isDebugEnabled())
				logger.debug("  -- --> personID: " + PersonRelationTree.showObjectID(personID) + ", personRelationCount: " + personRelationCount);

			result.put(personID, personRelationCount);
			subMonitor.worked(1);
		}

		subMonitor.done();
		return result;
	}

	// -------------------------------------------------------------------------------------------------- ++ ------>>
	@Override
	protected Map<ObjectID, Long> retrieveChildCountByPersonRelationIDs
	(Map<ObjectID, Long> result, Set<TuckedPersonRelationTreeNode> parentNodes, Set<PersonRelationID> personRelationIDs, ProgressMonitor monitor, int tix) {
		// ----------------------------->>---------->>-------------------------- NEW stuffs --------------------------------------->>----|
		ProgressMonitor subMonitor = new SubProgressMonitor(monitor, tix);
		subMonitor.beginTask("Retrieving child count...", personRelationIDs.size());
		
		if (logger.isDebugEnabled())
			logger.debug("~~~~~~~~~~~~~~ I'm here: @retrieve--[[ChildCount]]--ByPersonRelationIDs.");
		
		Set<PersonRelationID> unhandledPersonRelationIDs = new HashSet<PersonRelationID>();
		
		for (PersonRelationID personRelationID : personRelationIDs) {
			PropertySetID nextIDOnPath = getNextRelatedPropertySetIDOnPath(personRelationID);
			
			// When our tuckedPath ends, we should get nextIDOnPath == null.
			// TODO Do we do anything? Best to relegate this back to the super class's method. For this particular unhandled personRelationID.
			if (nextIDOnPath == null) {
				unhandledPersonRelationIDs.add(personRelationID);
				continue;
			}
			
			TuckedPersonRelationTreeNode tuckedNode = getTuckedNodeFromPersonRelationID(parentNodes, personRelationID);
			
			if (logger.isDebugEnabled()) {
				logger.debug("@retrieveChildCountByPersonRelationIDs: Now handling " + PersonRelationTree.showObjectID(personRelationID));
				logger.debug(" ~~ nextIDOnPath: " + (nextIDOnPath == null ? "null" : PersonRelationTree.showObjectID(nextIDOnPath)));
				logger.debug(" ~~ tuckedNode: " + (tuckedNode == null ? "null" : tuckedNode.toDebugString()));
			}
			
			if (tuckedNode == null) {
				System.err.println("!!! WARNing !!! tuckedNode to " + PersonRelationTree.showObjectID(personRelationID) + " is NULL!");
				result.put(personRelationID, 0L);
				subMonitor.worked(1);
				continue;
			}
			
			PropertySetID nodePropertySetID = tuckedNode.getPropertySetID();
			if (nodePropertySetID == null) // It is possible that parentNode.getPropertySetID() returns null.					
				nodePropertySetID = getCorrespondingPSID(personRelationID);
			
			Set<PropertySetID> propertySetIDsToRoot = CollectionUtil.createHashSetFromCollection(tuckedNode.getPropertySetIDsToRoot());
			Set<PropertySetID> nextIDsOnPath = nextIDOnPath == null ? null : CollectionUtil.createHashSet(nextIDOnPath);
			TuckedQueryCount tqCount = PersonRelationDAO.sharedInstance().getTuckedPersonRelationCount(
					null,nodePropertySetID, propertySetIDsToRoot, nextIDsOnPath,
					new SubProgressMonitor(monitor, 20));

			tuckedNode.setActualChildCountByObjectID(personRelationID, tqCount.actualChildCount);
			tuckedNode.setTuckedChildCountByObjectID(personRelationID, tqCount.tuckedChildCount);
			tuckedNode.setTuckedNodeStatus(nextIDOnPath == null ? TuckedNodeStatus.UNTUCKED : TuckedNodeStatus.TUCKED);
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
		
		// -----------------------------<<----------<<-------------------------- NEW stuffs ---------------------------------------<<----|

		subMonitor.done();
		return result;
	}

	
	/**
	 * @return the {@link TuckedPersonRelationTreeNode} containing within it's JDOObjectID, the given personRelationID.
	 * Returns null if the node cannot be found.
	 */
	private TuckedPersonRelationTreeNode getTuckedNodeFromPersonRelationID(Set<TuckedPersonRelationTreeNode> parentNodes, PersonRelationID personRelationID) {
		for (TuckedPersonRelationTreeNode tuckedNode : parentNodes)
			if (tuckedNode.getJdoObjectID().equals(personRelationID))
				return tuckedNode;
		
		return null;
	}

	
	// :::: TEST TEST TEST ::::
	protected void fireTuckChangedEvent(JDOLazyTreeNodesChangedEvent<ObjectID, TuckedPersonRelationTreeNode> changedEvent, ProgressMonitor monitor) {
		Set<TuckedPersonRelationTreeNode> parentsToRefresh = changedEvent.getParentsToRefresh();		
		if (logger.isDebugEnabled())
			logger.debug(" ************************* GOT it! parentsToRefresh.size() = " + parentsToRefresh.size() + " *************************");

		for (TuckedPersonRelationTreeNode parentNode : parentsToRefresh) {
			// Status change: From TUCKED to UNTUCKED.
			//  1. Retrieve the children.
			//  2. Create (a new node) + add only those children that are not already loaded.
			if (parentNode.getStatusToChangeTo().equals(TuckedNodeStatus.UNTUCKED)) {
				Collection<ObjectID> childObjectIDs = retrieveChildObjectIDs(parentNode, new SubProgressMonitor(monitor, 80)); // FIXME The monitor-tix.
				ObjectID nextObjectIDOnPath = getNextObjectIDOnPath(parentNode.getJdoObjectID()); // TODO The general case will have several.			
				for (ObjectID objectID : childObjectIDs) {
					if (!objectID.equals(nextObjectIDOnPath)) {
						TuckedPersonRelationTreeNode childNode = createNode();
						childNode.setActiveJDOObjectLazyTreeController(TuckedPersonRelationTreeController.this);
						childNode.setParent(parentNode);
						childNode.setJdoObjectID(objectID);
						
						addTreeNode(childNode);
						parentNode.addChildNode(childNode);
					}
				}
			}
			
			// Status change: From UNTUCKED back to TUCKED.
			else {
				// The set of TUCKED nodes are exactly from the same set of UNTUCKED nodes, which we have already created when
				// dealing with the status change form TUCKED to UNTUCKED. And also, we have kept a reference in the node's very
				// own loadedTuckedChildren, for use in future references.
				Collection<ObjectID> childObjectIDs = retrieveChildObjectIDs(parentNode, new SubProgressMonitor(monitor, 80)); // FIXME The monitor-tix.
				
				List<JDOObjectLazyTreeNode<ObjectID, Object, PersonRelationTreeController<? extends PersonRelationTreeNode>>> subListOfTuckedNodes = new ArrayList<JDOObjectLazyTreeNode<ObjectID,Object,PersonRelationTreeController<? extends PersonRelationTreeNode>>>(childObjectIDs.size());				
				Deque<TuckedPersonRelationTreeNode> loadedTuckedChildren = parentNode.getLoadedTuckedChildren();
				// To consider:
				// 1. What happens when a reference childObjectID is not in the loadedTuckedChildren?
				// 2. What if loadedTuckedChildren is null?
				// 3. What if loadedTuckedChildren is empty?
				
				// Assuming that loadedTuckedChildren is not null and not empty, and contains ALL childObjectIDs.
				for (ObjectID objectID : childObjectIDs) {
					for (TuckedPersonRelationTreeNode loadedTuckedChild : loadedTuckedChildren) {
						if (loadedTuckedChild.getJdoObjectID().equals(objectID))
							subListOfTuckedNodes.add(loadedTuckedChild);
					}
				}
				
				// Assuming no errors...
				parentNode.setChildNodes(subListOfTuckedNodes);
			}
		}

		// Fire the changed event.
		onJDOObjectsChanged(new JDOLazyTreeNodesChangedEvent<ObjectID, TuckedPersonRelationTreeNode>(this, parentsToRefresh));		
	}
	// :::: TEST TEST TEST ::::

	
	
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	// Retrieving ObjectIDs of the children of a given parentNode. 
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	@Override
	protected Collection<ObjectID> retrieveChildObjectIDsByPropertySetIDs(TuckedPersonRelationTreeNode parentNode, ProgressMonitor monitor) {
		ObjectID parentID = parentNode.getJdoObjectID();
		PropertySetID nextIDOnPath = getNextRelatedPropertySetIDOnPath(parentID);
		Collection<ObjectID> result = new ArrayList<ObjectID>();

		if (nextIDOnPath != null) {
			Collection<PersonRelationID> filteredPersonRelationIDs = null;
			filteredPersonRelationIDs = PersonRelationDAO.sharedInstance().getInclusiveFilteredPersonRelationIDs(
					null, (PropertySetID)parentID, null,
					null, CollectionUtil.createHashSet(nextIDOnPath),
					getPersonRelationComparator(),
					new SubProgressMonitor(monitor, 80));

			if (filteredPersonRelationIDs != null)
				result.addAll(filteredPersonRelationIDs);

			// TODO Consider: This gets the tucked-node and actual-node counts. Should we do this elsewhere instead?
			Set<PropertySetID> propertySetIDsToRoot = CollectionUtil.createHashSetFromCollection(parentNode.getPropertySetIDsToRoot());
			TuckedQueryCount tqCount = PersonRelationDAO.sharedInstance().getTuckedPersonRelationCount(
					null,(PropertySetID) parentID, propertySetIDsToRoot, CollectionUtil.createHashSet(nextIDOnPath),
					new SubProgressMonitor(monitor, 20));

			parentNode.setActualChildCountByObjectID(parentID, tqCount.actualChildCount);
			parentNode.setTuckedChildCountByObjectID(parentID, tqCount.tuckedChildCount);
			parentNode.setTuckedNodeStatus(nextIDOnPath == null ? TuckedNodeStatus.UNTUCKED : TuckedNodeStatus.TUCKED);
			
			if (logger.isDebugEnabled())
				logger.debug(parentNode.toDebugString());
		}

		return result;
	}

	
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	@Override
	protected Collection<ObjectID> retrieveChildObjectIDsByPersonRelationIDs(TuckedPersonRelationTreeNode parentNode, ProgressMonitor monitor) {
		// We need some kind of coordination, in order to know what to load here.
		// This depends on the information we can gather from the parentNode: check the status indicated by getStatusToChangeTo().
		if (logger.isDebugEnabled()) {
			logger.debug("~~~~~~~~~~~~~~ I'm here: @retrieveChild--((ObjectID))--sByPersonRelationIDs");
			logger.debug(" ::: parentNode: " + PersonRelationTree.showObjectID(parentNode.getPropertySetID()) + ",  statusToChangeTo: " + parentNode.getStatusToChangeTo());
		}
		
		
		// Our tucked-situational environment is rather unique.
		// The tucked-path is ALWAYs known before hand; i.e. upon initiation, we have the required tucked path, which is the basis for the entire TuckedNode concept.
		// Thus, at any node we encounter, we already know what the next PropertySetID is going to be, and so we don't need to fetch that ID again. This was
		// previously done by first fetching the PersonRelation and then through it we access the getToID(). Note that it is possible to have null, which simply 
		// carries two meanings: 1. we have reached the end of the line, or 2. ALL child nodes need to be fetched, since they don't belong to the tuckedPath.
		// ----------------------------->>---------->>-------------------------- NEW stuffs --------------------------------------->>----|
		Collection<ObjectID> result = new ArrayList<ObjectID>();
		ObjectID parentID = parentNode.getJdoObjectID();
		PropertySetID nextIDOnPath = getNextRelatedPropertySetIDOnPath(parentID);
		PropertySetID correspondingPSID = getCorrespondingPSID(parentID); // <-- This is the same as the original getToID(). And, only if the parentNode has been instantiated, then this is the same as parentNode.getPropertySetID().
		
		if (logger.isDebugEnabled()) {
			logger.debug("~~ CHECK I: correspondingPSID = " + PersonRelationTree.showObjectID(correspondingPSID));
			logger.debug("            nextIDOnPath = " + PersonRelationTree.showObjectID(nextIDOnPath));
		}
		
		
		// [Guard check 1] Here, if our nextIDOnPath is null, then this node should be treated as a normal node.
		if (nextIDOnPath == null)
			return super.retrieveChildObjectIDsByPersonRelationIDs(parentNode, monitor);
		
		// [Guard check 2] No need to handle a node that has already been handled.
		PropertySetID parentPSID = parentNode.getPropertySetID();
		if (parentPSID != null && parentNode.getStatusToChangeTo().equals(TuckedNodeStatus.UNSET))
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
		
		parentNode.setActualChildCountByObjectID(parentID, tqCount.actualChildCount);
		parentNode.setTuckedChildCountByObjectID(parentID, tqCount.tuckedChildCount);


		// The filtered IDs that we want.
		Collection<PersonRelationID> filteredPersonRelationIDs = null;
		
		// Now we handle [Case 1] and [Case 3].
		if (parentPSID == null || parentNode.getStatusToChangeTo().equals(TuckedNodeStatus.TUCKED)) {
			if (logger.isDebugEnabled())
				logger.debug(":::: Handling [Case 1] and [Case 3]");
			
			filteredPersonRelationIDs = PersonRelationDAO.sharedInstance().getInclusiveFilteredPersonRelationIDs(
					null, correspondingPSID, null,
					null, CollectionUtil.createHashSet(nextIDOnPath),
					getPersonRelationComparator(),
					new SubProgressMonitor(monitor, 80));
			
			parentNode.setTuckedNodeStatus(TuckedNodeStatus.TUCKED);
		}
		// Handle [Case 2].
		else if (parentNode.getStatusToChangeTo().equals(TuckedNodeStatus.UNTUCKED)) {
			if (logger.isDebugEnabled())
				logger.debug(":::: Handling [Case 2]");
			
			filteredPersonRelationIDs = PersonRelationDAO.sharedInstance().getFilteredPersonRelationIDs(
					null, correspondingPSID, null,
					null, propertySetIDsToRoot,
					getPersonRelationComparator(),
					new SubProgressMonitor(monitor, 80));			
			
			parentNode.setTuckedNodeStatus(TuckedNodeStatus.UNTUCKED);
		}
		
		if (logger.isDebugEnabled())
			logger.debug(parentNode.toDebugString());
		
		

		// Tidy up: I.
//		if (!parentNode.getStatusToChangeTo().equals(TuckedNodeStatus.UNSET))
//			parentNode.setChildNodes(null);
		
		// Tidy up: II
		parentNode.setStatusToChangeTo(TuckedNodeStatus.UNSET);	// Once we have successfully manipulated the tucked-situation, we revert this back to UNSET.
		if (filteredPersonRelationIDs != null) {
			parentNode.setChildNodeCount(filteredPersonRelationIDs.size());			
			result.addAll(filteredPersonRelationIDs);
		}

		return result;
		// -----------------------------<<----------<<-------------------------- NEW stuffs ---------------------------------------<<----|
		
		
		
		
//		ObjectID parentID = parentNode.getJdoObjectID();
//		PropertySetID nextIDOnPath = getNextObjectIDOnPath(parentID);
//		Collection<PersonRelationID> personRelationIDs = Collections.singleton((PersonRelationID)parentID);
//		Collection<ObjectID> result = new ArrayList<ObjectID>();
//
//		List<PersonRelation> personRelations = PersonRelationDAO.sharedInstance().getPersonRelations(
//				personRelationIDs,
//				new String[] { FetchPlan.DEFAULT, PersonRelation.FETCH_GROUP_TO_ID },
//				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 20)
//		);
//
//		if (!personRelations.isEmpty()) {
//			Collection<PersonRelationID> filteredPersonRelationIDs = null;
//			PersonRelation personRelation = personRelations.iterator().next();
//			
//			if (logger.isDebugEnabled()) {
//				logger.debug("~~ CHECK I: personRelation.getToID() = " + PersonRelationTree.showObjectID(personRelation.getToID()));
//				logger.debug("            correspondingPSID = " + PersonRelationTree.showObjectID(getCorrespondingPSID(parentID)));
//				logger.debug("            nextIDOnPath = " + PersonRelationTree.showObjectID(nextIDOnPath));
//				logger.debug("            parentNode.getPropertySetID() = " + PersonRelationTree.showObjectID(parentNode.getPropertySetID()));
//			}
//
//			// Handle the tucked situation.
//			if (nextIDOnPath != null) {
//				// This should load ONLY the necessary children.
//				filteredPersonRelationIDs = PersonRelationDAO.sharedInstance().getInclusiveFilteredPersonRelationIDs(
//						null, personRelation.getToID(), null,
//						null, CollectionUtil.createHashSet(nextIDOnPath),
//						getPersonRelationComparator(),
//						new SubProgressMonitor(monitor, 80));
//
//
//				// TODO Consider: This gets the tucked-node and actual-node counts. Should we do this elsewhere instead?
//				PropertySetID nodePropertySetID = parentNode.getPropertySetID();
//				if (nodePropertySetID == null) // It is possible that parentNode.getPropertySetID() returns null.					
//					nodePropertySetID = getCorrespondingPSID(parentID);
//				
//				Set<PropertySetID> propertySetIDsToRoot = CollectionUtil.createHashSetFromCollection(parentNode.getPropertySetIDsToRoot());
//				TuckedQueryCount tqCount = PersonRelationDAO.sharedInstance().getTuckedPersonRelationCount(
//						null, nodePropertySetID, propertySetIDsToRoot, CollectionUtil.createHashSet(nextIDOnPath), 
//						new SubProgressMonitor(monitor, 80));
//
//				parentNode.setActualChildCountByObjectID(parentID, tqCount.actualChildCount);
//				parentNode.setTuckedChildCountByObjectID(parentID, tqCount.tuckedChildCount);
//				parentNode.setTuckedNodeStatus(nextIDOnPath == null ? TuckedNodeStatus.UNTUCKED : TuckedNodeStatus.TUCKED);
//				
//				if (logger.isDebugEnabled())
//					logger.debug(parentNode.toDebugString());
//			}
//			
//			// Handle this as per the normal situation.
//			else {
//				Set<PropertySetID> toPropertySetIDsToExclude = CollectionUtil.createHashSetFromCollection(parentNode.getPropertySetIDsToRoot());
//				filteredPersonRelationIDs = PersonRelationDAO.sharedInstance().getFilteredPersonRelationIDs(
//						null, personRelation.getToID(), null,
//						null, toPropertySetIDsToExclude,
//						getPersonRelationComparator(),
//						new SubProgressMonitor(monitor, 80));
//			}
//
//			if (filteredPersonRelationIDs != null)
//				result.addAll(filteredPersonRelationIDs);
//		}
//
//		return result;
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
