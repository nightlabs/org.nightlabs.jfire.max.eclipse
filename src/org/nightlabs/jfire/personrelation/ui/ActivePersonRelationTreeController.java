package org.nightlabs.jfire.personrelation.ui;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.ActiveJDOObjectLazyTreeController;
import org.nightlabs.jfire.jdo.notification.TreeNodeParentResolver;
import org.nightlabs.progress.ProgressMonitor;

public class ActivePersonRelationTreeController
extends ActiveJDOObjectLazyTreeController<ObjectID, Object, PersonRelationTreeNode>
{
	@Override
	protected PersonRelationTreeNode createNode() {
		return new PersonRelationTreeNode();
	}

	@Override
	protected TreeNodeParentResolver createTreeNodeParentResolver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Class<?> getJDOObjectClass() {
		throw new UnsupportedOperationException("Due to other methods being overwritten, this method should never be called!");
	}

	@Override
	protected Map<ObjectID, Long> retrieveChildCount(Set<ObjectID> parentIDs, ProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection<ObjectID> retrieveChildObjectIDs(ObjectID parentID, ProgressMonitor monitor)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection<Object> retrieveJDOObjects(Set<ObjectID> objectIDs, ProgressMonitor monitor)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
