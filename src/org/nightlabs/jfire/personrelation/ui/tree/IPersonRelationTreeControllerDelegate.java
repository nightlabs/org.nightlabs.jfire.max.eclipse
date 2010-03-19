package org.nightlabs.jfire.personrelation.ui.tree;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.jdo.notification.TreeNodeMultiParentResolver;
import org.nightlabs.progress.ProgressMonitor;

/**
 * A delegate allowing to populate additional tree nodes into the {@link PersonRelationTree}.
 * <p>
 * It's recommended to subclass {@link AbstractPersonRelationTreeControllerDelegate} instead
 * of directly implementing this interface.
 * </p>
 *
 * @author marco schulze - marco at nightlabs dot de
 */
public interface IPersonRelationTreeControllerDelegate
{
	Map<ObjectID, Long> retrieveChildCount(Set<ObjectID> parentIDs, ProgressMonitor monitor);
	Collection<?> retrieveJDOObjects(Set<ObjectID> objectIDs, ProgressMonitor monitor);
	Collection<? extends ObjectID> retrieveChildObjectIDs(ObjectID parentID, ProgressMonitor monitor);
	Collection<Class<? extends Object>> getJDOObjectClasses();
	TreeNodeMultiParentResolver getPersonRelationParentResolverDelegate();
}
