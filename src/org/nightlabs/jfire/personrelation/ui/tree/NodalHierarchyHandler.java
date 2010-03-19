package org.nightlabs.jfire.personrelation.ui.tree;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedListener;
import org.nightlabs.jfire.personrelation.id.PersonRelationID;
import org.nightlabs.jfire.prop.id.PropertySetID;

/**
 * Puts together the series of methods that when combined, controls and manages the hierarchical expansion
 * of the nodes, following a pre-determined path (or paths) embedded in the {@link PersonRelationTree}.
 *
 * @author khaireel (at) nightlabs (dot) de
 */
public class NodalHierarchyHandler<PRTree extends PersonRelationTree> {
	private static final Logger logger = Logger.getLogger(NodalHierarchyHandler.class);

//	private Map<Class<? extends ObjectID>, List<Deque<ObjectID>>> relatablePathsToRoots; // The distinct paths. Check out NotificationListener below for details.
	private Map<Integer, Deque<ObjectID>> pathsToExpand_PRID;
	private Map<Integer, Deque<ObjectID>> expandedPaths_PRID;

	private PRTree personRelationTree = null;


	/**
	 * Creates a new instance of the NodalHiearchyHandler with a reference to the {@link PersonRelationTree} whose
	 * nodes are to be handled.
	 */
	public NodalHierarchyHandler(PRTree personRelationTree) {
		this.personRelationTree = personRelationTree;
		initAutoNodeExpansionLazyBehaviour();
	}

	/**
	 * Sets up the system of listeners, working with the directive-paths indicated in 'relatablePathsToRoots', from which
	 * the tree will react: To expand to the exact node, whichever the level, of the input LegalEntity.
	 */
	protected void initAutoNodeExpansionLazyBehaviour() {
		// This listener is expected to unravel the tree, appropriately following the pre-identified paths in pathsToBeExpanded.
		// Each path contains a Deque of PropertySetID, and will guide the lazy-expansion events once data becomes available, and
		// becomes necessary to be displayed.
		personRelationTree.getTree().addListener(SWT.SetData, new Listener() {
			@Override
			public void handleEvent(Event event) {
				handleSWTSetDataEvent(event);
			}
		});


		// This gives us a higher overall view of ALL nodes that have just been loaded than
		// the "personRelationTree.getTree().addListener(SWT.SetData, new Listener() {..." method above.
		personRelationTree.getPersonRelationTreeController().addJDOLazyTreeNodesChangedListener(new JDOLazyTreeNodesChangedListener<ObjectID, Object, PersonRelationTreeNode>() {
			@Override
			public void onJDOObjectsChanged(JDOLazyTreeNodesChangedEvent<ObjectID, PersonRelationTreeNode> changedEvent) {
				handleOnJDOObjectsChangedEvent(changedEvent);
			}
		});
	}


	/**
	 * Plug this into getPersonRelationTree().getTree().addListener(SWT.SetData, new Listener()...
	 */
	public void handleSWTSetDataEvent(Event event) {
		// Guard #1.
		if (arePathsToBeExpandedEmpty() || !(event.item instanceof TreeItem))
			return;

		// Guard #2.
		TreeItem treeItem = (TreeItem) event.item;
		if (treeItem == null || !(treeItem.getData() instanceof PersonRelationTreeNode))
			return;

		// Guard #3.
		PersonRelationTreeNode node = (PersonRelationTreeNode) treeItem.getData();
		if (node == null)
			return;

		// Guard #4.
		ObjectID nodeObjectID = node.getJdoObjectID();
		if (nodeObjectID == null)
			return;


		// Fine-tuned, recursive version II.
		Deque<ObjectID> objectIDsToRoot = (LinkedList<ObjectID>) node.getJDOObjectIDsToRoot();
		boolean isNodeMarkedForExpansion = false;
		boolean isNodeMarkedForSelection = false;
		for (int index : pathsToExpand_PRID.keySet()) {
			Deque<ObjectID> pathToExpand = pathsToExpand_PRID.get(index);
			Deque<ObjectID> expandedPath = expandedPaths_PRID.get(index);

			if (!pathToExpand.isEmpty() && pathToExpand.peekFirst().equals(nodeObjectID)) {
				if (logger.isDebugEnabled()) {
					logger.debug("*** *** *** Checking: nodeObjectID=" + PersonRelationTree.showObjectID(nodeObjectID) + " *** *** ***");
					logger.debug("Checking: " + PersonRelationTree.showDequePaths("pathToExpand", pathToExpand, true));
					logger.debug("Checking: " + PersonRelationTree.showDequePaths("expandedPath", expandedPath, true));
					logger.debug("---");
				}

				boolean isMatch = isMatchingSubPath(objectIDsToRoot, expandedPath, true, true);
				if (isMatch || expandedPath.isEmpty()) {
					isNodeMarkedForExpansion |= isMatch;
					expandedPath.push( pathToExpand.pop() );

					isNodeMarkedForSelection |= pathToExpand.isEmpty();
				}

				if (logger.isDebugEnabled()) {
					logger.debug("Checking: isMatch=" + (isMatch ? "T" : "F"));
					logger.debug("Checking: isNodeMarkedFor*Expansion*=" + (isNodeMarkedForExpansion ? "T" : "F"));
					logger.debug("Checking: isNodeMarkedForSelection=" + (isNodeMarkedForSelection ? "T" : "F"));
					logger.debug("---");
				}
			}
		}

		// Reflect changes on the node, if any.
		if (isNodeMarkedForSelection) {
			personRelationTree.getTree().setSelection(treeItem);
		}
		else if (isNodeMarkedForExpansion)
			treeItem.setExpanded(true);

	}

