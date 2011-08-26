package org.nightlabs.jfire.personrelation.ui.tree;

import javax.jdo.JDOHelper;

import org.nightlabs.jdo.ObjectID;


public abstract class AbstractPersonRelationTreeControllerDelegate
implements IPersonRelationTreeControllerDelegate
{

	@Override
	public ObjectID getJDOObjectID(Object jdoObject) {
		return (ObjectID) JDOHelper.getObjectId(jdoObject);
	}
	
	@Override
	public boolean includeObjectIDForLifecycleListener(ObjectID objectID) {
		return true;
	}
}
