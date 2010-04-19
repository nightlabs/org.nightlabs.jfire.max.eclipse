package org.nightlabs.jfire.personrelation.trade.ui.tucked;

import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeNode;

/**
 * This specifies the corresponding actions to handle the tuck-ing and untuck-ing of a {@link PersonRelationTreeNode} 
 *
 * @author khaireel at nightlabs dot de
 */
public interface ITuckActionHandler<N extends PersonRelationTreeNode> {
	/**
	 * Tucks the given node.
	 * @return the {@link TuckedNodeStatus} of the node after performing the tucking.
	 */
	public TuckedNodeStatus handleTuckAction(N node);

	/**
	 * Untucks the given node.
	 * @return the {@link TuckedNodeStatus} of the node after performing the untucking.
	 */
	public TuckedNodeStatus handleUnTuckAction(N node);
}
