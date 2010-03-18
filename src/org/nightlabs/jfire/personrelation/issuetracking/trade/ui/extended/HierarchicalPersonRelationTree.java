package org.nightlabs.jfire.personrelation.issuetracking.trade.ui.extended;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.personrelation.ui.PersonRelationTree;

/**
 * A specialised {@link PersonRelationTree} to handle the {@link HierarchicalPersonRelationIssueTreeView}.
 *
 * @author khaireel (at) nightlabs (dot) de
 */
public class HierarchicalPersonRelationTree extends PersonRelationTree {
	/**
	 * Creates a new instance of the HierarchicalPersonRelationTree.
	 */
	public HierarchicalPersonRelationTree(Composite parent, boolean isRestoreCollapseState, boolean isCreateContextMenu, boolean isMenuWithDrillDownAdapter) {
		super(parent, isRestoreCollapseState, isCreateContextMenu, isMenuWithDrillDownAdapter);
	}

}
