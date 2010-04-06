package org.nightlabs.jfire.personrelation.trade.ui.compact;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTree;

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
}
