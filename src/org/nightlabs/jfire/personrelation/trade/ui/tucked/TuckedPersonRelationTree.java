package org.nightlabs.jfire.personrelation.trade.ui.tucked;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedEventHandler;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTree;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeController;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeLabelProvider;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeNode;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeUtil;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.util.CollectionUtil;

/**
 * The {@link PersonRelationTree} with compressed (or 'tucked') nodes.
 * Properties in this tree:
 *   (i) simplified single column,
 *
 * See notes on "Search by Association".
 *
 * @author khaireel (at) nightlabs (dot) de
 */
public class TuckedPersonRelationTree extends PersonRelationTree<TuckedPersonRelationTreeNode> {
	private static final Logger logger = Logger.getLogger(TuckedPersonRelationTree.class);
	
	/**
	 * Creates a new instance of the TuckedPersonRelationTree.
	 */
	public TuckedPersonRelationTree(Composite parent, boolean isRestoreCollapseState, boolean isMenuWithDrillDownAdapter) {
		super(parent, isRestoreCollapseState, true, isMenuWithDrillDownAdapter); // By default, we shall have to by-pass the superclass's context menu management.
		
//		super(parent, isRestoreCollapseState, false, isMenuWithDrillDownAdapter); // By default, we shall have to by-pass the superclass's context menu management.
//		setupDynamicContextMenu();
		
		// Operational-coordination: To ensure that expanded children of a collapsed node are restored,
		// when the collapsed node is re-expanded. This is not the same as 'restoring collapsed state' which has been addressed by the super class.
		getTree().addTreeListener(new TuckedNodeTreeListener());
	}
	
	@Override
	protected PersonRelationTreeLabelProvider<TuckedPersonRelationTreeNode> createPersonRelationTreeLabelProvider(TreeViewer treeViewer) {
		return new TuckedPersonRelationTreeLabelProvider(treeViewer, getDisplay());
	}
	
	
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	// Dynamic context-menus that allows for tucking and untucking.
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	@Override
	protected void fillContextMenu(IMenuManager manager) {
		// We decide what menu-items to be display here, depending on the currently selected Node.
		TuckedPersonRelationTreeNode selectedNode = PersonRelationTreeNode.getPersonRelationTreeNodeFromSelection(getSelection());
		if (logger.isDebugEnabled())
			logger.debug(":: selectedNode: " + (selectedNode == null ? "null" : selectedNode.toDebugString()));

		// Fill the top part of the context-menu with TUCK/UNTUCK operations, but only when necessary.
		if (selectedNode != null) {
			if (selectedNode.getTuckedStatus().equals(TuckedNodeStatus.TUCKED))
				manager.add(new NodeTuckUntuckAction("Untuck node " + PersonRelationTreeUtil.showObjectID(selectedNode.getPropertySetID()), TuckedNodeStatus.UNTUCKED));
			
			else if (selectedNode.getTuckedStatus().equals(TuckedNodeStatus.UNTUCKED))
				manager.add(new NodeTuckUntuckAction("Tuck node " + PersonRelationTreeUtil.showObjectID(selectedNode.getPropertySetID()), TuckedNodeStatus.TUCKED));
			
			
			// Put a separator, to indicate a difference from the default items.
			manager.add(new Separator());
		}
		
		// Then fill up the rest of the menu-items with whatever has been registered in the super class.
		super.fillContextMenu(manager);
	}
	
	
	// Two main menu behaviours:
	//   [1] Untuck; [2] Tuck
	protected class NodeTuckUntuckAction extends Action {
		private TuckedNodeStatus tuckUntuckAction = null;
		
		public NodeTuckUntuckAction(String menuText, TuckedNodeStatus tuckUntuckAction) {
			setId(NodeTuckUntuckAction.class.getName());
			setText(menuText);
			
			this.tuckUntuckAction = tuckUntuckAction;
		}
		
		@Override
		public void run() {
			final TuckedPersonRelationTreeNode selectedNode = PersonRelationTreeNode.getPersonRelationTreeNodeFromSelection(getSelection());
			if (selectedNode != null) {
				selectedNode.setStatusToChangeTo(tuckUntuckAction); //TuckedNodeStatus.UNTUCKED);
				
//				Job tuckUntuckJob = new Job("Processing tucked node...") {
//					@Override
//					protected IStatus run(ProgressMonitor monitor) throws Exception {
//						((TuckedPersonRelationTreeController) getPersonRelationTreeController()).fireTuckChangedEvent(
//								new JDOLazyTreeNodesChangedEvent<ObjectID, TuckedPersonRelationTreeNode>(this, CollectionUtil.createHashSet(selectedNode)), monitor);
//						
//						monitor.done();
//						return Status.OK_STATUS;
//					}
//				};
//				
//				tuckUntuckJob.setPriority(Job.SHORT);
//				tuckUntuckJob.schedule();
				
				((TuckedPersonRelationTreeController) getPersonRelationTreeController()).fireTuckChangedEvent(
						new JDOLazyTreeNodesChangedEvent<ObjectID, TuckedPersonRelationTreeNode>(this, CollectionUtil.createHashSet(selectedNode)), new NullProgressMonitor()); // FIXME ... the bloody monitor!
			}
			
		}
	}
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	

	@Override
	protected PersonRelationTreeController<TuckedPersonRelationTreeNode> createPersonRelationTreeController() {
		return new TuckedPersonRelationTreeController() {
			@Override
			protected void onJDOObjectsChanged(JDOLazyTreeNodesChangedEvent<ObjectID, TuckedPersonRelationTreeNode> changedEvent) {
				super.onJDOObjectsChanged(changedEvent);
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
