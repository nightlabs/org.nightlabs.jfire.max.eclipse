package org.nightlabs.jfire.personrelation.issuetracking.trade.ui;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

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

public class PersonRelationIssueTreeView
extends LSDViewPart
{
	protected static final int DEFAULT_MAX_SEARCH_DEPTH = 10;
	private Map<Integer, Deque<PropertySetID>> pathsToExpand; // }--> See notes on Behr's specification with the PersonRelationTree. Kai.
	private Map<Integer, Deque<PropertySetID>> expandedPaths; // }

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
						// Join this with the entry-point-listener of this View, since the behaviour is exactly the same.
						// But we should be able to use the general one too?
						// ... So that we can trigger other views listening for the same event too.
						// ... But somehow, the 'subject' is not there?? Kai.
//						SelectionManager.sharedInstance().notify(new NotificationEvent(this, TradePlugin.ZONE_SALE, personID, LegalEntity.class));
						notificationListenerLegalEntitySelected.notify(new NotificationEvent(this, TradePlugin.ZONE_SALE, personID, LegalEntity.class));
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

		// Unravel the tree: Open it up to those paths we've discovered! Kai.
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
				if (!(treeItem.getData() instanceof PersonRelationTreeNode)) // || treeItem.getItemCount() <= 1)
					return;

				// Guard #3.
				PersonRelationTreeNode node = (PersonRelationTreeNode) treeItem.getData();
				PropertySetID node_propID = node.getPropertySetID();
				if (node_propID == null)
					return;


				// Fine-tuned, recursive version.
				Deque<PropertySetID> propertySetIDsToRoot = (LinkedList<PropertySetID>)node.getPropertySetIDsToRoot();
				boolean isNodeMarkedForExpansion = false;
				boolean isNodeMarkedForSelection = false;
				for (int index : pathsToExpand.keySet()) {
					Deque<PropertySetID> pathToExpand = pathsToExpand.get(index);
					Deque<PropertySetID> expandedPath = expandedPaths.get(index);

					if (!pathToExpand.isEmpty() && pathToExpand.peekFirst().equals(node_propID)) {
						boolean isMatch = isMatchingSubPath(propertySetIDsToRoot, expandedPath, true, true);
						if (isMatch || expandedPath.isEmpty()) {
							isNodeMarkedForExpansion |= isMatch;
							expandedPath.push( pathToExpand.pop() );

							isNodeMarkedForSelection |= pathToExpand.isEmpty();
						}
					}
				}


				// Reflect changes on the node, if any.
				if (isNodeMarkedForSelection)
					personRelationTree.getTree().setSelection(treeItem);
				else if (isNodeMarkedForExpansion)
					treeItem.setExpanded(true);


//				// Check to see the still non-expanded paths.
//				for(int index : pathsToExpand.keySet())
//					showDequePaths("   pathToExpand*[" + index + "]", pathsToExpand.get(index), !true);
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
			}
		});

		selectionProviderProxy.addRealSelectionProvider(personRelationTree);
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
		if (pathsToExpand != null && !pathsToExpand.isEmpty()) {
			for (Deque<PropertySetID> path : pathsToExpand.values())
				if (!path.isEmpty())
					return false;
		}

		return true;
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
	 */
	private PropertySetID getPropertySetID(ObjectID jdoObjectID, Object jdoObject) {
		if (jdoObjectID != null && jdoObjectID instanceof PropertySetID)
			return (PropertySetID)jdoObjectID;

		if (jdoObject != null && jdoObject instanceof PersonRelation)
			return ((PersonRelation)jdoObject).getToID();

		return null;
	}

	// -------------------------------------------------------------------------------------------------- FARK-MARK ------>>
	// For debugging...
	protected void showDequePaths(String preamble, Deque<? extends ObjectID> path, boolean isReversed) {
		System.err.print("++ " + preamble + " :: {");

		Iterator<? extends ObjectID> iter = isReversed ? path.descendingIterator() : path.iterator();
		while (iter.hasNext()) {
			String[] segID = iter.next().toString().split("&");
			System.err.print("[" + segID[1] + "]");
		}

		System.err.print("}\n");
	}

	protected void showExpandedTreePaths() {
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


			if (personID != null) {
				// Starting with the personID, we retrieve outgoing paths from it. Each path traces the personID's
				// relationship up through the hierachy of organisations, and terminates under one of the following
				// three conditions:
				//    1. When it reaches the mother of all subsidiary organisations (known as c^ \elemof C).
				//    2. When it detects a cyclic / nested-cyclic relationship between subsidiary-groups.
				//    3. When the length of the path reaches the preset DefaultMaximumSearchDepth.
				// For the sake of simplicity, let c^ be the terminal element in a path. Then all the unique c^'s
				// collated from the returned paths are the new roots for PersonRelationTree.
				// See original comments in revision #16575.
				Set<Deque<PropertySetID>> pathsToBeExpanded = PersonRelationDAO.sharedInstance().getRelationRoots(
						getAllowedPersonRelationTypes(), personID, DEFAULT_MAX_SEARCH_DEPTH, getProgressMonitor());

				// Initialise the path-expansion trackers.
				pathsToExpand = new HashMap<Integer, Deque<PropertySetID>>(pathsToBeExpanded.size());
				expandedPaths = new HashMap<Integer, Deque<PropertySetID>>(pathsToBeExpanded.size());

				// Prepare the new roots (the unique c^), and give references to the path-expansion trackers.
				final Set<PropertySetID> rootIDs = new HashSet<PropertySetID>();
				int index = 0;
				for (Deque<PropertySetID> path : pathsToBeExpanded) {
					rootIDs.add( path.peekFirst() );
					showDequePaths("   pathToExpand[" + index + "]", path, true);

					pathsToExpand.put(index, path);
					expandedPaths.put(index, new LinkedList<PropertySetID>());
					index++;
				}

				// Update the tree.
				personRelationTree.getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (!personRelationTree.isDisposed())
							personRelationTree.setInputPersonIDs(rootIDs);
					}
				});
			}
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
		return DEFAULT_MAX_SEARCH_DEPTH;
	}
}
