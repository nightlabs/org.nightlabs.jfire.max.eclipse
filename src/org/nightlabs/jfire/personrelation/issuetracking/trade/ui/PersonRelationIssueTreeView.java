package org.nightlabs.jfire.personrelation.issuetracking.trade.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.selection.SelectionProviderProxy;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedListener;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.PersonRelationType;
import org.nightlabs.jfire.personrelation.dao.PersonRelationDAO;
import org.nightlabs.jfire.personrelation.id.PersonRelationID;
import org.nightlabs.jfire.personrelation.id.PersonRelationTypeID;
import org.nightlabs.jfire.personrelation.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.personrelation.ui.PersonRelationTree;
import org.nightlabs.jfire.personrelation.ui.PersonRelationTreeNode;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;

public class PersonRelationIssueTreeView
extends LSDViewPart
{
	private static final Logger logger = Logger.getLogger(PersonRelationIssueTreeView.class);
	public static final String ID_VIEW = PersonRelationIssueTreeView.class.getName();

	protected static final int DEFAULT_MAX_SEARCH_DEPTH = 10; // --> To be placed appropriately in a ConfigModule.

	private Map<Class<? extends ObjectID>, List<Deque<ObjectID>>> relatablePathsToRoots; // The distinct paths. Check out NotificationListener below for details.
	private Map<Integer, Deque<ObjectID>> pathsToExpand_PRID;
	private Map<Integer, Deque<ObjectID>> expandedPaths_PRID;

	private PersonRelationTree personRelationTree;
	private SelectionProviderProxy selectionProviderProxy = new SelectionProviderProxy();

	private PropertySetID currentPersonID = null;

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		getSite().setSelectionProvider(selectionProviderProxy);
	}

	/**
	 * Creates a new instance of the PersonRelationTree.
	 * Override this method for preparing other domain-specific PersonRelationTree.
	 */
	protected PersonRelationTree createPersonRelationTree(Composite parent) {
		return new PersonRelationTree(parent,
				false, // without restoring the tree's collapse state
				true,  // with context menu(s)
				false  // without the drill-down adapter.
			);
	}

	/**
	 * Creates a NotificationListener that defines the behaviour of this View with respect to whatever Perspective.
	 * Override this method for preparing other domain-specific NotificationListener.
	 */
	private NotificationListener notificationListenerLegalEntitySelected = null;
	protected NotificationListener createNotificationListenerLegalEntitySelected() {
		return new NotificationAdapterJob(Messages.getString("org.nightlabs.jfire.personrelation.issuetracking.trade.ui.PersonRelationIssueTreeView.selectLegalEntityJob.title")) //$NON-NLS-1$
		{
			public void notify(org.nightlabs.notification.NotificationEvent notificationEvent) {
				// Kai. Revised behaviour and display of the PersonRelationTree (consolidated and optimised: from jfire_1.0 + BEHR).
				//      i.e. The root of the tree represents the mother of all organisation(s) currently having the currently
				//           input Person; and the Person entry itself (can also be several instances) is expanded from
				//           whichever branch(es) it comes from. The input Person itself will be duly highlighted.
				//           --> If multiple instances exists, then (at least for now) ONE of them will be selected.
				//           --> Also, it is possible to have multiple rootS, symbolising multiple motherS of organisationS.
				//
				//           The root (or roots) of the PersonRelationTree shall now have a new interpreted meaning. It refers
				//           to the c^ \elemOf C.
				Object subject = notificationEvent.getFirstSubject();
				PropertySetID personID = null;

				if ( !(subject instanceof PropertySetID) ) {
					AnchorID legalEntityID = (AnchorID) notificationEvent.getFirstSubject();
					LegalEntity legalEntity = null;
					if (legalEntityID != null) {
						legalEntity = LegalEntityDAO.sharedInstance().getLegalEntity(
								legalEntityID,
								new String[] { FetchPlan.DEFAULT, LegalEntity.FETCH_GROUP_PERSON },
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								getProgressMonitor()
						);
					}

					personID = (PropertySetID) (legalEntity == null ? null : JDOHelper.getObjectId(legalEntity.getPerson()));
				}
				else
					personID = (PropertySetID) subject;


				// Ensures that we don't unnecessarily retrieve the relationRootNodes for one that has already been retrieved and on display.
				if (personID != null && (currentPersonID == null || currentPersonID != personID)) {
					if (logger.isDebugEnabled()) {
						logger.debug("personID:" + showObjectID(personID) + ",  currentPersonID:" + showObjectID(currentPersonID));
					}

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
						relatablePathsToRoots = PersonRelationDAO.sharedInstance().getRelationRootNodes(
								getAllowedPersonRelationTypes(), personID, DEFAULT_MAX_SEARCH_DEPTH, getProgressMonitor());

						final Set<PropertySetID> rootIDs = initRelatablePathsToRoots();

						// Done and ready. Update the tree.
						currentPersonID = personID;
						personRelationTree.getDisplay().asyncExec(new Runnable() {
							public void run() {
								if (!personRelationTree.isDisposed())
									personRelationTree.setInputPersonIDs(rootIDs);
							}
						});
					} catch (Exception e) {
						// Failed to retrieve path!?? Something must have been null.
						e.printStackTrace();

						currentPersonID = personID;
						personRelationTree.getDisplay().asyncExec(new Runnable() {
							public void run() {
								if (!personRelationTree.isDisposed())
									personRelationTree.setInputPersonIDs(Collections.singleton(currentPersonID));
							}
						});
					}
				}
			}
		};
	}


	@Override
	public void createPartContents(Composite parent) {
		personRelationTree = createPersonRelationTree(parent);
		personRelationTree.setRestoreCollapseState(false);
		personRelationTree.getPersonRelationTreeController().addPersonRelationTreeControllerDelegate(
				new IssuePersonRelationTreeControllerDelegate()
		);
		personRelationTree.addPersonRelationTreeLabelProviderDelegate(new IssueLinkPersonRelationTreeLabelProviderDelegate());
		personRelationTree.addPersonRelationTreeLabelProviderDelegate(new IssueDescriptionPersonRelationTreeLabelProviderDelegate());
		personRelationTree.addPersonRelationTreeLabelProviderDelegate(new IssueCommentPersonRelationTreeLabelProviderDelegate());

		notificationListenerLegalEntitySelected = createNotificationListenerLegalEntitySelected();
		if (notificationListenerLegalEntitySelected != null) {
			SelectionManager.sharedInstance().addNotificationListener(
					TradePlugin.ZONE_SALE,
					LegalEntity.class, notificationListenerLegalEntitySelected
			);

			personRelationTree.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent event) {
					SelectionManager.sharedInstance().removeNotificationListener(
							TradePlugin.ZONE_SALE,
							LegalEntity.class, notificationListenerLegalEntitySelected
					);
				}
			});
		}


		// Set up the ORDERED context-menus ---> Qn: How do I access the texts that the plugin has access to?
		personRelationTree.addContextMenuContribution(new SelectPersonRelationTreeItemAction());
		personRelationTree.addContextMenuContribution(this, new CreatePersonRelationAction(), null, "Create new person relation", SharedImages.getSharedImageDescriptor(Activator.getDefault(), CreatePersonRelationAction.class));
		personRelationTree.addContextMenuContribution(this, new DeletePersonRelationAction(), null, "Delete person relation", SharedImages.getSharedImageDescriptor(Activator.getDefault(), DeletePersonRelationAction.class));
		personRelationTree.addContextMenuContribution(new OpenIssueEditorAction());
		personRelationTree.addContextMenuContribution(this, new CreateOrLinkIssueAction(), null, "Create new or link existing issue", SharedImages.getSharedImageDescriptor(Activator.getDefault(), CreateOrLinkIssueAction.class));
		personRelationTree.addContextMenuContribution(this, new CreateIssueCommentAction(), null, "Create new issue comment", SharedImages.getSharedImageDescriptor(Activator.getDefault(), CreateIssueCommentAction.class));

		personRelationTree.integratePriorityOrderedContextMenu(); // <-- Voila! THAT's IT!



		// Initialise a system of listeners for the personRelationTree that will expand it accordingly. See notes.
		initAutoNodeExpansionLazyBehaviour();


		// Notifies other view(s) that may wish to react upon the current selection in the tree, in the TradePlugin.ZONE_SALE.
		// See Rev. 16511 for other (FARK-MARKed) notes on manupulating the nodes and their contents. Kai.
		personRelationTree.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object selectedElement = personRelationTree.getFirstSelectedElement();
				if (selectedElement instanceof PersonRelationTreeNode) {
					PersonRelationTreeNode node = (PersonRelationTreeNode) selectedElement;
					// Debug.
					if (logger.isInfoEnabled() && node != null) {
						logger.info(showObjectIDs("propertySetIDsToRoot", node.getPropertySetIDsToRoot(), 5)); //$NON-NLS-1$
					}

					if (node != null) {
						// Kai: It is possible that the selected treeNode does not contain a Person-related object (e.g. Issue, IssueComment, etc.).
						// But we may be interested of the Person-related object to which the selected treeNode belongs to.
						// --> Thus, in this case, we traverse up the parent until we get to a node representing a Person-related object.
						// --> This iterative traversal always have a base case, since the root node(s) in the PersonRelationTree is always a Person-related object.
						node = traverseUpUntilPerson(node);

						PropertySetID personID = node.getPropertySetID();
						if (personID != null)
							SelectionManager.sharedInstance().notify(new NotificationEvent(this, TradePlugin.ZONE_SALE, personID, Person.class));
					}
				}
			}
		});

		selectionProviderProxy.addRealSelectionProvider(personRelationTree);
	}



	// -------------------- [Helpers: Auto-expansion behaviour of the Lazy-tree] ---------------------------------------------->>
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
							logger.debug("*** *** *** Checking: nodeObjectID=" + showObjectID(nodeObjectID) + " *** *** ***");
							logger.debug("Checking: " + showDequePaths("pathToExpand", pathToExpand, true));
							logger.debug("Checking: " + showDequePaths("expandedPath", expandedPath, true));
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
					if (logger.isDebugEnabled()) {
						logger.debug(" ----------------->>>> On addJDOLazyTreeNodesChangedListener: [Checking loaded nodes, " + loadedTreeNodes.size() + "]");
						for (int index : pathsToExpand_PRID.keySet()) {
							logger.debug(" --> Checking: " + showDequePaths("pathToExpand", pathsToExpand_PRID.get(index), true));
							logger.debug(" --> Checking: " + showDequePaths("expandedPath", expandedPaths_PRID.get(index), true));
							logger.debug(" --> Checking: " + showObjectIDs("nextObjectIDsOnPaths", nextObjectIDsOnPaths, 5));
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
									logger.debug(" :: @" + posIndex + ", (loaded) nodeObjID:" +  showObjectID(nodeObjID) + (isOnNextPath ? " <-- Match!" : ""));

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
							logger.debug(" -->> " + showObjectIDs("childrenJDOObjectIDs", childrenJDOObjectIDs, 5));
						}

						// Locate the index of the childnode we want to force to be loaded.
						posIndex = 0;
						for (ObjectID objectID : childrenJDOObjectIDs) {
							if (nextObjectIDsOnPaths.contains(objectID)) {
								if (logger.isDebugEnabled()) {
									logger.debug(" -->>->> FOUND! @posIndex:" + posIndex + ", objectID:" + showObjectID(objectID));
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
		if (logger.isDebugEnabled())
			logger.debug("*** *** *** initRelatablePathsToRoots() :: [PSid:" + pathsToRoot_PSID.size() + "][PRid:" + pathsToRoot_PRID.size() + "] *** *** ***");

		while (iterPaths_PSID.hasNext()) {
			Deque<ObjectID> path_PSID = iterPaths_PSID.next();
			Deque<ObjectID> path_PRID = iterPaths_PRID.next();
			pathsToExpand_PRID.put(index, new LinkedList<ObjectID>(path_PRID)); // Maintain a copy.
			expandedPaths_PRID.put(index, new LinkedList<ObjectID>());

			if (logger.isDebugEnabled()) {
				logger.debug("@index:" + index + " " + showDequePaths("PSID", path_PSID, true));
				logger.debug("@index:" + index + " " + showDequePaths("PRID", path_PRID, true));
				logger.debug("--------------------");
			}

			rootIDs.add((PropertySetID) path_PSID.peekFirst());
			index++;
		}

		// Done.
		return rootIDs;
	}
	// <<-------------------- [Helpers: Auto-expansion behaviour of the Lazy-tree] ----------------------------------------------

	/**
	 * Given a set of {@link PropertySetID}s, invoke the {@link TradeManagerRemote} to retrieve the respective
	 * {@link AnchorID}s, and subsequently triggers a {@link NotificationEvent} through the {@link TradePlugin}.ZONE_SALE,
	 * passing the {@link AnchorID}s in order of appearance from the input {@link PropertySetID}s.
	 */
	protected void prepareAndHandleNotification(PropertySetID... propertySetIDs) {
		try {
			TradeManagerRemote tm = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
			AnchorID[] anchorIDs = new AnchorID[propertySetIDs.length];
			for (int i=0; i<propertySetIDs.length; i++) {
				LegalEntity le = propertySetIDs[i] == null ? null :
					tm.getLegalEntityForPerson(propertySetIDs[i], new String[] {FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

				anchorIDs[i] = (AnchorID) (le == null ? null : JDOHelper.getObjectId(le));
			}

			SelectionManager.sharedInstance().notify(
					new NotificationEvent(this, TradePlugin.ZONE_SALE, anchorIDs, new Class<?>[] {LegalEntity.class}));

		} catch (LoginException e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * Traverses up the tree in search for the first node containing a representation of a Person-related object.
	 * @return the first node it finds containing a representation of a Person-related object.
	 */
	private PersonRelationTreeNode traverseUpUntilPerson(PersonRelationTreeNode node) {
		ObjectID jdoObjectID = node.getJdoObjectID();
		Object jdoObject = node.getJdoObject();
		if (jdoObject != null && jdoObjectID != null) {
			// Base case.
			if (jdoObjectID instanceof PropertySetID || jdoObject instanceof PersonRelation)
				return node;

			// Iterative case.
			return traverseUpUntilPerson(node.getParent());
		}

		return node;
	}

	/**
	 * Given the jdoObjectID and/or the jdoObject, return the PropertySetID if either inputs are of the Person-related object.
	 * @return null if neither parameters are related to a Person object.
	 *
	 * Deprecated since 2010.02.26. In favour of directly calling {@link PersonRelationTreeNode}.getPropertySetID().
	 */
	@Deprecated
	protected PropertySetID getPropertySetID(ObjectID jdoObjectID, Object jdoObject) {
		if (jdoObjectID != null && jdoObjectID instanceof PropertySetID)
			return (PropertySetID)jdoObjectID;

		if (jdoObject != null && jdoObject instanceof PersonRelation)
			return ((PersonRelation)jdoObject).getToID();

		return null;
	}


	public PersonRelationTree getPersonRelationTree() {
		return personRelationTree;
	}


	protected Set<PersonRelationTypeID> allowedRelationTypeIDs;

	/**
	 * @return the Set of {@link PersonRelationTypeID}s, which guides the search for the appropriate paths from the
	 * person-relation graph, given an input {@link PropertySetID}.
	 */
	protected Set<PersonRelationTypeID> getAllowedPersonRelationTypes() {
		if (allowedRelationTypeIDs == null) {
			allowedRelationTypeIDs = new HashSet<PersonRelationTypeID>();

			allowedRelationTypeIDs.add(PersonRelationType.PredefinedRelationTypes.subsidiary);
			allowedRelationTypeIDs.add(PersonRelationType.PredefinedRelationTypes.employed);
			allowedRelationTypeIDs.add(PersonRelationType.PredefinedRelationTypes.child);
			allowedRelationTypeIDs.add(PersonRelationType.PredefinedRelationTypes.friend);
		}

		return allowedRelationTypeIDs;
	}

	protected int getMaxSearchDepth()
	{
		return DEFAULT_MAX_SEARCH_DEPTH;
	}



	// -----------------------------------------------------------------------------------------------------------------------------------|
	//  Tree-view-specific Actions.
	// -----------------------------------------------------------------------------------------------------------------------------------|
	private class SelectPersonRelationTreeItemAction extends Action implements IViewActionDelegate {
		private PropertySetID selectedPersonID = null;

		public SelectPersonRelationTreeItemAction() {
			setId(SelectPersonRelationTreeItemAction.class.getName());
			setText("Focus trade on this business partner"); // In this context, we handle either Peron or PersonRelation. We ignore Issue-related stuffs, since they'd be already handled appropriately.
		}

		@Override
		public void run() {
			if (selectedPersonID != null)
				prepareAndHandleNotification(selectedPersonID);
		}

		@Override
		public void selectionChanged(IAction action, ISelection selection) {
			selectedPersonID = null;
			PersonRelationTreeNode node = PersonRelationTreeNode.getPersonRelationTreeNodeFromSelection(selection);
			if (node == null) {
				action.setEnabled(false);
				return;
			}

			selectedPersonID = node.getPropertySetID();
			action.setEnabled(selectedPersonID != null);
		}

		@Override
		public void run(IAction action) { run(); }

		@Override
		public void init(IViewPart view) {
			throw new UnsupportedOperationException("This method should never be used."); //$NON-NLS-1$
		}
	}


	// -------------------------------------------------------------------------------------------------- ++ ------>>
	// I. Quick debug.
	protected String showDequePaths(String preamble, Deque<? extends ObjectID> path, boolean isReversed) {
		String str = "++ " + preamble + " :: {";
		Iterator<? extends ObjectID> iter = isReversed ? path.descendingIterator() : path.iterator();
		while (iter.hasNext())
			str += showObjectID(iter.next());

		return str + "}";
	}

	// II. Quick debug.
	protected String showObjectIDs(String preamble, List<? extends ObjectID> objIDs, int modLnCnt) {
		if (objIDs == null)
			return "++ " + preamble + " :: NULL";

		String str = "++ " + preamble + " (" + objIDs.size() + ") :: {\n     ";
		int ctr = 0;
		for (ObjectID objectID : objIDs) {
			str += "(" + ctr + ")" + showObjectID(objectID) + " ";
			ctr++;

			if (ctr % modLnCnt == 0)
				str += "\n     ";
		}

		return str + "\n   }";
	}

	// III. Quick debug.
	protected String showObjectID(ObjectID objectID) {
		if (objectID == null)
			return "[null]";

		String[] segID = objectID.toString().split("&");
		return "[" + segID[1] + "]";
	}
	// -------------------------------------------------------------------------------------------------- ++ ------>>

}
