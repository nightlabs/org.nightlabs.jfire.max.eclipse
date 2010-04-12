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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
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
import org.nightlabs.jfire.personrelation.trade.ui.compact.CompactedPersonRelationTree;
import org.nightlabs.jfire.personrelation.trade.ui.compact.CompactedPersonRelationTreeController;
import org.nightlabs.jfire.personrelation.trade.ui.compact.CompactedPersonRelationTreeNode;
import org.nightlabs.jfire.personrelation.trade.ui.compact.CompactedPropertySetTreeLabelProviderDelegate;
import org.nightlabs.jfire.personrelation.ui.AbstractPersonRelationTreeView;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeController;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeUtil;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;

/**
 * The view in which we deploy our concepts of the {@link CompactedPersonRelationTreeNode}s.
 *
 * See notes on "Search by Association".
 *
 * @author khaireel at nightlabs dot de
 */
public class CompactedPersonRelationTreeView extends AbstractPersonRelationTreeView<CompactedPersonRelationTreeNode, CompactedPersonRelationTree> {
	public static final String ID_VIEW = CompactedPersonRelationTreeView.class.getName();
	
	private static final Logger logger = Logger.getLogger(CompactedPersonRelationTreeView.class);
	private static final int DEFAULT_MAX_SEARCH_DEPTH = 100;
	
	
	/**
	 * Creates a new instance of the PersonRelationTree, and initialises it as completely as possible; i.e. supply the delegates it requires.
	 */
	@Override
	protected CompactedPersonRelationTree createAndInitPersonRelationTree(Composite parent) {
		CompactedPersonRelationTree personRelationTree = new CompactedPersonRelationTree(parent,
				false, // without restoring the tree's collapse state
				false  // without the drill-down adapter.
			);
		
		// Require additional fetch group(s) when dealing with the label providers related to the tree.
		Object[] fetchGroupPersonRelation = ArrayUtils.addAll(PersonRelationTreeController.FETCH_GROUPS_PERSON_RELATION, new String[] {Person.FETCH_GROUP_FULL_DATA} );
		PersonRelationTreeController<CompactedPersonRelationTreeNode> personRelationTreeController = personRelationTree.getPersonRelationTreeController();
		personRelationTreeController.setPersonRelationFetchGroups((String[]) fetchGroupPersonRelation);
		
		// Special delegate(s).
		CompactedPersonRelationTreeController cprtController = (CompactedPersonRelationTreeController) personRelationTreeController;
		personRelationTree.addPersonRelationTreeLabelProviderDelegate(new CompactedPropertySetTreeLabelProviderDelegate(cprtController));
		
		return personRelationTree;
	}

	
	
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// [Section X] Handling context menus deployed into the PeronsRelationTree.
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	@Override
	protected void registerContextMenuContibutions(CompactedPersonRelationTree personRelationTree) {
		personRelationTree.addContextMenuContribution(new SelectBusinessPartnerTreeItemAction("Focus trade on this business partner"));
	}
	

	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// [Section X] Handling notifications and preparing the listeners, with respect to the currently selected LegalEntity.
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// The currentPersonID that has focus.
	protected PropertySetID currentPersonID = null;

	@Override
	protected NotificationListener createAndRegisterNotificationListenerLegalEntitySelected(CompactedPersonRelationTree personRelationTree) {
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
						logger.debug("---> personID:" + PersonRelationTreeUtil.showObjectID(personID) + ",  currentPersonID:" + PersonRelationTreeUtil.showObjectID(currentPersonID));

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

						CompactedPersonRelationTreeController treeController = (CompactedPersonRelationTreeController) getPersonRelationTree().getPersonRelationTreeController();
						final Set<PropertySetID> rootIDs = treeController.setTuckedPaths(relatablePathsToRoots);

						if (logger.isDebugEnabled())
							logger.debug(PersonRelationTreeUtil.showObjectIDs("---> rootIDs:", rootIDs, 10));
						
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
	 * Initialises all other listeners that the {@link CompactedPersonRelationTree} requires for its fundamental operational behaviour.
	 */
	@Override
	protected void initPersonRelationTreeListeners(CompactedPersonRelationTree personRelationTree) {
		personRelationTree.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty())
					return;
				
				ISelection selectedElement = event.getSelection();
				if (selectedElement != null && selectedElement instanceof TreeSelection) {
					Object treeElem = ((TreeSelection) selectedElement).getFirstElement();
					if (treeElem != null && treeElem instanceof CompactedPersonRelationTreeNode) {
						CompactedPersonRelationTreeNode node = (CompactedPersonRelationTreeNode) treeElem;
						
						if (logger.isDebugEnabled()) {
							String str = "\n" + PersonRelationTreeUtil.showObjectIDs("PS-IDs to root", node.getPropertySetIDsToRoot(), 10);
							str += "\n" + PersonRelationTreeUtil.showObjectIDs("PR-IDs to root", node.getJDOObjectIDsToRoot(), 10);
							logger.debug(str);
							logger.debug(node.toDebugString());
						}
					}
				}
				
			}
		});
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


	
	
	
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// [Section X] Handling the recognised PersonRelationTypeIDs.
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// This handles the system of listeners needed to engage the Lazy-Tree-nodes to perform the
	// behaviour that automatically expands a given set of hiearchical paths, whenever available.
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
}
