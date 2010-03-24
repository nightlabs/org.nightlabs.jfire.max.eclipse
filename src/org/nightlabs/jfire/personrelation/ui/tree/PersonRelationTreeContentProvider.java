package org.nightlabs.jfire.personrelation.ui.tree;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOObjectLazyTreeContentProvider;

public class PersonRelationTreeContentProvider<N extends PersonRelationTreeNode>
extends JDOObjectLazyTreeContentProvider<ObjectID, Object, N> {
}
