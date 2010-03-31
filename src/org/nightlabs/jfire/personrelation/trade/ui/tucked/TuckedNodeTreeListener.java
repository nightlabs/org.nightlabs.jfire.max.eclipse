package org.nightlabs.jfire.personrelation.trade.ui.tucked;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Operational-coordination: To ensure that expanded children of a collapsed node are restored,
 * when the collapsed node is re-expanded. This is not the same as 'restoring collapsed state' which has been addressed by the super class.
 * 
 * @author khaireel
 */
public class TuckedNodeTreeListener implements TreeListener {
	private static final Logger logger = Logger.getLogger(TuckedNodeTreeListener.class);
	
	@Override
	public void treeExpanded(TreeEvent event) {
		if (logger.isDebugEnabled()) {
			logger.debug(" ----------->>> @treeExpanded: event.item.class = " + event.item.getClass().getSimpleName());
			
			if (event.item instanceof TreeItem) {
				TreeItem treeItem = (TreeItem) event.item;
				Object itemData = treeItem.getData();
				
				if (itemData instanceof TuckedPersonRelationTreeNode) {
					TuckedPersonRelationTreeNode tuckedNode = (TuckedPersonRelationTreeNode) itemData;
					tuckedNode.toggleNodeExpandedState();
					treeItem.setExpanded(true);

					logger.debug(" :: event.item.getData() --> tuckedNode: " + tuckedNode.toDebugString());
					
					// We should not have the problem of accessing the child nodes we need, since at this point of handling the
					// event, all the necessary children would have already been previously loaded.
					TreeItem[] childItems = treeItem.getItems();
					logger.debug(" :: treeItem.childItems = " + (childItems == null ? "[null]" : childItems.length));
					
					// Recursively check out those children with !isCollapsed, and expand those nodes.
					restoreChildNodesCollapsedStates(childItems);
				}
			}
		}
	}
	
	// The recrusive method, via depth-first-search, to restore any children's collapsed state.
	private void restoreChildNodesCollapsedStates(TreeItem[] childItems) {
		// Base case I: Les guardian.
		if (childItems == null)
			return;
		
		// Search depth-first.
		for (TreeItem treeItem : childItems) {
			Object itemData = treeItem.getData();
			
			if (itemData instanceof TuckedPersonRelationTreeNode) {
				TuckedPersonRelationTreeNode tuckedNode = (TuckedPersonRelationTreeNode) itemData;
				
				if (logger.isDebugEnabled())
					logger.debug("## ### #### @restoreChildNodesCollapsedStates: tuckedNode " + tuckedNode.toDebugString());
				
				// Base case II: Expansion stops when isNodeCollapsed() is true.
				// Recursive case, continue expansion, and its children, if required.
				if (tuckedNode.isNodeExpanded()) {
					treeItem.setExpanded(true);
					if (logger.isDebugEnabled())
						logger.debug("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ~~~~~~~~~~~~~~~~~~ EXPANDED!");
						
					restoreChildNodesCollapsedStates(treeItem.getItems());
				}
				else if (logger.isDebugEnabled())
					logger.debug(" ************ WTF?? ***********");
					
			}					
		}
	}
	
	@Override
	public void treeCollapsed(TreeEvent event) {
		if (logger.isDebugEnabled()) {
			logger.debug(" <<<----------- @treeCollapsed: event.item.class = " + event.item.getClass().getSimpleName());
			
			if (event.item instanceof TreeItem) {
				TreeItem treeItem = (TreeItem) event.item;
				Object itemData = treeItem.getData();
				
				if (itemData instanceof TuckedPersonRelationTreeNode) {
					TuckedPersonRelationTreeNode tuckedNode = (TuckedPersonRelationTreeNode) itemData;
					tuckedNode.toggleNodeExpandedState();

					logger.debug(" :: event.item.getData() --> tuckedNode: " + tuckedNode.toDebugString());							
				}
			}
		}
	}
}
