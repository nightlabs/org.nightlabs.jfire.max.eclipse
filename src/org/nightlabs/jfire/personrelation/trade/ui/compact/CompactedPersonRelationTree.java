package org.nightlabs.jfire.personrelation.trade.ui.compact;

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
 * An extended {@link PersonRelationTree}, specifically containing {@link CompactedPersonRelationTreeNode}s.
 *
 * @author khaireel at nightlabs dot de
 */
public class CompactedPersonRelationTree extends PersonRelationTree<CompactedPersonRelationTreeNode> {
	/**
	 * Creates a new instance of the CompactedPersonRelationTree.
	 */
	public CompactedPersonRelationTree(Composite parent, boolean isRestoreCollapseState, boolean isMenuWithDrillDownAdapter) {
		super(parent, isRestoreCollapseState, false, isMenuWithDrillDownAdapter); // By default, we shall have to by-pass the superclass's context menu management.
	}
	
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
