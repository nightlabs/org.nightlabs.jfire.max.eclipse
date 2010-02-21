package org.nightlabs.jfire.personrelation.issuetracking.trade.ui;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.selection.SelectionProviderProxy;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedListener;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueComment;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.id.IssueDescriptionID;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditor;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorInput;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.PersonRelationType;
import org.nightlabs.jfire.personrelation.dao.PersonRelationDAO;
import org.nightlabs.jfire.personrelation.dao.PersonRelationTypeDAO;
import org.nightlabs.jfire.personrelation.id.PersonRelationID;
import org.nightlabs.jfire.personrelation.id.PersonRelationTypeID;
import org.nightlabs.jfire.personrelation.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.personrelation.ui.CompanyPersonRelationTreeLabelProviderDelegate;
import org.nightlabs.jfire.personrelation.ui.PersonRelationTree;
import org.nightlabs.jfire.personrelation.ui.PersonRelationTreeNode;
import org.nightlabs.jfire.personrelation.ui.createrelation.CreatePersonRelationWizard;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

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

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		getSite().setSelectionProvider(selectionProviderProxy);
	}

	@Override
	public void createPartContents(Composite parent) {
		personRelationTree = new PersonRelationTree(parent);
		personRelationTree.setRestoreCollapseState(false);
		personRelationTree.getPersonRelationTreeController().addPersonRelationTreeControllerDelegate(
				new IssuePersonRelationTreeControllerDelegate()
		);

		personRelationTree.addPersonRelationTreeLabelProviderDelegate(new IssueLinkPersonRelationTreeLabelProviderDelegate());
		personRelationTree.addPersonRelationTreeLabelProviderDelegate(new IssueDescriptionPersonRelationTreeLabelProviderDelegate());
		personRelationTree.addPersonRelationTreeLabelProviderDelegate(new IssueCommentPersonRelationTreeLabelProviderDelegate());
		personRelationTree.addPersonRelationTreeLabelProviderDelegate(new CompanyPersonRelationTreeLabelProviderDelegate()); // Company-specific relations: companyGroup/subsidiary.

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




		// --->> Context-menu setup.
		// FIXME How do I use CreatePersonRelationAction (and 4 others) that are IViewActionDelegate (rather than IAction
		//       or IContributionItem) that are already established extensions in the plugin.xml. Kai.
		// Or is this even possible??
		cPersReln_Action = new CreatePersonRelation_Action();
		dPersReln_Action = new DeletePersonRelation_Action();
		cIssue_Action = new CreateIssue_Action();
		cIssueComment_Action = new CreateIssueComment_Action();

		personRelationTree.addContextMenuContribution(cPersReln_Action);
		personRelationTree.addContextMenuContribution(dPersReln_Action);
		personRelationTree.addContextMenuContribution(cIssue_Action);
		personRelationTree.addContextMenuContribution(cIssueComment_Action);

//		// ------------- FARK-MARK tests ---------------------------------------------------------->>
//		String createIssueAction_ID = "org.nightlabs.jfire.personrelation.issuetracking.trade.ui.CreateIssueAction";
//		IExtensionPoint exPt = Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.ui.viewActions");
//		if (exPt != null) {
//			IExtension[] extensions = exPt.getExtensions();
//
//			if (logger.isInfoEnabled()) {
//				String packageName = this.getClass().getPackage().getName();
//				logger.info("------------------------------------[" + packageName + "]");
//				for (IExtension extension : extensions) {
//					logger.info("~~ Simple: " + extension.getSimpleIdentifier() + ", Namespace: " + extension.getNamespaceIdentifier());
//
//					if (extension.getNamespaceIdentifier().equals(packageName)) {
//						IConfigurationElement[] confElems = extension.getConfigurationElements();
//						for (IConfigurationElement confElem : confElems) {
//							IConfigurationElement[] confChildren = confElem.getChildren();
//							logger.info("   ~~ confElem: " + confElem.getName() + " [" + confChildren.length + "]");
//
//							for (IConfigurationElement confChild : confChildren) {
//								if (confChild.isValid()) {
//									IConfigurationElement[] confChildChildren = confChild.getChildren();
//									logger.info("      --> confChild: " + confChild.getName() + "[" + confChildChildren.length + "], " + confChild.getNamespaceIdentifier());
//									logger.info("          (Class: " + confChild.getClass().getName() + "), " + confChild.getValue());
//
//									String[] attributeNames = confElem.getAttributeNames();
//									for (String attName : attributeNames) {
//										logger.info("          @" + attName + ": " + confChild.getAttribute(attName));
//									}
//
//
//									// Look for the id.
//									if (confChild.getAttribute("id").equals(createIssueAction_ID)) {
//										// So what?? I will get an IActionDelegate, not an IAction... how do I integrate this into the context menu
//									}
//								}
//							}
//						}
//					}
//
//				}
//			}
//		}
//		// ------------- FARK-MARK tests ---------------------------------------------------------->>



		personRelationTree.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				Set<PersonRelationTreeNode> selectedTreeNodes = personRelationTree.getSelectedElements();
				if (selectedTreeNodes.size() != 1)
					return;

				PersonRelationTreeNode treeNode = selectedTreeNodes.iterator().next();
				Object object = treeNode.getJdoObject();

				// Handles Person-related stuffs (entry-point)------------------------------------------------------->>
				currentPersonID = null;
				if (object != null && object instanceof PersonRelation) {
//					final PropertySetID personID = treeNode.getPropertySetID();
					currentPersonID = treeNode.getPropertySetID();
					if (currentPersonID != null) {
						// Join this with the entry-point-listener of this View, since the behaviour is exactly the same.
						try {
							TradeManagerRemote tm = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
							LegalEntity le = tm.getLegalEntityForPerson(currentPersonID, new String[] {FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
							if (le != null) {
								AnchorID anchorID = (AnchorID) JDOHelper.getObjectId(le);
								SelectionManager.sharedInstance().notify(new NotificationEvent(this, TradePlugin.ZONE_SALE, anchorID, LegalEntity.class));
							}
							else
								notificationListenerLegalEntitySelected.notify(new NotificationEvent(this, TradePlugin.ZONE_SALE, currentPersonID, LegalEntity.class));

						} catch (LoginException e) {
							throw new RuntimeException(e);
						}
					}
				}

				// Handles Issue stuffs ----------------------------------------------------------------------------->>
				else {
					ObjectID objectID = treeNode.getJdoObjectID();
					IssueID issueID = null;
					if (object instanceof IssueLink) {
						IssueLink issueLink = (IssueLink) object;
						issueID = (IssueID) JDOHelper.getObjectId(issueLink.getIssue());
					}
					else if (objectID instanceof IssueDescriptionID) {
						IssueDescriptionID issueDescriptionID = (IssueDescriptionID)objectID;
						issueID = IssueID.create(issueDescriptionID.organisationID, issueDescriptionID.issueID);
					}
					else if (object instanceof IssueComment) {
						IssueComment issueComment = (IssueComment) object;
						issueID = issueComment.getIssueID();
					}

					if (issueID != null) {
						IssueEditorInput issueEditorInput = new IssueEditorInput(issueID);
						try {
							Editor2PerspectiveRegistry.sharedInstance().openEditor(issueEditorInput, IssueEditor.EDITOR_ID);
						} catch (Exception e1) {
							throw new RuntimeException(e1);
						}
					}
				}

			}
		});

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
						if (logger.isInfoEnabled()) {
							logger.info("*** *** *** Checking: nodeObjectID=" + showObjectID(nodeObjectID) + " *** *** ***");
							logger.info("Checking: " + showDequePaths("pathToExpand", pathToExpand, true));
							logger.info("Checking: " + showDequePaths("expandedPath", expandedPath, true));
							logger.info("---");
						}

						boolean isMatch = isMatchingSubPath(objectIDsToRoot, expandedPath, true, true);
						if (isMatch || expandedPath.isEmpty()) {
							isNodeMarkedForExpansion |= isMatch;
							expandedPath.push( pathToExpand.pop() );

							isNodeMarkedForSelection |= pathToExpand.isEmpty();
						}

						if (logger.isInfoEnabled()) {
							logger.info("Checking: isMatch=" + (isMatch ? "T" : "F"));
							logger.info("Checking: isNodeMarkedFor*Expansion*=" + (isNodeMarkedForExpansion ? "T" : "F"));
							logger.info("Checking: isNodeMarkedForSelection=" + (isNodeMarkedForSelection ? "T" : "F"));
							logger.info("---");
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
					if (logger.isInfoEnabled()) {
						logger.info(" ----------------->>>> On addJDOLazyTreeNodesChangedListener: [Checking loaded nodes, " + loadedTreeNodes.size() + "]");
						for (int index : pathsToExpand_PRID.keySet()) {
							logger.info(" --> Checking: " + showDequePaths("pathToExpand", pathsToExpand_PRID.get(index), true));
							logger.info(" --> Checking: " + showDequePaths("expandedPath", expandedPaths_PRID.get(index), true));
							logger.info(" --> Checking: " + showObjectIDs("nextObjectIDsOnPaths", nextObjectIDsOnPaths, 5));
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
								if (logger.isInfoEnabled())
									logger.info(" :: @" + posIndex + ", (loaded) nodeObjID:" +  showObjectID(nodeObjID) + (isOnNextPath ? " <-- Match!" : ""));

								personRelationTree.setSelection(node);
								ISelection selection = personRelationTree.getTreeViewer().getSelection();
								personRelationTree.getTreeViewer().setSelection(selection, true);
								break;
							}
						}
						else {
							if (logger.isInfoEnabled())
								logger.info(" :: @" + posIndex + ", node: [null]");
						}

						posIndex++;
					}


					// If we make it through to here, then we need to force the node we want to be loaded.
					// But only if the parentNode is NOT null.
					if (parNode != null) {
						List<ObjectID> childrenJDOObjectIDs = parNode.getChildrenJDOObjectIDs();
						long nodeCount = parNode.getChildNodeCount();
						int childNodeCnt = childrenJDOObjectIDs != null ? childrenJDOObjectIDs.size() : -1;
						if (logger.isInfoEnabled()) {
							logger.info(" -->> parNode.childNodeCount: " + nodeCount + ", childNodeCnt: " + childNodeCnt);
							logger.info(" -->> " + showObjectIDs("childrenJDOObjectIDs", childrenJDOObjectIDs, 5));
						}

						// Locate the index of the childnode we want to force to be loaded.
						posIndex = 0;
						for (ObjectID objectID : childrenJDOObjectIDs) {
							if (nextObjectIDsOnPaths.contains(objectID)) {
								if (logger.isInfoEnabled()) {
									logger.info(" -->>->> FOUND! @posIndex:" + posIndex + ", objectID:" + showObjectID(objectID));
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


		// See Rev. 16511 for other (FARK-MARKed) notes on manupulating the nodes and their contents. Kai.
		personRelationTree.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				currentPersonID = null;
				selectedPersonRelation = null;
				selectedIssueID = null;

				ISelection selection = event.getSelection();
				if (selection.isEmpty())
					return;

				PersonRelationTreeNode selectedNode = (PersonRelationTreeNode) ((TreeSelection) selection).getFirstElement();
				if (selectedNode != null) {
					// The general case: Person-related.
					currentPersonID = selectedNode.getPropertySetID();
					Object jdoObject = selectedNode.getJdoObject();
					if (jdoObject instanceof PersonRelation)
						selectedPersonRelation = (PersonRelation) jdoObject;

					// The general case: Issue-related.
					if (currentPersonID == null && selectedPersonRelation == null) {
						if (jdoObject instanceof IssueLink) {
							Issue issue = ((IssueLink) jdoObject).getIssue();
							selectedIssueID = (IssueID) JDOHelper.getObjectId(issue);
						}
						else if (jdoObject instanceof IssueComment) {
							selectedIssueID = ((IssueComment) jdoObject).getIssueID();
						}
						else if (selectedNode.getJdoObjectID() instanceof IssueDescriptionID) {
							IssueDescriptionID issueDescID = (IssueDescriptionID) selectedNode.getJdoObjectID();
							selectedIssueID = IssueID.create(issueDescID.organisationID, issueDescID.issueID);
						}
					}


					// Reaction. Part I.
					// Notifies other view(s) that may wish to react upon the current selection in the tree, in the TradePlugin.ZONE_SALE.
					// It is possible that the selected treeNode does not contain a Person-related object (e.g. Issue, IssueComment, etc.).
					// But we may be interested of the Person-related object to which the selected treeNode belongs to.
					// --> Thus, in this case, we traverse up the parent until we get to a node representing a Person-related object.
					// --> This iterative traversal always have a base case, since the root node(s) in the PersonRelationTree is always a Person-related object.
					selectedNode = traverseUpUntilPerson(selectedNode);
					PropertySetID personID = selectedNode.getPropertySetID();
					if (personID != null)
						SelectionManager.sharedInstance().notify(new NotificationEvent(this, TradePlugin.ZONE_SALE, personID, Person.class));


//					// Debug.
//					if (logger.isInfoEnabled()) {
//						logger.info(showObjectIDs("objectIDsToRoot", selectedNode.getJDOObjectIDsToRoot(), 5));
//						logger.info(showObjectIDs("propertySetIDsToRoot", selectedNode.getPropertySetIDsToRoot(), 5));
//						logger.info(" ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ");
//					}
				}

				// Reaction. Part II.
				cPersReln_Action.setEnabled(currentPersonID != null);
				dPersReln_Action.setEnabled(selectedPersonRelation != null);
				cIssue_Action.setEnabled(currentPersonID != null);
				cIssueComment_Action.setEnabled(selectedIssueID != null);
			}
		});

		selectionProviderProxy.addRealSelectionProvider(personRelationTree);
	}

	// -------------------- [Helpers: Auto-expansion behaviour of the Lazy-tree] ---------------------------------------------->>
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
		List<Deque<ObjectID>> pathsToRoot_PSID = relatablePathsToRoots.get(PropertySetID.class);    // <-- PropertySetID only.
		List<Deque<ObjectID>> pathsToRoot_PRID = relatablePathsToRoots.get(PersonRelationID.class); // <-- PropertySetID + PersonRelationID.

		// Initialise the path-expansion trackers.
		pathsToExpand_PRID = new HashMap<Integer, Deque<ObjectID>>(pathsToRoot_PRID.size());
		expandedPaths_PRID = new HashMap<Integer, Deque<ObjectID>>(pathsToRoot_PRID.size());

		Set<PropertySetID> rootIDs = new HashSet<PropertySetID>();
		Iterator<Deque<ObjectID>> iterPaths_PSID = pathsToRoot_PSID.iterator();
		Iterator<Deque<ObjectID>> iterPaths_PRID = pathsToRoot_PRID.iterator();
		int index = 0;
		if (logger.isInfoEnabled())
			logger.info("*** *** *** initRelatablePathsToRoots() :: [PSid.size:" + pathsToRoot_PSID.size() + "][PRid.size:" + pathsToRoot_PRID.size() + "] *** *** ***");

		while (iterPaths_PSID.hasNext()) {
			Deque<ObjectID> path_PSID = iterPaths_PSID.next();
			Deque<ObjectID> path_PRID = iterPaths_PRID.next();
			pathsToExpand_PRID.put(index, new LinkedList<ObjectID>(path_PRID)); // Maintain a copy.
			expandedPaths_PRID.put(index, new LinkedList<ObjectID>());

			if (logger.isInfoEnabled()) {
				logger.info("@index:" + index + " " + showDequePaths("PSID", path_PSID, true));
				logger.info("@index:" + index + " " + showDequePaths("PRID", path_PRID, true));
				logger.info("-------------------- ?%&$# -------->>>");
			}

			rootIDs.add((PropertySetID) path_PSID.peekFirst());
			index++;
		}

		// Done.
		return rootIDs;
	}
	// <<-------------------- [Helpers: Auto-expansion behaviour of the Lazy-tree] ----------------------------------------------



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



	// -----------------------------------------------------------------------------------------------------------------------------------|
	// ---[ Actions ]---------------------------------------------------------------------------------------------------------------------|
	// -----------------------------------------------------------------------------------------------------------------------------------|
	// FIXME How do I make references to the already established IViewActionDelegate-extensions in the plugin.xml?
	//       Preexistings: [1] CreatePersonRelationAction
	//                     [2] DeletePersonRelationAction
	//                     [3] CreateIssueAction
	//                     [4] CreateIssueCommentAction
	private CreatePersonRelation_Action cPersReln_Action;
	private DeletePersonRelation_Action dPersReln_Action;
	private CreateIssue_Action cIssue_Action;
	private CreateIssueComment_Action cIssueComment_Action;

	// <-- Preexisting [1]
	private class CreatePersonRelation_Action extends Action {
		public CreatePersonRelation_Action() {
			setId(CreatePersonRelation_Action.class.getName());
			setImageDescriptor(SharedImages.ADD_16x16);
			setToolTipText("Create new person relation");
			setText("Create new person relation");
		}

		@Override
		public void run() {
			if (currentPersonID != null) {
				CreatePersonRelationWizard wizard = new CreatePersonRelationWizard(currentPersonID);
				new DynamicPathWizardDialog(wizard).open();
			}
		}
	}

	// <-- Preexisting [2]
	private class DeletePersonRelation_Action extends Action {
		public DeletePersonRelation_Action() {
			setId(DeletePersonRelation_Action.class.getName());
			setImageDescriptor(SharedImages.DELETE_16x16);
			setToolTipText("Delete person relation");
			setText("Delete person relation");
		}

		@Override
		public void run() {
			if (selectedPersonRelation != null) {
				final PersonRelation personRelation = selectedPersonRelation;
				Job job = new Job(Messages.getString("org.nightlabs.jfire.personrelation.issuetracking.trade.ui.DeletePersonRelationAction.job.deletingPersonRelation.name")) { //$NON-NLS-1$
					@Override
					protected IStatus run(ProgressMonitor monitor) throws Exception {
						PersonRelationTypeID personRelationTypeID = (PersonRelationTypeID) JDOHelper.getObjectId(personRelation.getPersonRelationType());
						if (personRelationTypeID == null)
							throw new IllegalStateException("JDOHelper.getObjectId(personRelation.getPersonRelationType()) returned null! personRelation=" + personRelation + " personRelationType=" + personRelation.getPersonRelationType()); //$NON-NLS-1$ //$NON-NLS-2$

						PersonRelationDAO.sharedInstance().deletePersonRelation(
								personRelationTypeID,
								personRelation.getFromID(),
								personRelation.getToID(),
								monitor
						);

						return Status.OK_STATUS;
					}
				};

				job.setUser(true);
				job.setPriority(Job.INTERACTIVE);
				job.schedule();
			}
		}
	}

	// <-- Preexisting [3]
	private class CreateIssue_Action extends Action {
		public CreateIssue_Action() {
			setId(CreateIssue_Action.class.getName());
			setToolTipText("Create new issue");
			setText("Create new issue");
		}

		@Override
		public void run() {
			if (currentPersonID != null) {
				final PropertySetID selectedPersonID = currentPersonID;
				final Shell shell = getSite().getShell();
				final Display display = shell.getDisplay();

				Job job = new Job(Messages.getString("org.nightlabs.jfire.personrelation.issuetracking.trade.ui.CreateIssueAction.job.openingWizard.name")) { //$NON-NLS-1$
					@Override
					protected IStatus run(ProgressMonitor monitor) throws Exception {
						monitor.beginTask(Messages.getString("org.nightlabs.jfire.personrelation.issuetracking.trade.ui.CreateIssueAction.task.openingWizard.name"), 100); //$NON-NLS-1$
						try {
							final CreateIssueWizard wizard = new CreateIssueWizard(selectedPersonID, new SubProgressMonitor(monitor, 100));

							display.asyncExec(new Runnable() {
								public void run() {
									DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(shell, wizard);
									dialog.open();
								}
							});

							return Status.OK_STATUS;
						} finally {
							monitor.done();
						}
					}
				};

				job.setUser(true);
				job.setPriority(Job.INTERACTIVE);
				job.schedule();
			}
		}
	}

	// <-- Preexisting [4]
	private class CreateIssueComment_Action extends Action {
		public CreateIssueComment_Action() {
			setId(CreateIssueComment_Action.class.getName());
			setToolTipText("Create new issue comment");
			setText("Create new issue commment");
		}

		@Override
		public void run() {
			if (selectedIssueID != null) {
				new CreateIssueCommentDialog(getSite().getShell(), selectedIssueID).open();
			}
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------|
	// ---[ Entry notification ]----------------------------------------------------------------------------------------------------------|
	// -----------------------------------------------------------------------------------------------------------------------------------|
	private PropertySetID currentPersonID = null;          // Local control.
	private PropertySetID currentBusinessPartnerID = null; // Global control.
	private PersonRelation selectedPersonRelation = null;
	private IssueID selectedIssueID = null;
	private NotificationListener notificationListenerLegalEntitySelected = new NotificationAdapterJob(Messages.getString("org.nightlabs.jfire.personrelation.issuetracking.trade.ui.PersonRelationIssueTreeView.selectLegalEntityJob.title")) //$NON-NLS-1$
	{
		public void notify(org.nightlabs.notification.NotificationEvent notificationEvent) {
			// Kai. The behaviour of the PersonRelationTree has been amended to comform to Behr's specification.
			//      i.e. The root of the tree represents the mother of all organisation(s) currently having the currently
			//           input Person; and the Person entry itself (can also be several instances) is expanded from
			//           whichever branch(es) it comes from. The input Person itself will be duly highlighted.
			//           --> If multiple instances exists, then (at least for now) ONE of them will be selected.
			//           --> Also, it is possible to have multiple rootS, symbolising multiple motherS of organisationS.
			//
			//           The root (or roots) of the PersonRelationTree shall now have a new interpreted meaning. It refers
			//           to the c^ \elemOf C.
			//
			// To revert back to the original behaviour is simple:
			// Just pass the PropertySetID of the trading-partner/customer to be displayed as root.
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


			// Don't quite know why, but this NotificationListener is always called twice (in a row)!
			// Ensures that we dont unnecessarily retrieve the relationRootNodes (expensive).
			if (personID != null && (currentBusinessPartnerID == null || currentBusinessPartnerID != personID)) {
				if (logger.isInfoEnabled()) {
					logger.info("personID:" + showObjectID(personID) + ",  currentBusinessPartnerID:" + showObjectID(currentBusinessPartnerID));
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
				relatablePathsToRoots = PersonRelationDAO.sharedInstance().getRelationRootNodes(
						getAllowedPersonRelationTypes(), personID, DEFAULT_MAX_SEARCH_DEPTH, getProgressMonitor());

				final Set<PropertySetID> rootIDs = initRelatablePathsToRoots();

				// Done and ready. Update the tree.
				currentBusinessPartnerID = personID;
				personRelationTree.getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (!personRelationTree.isDisposed()) {
							personRelationTree.setInputPersonIDs(rootIDs);

							// On observation (Kai):
							//   Once the new roots have been duly loaded, the Action buttons in this view fails to be
							//   (selectively) activated. This could be due to the fact that the SelectionChangedListener has
							//   not been triggered? (Really?) Or that it processed the SelectionChangedListener first?
							//
							//   But if we click again on the same selected item, the buttons become active as per normal behaviour.
						}
					}
				});
			}
		}
	};

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
			final String[] allowedRelations = {"subsidiary", "employed", "child"};
			allowedRelationTypeIDs = new HashSet<PersonRelationTypeID>();

			Job job = new Job("Loading relations...") {
				@Override
				protected IStatus run(ProgressMonitor monitor) throws Exception {
					List<PersonRelationType> persRelTypes = PersonRelationTypeDAO.sharedInstance().getPersonRelationTypes(
							new String[] {FetchPlan.DEFAULT, PersonRelationType.FETCH_GROUP_NAME},
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

					List<String> relationStrings = Arrays.asList(allowedRelations);
					for (PersonRelationType prt : persRelTypes) {
						String str = "~~ PersonRelationTypeID: \"" + prt.getPersonRelationTypeID() + "\"";
						if (relationStrings.contains(prt.getPersonRelationTypeID())) {
							allowedRelationTypeIDs.add((PersonRelationTypeID) JDOHelper.getObjectId(prt));
							str += " <----- [ADDED to allowedRelationTypes]";
						}

						if (logger.isInfoEnabled())
							logger.info(str);
					}

					return Status.OK_STATUS;
				}
			};

			job.setPriority(Job.SHORT);
			job.schedule();
		}
		return allowedRelationTypeIDs;
	}

	protected int getMaxSearchDepth()
	{
		return DEFAULT_MAX_SEARCH_DEPTH;
	}





	// -------------------------------------------------------------------------------------------------- FARK-MARK ------>>
	// I. Quick debug.
	private String showDequePaths(String preamble, Deque<? extends ObjectID> path, boolean isReversed) {
		String str = "++ " + preamble + " :: {";
		Iterator<? extends ObjectID> iter = isReversed ? path.descendingIterator() : path.iterator();
		while (iter.hasNext())
			str += showObjectID(iter.next());

		return str + "}";
	}

	// II. Quick debug.
	private String showObjectIDs(String preamble, List<? extends ObjectID> objIDs, int modLnCnt) {
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
	private String showObjectID(ObjectID objectID) {
		if (objectID == null)
			return "[null]";

		String[] segID = objectID.toString().split("&");
		return "[" + segID[1] + "]";
	}
	// -------------------------------------------------------------------------------------------------- FARK-MARK ------>>

}
