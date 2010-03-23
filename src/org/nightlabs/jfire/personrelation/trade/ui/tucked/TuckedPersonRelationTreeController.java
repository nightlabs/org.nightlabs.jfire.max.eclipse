package org.nightlabs.jfire.personrelation.trade.ui.tucked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.dao.PersonRelationDAO;
import org.nightlabs.jfire.personrelation.id.PersonRelationID;
import org.nightlabs.jfire.personrelation.ui.tree.IPersonRelationTreeControllerDelegate;
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


			result.put(personID, personRelationCount);
			subMonitor.worked(1);
		}

		subMonitor.done();
		return result;
	}

	@Override
	protected Map<ObjectID, Long> retrieveChildCountByPersonRelationIDs
	(Map<ObjectID, Long> result, Set<TuckedPersonRelationTreeNode> parentNodes, Set<PersonRelationID> personRelationIDs, ProgressMonitor monitor, int tix) {
		List<PersonRelation> personRelations = PersonRelationDAO.sharedInstance().getPersonRelations(
				personRelationIDs,
				new String[] {
						FetchPlan.DEFAULT,
						PersonRelation.FETCH_GROUP_TO_ID,
				},
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
			System.err.println("BLAAAHHHHHHHHHHHHHHHH: " + PersonRelationTree.showObjectID(personRelationID));

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

				System.err.println("BLAAAHHHHHHHHHHHHHHHH: " + PersonRelationTree.showObjectID(personRelationID));

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

				node.setActualChildCount(personRelationID, actualPersonRelationCount);
				// -------------------------------------------------------------------------------------------------- ++ ------>>

				result.put(personRelationID, personRelationCount);
				subMonitor.worked(1);
			}
		}

		subMonitor.done();
		return result;
	}


	// -------------------------------------------------------------------------------------------------- ++ ------>>
	@Override
	protected Collection<ObjectID> retrieveChildObjectIDs(TuckedPersonRelationTreeNode parentNode, ProgressMonitor monitor) {
		// If no tucked paths, this should act normally. For now. FIXME.
		if (tuckedPRIDPaths == null || tuckedPRIDPaths.isEmpty())
			return super.retrieveChildObjectIDs(parentNode, monitor);

		// The filter: Don't add child if its ID is already listed in the parentNode's path to the root.
		Collection<PropertySetID> rootPersonIDs = getRootPersonIDs();
		if (rootPersonIDs == null)
			return Collections.emptyList();

		ObjectID parentID = parentNode.getJdoObjectID();
		PropertySetID nextIDOnPath = getNextObjectIDOnPath(parentID);

		Set<PropertySetID> toPropertySetIDsToExclude = CollectionUtil.createHashSetFromCollection(parentNode.getPropertySetIDsToRoot());
		Collection<ObjectID> result = new ArrayList<ObjectID>(); // Note: It is from the index position of this Collection that we know the position of the child-nodes.
		                                                         //       And if we are to impose any ordering of the nodes, this should be the place to affect it. Kai.

		monitor.beginTask("Retrieving children...", 100);
		try {
			if (parentID == null) {
				result.addAll(rootPersonIDs);
			}
			else if (parentID instanceof PropertySetID) {
				Collection<PersonRelationID> filteredPersonRelationIDs = null;
				// -------------------------------------------------------------------------------------------------- ++ ------>> TODO Deal with root nodes.
				if (nextIDOnPath == null) { // Should not happen.
					filteredPersonRelationIDs = PersonRelationDAO.sharedInstance().getFilteredPersonRelationIDs(
							null, (PropertySetID)parentID, null,
							null, toPropertySetIDsToExclude,
							getPersonRelationComparator(),
							new SubProgressMonitor(monitor, 80));
				}
				// -------------------------------------------------------------------------------------------------- ++ ------>>
				else {
					filteredPersonRelationIDs = PersonRelationDAO.sharedInstance().getInclusiveFilteredPersonRelationIDs(
							null, (PropertySetID)parentID, null,
							null, CollectionUtil.createHashSet(nextIDOnPath),
							getPersonRelationComparator(),
							new SubProgressMonitor(monitor, 80));
				}
				// -------------------------------------------------------------------------------------------------- ++ ------>>

				result.addAll(filteredPersonRelationIDs);
			}
			else if (parentID instanceof PersonRelationID) {
				Collection<PersonRelationID> personRelationIDs = Collections.singleton((PersonRelationID)parentID);
				List<PersonRelation> personRelations = PersonRelationDAO.sharedInstance().getPersonRelations(
						personRelationIDs,
						new String[] {
								FetchPlan.DEFAULT,
								PersonRelation.FETCH_GROUP_TO_ID,
						},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 20)
				);

				if (!personRelations.isEmpty()) {
					Collection<PersonRelationID> filteredPersonRelationIDs = null;
					PersonRelation personRelation = personRelations.iterator().next();

					// -------------------------------------------------------------------------------------------------- ++ ------>>
					if (nextIDOnPath == null) { // <-- This should not happen!
						filteredPersonRelationIDs = PersonRelationDAO.sharedInstance().getFilteredPersonRelationIDs(
								null, personRelation.getToID(), null,
								null, toPropertySetIDsToExclude,
								getPersonRelationComparator(),
								new SubProgressMonitor(monitor, 80));
					}
					// -------------------------------------------------------------------------------------------------- ++ ------>>
					else {
						filteredPersonRelationIDs = PersonRelationDAO.sharedInstance().getInclusiveFilteredPersonRelationIDs(
								null, personRelation.getToID(), null,
								null, CollectionUtil.createHashSet(nextIDOnPath),
								getPersonRelationComparator(),
								new SubProgressMonitor(monitor, 80));
					}
					// -------------------------------------------------------------------------------------------------- ++ ------>>

					result.addAll(filteredPersonRelationIDs);
				}
			}


			List<IPersonRelationTreeControllerDelegate> delegates = getPersonRelationTreeControllerDelegates();
			for (IPersonRelationTreeControllerDelegate delegate : delegates) {
				Collection<? extends ObjectID> childObjectIDs = delegate.retrieveChildObjectIDs(parentID, new SubProgressMonitor(monitor, 20));
				if (childObjectIDs != null)
					result.addAll(childObjectIDs);
			}

			return result;
		} finally {
			monitor.done();
		}
	}

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
}
