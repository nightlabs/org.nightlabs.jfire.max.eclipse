package org.nightlabs.jfire.personrelation.trade.ui.tucked.compact;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedEventHandler;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.trade.ui.tucked.ITuckActionHandler;
import org.nightlabs.jfire.personrelation.trade.ui.tucked.TuckedNodeStatus;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTree;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeController;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeNode;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeUtil;

/**
 * An extended {@link PersonRelationTree}, specifically containing {@link CompactedPersonRelationTreeNode}s.
 *
 * @author khaireel at nightlabs dot de
 */
public class CompactedPersonRelationTree extends PersonRelationTree<CompactedPersonRelationTreeNode> 
implements ITuckActionHandler<CompactedPersonRelationTreeNode> {
	/**
	 * Creates a new instance of the CompactedPersonRelationTree.
	 */
	public CompactedPersonRelationTree(Composite parent, boolean isMenuWithDrillDownAdapter) {
		// By default, (i) we shall NOT restore the tree's collapsed state, but (ii) use the convenient context-menu management framework offered in the super class.
		super(parent, false, true, isMenuWithDrillDownAdapter);
	}
	
	
	
	// ----------------------------------------------------------------------------------- || -------------------------------------------->>
	// [Section] Dynamic context-menus that allows for tucking and untucking.
	// ----------------------------------------------------------------------------------- || -------------------------------------------->>
	@Override
	protected void fillContextMenu(IMenuManager manager) {
		// We decide what menu-items to be display here, depending on the currently selected Node.
		CompactedPersonRelationTreeNode selectedNode = PersonRelationTreeNode.getPersonRelationTreeNodeFromSelection(getSelection());

		// Fill the top part of the context-menu with TUCK/UNTUCK operations, but only when necessary.
		if (selectedNode != null && (!selectedNode.getNodeStatus().equals(TuckedNodeStatus.NORMAL) || !selectedNode.getNodeStatus().equals(TuckedNodeStatus.UNSET))) {
			CompactedPersonRelationTreeNode[] tuckedPathNodes = selectedNode.getTuckedPathNodes();
			boolean isAnyMenuItemAdded = false;
			for (CompactedPersonRelationTreeNode tuckedPathNode : tuckedPathNodes) {
				String nodeInfoStr = getNodeInfoForTuckMenuItem(tuckedPathNode);
				TuckedNodeStatus tuckedNodeStatus = tuckedPathNode.getNodeStatus();
				
				if (tuckedNodeStatus.equals(TuckedNodeStatus.TUCKED))
					manager.add(new NodeTuckUntuckAction("Untuck node: " + nodeInfoStr, false, this));
				else if (tuckedNodeStatus.equals(TuckedNodeStatus.UNTUCKED))
					manager.add(new NodeTuckUntuckAction("Tuck node: " + nodeInfoStr, true, this));
				
				if (!isAnyMenuItemAdded)
					isAnyMenuItemAdded = tuckedNodeStatus.equals(TuckedNodeStatus.TUCKED) || tuckedNodeStatus.equals(TuckedNodeStatus.UNTUCKED);
			}
			
			// Put a separator, to indicate a difference from the default items.
			if (isAnyMenuItemAdded)
				manager.add(new Separator());
		}
		
		// Then fill up the rest of the menu-items with whatever has been registered in the super class.
		super.fillContextMenu(manager);
	}
	
	/**
	 * @return a more user-friendly string for displaying in the menu-item.
	 */
	private String getNodeInfoForTuckMenuItem(CompactedPersonRelationTreeNode node) {
		String str = "";
		Object jdoObject = node.getJdoObject();
		if (jdoObject != null) {
			if (jdoObject instanceof Person)
				str = ((Person) jdoObject).getDisplayName();
			else if (jdoObject instanceof PersonRelation) {
				PersonRelation personRelation = (PersonRelation) jdoObject;
				str = personRelation.getTo().getDisplayName();
			}
		}
		
		if (str == "")
			str = PersonRelationTreeUtil.showObjectID(node.getPropertySetID());
		
		return str;
	}
	
	@Override
	public TuckedNodeStatus handleTuckAction(CompactedPersonRelationTreeNode node) {
		return null;
	}

	@Override
	public TuckedNodeStatus handleUnTuckAction(CompactedPersonRelationTreeNode node) {
		return null;
	}
	
	
	/**
	 * An {@link Action} to be placed as a dynamic-menu-item that allows for tucking or untucking a valid {@link CompactedPersonRelationTreeNode}. 
	 */
	protected class NodeTuckUntuckAction extends Action {
		private ITuckActionHandler<CompactedPersonRelationTreeNode> tuckNodeActionHandler;
		private boolean isTuckAction = true; // True suggests this to be a TUCKing action. False suggests this to be an UN-TUCKing action.
		
		public NodeTuckUntuckAction(String menuText, boolean isTuckAction, ITuckActionHandler<CompactedPersonRelationTreeNode> tuckNodeActionHandler) {
			setId(NodeTuckUntuckAction.class.getName());
			setText(menuText);
			
			this.tuckNodeActionHandler = tuckNodeActionHandler;
			this.isTuckAction = isTuckAction;
		}
		
		@Override
		public void run() {
			CompactedPersonRelationTreeNode selectedNode = PersonRelationTreeNode.getPersonRelationTreeNodeFromSelection(getSelection());
			if (isTuckAction)
				tuckNodeActionHandler.handleTuckAction(selectedNode);
			else 
				tuckNodeActionHandler.handleUnTuckAction(selectedNode);
			
			// TODO More to come...
		}
	}
	
	// ----------------------------------------------------------------------------------- || -------------------------------------------->>
	// [Section] Other related initialisers.
	// ----------------------------------------------------------------------------------- || -------------------------------------------->>	
	@Override
	protected PersonRelationTreeController<CompactedPersonRelationTreeNode> createPersonRelationTreeController() {
		return new CompactedPersonRelationTreeController() {
			@Override
			protected void onJDOObjectsChanged(JDOLazyTreeNodesChangedEvent<ObjectID, CompactedPersonRelationTreeNode> changedEvent) {
				JDOLazyTreeNodesChangedEventHandler.handle(getTreeViewer(), changedEvent);
			}
		};
	}

	@Override
	public void createTreeColumns(Tree tree) {
		TableLayout tableLayout = new TableLayout();

		@SuppressWarnings("unused")
		TreeColumn column = new TreeColumn(tree, SWT.LEFT);
		tableLayout.addColumnData(new ColumnWeightData(1));
		tree.setLayout(tableLayout);
		tree.setHeaderVisible(false);
	}
}
