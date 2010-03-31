package org.nightlabs.jfire.personrelation.trade.ui;

import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.security.auth.login.LoginException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.personrelation.PersonRelationType;
import org.nightlabs.jfire.personrelation.dao.PersonRelationDAO;
import org.nightlabs.jfire.personrelation.id.PersonRelationTypeID;
import org.nightlabs.jfire.personrelation.trade.ui.tucked.TuckedPersonRelationTree;
import org.nightlabs.jfire.personrelation.trade.ui.tucked.TuckedPersonRelationTreeController;
import org.nightlabs.jfire.personrelation.trade.ui.tucked.TuckedPersonRelationTreeLabelProviderDelegate;
import org.nightlabs.jfire.personrelation.trade.ui.tucked.TuckedPersonRelationTreeNode;
import org.nightlabs.jfire.personrelation.trade.ui.tucked.TuckedPropertySetTreeLabelProviderDelegate;
import org.nightlabs.jfire.personrelation.ui.AbstractPersonRelationTreeView;
import org.nightlabs.jfire.personrelation.ui.tree.NodalHierarchyHandler;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTree;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeController;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;

/**
 * A slightly different portrayal of nodes in the {@link PersonRelationTree}; i.e. they are 'tucked' with the idea
 * of space conservation when search results returns a long list of hits.
 *
 * See notes on "Search by Association".
 *
 * @author khaireel (at) nightlabs (dot) de
 */
