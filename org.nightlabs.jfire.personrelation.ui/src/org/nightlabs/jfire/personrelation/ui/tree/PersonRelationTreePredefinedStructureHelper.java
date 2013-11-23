package org.nightlabs.jfire.personrelation.ui.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedListener;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.PersonRelationManagerRemote;
import org.nightlabs.jfire.personrelation.dao.PersonRelationDAO;
import org.nightlabs.jfire.personrelation.id.PersonRelationID;
import org.nightlabs.jfire.personrelation.id.PersonRelationTypeID;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.progress.ProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper that will allow for resolving a {@link PropertySet}s position in a
 * predefined structure of {@link PersonRelation}s. It is able to resolve the
 * roots of this structure and set this as input to a {@link PersonRelationTree}
 * . Additionally it is capable of auto-expanding the path to the original
 * PropertySet in the tree.
 * <p>
 * This is useful for setups where a organisation does structure its
 * PersonRelations with a distinct set of relations and would like to see all
 * those relations in the tree regardless where inside this structure the
 * actually selected {@link PropertySet} is.
 * </p>
 * <p>
 * Example: Structure is always [company] -&gt; [subsidiary] -&gt;
 * [employee/contact-person]. Now when a employee is selected the tree should
 * not show the [employee] as root, but its [company] or [subsidiary] as upper
 * nodes to the selected [employee]. This can be done using this
 * {@link PersonRelationTreePredefinedStructureHelper}.
 * </p>
 * <p>
 * Ported from jfire 1.0 by Alex.
 * </p>
 * 
 * @author abieber
 * @author khaireel (original author)
 */
public class PersonRelationTreePredefinedStructureHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(PersonRelationTreePredefinedStructureHelper.class);
	
	/**
	 * This is set in {@link #selectPerson(PropertySetID, ProgressMonitor)}. It
	 * is retrieved from the server. It contains the root-IDs and the path to
	 * the selected PropertySet from each root. This is used by the
	 * auto-expansion to travers/expand down the tree
	 */
	private Map<Class<? extends ObjectID>, List<Deque<ObjectID>>> relatablePathsToRoots; // The distinct paths. Check out NotificationListener below for details.
	
	/** Internally used during auto-expansion */
	private Map<Integer, Deque<ObjectID>> pathsToExpand_PRID;
	/** Internally used during auto-expansion */
	private Map<Integer, Deque<ObjectID>> expandedPaths_PRID;

	/**
	 * The {@link PersonRelationTree} this helper operates on.
	 */
	@SuppressWarnings("rawtypes")
	private PersonRelationTree personRelationTree;
	
	/**
	 * The {@link PersonRelationTypeID} that make up the structure to show.
	 */
	private Set<PersonRelationTypeID> predefinedStructurePersonRelationTypes;

	/**
	 * The maximum search depth to resolve the structure.
	 */
	private int maxSearchDepth = 10;
	
	/**
	 * The currently selected {@link PropertySetID}. Used to skip resolving if it is selected again.
	 */
	private PropertySetID currentPersonID;
	
	
	/**
	 * Create a {@link PersonRelationTreePredefinedStructureHelper} for the
	 * given {@link PersonRelationTree}.
	 * 
	 * @param personRelationTree
	 *            The {@link PersonRelationTree} that the helper should operate
	 *            on.
	 * @param predefinedStructurePersonRelationTypes
	 *            The set of {@link PersonRelationTypeID} that make up the
	 *            predefined structure to resolve.
	 * @param maxSearchDepth
	 *            The maximum depth to resolve relations to.l
	 * @param doSelectionAutoExpansion
	 *            Whether the helper should enable auto-expansion and selection
	 *            of the originally selected {@link PropertySetID} in the
	 *            resolved structure.
	 */
	@SuppressWarnings("rawtypes")
	public PersonRelationTreePredefinedStructureHelper(PersonRelationTree personRelationTree,
			Set<PersonRelationTypeID> predefinedStructurePersonRelationTypes,
			Integer maxSearchDepth, boolean doSelectionAutoExpansion) {
		super();
		this.personRelationTree = personRelationTree;
		this.predefinedStructurePersonRelationTypes = predefinedStructurePersonRelationTypes;
		if (maxSearchDepth != null)
			this.maxSearchDepth = maxSearchDepth;
		
		if (doSelectionAutoExpansion) {
			initAutoNodeExpansionLazyBehaviour();
		}
	}
	
	/**
	 * Convenience constructor that will the default search-depth.
	 * 
	 * @see #PersonRelationTreePredefinedStructureHelper(PersonRelationTree, Set, Integer, boolean)
	 */
	@SuppressWarnings("rawtypes") 
	public PersonRelationTreePredefinedStructureHelper(PersonRelationTree personRelationTree, Set<PersonRelationTypeID> predefinedStructurePersonRelationTypes, boolean doSelectionAutoExpansion) {
		this(personRelationTree, predefinedStructurePersonRelationTypes, null, doSelectionAutoExpansion);
	}
	
	/**
	 * Convenience constructor that will enable for auto-expansion and use the default search-depth.
	 * 
	 * @see #PersonRelationTreePredefinedStructureHelper(PersonRelationTree, Set, Integer, boolean)
	 */
	@SuppressWarnings("rawtypes") 
	public PersonRelationTreePredefinedStructureHelper(PersonRelationTree personRelationTree, Set<PersonRelationTypeID> predefinedStructurePersonRelationTypes) {
		this(personRelationTree, predefinedStructurePersonRelationTypes, null, true);
	}

	/**
	 * Select the given {@link PropertySetID} in the {@link PersonRelationTree}
	 * of this helper. The helper will first resolve the structure roots
	 * according to the predefinedStructurePersonRelationTypes it was created
	 * with and set those as root for the tree. If auto-expansion was turned on,
	 * the given {@link PropertySetID} will be asynchronously selected in the
	 * tree.
	 * 
	 * @param personID The {@link PropertySetID} of the person to select.
	 * @param monitor A monitor to report progress (of the structure-resolving) to.
	 */
	public void selectPerson(PropertySetID personID, ProgressMonitor monitor) {
		if (personID != null && (currentPersonID == null || currentPersonID != personID)) {

			// Starting with the personID, we retrieve outgoing paths from it. Each path traces the personID's
			// relationship up through the hierachy of organisations, and terminates under one of the following
			// three conditions:
			//    1. When it reaches the mother of all subsidiary organisations (known as c^ \elemof C).
			//    2. When it detects a cyclic / nested-cyclic relationship between subsidiary-groups.
			//    3. When the length of the path reaches the preset DefaultMaximumSearchDepth.
			// For the sake of simplicity, let c^ be the terminal element in a path. Then all the unique c^'s
			// collated from the returned paths are the new roots for PersonRelationTree.
			// See original comments in revision #16575.
			//
			// Since 2010.01.24. Kai.
			// The method 'getRelationRootNodes()' returns two Sets of Deque-paths in a single Map, distinguished by the following keys:
			//   1. Key[PropertySetID.class] :: Deque-path(s) to root(s) containing only PropertySetIDs.
			//   2. Key[PersonRelationID.class] :: Deque-path(s) to root(s) containing contigious PersonRelationID elements ending with the terminal PropertySetID.
			try {
				relatablePathsToRoots = retrieveRelatablePathsToRoots(personID, monitor);

				final Set<PropertySetID> rootIDs = initRelatablePathsToRoots();

				// Done and ready. Update the tree.
				currentPersonID = personID;
				personRelationTree.getDisplay().asyncExec(new Runnable() {
					@SuppressWarnings("unchecked")
					public void run() {
						if (!personRelationTree.isDisposed()) {
							personRelationTree.setInputPersonIDs(rootIDs);
							logger.debug("personRelationTree.setInputPersonIDs("+rootIDs+")");
						}
					}
				});
			} catch (Exception e) {
				// Failed to retrieve path!
				logger.error("Failed to retrieve rootPaths for " + currentPersonID + " falling back to selecting the given Person itself", e);

				currentPersonID = personID;
				personRelationTree.getDisplay().asyncExec(new Runnable() {
					@SuppressWarnings("unchecked")
					public void run() {
						if (!personRelationTree.isDisposed()) {
							personRelationTree.setInputPersonIDs(Collections.singleton(currentPersonID));
							logger.debug("personRelationTree.setInputPersonIDs("+Collections.singleton(currentPersonID)+")");
						}
					}
				});

			}
		}
	}

	/**
	 * This method is called to retrieve the paths to the possible roots for the
	 * given personID. The default implementation resolves the paths using
	 * {@link PersonRelationManagerRemote#getRootNodes(Set, PropertySetID, int)}
	 * based on the person to select and on the predefined
	 * PersonRelationType-structure of this helper (see
	 * {@link #getPredefinedStructurePersonRelationTypes()} and
	 * {@link #getMaxSearchDepth()}).
	 * <p>
	 * The helper will use the result of this method to set its keySet as roots
	 * of the {@link PersonRelationTree} and auto-expanding the paths to the
	 * selected personID.
	 * </p>
	 * <p>
	 * This method can be overwritten in sub-classes if different paths/ have
	 * should be processed by the helper.
	 * </p>
	 * 
	 * @param personID The ID of the Person the paths to roots should be resolved.
	 * @param monitor The monitor to report progress to.
	 * @return
	 */
	protected Map<Class<? extends ObjectID>, List<Deque<ObjectID>>> retrieveRelatablePathsToRoots(PropertySetID personID, ProgressMonitor monitor) {
		return PersonRelationDAO.sharedInstance().getRelationRootNodes(getPredefinedStructurePersonRelationTypes(), personID, getMaxSearchDepth(), monitor);
	}
	
	public Set<PersonRelationTypeID> getPredefinedStructurePersonRelationTypes() {
		return predefinedStructurePersonRelationTypes;
	}
	
	public int getMaxSearchDepth() {
		return maxSearchDepth;
	}
	
	/**
	 * Prepares the data from 'relatablePathsToRoots', ready for use in the path-expansion manipulations.
	 * @return the set of {@link PropertySetID}s for the root(s) of the tree.
	 */
	private Set<PropertySetID> initRelatablePathsToRoots() {
		List<Deque<ObjectID>> pathsToRoot_PSID = relatablePathsToRoots.get(PropertySetID.class);    // <-- mixed PropertySetID & PersonRelationID.
		List<Deque<ObjectID>> pathsToRoot_PRID = relatablePathsToRoots.get(PersonRelationID.class); // <-- PropertySetID only.

		// Initialise the path-expansion trackers.
		pathsToExpand_PRID = new HashMap<Integer, Deque<ObjectID>>(pathsToRoot_PRID.size());
		expandedPaths_PRID = new HashMap<Integer, Deque<ObjectID>>(pathsToRoot_PRID.size());

		Set<PropertySetID> rootIDs = new HashSet<PropertySetID>();
		Iterator<Deque<ObjectID>> iterPaths_PSID = pathsToRoot_PSID.iterator();
		Iterator<Deque<ObjectID>> iterPaths_PRID = pathsToRoot_PRID.iterator();
		int index = 0;
		if (logger.isTraceEnabled())
			logger.trace("*** *** *** initRelatablePathsToRoots() :: [PSid:" + pathsToRoot_PSID.size() + "][PRid:" + pathsToRoot_PRID.size() + "] *** *** ***");

		while (iterPaths_PSID.hasNext()) {
			Deque<ObjectID> path_PSID = iterPaths_PSID.next();
			Deque<ObjectID> path_PRID = iterPaths_PRID.next();
			pathsToExpand_PRID.put(index, new LinkedList<ObjectID>(path_PRID)); // Maintain a copy.
			expandedPaths_PRID.put(index, new LinkedList<ObjectID>());

			if (logger.isTraceEnabled()) {
				logger.trace("@index:" + index + " " + showDequePaths("PSID", path_PSID, true));
				logger.trace("@index:" + index + " " + showDequePaths("PRID", path_PRID, true));
				logger.trace("--------------------");
			}

			rootIDs.add((PropertySetID) path_PSID.peekFirst());
			index++;
		}

		// Done.
		return rootIDs;
	}


	/**
	 * Sets up the system of listeners, working with the directive-paths indicated in 'relatablePathsToRoots', from which
	 * the tree will react: To expand to the exact node, whichever the level, of the input LegalEntity.
	 */
	@SuppressWarnings("unchecked")
	protected void initAutoNodeExpansionLazyBehaviour() {
		// This listener is expected to unravel the tree, appropriately following the pre-identified paths in pathsToBeExpanded.
		// Each path contains a Deque of PropertySetID, and will guide the lazy-expansion events once data becomes available, and
		// becomes necessary to be displayed.
		personRelationTree.getTree().addListener(SWT.SetData, new Listener() {
			@Override
			public void handleEvent(Event event) {
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
						if (logger.isTraceEnabled()) {
							logger.trace("*** *** *** Checking: nodeObjectID=" + showObjectID(nodeObjectID) + " *** *** ***");
							logger.trace("Checking: " + showDequePaths("pathToExpand", pathToExpand, true));
							logger.trace("Checking: " + showDequePaths("expandedPath", expandedPath, true));
							logger.trace("---");
						}

						boolean isMatch = isMatchingSubPath(objectIDsToRoot, expandedPath, true, true);
						if (isMatch || expandedPath.isEmpty()) {
							isNodeMarkedForExpansion |= isMatch;
							expandedPath.push( pathToExpand.pop() );

							isNodeMarkedForSelection |= pathToExpand.isEmpty();
						}

						if (logger.isTraceEnabled()) {
							logger.trace("Checking: isMatch=" + (isMatch ? "T" : "F"));
							logger.trace("Checking: isNodeMarkedFor*Expansion*=" + (isNodeMarkedForExpansion ? "T" : "F"));
							logger.trace("Checking: isNodeMarkedForSelection=" + (isNodeMarkedForSelection ? "T" : "F"));
							logger.trace("---");
						}
					}
				}

				// Reflect changes on the node, if any.
				if (isNodeMarkedForSelection) {
//					personRelationTree.getTree().setSelection(treeItem);
					personRelationTree.setSelection(node);
					ISelection selection = personRelationTree.getTreeViewer().getSelection();
					personRelationTree.getTreeViewer().setSelection(selection, true);
				}
				else if (isNodeMarkedForExpansion)
					treeItem.setExpanded(true);

			}
		});


		// This gives us a higher overall view of ALL nodes that have just been loaded than
		// the "personRelationTree.getTree().addListener(SWT.SetData, new Listener() {..." method above.
		personRelationTree.getPersonRelationTreeController().addJDOLazyTreeNodesChangedListener(new JDOLazyTreeNodesChangedListener<ObjectID, Object, PersonRelationTreeNode>() {
			@Override
			public void onJDOObjectsChanged(JDOLazyTreeNodesChangedEvent<ObjectID, PersonRelationTreeNode> changedEvent) {
				// (Seems fixed).
				//       There was a problem: The TreeViewer does not 'reveal' an item (or possibly items) that are located
				//       far below. The pathToExpand{} still contains the path to be extended, but we receive no more
				//       events for our listener to react!

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
					if (logger.isTraceEnabled()) {
						logger.trace(" ----------------->>>> On addJDOLazyTreeNodesChangedListener: [Checking loaded nodes, " + loadedTreeNodes.size() + "]");
						for (int index : pathsToExpand_PRID.keySet()) {
							logger.trace(" --> Checking: " + showDequePaths("pathToExpand", pathsToExpand_PRID.get(index), true));
							logger.trace(" --> Checking: " + showDequePaths("expandedPath", expandedPaths_PRID.get(index), true));
							logger.trace(" --> Checking: " + showObjectIDs("nextObjectIDsOnPaths", nextObjectIDsOnPaths, 5));
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
								if (logger.isTraceEnabled())
									logger.trace(" :: @" + posIndex + ", (loaded) nodeObjID:" +  showObjectID(nodeObjID) + (isOnNextPath ? " <-- Match!" : ""));

								personRelationTree.setSelection(node);
								ISelection selection = personRelationTree.getTreeViewer().getSelection();
								personRelationTree.getTreeViewer().setSelection(selection, true);
								break;
							}
						}
						else {
							if (logger.isTraceEnabled())
								logger.trace(" :: @" + posIndex + ", node: [null]");
						}

						posIndex++;
					}


					// If we make it through to here, then we need to force the node we want to be loaded.
					// But only if the parentNode is NOT null.
					if (parNode != null) {
						List<ObjectID> childrenJDOObjectIDs = parNode.getChildrenJDOObjectIDs();
						long nodeCount = parNode.getChildNodeCount();
						int childNodeCnt = childrenJDOObjectIDs != null ? childrenJDOObjectIDs.size() : -1;
						if (logger.isTraceEnabled()) {
							logger.trace(" -->> parNode.childNodeCount: " + nodeCount + ", childNodeCnt: " + childNodeCnt);
							logger.trace(" -->> " + showObjectIDs("childrenJDOObjectIDs", childrenJDOObjectIDs, 5));
						}

						// Locate the index of the childnode we want to force to be loaded.
						posIndex = 0;
						for (ObjectID objectID : childrenJDOObjectIDs) {
							if (nextObjectIDsOnPaths.contains(objectID)) {
								if (logger.isTraceEnabled()) {
									logger.trace(" -->>->> FOUND! @posIndex:" + posIndex + ", objectID:" + showObjectID(objectID));
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
		});
	}
	
	
	/**
	 * @return the relatable paths to roots.
	 */
	protected Map<Class<? extends ObjectID>, List<Deque<ObjectID>>> getRelatablePathsToRoots() {
		return relatablePathsToRoots;
	}

	
	/**
	 * @return true if all the paths in pathsToExpand are empty.
	 */
	private boolean arePathsToBeExpandedEmpty() {
		if (pathsToExpand_PRID != null && !pathsToExpand_PRID.isEmpty()) {
			for (Deque<ObjectID> path : pathsToExpand_PRID.values())
				if (!path.isEmpty())
					return false;
		}

		return true;
	}
	
	/**
	 * @return true if and only if the given expandedPath is a proper sub-path of the given pathToRoot.
	 */
	private boolean isMatchingSubPath(Deque<? extends ObjectID> pathToRoot, Deque<? extends ObjectID> expandedPath, boolean isReversePathToRoot, boolean isReverseExpandedPath) {
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
	 * @return the next {@link ObjectID}s on the path(s) to the root(s).
	 */
	private List<ObjectID> getNextObjectIDsOnPaths() {
		List<ObjectID> nextObjIDs = new ArrayList<ObjectID>(pathsToExpand_PRID.size());
		for (int index : pathsToExpand_PRID.keySet()) {
			Deque<ObjectID> path_PRID = pathsToExpand_PRID.get(index);
			if (path_PRID != null && !path_PRID.isEmpty())
				nextObjIDs.add(path_PRID.peekFirst());
		}

		return nextObjIDs;
	}
	
	/**
	 * A more informative method. One that checks every Deque in pathsToExpand, and counts the number of those
	 * paths that are empty.
	 * @return the number of empty paths in pathsToExpand. Returns 0 if pathsToExpand is null.
	 */
	private int getNumberOfEmptyPaths() {
		int cnt = 0;
		if (pathsToExpand_PRID != null && !pathsToExpand_PRID.isEmpty()) {
			for (Deque<ObjectID> path : pathsToExpand_PRID.values())
				if (path.isEmpty())
					cnt++;
		}

		return cnt;
	}
	
	
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	//  Will be removed once ALL testings are completed! Kai.
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	// I. Quick debug.
	public static String showDequePaths(String preamble, Deque<? extends ObjectID> path, boolean isReversed) {
		String str = "++ " + preamble + " :: {";
		Iterator<? extends ObjectID> iter = isReversed ? path.descendingIterator() : path.iterator();
		while (iter.hasNext())
			str += showObjectID(iter.next());

		return str + "}";
	}

	// II. Quick debug.
	public static String showObjectIDs(String preamble, Collection<? extends ObjectID> objIDs, int modLnCnt) {
		if (objIDs == null)
			return preamble + " :: NULL";

		int len = objIDs.size();
		String str = preamble + " (size: " + len + ") :: {" + (len > modLnCnt ? "\n     " : " ");
		int ctr = 0;
		for (ObjectID objectID : objIDs) {
			str += "(" + ctr + ")" + showObjectID(objectID, true) + " ";
			ctr++;

			if (ctr % modLnCnt == 0)
				str += "\n     ";
		}

		return str + (len > modLnCnt ? "\n   }" : "}");
	}

	// III. Quick debug.
	public static String showObjectID(ObjectID objectID) {
		return showObjectID(objectID, false);
	}

	// III.a Quick debug.
	public static String showObjectID(ObjectID objectID, boolean isShortened) {
		if (objectID == null)
			return "[null]";

		String[] segID = objectID.toString().split("&");
		String str = segID[1];

		if (isShortened) {
			str = str.replaceFirst("propertySetID", "pSid");
			str = str.replaceFirst("personRelationID", "pRid");
		}

		return "[" + str + "]";
	}
	
	
}
