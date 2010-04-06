package org.nightlabs.jfire.personrelation.trade.ui;

import java.util.HashSet;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.security.auth.login.LoginException;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.personrelation.PersonRelationType;
import org.nightlabs.jfire.personrelation.id.PersonRelationTypeID;
import org.nightlabs.jfire.personrelation.trade.ui.compact.CompactedPersonRelationTree;
import org.nightlabs.jfire.personrelation.trade.ui.compact.CompactedPersonRelationTreeNode;
import org.nightlabs.jfire.personrelation.ui.AbstractPersonRelationTreeView;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManagerRemote;
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
	
	
	/**
	 * Creates a new instance of the PersonRelationTree, and initialises it as completely as possible; i.e. supply the delegates it requires.
	 */
	@Override
	protected CompactedPersonRelationTree createAndInitPersonRelationTree(Composite parent) {
		CompactedPersonRelationTree personRelationTree = new CompactedPersonRelationTree(parent,
				false, // without restoring the tree's collapse state
				false  // without the drill-down adapter.
			);
		
		return personRelationTree;
	}

	
	
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// [Section X] Handling context menus deployed into the PeronsRelationTree.
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	@Override
	protected void registerContextMenuContibutions(CompactedPersonRelationTree personRelationTree) {
	}
	

	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// [Section X] Handling notifications and preparing the listeners, with respect to the currently selected LegalEntity.
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// The currentPersonID that has focus.
	protected PropertySetID currentPersonID = null;

	@Override
	protected NotificationListener createAndRegisterNotificationListenerLegalEntitySelected(CompactedPersonRelationTree personRelationTree) {
		return null;
	}

	@Override
	protected void initPersonRelationTreeListeners(CompactedPersonRelationTree personRelationTree) {
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
