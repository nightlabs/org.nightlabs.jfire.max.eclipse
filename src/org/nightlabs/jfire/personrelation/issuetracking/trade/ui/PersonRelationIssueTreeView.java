package org.nightlabs.jfire.personrelation.issuetracking.trade.ui;

import java.util.Collections;

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
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.issuetracking.trade.ui.extended.ExtendedPersonRelationIssueTreeView;
import org.nightlabs.jfire.personrelation.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.personrelation.ui.PersonRelationTree;
import org.nightlabs.jfire.personrelation.ui.PersonRelationTreeController;
import org.nightlabs.jfire.personrelation.ui.PersonRelationTreeNode;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;

/**
 * This is shall always stand to be JFire's original {@link PersonRelationIssueTreeView}, with all its original conceptual behaviours intact.
 * There is an extended version of this, the {@link ExtendedPersonRelationIssueTreeView} which serves as a platform used as a sandbox to test the new ideas
 * and consolidate our experiences from the development of BEHR's specific {@link PersonRelationTree}.
 *
 * Since 2010.03.17, we have restored the original behaviours of this {@link PersonRelationIssueTreeView} to the pre-BEHR era. Kai.
 * TODO In the jfire_1.0 branch, the original behaviours of the PersonRelationIssueTreeView was (unconstitutionally) changed when working on the BEHR project. Restore it too!
 *
 * @author Marco หงุ่ยตระกูล-Schulze - marco at nightlabs dot de
 * @author khaireel (at) nightlabs (dot) de
 */
public class PersonRelationIssueTreeView
extends LSDViewPart
{
	private static final Logger logger = Logger.getLogger(PersonRelationIssueTreeView.class);
	public static final String ID_VIEW = PersonRelationIssueTreeView.class.getName();

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
				true   // with the drill-down adapter.
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
					currentPersonID = personID;
					personRelationTree.getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (!personRelationTree.isDisposed())
								personRelationTree.setInputPersonIDs(Collections.singleton(currentPersonID));
						}
					});
				}
			}
		};
	}


	@Override
	public void createPartContents(Composite parent) {
		personRelationTree = createPersonRelationTree(parent);
		PersonRelationTreeController personRelationTreeController = personRelationTree.getPersonRelationTreeController();

		// Delegate controller and label-providers for handling Issues in the PersonRelationTree.
		personRelationTreeController.addPersonRelationTreeControllerDelegate(new IssuePersonRelationTreeControllerDelegate());
		personRelationTree.addPersonRelationTreeLabelProviderDelegate(new IssueLinkPersonRelationTreeLabelProviderDelegate());
		personRelationTree.addPersonRelationTreeLabelProviderDelegate(new IssueDescriptionPersonRelationTreeLabelProviderDelegate());
		personRelationTree.addPersonRelationTreeLabelProviderDelegate(new IssueCommentPersonRelationTreeLabelProviderDelegate());

		// Notifier registration.
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


		// Notifies other view(s) that may wish to react upon the current selection in the tree, in the TradePlugin.ZONE_SALE.
		personRelationTree.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty())
					return;

				Object selectedElement = personRelationTree.getFirstSelectedElement();
				if (selectedElement instanceof PersonRelationTreeNode) {
					PersonRelationTreeNode node = (PersonRelationTreeNode) selectedElement;
					// Debug.
					if (logger.isInfoEnabled() && node != null) {
						logger.info(PersonRelationTree.showObjectIDs("propertySetIDsToRoot", node.getPropertySetIDsToRoot(), 5)); //$NON-NLS-1$
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

		// Automatically expand the root node?
		personRelationTree.getTree().addListener(SWT.SetData, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (!(event.item instanceof TreeItem))
					return;

				TreeItem treeItem = (TreeItem) event.item;
				if (treeItem == null || !(treeItem.getData() instanceof PersonRelationTreeNode))
					return;

				PersonRelationTreeNode node = (PersonRelationTreeNode) treeItem.getData();
				if (node == null)
					return;

				PropertySetID propertySetID = node.getPropertySetID();
				if (propertySetID == null || !propertySetID.equals(currentPersonID))
					return;

				treeItem.setExpanded(true);
			}
		});

		selectionProviderProxy.addRealSelectionProvider(personRelationTree);
	}



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
	protected PersonRelationTreeNode traverseUpUntilPerson(PersonRelationTreeNode node) {
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

	public PersonRelationTree getPersonRelationTree() {
		return personRelationTree;
	}


	// -----------------------------------------------------------------------------------------------------------------------------------|
	//  Tree-view-specific Actions.
	// -----------------------------------------------------------------------------------------------------------------------------------|
	protected class SelectPersonRelationTreeItemAction extends Action implements IViewActionDelegate {
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

}
