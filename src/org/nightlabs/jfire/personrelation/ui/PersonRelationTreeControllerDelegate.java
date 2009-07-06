package org.nightlabs.jfire.personrelation.ui;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.progress.ProgressMonitor;

public interface PersonRelationTreeControllerDelegate
{
	Map<ObjectID, Long> retrieveChildCount(Set<ObjectID> parentIDs, ProgressMonitor monitor);
	Collection<Object> retrieveJDOObjects(Set<ObjectID> objectIDs, ProgressMonitor monitor);
	Collection<ObjectID> retrieveChildObjectIDs(ObjectID parentID, ProgressMonitor monitor);
}
