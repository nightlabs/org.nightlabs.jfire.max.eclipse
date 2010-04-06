package org.nightlabs.jfire.personrelation.trade.ui.compact;

import org.nightlabs.jfire.personrelation.trade.ui.tucked.TuckedPersonRelationTreeNode;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeNode;

/**
 * An extension of the original {@link PersonRelationTreeNode}, this particular {@link CompactedPersonRelationTreeNode} is 
 * an even more compacted version of our earlier {@link TuckedPersonRelationTreeNode}.
 * While a normal {@link TuckedPersonRelationTreeNode} holds 'local' tucked-information, the {@link CompactedPersonRelationTreeNode} holds
 * a more 'global' compacted-information, of entire tucked-paths in a single node. Its main deployment is intended for UIs where
 * vertical space is sparse.
 * 
 * 
 * Properties:
 * 1. The node, if not {@link CompactedNodeStatus}.NORMAL or {@link CompactedNodeStatus}.UNSET, contains the entire tucked-path
 *    it represents from its (standing) position in the tree.
 *     
 * 2. The children of this node are the children of the last element in the tucked-path.
 * 
 * 3. For each element in the tucked-path represented by this node, we maintain the same information as we have done
 *    with the {@link TuckedPersonRelationTreeNode}:
 *        I. actualChildCount;
 *       II. tuckedChildCount; and
 *      III. tuckedStatus;
 *      
 * 4. We continue to maintain a cache of loaded children (upon untucking/uncompacting, and then subsequent re-tucking/re-compacting)
 *    of the related last element in the tucked-path.
 * 
 *
 * @author khaireel at nightlabs dot de
 */
public class CompactedPersonRelationTreeNode extends PersonRelationTreeNode {
}
