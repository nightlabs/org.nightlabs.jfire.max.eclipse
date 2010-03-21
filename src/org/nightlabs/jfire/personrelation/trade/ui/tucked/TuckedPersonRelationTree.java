package org.nightlabs.jfire.personrelation.trade.ui.tucked;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedEventHandler;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTree;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeController;

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
	/**
	 * Creates a new instance of the TuckedPersonRelationTree.
	 */
	public TuckedPersonRelationTree(Composite parent, boolean isRestoreCollapseState, boolean isCreateContextMenu, boolean isMenuWithDrillDownAdapter) {
		super(parent, isRestoreCollapseState, isCreateContextMenu, isMenuWithDrillDownAdapter);
	}

	@Override
	protected PersonRelationTreeController<TuckedPersonRelationTreeNode> createPersonRelationTreeController() {
		return new TuckedPersonRelationTreeController() {
			@Override
			protected void onJDOObjectsChanged(JDOLazyTreeNodesChangedEvent<ObjectID, TuckedPersonRelationTreeNode> changedEvent) {
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