	/**
	 * Plug this into getPersonRelationTree().getPersonRelationTreeController().addJDOLazyTreeNodesChangedListener(new JDOLazyTreeNodesChangedListener<ObjectID, Object, PersonRelationTreeNode>()...
	 */
	public void handleOnJDOObjectsChangedEvent(JDOLazyTreeNodesChangedEvent<ObjectID, PersonRelationTreeNode> changedEvent) {
		// Guard #1.
		if (getNumberOfEmptyPaths() >= 1 || pathsToExpand_PRID == null)
			return;

		// Guard #2.
		List<ObjectID> nextObjectIDsOnPaths = getNextObjectIDsOnPaths();
		if (nextObjectIDsOnPaths.isEmpty())
			return;

		// Current experimental setup:
		//   A PropertySetID in one of the pathsToExpand is not within the view, and thus was not loaded.
		//   Find out it's index (position), with respect to its parent.
		List<PersonRelationTreeNode> loadedTreeNodes = changedEvent.getLoadedTreeNodes();
		if (loadedTreeNodes != null && !loadedTreeNodes.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug(" ----------------->>>> On addJDOLazyTreeNodesChangedListener: [Checking loaded nodes, " + loadedTreeNodes.size() + "]");
				for (int index : pathsToExpand_PRID.keySet()) {
					logger.debug(" --> Checking: " + PersonRelationTree.showDequePaths("pathToExpand", pathsToExpand_PRID.get(index), true));
					logger.debug(" --> Checking: " + PersonRelationTree.showDequePaths("expandedPath", expandedPaths_PRID.get(index), true));
					logger.debug(" --> Checking: " + PersonRelationTree.showObjectIDs("nextObjectIDsOnPaths", nextObjectIDsOnPaths, 5));
				}
			}

			int posIndex = 0;
			PersonRelationTreeNode parNode = null;
			for (PersonRelationTreeNode node : loadedTreeNodes) {
				if (node != null) {
					if (parNode == null)
						parNode = node.getParent();	// Save parent for later use.

					ObjectID nodeObjID = node.getJdoObjectID();
					boolean isOnNextPath = nextObjectIDsOnPaths.contains(nodeObjID);

					if (isOnNextPath) {
						if (logger.isDebugEnabled())
							logger.debug(" :: @" + posIndex + ", (loaded) nodeObjID:" +  PersonRelationTree.showObjectID(nodeObjID) + (isOnNextPath ? " <-- Match!" : ""));

						personRelationTree.setSelection(node);
						ISelection selection = personRelationTree.getTreeViewer().getSelection();
						personRelationTree.getTreeViewer().setSelection(selection, true);
						break;
					}
				}
				else {
					if (logger.isDebugEnabled())
						logger.debug(" :: @" + posIndex + ", node: [null]");
				}

				posIndex++;
			}


			// If we make it through to here, then we need to force the node we want to be loaded.
			// But only if the parentNode is NOT null.
			if (parNode != null) {
				List<ObjectID> childrenJDOObjectIDs = parNode.getChildrenJDOObjectIDs();
				long nodeCount = parNode.getChildNodeCount();
				int childNodeCnt = childrenJDOObjectIDs != null ? childrenJDOObjectIDs.size() : -1;
				if (logger.isDebugEnabled()) {
					logger.debug(" -->> parNode.childNodeCount: " + nodeCount + ", childNodeCnt: " + childNodeCnt);
					logger.debug(" -->> " + PersonRelationTree.showObjectIDs("childrenJDOObjectIDs", childrenJDOObjectIDs, 5));
				}

				// Locate the index of the childnode we want to force to be loaded.
				posIndex = 0;
				for (ObjectID objectID : childrenJDOObjectIDs) {
					if (nextObjectIDsOnPaths.contains(objectID)) {
						if (logger.isDebugEnabled()) {
							logger.debug(" -->>->> FOUND! @posIndex:" + posIndex + ", objectID:" + PersonRelationTree.showObjectID(objectID));
						}

						// Force the child to be loaded, and duly have it selected.
						PersonRelationTreeNode node = (PersonRelationTreeNode) parNode.getChildNodes().get(posIndex);
						personRelationTree.setSelection(node);
						ISelection selection = personRelationTree.getTreeViewer().getSelection();
						personRelationTree.getTreeViewer().setSelection(selection, true);
					}

					posIndex++;
				}
			}
		}

	}


	/**
	 * @return true if and only if the given expandedPath is a proper sub-path of the given pathToRoot.
	 */
	protected boolean isMatchingSubPath(Deque<? extends ObjectID> pathToRoot, Deque<? extends ObjectID> expandedPath, boolean isReversePathToRoot, boolean isReverseExpandedPath) {
		if (pathToRoot.size() < expandedPath.size())
			return false;

		Iterator<? extends ObjectID> iterToRoot = isReversePathToRoot ? pathToRoot.descendingIterator() : pathToRoot.iterator();
		Iterator<? extends ObjectID> iterToExpand = isReverseExpandedPath ? expandedPath.descendingIterator() : expandedPath.iterator();
		while (iterToExpand.hasNext()) {
			ObjectID oid_1 = iterToRoot.next();
			ObjectID oid_2 = iterToExpand.next();

			if (!oid_1.equals(oid_2))
				return false;
		}

		return true;
	}

	/**
	 * @return true if all the paths in pathsToExpand are empty.
	 */
	protected boolean arePathsToBeExpandedEmpty() {
		if (pathsToExpand_PRID != null && !pathsToExpand_PRID.isEmpty()) {
			for (Deque<ObjectID> path : pathsToExpand_PRID.values())
				if (!path.isEmpty())
					return false;
		}

		return true;
	}

	/**
	 * A more informative method. One that checks every Deque in pathsToExpand, and counts the number of those
	 * paths that are empty.
	 * @return the number of empty paths in pathsToExpand. Returns 0 if pathsToExpand is null.
	 */
	protected int getNumberOfEmptyPaths() {
		int cnt = 0;
		if (pathsToExpand_PRID != null && !pathsToExpand_PRID.isEmpty()) {
			for (Deque<ObjectID> path : pathsToExpand_PRID.values())
				if (path.isEmpty())
					cnt++;
		}

		return cnt;
	}

	/**
	 * @return the next {@link ObjectID}s on the path(s) to the root(s).
	 */
	protected List<ObjectID> getNextObjectIDsOnPaths() {
		List<ObjectID> nextObjIDs = new ArrayList<ObjectID>(pathsToExpand_PRID.size());
		for (int index : pathsToExpand_PRID.keySet()) {
			Deque<ObjectID> path_PRID = pathsToExpand_PRID.get(index);
			if (path_PRID != null && !path_PRID.isEmpty())
				nextObjIDs.add(path_PRID.peekFirst());
		}

		return nextObjIDs;
	}

	/**
	 * Prepares the data from 'relatablePathsToRoots', ready for use in the path-expansion manipulations.
	 * @return the set of {@link PropertySetID}s for the root(s) of the tree.
	 */
	public Set<PropertySetID> initRelatablePathsToRoots(Map<Class<? extends ObjectID>, List<Deque<ObjectID>>> relatablePathsToRoots) {
		List<Deque<ObjectID>> pathsToRoot_PSID = relatablePathsToRoots.get(PropertySetID.class);    // <-- mixed PropertySetID & PersonRelationID.
		List<Deque<ObjectID>> pathsToRoot_PRID = relatablePathsToRoots.get(PersonRelationID.class); // <-- PropertySetID only.

		// Initialise the path-expansion trackers.
		pathsToExpand_PRID = new HashMap<Integer, Deque<ObjectID>>(pathsToRoot_PRID.size());
		expandedPaths_PRID = new HashMap<Integer, Deque<ObjectID>>(pathsToRoot_PRID.size());

		Set<PropertySetID> rootIDs = new HashSet<PropertySetID>();
		Iterator<Deque<ObjectID>> iterPaths_PSID = pathsToRoot_PSID.iterator();
		Iterator<Deque<ObjectID>> iterPaths_PRID = pathsToRoot_PRID.iterator();
		int index = 0;
		if (logger.isDebugEnabled())
			logger.debug("*** *** *** initRelatablePathsToRoots() :: [PSid:" + pathsToRoot_PSID.size() + "][PRid:" + pathsToRoot_PRID.size() + "] *** *** ***");

		while (iterPaths_PSID.hasNext()) {
			Deque<ObjectID> path_PSID = iterPaths_PSID.next();
			Deque<ObjectID> path_PRID = iterPaths_PRID.next();
			pathsToExpand_PRID.put(index, new LinkedList<ObjectID>(path_PRID)); // Maintain a copy.
			expandedPaths_PRID.put(index, new LinkedList<ObjectID>());

			if (logger.isDebugEnabled()) {
				logger.debug("@index:" + index + " " + PersonRelationTree.showDequePaths("PSID", path_PSID, true));
				logger.debug("@index:" + index + " " + PersonRelationTree.showDequePaths("PRID", path_PRID, true));
				logger.debug("--------------------");
			}

			rootIDs.add((PropertySetID) path_PSID.peekFirst());
			index++;
		}

		// Done.
		return rootIDs;
	}

}
