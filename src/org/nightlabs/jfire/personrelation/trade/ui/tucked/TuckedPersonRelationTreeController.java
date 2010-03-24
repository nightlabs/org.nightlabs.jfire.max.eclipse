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
import javax.jdo.JDOHelper;

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

	@Override
	protected Map<ObjectID, Long> retrieveChildCountByPersonRelationIDs
	(Map<ObjectID, Long> result, Set<TuckedPersonRelationTreeNode> parentNodes, Set<PersonRelationID> personRelationIDs, ProgressMonitor monitor, int tix) {
		if (logger.isDebugEnabled())
			logger.debug("@retrieve..ChildCount..By<<PersonRelationID>>s");

		List<PersonRelation> personRelations = PersonRelationDAO.sharedInstance().getPersonRelations(
				personRelationIDs,
				new String[] { FetchPlan.DEFAULT, PersonRelation.FETCH_GROUP_TO_ID },
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new SubProgressMonitor(monitor, tix / 2)
		);

		ProgressMonitor subMonitor = new SubProgressMonitor(monitor, tix / 2);
		subMonitor.beginTask("Retrieving child count...", personRelations.size());

		// In order to count with the filter, where we ensure that none of the unnecessary children should been repeated,
		// we need to check the IDs for comparison. But we don't need to make the server return us all the filtered IDs. Just to count them, and get the number.
		for (PersonRelation personRelation : personRelations) {
			long personRelationCount = 0;
			PersonRelationID personRelationID = (PersonRelationID) JDOHelper.getObjectId(personRelation);

			if (logger.isDebugEnabled())
				logger.debug("  -- --> personRelationID (OUTER): " + PersonRelationTree.showObjectID(personRelationID));

			// Also, based on the tucked-path of PersonRelationIDs, we determine the next node on the path.
			PropertySetID nextIDOnPath = getNextObjectIDOnPath(personRelationID);
			if (nextIDOnPath == null) {
				// We're done. We don't want to display any children of this node that are not on the tuckedPath.
				result.put(personRelationID, 0L);
				subMonitor.worked(1);
				continue;
			}

			// Revised version. We now check our reference to the Set of parentNodes, to make sure we have the correct Node.
			TuckedPersonRelationTreeNode node = null;
			List<TuckedPersonRelationTreeNode> treeNodes = getTreeNodeList(personRelationID);
			if (treeNodes == null) {
				// Should throw something...
				throw new IllegalStateException("getTreeNodeList() for personRelationID " + personRelationID + " is NULL!");
			}
			else {
				if (treeNodes.size() == 1)
					node = treeNodes.get(0);
				else {
					// If the mapping key for personRelationID returns more than one node, then we essentially need to find the correct one.
					for (TuckedPersonRelationTreeNode treeNode : treeNodes)
						if (parentNodes.contains(treeNode)) {
							node = treeNode;
							break;
						}
				}

				// -------------------------------------------------------------------------------------------------- ++ ------>>
				Set<PropertySetID> toPropertySetIDsToInclude = CollectionUtil.createHashSet(nextIDOnPath);
				personRelationCount = PersonRelationDAO.sharedInstance().getInclusiveFilteredPersonRelationCount(
						null, personRelation.getToID(), null,
						null, toPropertySetIDsToInclude, new SubProgressMonitor(monitor, 80)
				);
				// -------------------------------------------------------------------------------------------------- ++ ------>>
				Set<PropertySetID> toPropertySetIDsToExclude = CollectionUtil.createHashSetFromCollection(node.getPropertySetIDsToRoot());
				long actualPersonRelationCount = PersonRelationDAO.sharedInstance().getFilteredPersonRelationCount(
						null, personRelation.getToID(), null,
						null, toPropertySetIDsToExclude, new SubProgressMonitor(monitor, 80)
				);

//				node.setChildCountByObjectID(personRelationID, actualPersonRelationCount);
				// -------------------------------------------------------------------------------------------------- ++ ------>>

				if (logger.isDebugEnabled())
					logger.debug("  -- --> personRelationID (INNER): " + PersonRelationTree.showObjectID(personRelationID) + ", personRelationCount: " + personRelationCount);

				result.put(personRelationID, personRelationCount);
				subMonitor.worked(1);
			}
		}

		subMonitor.done();
		return result;
	}



	// -------------------------------------------------------------------------------------------------- ++ ------>>
	@Override
	protected Collection<ObjectID> retrieveChildObjectIDsByPropertySetIDs(TuckedPersonRelationTreeNode parentNode, ProgressMonitor monitor) {
		if (logger.isDebugEnabled())
			logger.debug("@retrieveChildObjectIDsBy((PropertySetID))s: " + PersonRelationTree.showObjectID(parentNode.getJdoObjectID()));

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


	@Override
	protected Collection<ObjectID> retrieveChildObjectIDsByPersonRelationIDs(TuckedPersonRelationTreeNode parentNode, ProgressMonitor monitor) {
		if (logger.isDebugEnabled())
			logger.debug("@retrieveChildObjectIDsBy((PersonRelationID))s: " + PersonRelationTree.showObjectID(parentNode.getJdoObjectID()));

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

			if (nextIDOnPath != null) {
				filteredPersonRelationIDs = PersonRelationDAO.sharedInstance().getInclusiveFilteredPersonRelationIDs(
						null, personRelation.getToID(), null,
						null, CollectionUtil.createHashSet(nextIDOnPath),
						getPersonRelationComparator(),
						new SubProgressMonitor(monitor, 80));


				// TODO Consider: This gets the tucked-node and actual-node counts. Should we do this elsewhere instead?
				Set<PropertySetID> propertySetIDsToRoot = CollectionUtil.createHashSetFromCollection(parentNode.getPropertySetIDsToRoot());
				TuckedQueryCount tqCount = PersonRelationDAO.sharedInstance().getTuckedPersonRelationCount(
						null, parentNode.getPropertySetID(), propertySetIDsToRoot, CollectionUtil.createHashSet(nextIDOnPath),
						new SubProgressMonitor(monitor, 80));

				parentNode.setActualChildCountByObjectID(parentID, tqCount.actualChildCount);
				parentNode.setTuckedChildCountByObjectID(parentID, tqCount.tuckedChildCount);
				if (logger.isDebugEnabled())
					logger.debug(parentNode.toDebugString());
			}

			if (filteredPersonRelationIDs != null)
				result.addAll(filteredPersonRelationIDs);
		}

		return result;
	}



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
