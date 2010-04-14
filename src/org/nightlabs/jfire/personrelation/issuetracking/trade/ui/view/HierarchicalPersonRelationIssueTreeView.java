package org.nightlabs.jfire.personrelation.issuetracking.trade.ui.view;

import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.personrelation.PersonRelationType;
import org.nightlabs.jfire.personrelation.dao.PersonRelationDAO;
import org.nightlabs.jfire.personrelation.id.PersonRelationTypeID;
import org.nightlabs.jfire.personrelation.issuetracking.trade.ui.extended.PersonRelationTreeLabelProviderDelegate;
import org.nightlabs.jfire.personrelation.issuetracking.trade.ui.extended.PropertySetTreeLabelProviderDelegate;
import org.nightlabs.jfire.personrelation.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.personrelation.ui.tree.NodalHierarchyHandler;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTree;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeController;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeNode;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeUtil;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.ui.TradePerspective;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationListener;

/**
 * This serves as a platform from which the original conceptions of the {@link PersonRelationIssueTreeView} (created to serve JFire's default behaviours),
 * is extended (and to a certain extent, consolidated), from our experiences in developing BEHR's specific tree. This particular extension is
 * also used as a sandbox to test the new ideas for our "search-by-association" specifications.
 *
 * This View is made available, but is not the default on display in the {@link TradePerspective}.
 *
 * @author khaireel (at) nightlabs (dot) de
 */
public class HierarchicalPersonRelationIssueTreeView extends PersonRelationIssueTreeView {
	public static final String ID_VIEW = HierarchicalPersonRelationIssueTreeView.class.getName();
	private static final Logger logger = Logger.getLogger(HierarchicalPersonRelationIssueTreeView.class);
	private static final int DEFAULT_MAX_SEARCH_DEPTH = 10;

	// This handles the system of listeners needed to engage the Lazy-Tree-nodes to perform the
	// behaviour that automatically expands a given set of hiearchical paths, whenever available.
	protected NodalHierarchyHandler<PersonRelationTreeNode, PersonRelationTree<PersonRelationTreeNode>> nodalHierachyHandler = null;
	protected Set<PersonRelationTypeID> allowedRelationTypeIDs;



	@Override
	public void createPartContents(Composite parent) {
		super.createPartContents(parent);

		// Initialise a system of listeners for the personRelationTree that will expand it accordingly.
		// This is expected to be cleaner, more efficient, extendable, and more debug-friendly.
		nodalHierachyHandler = new NodalHierarchyHandler<PersonRelationTreeNode, PersonRelationTree<PersonRelationTreeNode>>(getPersonRelationTree());
	}

	@Override
	protected PersonRelationTree<PersonRelationTreeNode> createAndInitPersonRelationTree(Composite parent) {
		PersonRelationTree<PersonRelationTreeNode> personRelationTree =  super.createAndInitPersonRelationTree(parent);

		Object[] fetchGroupPersonRelation = ArrayUtils.addAll(PersonRelationTreeController.FETCH_GROUPS_PERSON_RELATION, new String[] {Person.FETCH_GROUP_DATA_FIELDS} );
		PersonRelationTreeController<PersonRelationTreeNode> personRelationTreeController = personRelationTree.getPersonRelationTreeController();
		personRelationTreeController.setPersonRelationFetchGroups((String[]) fetchGroupPersonRelation);

		// Delegate specialised label providers for this hierarchical view.
		personRelationTree.addPersonRelationTreeLabelProviderDelegate(new PropertySetTreeLabelProviderDelegate(personRelationTreeController));
		personRelationTree.addPersonRelationTreeLabelProviderDelegate(new PersonRelationTreeLabelProviderDelegate());

		return personRelationTree;
	}

	@Override
	protected NotificationListener createAndRegisterNotificationListenerLegalEntitySelected(PersonRelationTree<PersonRelationTreeNode> personRelationTree) {
		final NotificationListener notificationListener =  new NotificationAdapterJob(Messages.getString("org.nightlabs.jfire.personrelation.issuetracking.trade.ui.PersonRelationIssueTreeView.selectLegalEntityJob.title")) //$NON-NLS-1$
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
						logger.debug("personID:" + PersonRelationTreeUtil.showObjectID(personID) + ",  currentPersonID:" + PersonRelationTreeUtil.showObjectID(currentPersonID));
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
						Map<Class<? extends ObjectID>, List<Deque<ObjectID>>> relatablePathsToRoots = PersonRelationDAO.sharedInstance().getRelationRootNodes(
								getAllowedPersonRelationTypes(), personID, DEFAULT_MAX_SEARCH_DEPTH, getProgressMonitor());

						final Set<PropertySetID> rootIDs = nodalHierachyHandler.initRelatablePathsToRoots(relatablePathsToRoots);

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
