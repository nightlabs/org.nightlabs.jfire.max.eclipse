package org.nightlabs.jfire.personrelation.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.nightlabs.base.ui.selection.SelectionProviderProxy;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTree;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeNode;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;

/**
 * An abstract {@link LSDViewPart} containing the factorised elements necessary to effect a clear and concise
 * View when used in a Perspective. The factorised elements are (so far, from our experiences with the PersonRelationIssueTreeView, BEHR's-TreeView, and the HierarchicalPersonRelationIssueTreeView)
 * as follows:
 *     (i) the genericised {@link PersonRelationTree},
 *    (ii) the contribution method for the priority-ordered context-menus,
 *   (iii) the {@link NotificationListener},
 *
 * @author khaireel (at) nightlabs (dot) de
 */
public abstract class AbstractPersonRelationTreeView<PRT extends PersonRelationTree> extends LSDViewPart {
	private SelectionProviderProxy selectionProviderProxy = new SelectionProviderProxy();
	private PRT personRelationTree = null;

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		getSite().setSelectionProvider(selectionProviderProxy);
	}

	/**
	 * Creates a new instance of the PersonRelationTree, and initialises it as completely as possible; i.e. supply the
	 * delegates it requires.
	 */
	protected abstract PRT createAndInitPersonRelationTree(Composite parent);

	/**
	 * Set up the ORDERED set of context-menus into the {@link PersonRelationTree}.
	 */
	protected abstract void registerContextMenuContibutions(PRT personRelationTree);

	/**
	 * Initialises all other listeners that the {@link PersonRelationTree} requires for its fundamental operational behaviour.
	 */
	protected abstract void initPersonRelationTreeListeners(PRT personRelationTree);

	/**
	 * Creates a NotificationListener that defines the behaviour of this View with respect to whatever Perspective.
	 */
	protected abstract NotificationListener createAndRegisterNotificationListenerLegalEntitySelected(PRT personRelationTree);

	/**
	 * Given a set of {@link PropertySetID}s, invoke the {@link TradeManagerRemote} to retrieve the respective
	 * {@link AnchorID}s, and subsequently triggers a {@link NotificationEvent} through the {@link TradePlugin}.ZONE_SALE (for example),
	 * by passing the {@link AnchorID}s in order of appearance from the input {@link PropertySetID}s.
	 */
	protected abstract void prepareAndHandleNotification(PropertySetID... propertySetIDs);



	@Override
	public void createPartContents(Composite parent) {
		// Create and initialise the PersonRelationTree.
		personRelationTree = createAndInitPersonRelationTree(parent);

		// Register PRIORITY-ORDERED context-menus.
		registerContextMenuContibutions(personRelationTree);
		personRelationTree.integratePriorityOrderedContextMenu();

		// Initialise all other operational listeners for the PersonRelationTree.
		initPersonRelationTreeListeners(personRelationTree);
		createAndRegisterNotificationListenerLegalEntitySelected(personRelationTree);

		// And finally...
		selectionProviderProxy.addRealSelectionProvider(personRelationTree);
	}

	/**
	 * @return the {@link PersonRelationTree} used by this View.
	 */
	public PRT getPersonRelationTree() {
		return personRelationTree;
	}


	// -----------------------------------------------------------------------------------------------------------------------------------|
	//  Tree-view-specific Actions.
	// -----------------------------------------------------------------------------------------------------------------------------------|
	/**
	 * The default {@link Action} when selecting a node containing a valid {@link LegalEntity} who is to be treated as the business partner.
	 * Upon the selection of a node with a valid {@link PropertySetID}, the run() method triggers abstract method prepareAndHandleNotification().
	 */
	protected class SelectBusinessPartnerTreeItemAction extends Action implements IViewActionDelegate {
		private PropertySetID selectedPersonID = null;

		public SelectBusinessPartnerTreeItemAction(String actionText) {
			setId(SelectBusinessPartnerTreeItemAction.class.getName());
			setText(actionText);
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
