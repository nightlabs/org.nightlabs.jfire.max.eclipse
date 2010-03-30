package org.nightlabs.jfire.personrelation.trade.ui.tucked;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedEventHandler;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTree;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeController;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeLabelProvider;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeNode;
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
		super(parent, isRestoreCollapseState, false, isMenuWithDrillDownAdapter); // By default, we shall have to by-pass the superclass's context menu management.
		setupDynamicContextMenu();
	}
	
	@Override
	protected PersonRelationTreeLabelProvider<TuckedPersonRelationTreeNode> createPersonRelationTreeLabelProvider(TreeViewer treeViewer) {
		return new TuckedPersonRelationTreeLabelProvider(treeViewer, getDisplay());
	}
	
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	// TESTs... for dynamic context-menus.
	// -------------------------------------------------------------------------------------------------- ++ ------>>
	private MenuManager menuMgr = null;
	
	// Contents in the menu is directly dependent on the node.
	// Should take control away from the super-class's menu-manager.
	protected void setupDynamicContextMenu() {
		// We need to know about the currently selected item in the Tree, so as to adjust the menu contents.
		// But in this second approach, the only two possible menu items are: "Untuck" and "Tuck".
		// Initialise our own context-menu, for dynamic manipulations.
		menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				handleContextMenuPopulation(manager);
			}
		});
		
		Control control = getTreeViewer().getControl();
		Menu menu = menuMgr.createContextMenu(control);
		control.setMenu(menu);		
	}
	
	/**
	 * We decide what menu-items to be display here, depending on the currently selected Node.
	 * TODO Consider what to do when the currently selected node is not a TuckedNode.
	 */
	protected void handleContextMenuPopulation(IMenuManager manager) {
		TuckedPersonRelationTreeNode selectedNode = PersonRelationTreeNode.getPersonRelationTreeNodeFromSelection(getSelection());
		if (logger.isDebugEnabled())
			logger.debug(":: selectedNode: " + (selectedNode == null ? "null" : selectedNode.toDebugString()));

		if (selectedNode != null) {
			if (selectedNode.getTuckedStatus().equals(TuckedNodeStatus.TUCKED))
				manager.add(new NodeTuckUntuckAction("Untuck node " + PersonRelationTree.showObjectID(selectedNode.getPropertySetID()), TuckedNodeStatus.UNTUCKED));
			
			else if (selectedNode.getTuckedStatus().equals(TuckedNodeStatus.UNTUCKED))
				manager.add(new NodeTuckUntuckAction("Tuck node " + PersonRelationTree.showObjectID(selectedNode.getPropertySetID()), TuckedNodeStatus.TUCKED));
		}
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
