package org.nightlabs.jfire.personrelation.ui;

import java.util.Collection;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOObjectLazyTreeNode;

/**
 * Given a proper JDOObjectLazyTreeNode, apply a filter-rule control, which shall then
 * determine whether or not to continue loading the node's children.
 *
 * @author khaireel
 */
public interface NodeFilterControlRule <N extends JDOObjectLazyTreeNode<ObjectID, Object, ?>> {
	/**
	 * Determine the criteria here whether or not to load a given node's children.
	 */
	public boolean isContinueToLoadChildren();

	/**
	 * @return only the children of the given node that fulfill the filter rule.
	 */
	public Collection<N> getFilteredChildren(N node);
}
