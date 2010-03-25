package org.nightlabs.jfire.personrelation.trade.ui.tucked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.apache.log4j.Logger;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.PersonRelationManagerRemote.TuckedQueryCount;
import org.nightlabs.jfire.personrelation.dao.PersonRelationDAO;
import org.nightlabs.jfire.personrelation.id.PersonRelationID;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTree;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeController;
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
			PropertySetID nextIDOnPath = getNextObjectIDOnPath(personID);

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
		
		for (PersonRelationID personRelationID : personRelationIDs) {
			PropertySetID nextIDOnPath = getNextObjectIDOnPath(personRelationID);
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
			tuckedNode.setTuckedDetachedStatus(nextIDOnPath == null);
			if (logger.isDebugEnabled())
				logger.debug("*** " + tuckedNode.toDebugString());
			
			
			// See notes on setting the childCount for a TuckedNode.
			long childCount = nextIDOnPath == null || !tuckedNode.isNodeTucked() ? tqCount.actualChildCount-tqCount.tuckedChildCount : tqCount.tuckedChildCount;

			result.put(personRelationID, childCount);
			subMonitor.worked(1);
		}
		
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

	
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	// Retrieving ObjectIDs of the children of a given parentNode. 
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	@Override
	protected Collection<ObjectID> retrieveChildObjectIDsByPropertySetIDs(TuckedPersonRelationTreeNode parentNode, ProgressMonitor monitor) {
		ObjectID parentID = parentNode.getJdoObjectID();
		PropertySetID nextIDOnPath = getNextObjectIDOnPath(parentID);
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
			if (logger.isDebugEnabled())
				logger.debug(parentNode.toDebugString());
		}

		return result;
	}

	
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	@Override
	protected Collection<ObjectID> retrieveChildObjectIDsByPersonRelationIDs(TuckedPersonRelationTreeNode parentNode, ProgressMonitor monitor) {
		ObjectID parentID = parentNode.getJdoObjectID();
		PropertySetID nextIDOnPath = getNextObjectIDOnPath(parentID);
		Collection<PersonRelationID> personRelationIDs = Collections.singleton((PersonRelationID)parentID);
		Collection<ObjectID> result = new ArrayList<ObjectID>();

		List<PersonRelation> personRelations = PersonRelationDAO.sharedInstance().getPersonRelations(
				personRelationIDs,
				new String[] { FetchPlan.DEFAULT, PersonRelation.FETCH_GROUP_TO_ID },
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 20)
		);

		if (!personRelations.isEmpty()) {
			Collection<PersonRelationID> filteredPersonRelationIDs = null;
			PersonRelation personRelation = personRelations.iterator().next();

			// Handle the tucked situation.
			if (nextIDOnPath != null) {
				// This should load ONLY the necessary children.
				filteredPersonRelationIDs = PersonRelationDAO.sharedInstance().getInclusiveFilteredPersonRelationIDs(
						null, personRelation.getToID(), null,
						null, CollectionUtil.createHashSet(nextIDOnPath),
						getPersonRelationComparator(),
						new SubProgressMonitor(monitor, 80));


				// TODO Consider: This gets the tucked-node and actual-node counts. Should we do this elsewhere instead?
				PropertySetID nodePropertySetID = parentNode.getPropertySetID();
				if (nodePropertySetID == null) // It is possible that parentNode.getPropertySetID() returns null.					
					nodePropertySetID = getCorrespondingPSID(parentID);
				
				Set<PropertySetID> propertySetIDsToRoot = CollectionUtil.createHashSetFromCollection(parentNode.getPropertySetIDsToRoot());
				TuckedQueryCount tqCount = PersonRelationDAO.sharedInstance().getTuckedPersonRelationCount(
						null, nodePropertySetID, propertySetIDsToRoot, CollectionUtil.createHashSet(nextIDOnPath), 
						new SubProgressMonitor(monitor, 80));

				parentNode.setActualChildCountByObjectID(parentID, tqCount.actualChildCount);
				parentNode.setTuckedChildCountByObjectID(parentID, tqCount.tuckedChildCount);
				parentNode.setTuckedDetachedStatus(nextIDOnPath == null);
				
				if (logger.isDebugEnabled())
					logger.debug(parentNode.toDebugString());
			}
			
			// Handle this as per the normal situation.
			else {
				Set<PropertySetID> toPropertySetIDsToExclude = CollectionUtil.createHashSetFromCollection(parentNode.getPropertySetIDsToRoot());
				filteredPersonRelationIDs = PersonRelationDAO.sharedInstance().getFilteredPersonRelationIDs(
						null, personRelation.getToID(), null,
						null, toPropertySetIDsToExclude,
						getPersonRelationComparator(),
						new SubProgressMonitor(monitor, 80));
			}

			if (filteredPersonRelationIDs != null)
				result.addAll(filteredPersonRelationIDs);
		}

		return result;
	}



	// -------------------------------------------------------------------------------------------------- ++ ------>>
	// These manage the the correlated correspondence between the tuckedPSIDPaths and tuckedPRIDPaths. 
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	/**
	 * @return the next {@link PropertySetID} on the related tuckedPSIDpath, based on the given {@link ObjectID} from the tuckedPRIDpath.
	 * Returns null, if no match is found.
	 */
	private PropertySetID getNextObjectIDOnPath(ObjectID currentPRID) {
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