public class TuckedPersonRelationTreeView extends
AbstractPersonRelationTreeView<TuckedPersonRelationTreeNode, TuckedPersonRelationTree> {
	public static final String ID_VIEW = TuckedPersonRelationTreeView.class.getName();
	private static final Logger logger = Logger.getLogger(TuckedPersonRelationTreeView.class);
	private static final int DEFAULT_MAX_SEARCH_DEPTH = 10;

	// This handles the system of listeners needed to engage the Lazy-Tree-nodes to perform the
	// behaviour that automatically expands a given set of hiearchical paths, whenever available.
	protected NodalHierarchyHandler<TuckedPersonRelationTreeNode, TuckedPersonRelationTree> nodalHierachyHandler = null;
	protected Set<PersonRelationTypeID> allowedRelationTypeIDs;

	// The currentPersonID that has focus.
	protected PropertySetID currentPersonID = null;

	@Override
	public void createPartContents(Composite parent) {
		super.createPartContents(parent);

		// Initialise a system of listeners for the personRelationTree that will expand it accordingly.
		// This is expected to be cleaner, more efficient, extendable, and more debug-friendly.
		nodalHierachyHandler = new NodalHierarchyHandler<TuckedPersonRelationTreeNode, TuckedPersonRelationTree>(getPersonRelationTree());
	}


	/**
	 * Creates a new instance of the PersonRelationTree, and initialises it as completely as possible; i.e. supply the
	 * delegates it requires.
	 */
	@Override
	protected TuckedPersonRelationTree createAndInitPersonRelationTree(Composite parent) {
		TuckedPersonRelationTree personRelationTree = new TuckedPersonRelationTree(parent,
				false, // without restoring the tree's collapse state
				false  // without the drill-down adapter.
			);

		// Require additional fetch group(s) when dealing with the label providers related to the tree.
		Object[] fetchGroupPersonRelation = ArrayUtils.addAll(PersonRelationTreeController.FETCH_GROUPS_PERSON_RELATION, new String[] {Person.FETCH_GROUP_FULL_DATA} );
		PersonRelationTreeController<TuckedPersonRelationTreeNode> personRelationTreeController = personRelationTree.getPersonRelationTreeController();
		personRelationTreeController.setPersonRelationFetchGroups((String[]) fetchGroupPersonRelation);

		// Special delegate(s).
		personRelationTree.addPersonRelationTreeLabelProviderDelegate(new TuckedPropertySetTreeLabelProviderDelegate(personRelationTreeController));
		personRelationTree.addPersonRelationTreeLabelProviderDelegate(new TuckedPersonRelationTreeLabelProviderDelegate(personRelationTreeController));

		return personRelationTree;
	}

	/**
	 * Set up the ORDERED set of context-menus into the {@link TuckedPersonRelationTree}.
	 */
	@Override
	protected void registerContextMenuContibutions(TuckedPersonRelationTree personRelationTree) {
		personRelationTree.addContextMenuContribution(new SelectBusinessPartnerTreeItemAction("Focus trade on this business partner"));
	}

	/**
	 * Initialises all other listeners that the {@link TuckedPersonRelationTree} requires for its fundamental operational behaviour.
	 */
	@Override
	protected void initPersonRelationTreeListeners(TuckedPersonRelationTree personRelationTree) {
		// Notifies other view(s) that may wish to react upon the current selection in the tree, in the TradePlugin.ZONE_SALE.
		personRelationTree.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty())
					return;

				Object selectedElement = getPersonRelationTree().getFirstSelectedElement();
				if (selectedElement instanceof TuckedPersonRelationTreeNode) {
					TuckedPersonRelationTreeNode node = (TuckedPersonRelationTreeNode) selectedElement;
					if (logger.isInfoEnabled() && node != null) {
						logger.debug(":: Click: " + node.toDebugString());
						
						String str = "\n" + PersonRelationTree.showObjectIDs("PS-IDs to root", node.getPropertySetIDsToRoot(), 10);
						str += "\n" + PersonRelationTree.showObjectIDs("PR-IDs to root", node.getJDOObjectIDsToRoot(), 10);
						logger.info(str);
					}

					if (node != null) {
						PropertySetID personID = node.getPropertySetID();
						if (personID != null)
							SelectionManager.sharedInstance().notify(new NotificationEvent(this, TradePlugin.ZONE_SALE, personID, Person.class));
					}
				}
			}
		});
	}

	/**
	 * Creates a NotificationListener that defines the behaviour of this View with respect to whatever Perspective.
	 */
	@Override
	protected NotificationListener createAndRegisterNotificationListenerLegalEntitySelected(TuckedPersonRelationTree personRelationTree) {
		final NotificationListener notificationListener = new NotificationAdapterJob("Processing legal entity...")
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
					if (logger.isDebugEnabled())
						logger.debug("---> personID:" + PersonRelationTree.showObjectID(personID) + ",  currentPersonID:" + PersonRelationTree.showObjectID(currentPersonID));

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
						Map<Class<? extends ObjectID>, List<Deque<ObjectID>>> relatablePathsToRoots = PersonRelationDAO.sharedInstance().getRelationRootNodes(
								getAllowedPersonRelationTypes(), personID, DEFAULT_MAX_SEARCH_DEPTH, getProgressMonitor());

						((TuckedPersonRelationTreeController)getPersonRelationTree().getPersonRelationTreeController()).setTuckedPaths(relatablePathsToRoots);
						final Set<PropertySetID> rootIDs = nodalHierachyHandler.initRelatablePathsToRoots(relatablePathsToRoots);

						if (logger.isDebugEnabled())
							logger.debug(PersonRelationTree.showObjectIDs("---> rootIDs:", rootIDs, 10));
						
						// Done and ready. Update the tree.
						currentPersonID = personID;
						getPersonRelationTree().getDisplay().asyncExec(new Runnable() {
							public void run() {
								if (!getPersonRelationTree().isDisposed())
									getPersonRelationTree().setInputPersonIDs(rootIDs);
							}
						});
					} catch (Exception e) {
						e.printStackTrace(); // Failed to retrieve path!?? Something must have been null.
					}
				}
			}
		};

		SelectionManager.sharedInstance().addNotificationListener(
				TradePlugin.ZONE_SALE,
				LegalEntity.class, notificationListener
		);

		personRelationTree.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				SelectionManager.sharedInstance().removeNotificationListener(
						TradePlugin.ZONE_SALE,
						LegalEntity.class, notificationListener
				);
			}
		});

		return notificationListener;
	}

	/**
	 * Given a set of {@link PropertySetID}s, invoke the {@link TradeManagerRemote} to retrieve the respective
	 * {@link AnchorID}s, and subsequently triggers a {@link NotificationEvent} through the {@link TradePlugin}.ZONE_SALE (for example),
	 * by passing the {@link AnchorID}s in order of appearance from the input {@link PropertySetID}s.
	 */
	@Override
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
}
