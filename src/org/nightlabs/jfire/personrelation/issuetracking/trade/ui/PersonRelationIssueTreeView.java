package org.nightlabs.jfire.personrelation.issuetracking.trade.ui;

import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
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
import org.nightlabs.jfire.personrelation.dao.PersonRelationTypeDAO;
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
	protected static final int	DefaultMaximumSearchDepth	= 10;
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
								// One-time setup.
								if (name2PersRelnTypeID == null) {
									List<PersonRelationType> persRelTypes = PersonRelationTypeDAO.sharedInstance().getPersonRelationTypes(
											new String[] {FetchPlan.DEFAULT, PersonRelationType.FETCH_GROUP_NAME },
											NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

									name2PersRelnTypeID = new HashMap<String, PersonRelationTypeID>();
									for (PersonRelationType prt : persRelTypes)
										name2PersRelnTypeID.put(prt.getPersonRelationTypeID(), PersonRelationTypeID.create(prt.getOrganisationID(), prt.getPersonRelationTypeID()));
								}


								// If a Person-Trading-Partner has been selected; see notes.
								// Otherwise, the selected 'organisation' becomes the new root.
								if (personRelnType.getPersonRelationTypeID().equals("employing")) {
									// TODO Call the server method here; get the new roots; and update the tree.
								}
								else {
									personRelationTree.getDisplay().asyncExec(new Runnable() {
										public void run() {
											if (!personRelationTree.isDisposed())
												personRelationTree.setInputPersonIDs(Collections.singleton(personID));
										}
									});
								}

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
			}
		});

		selectionProviderProxy.addRealSelectionProvider(personRelationTree);
	}


	// -------------------------------------------------------------------------------------------------- FARK-MARK ------>>
	private Map<String, PersonRelationTypeID> name2PersRelnTypeID = null; // Maintain a one-time reference to a working set of PersonRelationTypeID by their names.

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
