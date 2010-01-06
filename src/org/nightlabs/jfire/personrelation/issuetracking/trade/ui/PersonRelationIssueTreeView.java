package org.nightlabs.jfire.personrelation.issuetracking.trade.ui;

import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;
import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.base.ui.selection.SelectionProviderProxy;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
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
import org.nightlabs.jfire.personrelation.id.PersonRelationTypeID;
import org.nightlabs.jfire.personrelation.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.personrelation.ui.PersonRelationTree;
import org.nightlabs.jfire.personrelation.ui.PersonRelationTreeNode;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;

public class PersonRelationIssueTreeView
extends LSDViewPart
{
	protected static final int DefaultMaximumSearchDepth	= 10;
//	private Set<Deque<ObjectID>> pathsToBeExpanded = null; // See notes on Behr's specification with the PersonRelationTree. Kai.
	private Set<Deque<PropertySetID>> pathsToBeExpanded = null; // See notes on Behr's specification with the PersonRelationTree. Kai.
	private Deque<PropertySetID> testPath = null;
	private Deque<PropertySetID> testPathExpanded = null;

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
		personRelationTree.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				Set<PersonRelationTreeNode> selectedTreeNodes = personRelationTree.getSelectedElements();
				if (selectedTreeNodes.size() != 1)
					return;

				PersonRelationTreeNode treeNode = selectedTreeNodes.iterator().next();
				ObjectID objectID = treeNode.getJdoObjectID();
				Object object = treeNode.getJdoObject();

				// Handles Person-related stuffs (entry-point)------------------------------------------------------->>
				// See notes for description, look under "Specifications for Behr" ---------------------------------->>
				if (object != null && object instanceof PersonRelation) {
					final PropertySetID personID = getPropertySetID(objectID, object);

					if (personID != null) {
						final PersonRelation personReln = (PersonRelation) object;
						final PersonRelationType personRelnType = personReln.getPersonRelationType();

						Job job = new Job("Loading relations") {
							@Override
							protected IStatus run(ProgressMonitor monitor) throws Exception {
								// FIXME These remarks have changed a bit. We no longer use PersonRelationIDs to expand.
								// [A] If a Person-Trading-Partner has been selected; see notes.
								// [B] Otherwise, the selected 'organisation' becomes the new root.

								// <--- See [A].
								if (personRelnType.getPersonRelationTypeID().equals("employing")) {
									// --- FARK-MARK --- CHANGED to PropertySetID°
									// Starting with the personID, we retrieve outgoing paths from it. Each path traces the personID's
									// relationship up through the hierachy of organisations, and terminates under one of the following
									// three conditions:
									//    1. When it reaches the mother of all subsidiary organisations (known as c^ \elemof C).
									//    2. When it detects a cyclic / nested-cyclic relationship between subsidiary-groups.
									//    3. When the length of the path reaches the preset DefaultMaximumSearchDepth.
									// For the sake of simplicity, let c^ be the terminal element in a path. Then all the unique c^'s
									// collated from the returned paths are the new roots for PersonRelationTree.
									pathsToBeExpanded = PersonRelationDAO.sharedInstance().getRelationRoots(
											getAllowedPersonRelationTypes(), personID, DefaultMaximumSearchDepth, monitor);

									// Prepare the new roots (the unique c^).
									final Set<PropertySetID> rootIDs = new HashSet<PropertySetID>();
									for (Deque<PropertySetID> path : pathsToBeExpanded) {
										rootIDs.add( path.peekFirst() );
										showDequePaths("   pathToExpand", path, false);
									}

									testPath = pathsToBeExpanded.iterator().next(); // For current testing.


									// Update the tree.
									personRelationTree.getDisplay().asyncExec(new Runnable() {
										public void run() {
											// Each path \elemOf paths is represented in a Deque in a reverse-order traversal to the root's
											// PropertySetID c^ as follows:
											//   path = { g(p_i), g(c_0), g(c_1), ..., g(c_j), c^ },
											//   where p_i \elemOf P is the PropertySetID of the trading partner (in this case, a Person);
											//         c_i \elemOf C is the PropertySetID of an organisation; and
											//         g(x) is the PersonRelationID of x, where x be an arbitrary PropertySetID.
											// The paths are passed to the Tree's controller to load the necessary nodes through the
											// expandPaths() method, and returns the TreePaths, which we shall use to inform the TreeViewer
											// to expand to the levels we want displayed.
											if (!personRelationTree.isDisposed()) {
												personRelationTree.setInputPersonIDs(rootIDs);
												showDequePaths(">> testPath", testPath, false);

												// Naive approach IIa.v16.9.8b:
												//   --> We wait until everything's loaded from these rootIDs in the new tree.
												//   --> Afterwhich, endeavour to expand the paths we found.
												// We let the system of Listeners do the trick!


												// ----------------------------- FARK-MARK ----------------------->>
//												// Go and freaking expand the paths!!!
//												List<TreePath> treePaths = personRelationTree.getPersonRelationTreeController().expandPaths(paths);
//												System.err.println("::::: Paths to expand :::");
//												for (TreePath treePath : treePaths) {
//													System.err.print(" -->> treePath[len:" + treePath.getSegmentCount() + "] :: ");
//													for (int i=0; i<treePath.getSegmentCount(); i++) {
//														PersonRelationTreeNode segment = (PersonRelationTreeNode) treePath.getSegment(i);
//														String[] segID = segment.getJdoObjectID().toString().split("&");
//														System.err.print("[ID:" + segID[1] + "," + segment.getChildNodeCount() + "]");
//													}
//
//													System.err.println();
//												}
//												System.err.println("\n");
//
//
//
//												for (TreePath treePath : treePaths)
//													personRelationTree.getTreeViewer().expandToLevel(treePath, 100);
//
//
//												// Now check with the TreeViewer?
//												System.err.println("::::: Expanded paths from the TreeViewer :::");
//												TreePath[] etreePaths = personRelationTree.getTreeViewer().getExpandedTreePaths();
//												for (TreePath treePath : etreePaths) {
//													System.err.print(" -->> treePath[len:" + treePath.getSegmentCount() + "] :: ");
//													for (int i=0; i<treePath.getSegmentCount(); i++) {
//														PersonRelationTreeNode segment = (PersonRelationTreeNode) treePath.getSegment(i);
//														String[] segID = segment.getJdoObjectID().toString().split("&");
//														System.err.print("[ID:" + segID[1] + "," + segment.getChildNodeCount() + "]");
//													}
//
//													System.err.println();
//												}
//												System.err.println("\n");
												// ----------------------------- FARK-MARK ----------------------->>
											}
										}
									});
								}

								// <--- See [B].
								else
									personRelationTree.getDisplay().asyncExec(new Runnable() {
										public void run() {
											if (!personRelationTree.isDisposed())
												personRelationTree.setInputPersonIDs(Collections.singleton(personID));
										}
									});

								return Status.OK_STATUS;
							}
						};

						job.setPriority(Job.SHORT);
						job.schedule();
					}

				}

				// Handles Issue stuffs ----------------------------------------------------------------------------->>
				else {
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

		// Notifies other view(s) that may wish to react upon the current selection in the tree, in the TradePlugin.ZONE_SALE.
		// See Rev. 16511 for other (FARK-MARKed) notes on manupulating the nodes and their contents. Kai.
		personRelationTree.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				PersonRelationTreeNode node = personRelationTree.getFirstSelectedElement();
				if (node != null) {
					// Kai: It is possible that the selected treeNode does not contain a Person-related object (e.g. Issue, IssueComment, etc.).
					// But we may be interested of the Person-related object to which the selected treeNode belongs to.
					// --> Thus, in this case, we traverse up the parent until we get to a node representing a Person-related object.
					// --> This iterative traversal always have a base case, since the root node(s) in the PersonRelationTree is always a Person-related object.
					node = traverseUpUntilPerson(node);

					ObjectID jdoObjectID = node.getJdoObjectID();
					Object jdoObject = node.getJdoObject();

					PropertySetID personID = getPropertySetID(jdoObjectID, jdoObject);
					if (personID != null)
						SelectionManager.sharedInstance().notify(new NotificationEvent(this, TradePlugin.ZONE_SALE, personID, Person.class));
				}

				// ----------------------------- FARK-MARK ----------------------->>
				showExpandedTreePaths();
				// ----------------------------- FARK-MARK ----------------------->>
			}
		});

		selectionProviderProxy.addRealSelectionProvider(personRelationTree);



		// ----------------------------- FARK-MARK ----------------------->>
		// Unravel the tree: Open it up to those paths we've discovered! Kai.
		// HOWEVER...
		//   This approach requires the 'unique' PropertySetIDs in the pathToExpand, because the PersonRelationIDs
		//   retrived and the PersonRelationIDs created on the tree are NOT the same.
		//
		// E.g.
		// pathToExpand :: [propertySetID=6][personRelationID=25][personRelationID=23][personRelationID=20] ~~ Retrieved from server.
		//
		// On the client side:
		// [01]
		// ++ objectIDsToRoot :: [propertySetID=7]
		// ++ objectIDsToRoot :: [propertySetID=6] <------------ FOUND! Expanding...
		//
		// [02]
		// ++ objectIDsToRoot :: [propertySetID=7]
		// ++ objectIDsToRoot :: [propertySetID=6][personRelationID=24]
		// ++ objectIDsToRoot :: [propertySetID=6][personRelationID=26]
		// ++ objectIDsToRoot :: [propertySetID=6][personRelationID=28]
		// ++ objectIDsToRoot :: [propertySetID=6][personRelationID=3a]
		//
		// DONE.
		//
		// We fail to detect the [personRelationID=25] after [propertySetID=6] on the second level.
		// On inspection of the data, we found that [personRelationID=25] is supposed to be [personRelationID=24], which we wanted
		// to detect to further our expansion.
		testPathExpanded = new LinkedList<PropertySetID>();
		personRelationTree.getTree().addListener(SWT.SetData, new Listener() {
			@Override
			public void handleEvent(Event event) {
				// Guard #1.
				if (pathsToBeExpanded == null || pathsToBeExpanded.isEmpty() || !(event.item instanceof TreeItem))
					return;

				// Guard #2.
				TreeItem treeItem = (TreeItem) event.item;
				if (!(treeItem.getData() instanceof PersonRelationTreeNode) || treeItem.getItemCount() <= 1)
					return;

				// Guard #3.
				PersonRelationTreeNode node = (PersonRelationTreeNode) treeItem.getData();
				if (node.getPropertySetID() == null)
					return;

				Deque<PropertySetID> propertySetIDsToRoot = (LinkedList<PropertySetID>)node.getPropertySetIDsToRoot();
				String[] segID = node.getPropertySetID().toString().split("&");
				showDequePaths("--------------------> @Node[ID:" + segID[1] + "] ::", propertySetIDsToRoot, true);

				PropertySetID id_onPath = testPath.peekFirst();
				if (propertySetIDsToRoot.contains(id_onPath)) {
					showDequePaths("~~ Checking: propertySetIDsToRoot", propertySetIDsToRoot, true);
					showDequePaths("~~ Checking:     testPathExpanded", testPathExpanded, !true);

					boolean isMatch = isMatching(propertySetIDsToRoot, testPathExpanded, true, true);
					System.err.println("  :::::: isMatch = " + (isMatch ? "True" : "False"));
					if (testPathExpanded.isEmpty() || isMatch) {
						testPathExpanded.push( testPath.pop() );
						showDequePaths("EXPANDING: propertySetIDsToRoot", propertySetIDsToRoot, true);
						showDequePaths(">> testPath", testPath, !true);
						showDequePaths(">> testPathExpanded", testPathExpanded, !true);

						if (testPath.isEmpty()) {
							// Highlight here.
							personRelationTree.getTree().setSelection(treeItem);
						}
						else
							treeItem.setExpanded(true);

						System.err.println();
					}
				}
			}
		});

		// ----------------------------- FARK-MARK ----------------------->>
	}


	// -------------------------------------------------------------------------------------------------- FARK-MARK ------>>
	protected boolean isMatching(Deque<? extends ObjectID> pathToRoot, Deque<? extends ObjectID> expandedPath, boolean isReversePathToRoot, boolean isReverseExpandedPath) {
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



	protected boolean isMatchingSubPath(Deque<? extends ObjectID> pathToRoot, Deque<? extends ObjectID> pathToExpand, boolean isReversePathToRoot) {
		if (pathToRoot.size() >= pathToExpand.size()-1)
			return false;

		Iterator<? extends ObjectID> iterToRoot = isReversePathToRoot ? pathToRoot.descendingIterator() : pathToRoot.iterator();
		Iterator<? extends ObjectID> iterToExpand = pathToExpand.iterator();
		while (iterToRoot.hasNext() && iterToExpand.hasNext()) {
			ObjectID oid_1 = iterToRoot.next();
			ObjectID oid_2 = iterToExpand.next();

			if (!oid_1.equals(oid_2))
				return false;
		}

		return true;
	}

	protected void showDequePaths(String preamble, Deque<? extends ObjectID> path, boolean isReversed) {
		System.err.print("++ " + preamble + " :: ");

		Iterator<? extends ObjectID> iter = isReversed ? path.descendingIterator() : path.iterator();
		while (iter.hasNext()) {
			String[] segID = iter.next().toString().split("&");
			System.err.print("[" + segID[1] + "]");
		}

		System.err.println();
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
	 * @return null if neither paramters are related to a Person object.
	 */
	private PropertySetID getPropertySetID(ObjectID jdoObjectID, Object jdoObject) {
		if (jdoObjectID != null && jdoObjectID instanceof PropertySetID)
			return (PropertySetID)jdoObjectID;

		if (jdoObject != null && jdoObject instanceof PersonRelation)
			return ((PersonRelation)jdoObject).getToID();

		return null;
	}

	// Quick screen debug-output.
	// Check out: getVisibleExpandedElements().
	private void showExpandedTreePaths() {
		TreePath[] treePaths = personRelationTree.getTreeViewer().getExpandedTreePaths();
		for (TreePath treePath : treePaths) {
			System.err.print(" -->> treePath[len:" + treePath.getSegmentCount() + "] :: ");
			for (int i=0; i<treePath.getSegmentCount(); i++) {
				PersonRelationTreeNode segment = (PersonRelationTreeNode) treePath.getSegment(i);
				String[] segID = segment.getJdoObjectID().toString().split("&");
				System.err.print("[ID:" + segID[1] + "," + segment.getChildNodeCount() + "]");
			}

			System.err.println();
		}
		System.err.println("\n");
	}
	// -------------------------------------------------------------------------------------------------- FARK-MARK ------>>


	private NotificationListener notificationListenerLegalEntitySelected = new NotificationAdapterJob(Messages.getString("org.nightlabs.jfire.personrelation.issuetracking.trade.ui.PersonRelationIssueTreeView.selectLegalEntityJob.title")) //$NON-NLS-1$
	{
		public void notify(org.nightlabs.notification.NotificationEvent notificationEvent) {
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

			final PropertySetID personID = (PropertySetID) (legalEntity == null ? null : JDOHelper.getObjectId(legalEntity.getPerson()));

			final Set<Deque<PropertySetID>> rootToSourcePaths;
			if (personID == null)
				rootToSourcePaths = Collections.emptySet();
			else
				rootToSourcePaths = PersonRelationDAO.sharedInstance().getRelationRoots(getAllowedPersonRelationTypes(), personID,
						getMaxSearchDepth(), getProgressMonitor());

			final Set<PropertySetID> rootNodes = new HashSet<PropertySetID>();
			for (Deque<PropertySetID> path : rootToSourcePaths)
			{
				rootNodes.add(path.peek());
			}

			personRelationTree.getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (personRelationTree.isDisposed())
						return;

					personRelationTree.setInputPersonIDs(rootNodes, personID);

					// TODO: expand the tree to show the rootToSourcePaths. (marius)
				}
			});
		}
	};

	public PersonRelationTree getPersonRelationTree() {
		return personRelationTree;
	}

	protected Set<PersonRelationTypeID> allowedRelationTypes;
	protected Set<PersonRelationTypeID> getAllowedPersonRelationTypes()
	{
		if (allowedRelationTypes == null)
		{
			allowedRelationTypes = new HashSet<PersonRelationTypeID>();
			allowedRelationTypes.add(PersonRelationType.PredefinedRelationTypes.branchOffice);
			allowedRelationTypes.add(PersonRelationType.PredefinedRelationTypes.subsidiary);
			allowedRelationTypes.add(PersonRelationType.PredefinedRelationTypes.employed);
		}
		return allowedRelationTypes;
	}

	protected int getMaxSearchDepth()
	{
		return DefaultMaximumSearchDepth;
	}
}
