package org.nightlabs.jfire.personrelation.ui.tree;

import java.util.Collection;

import javax.jdo.JDOHelper;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.prop.id.PropertySetID;


public abstract class AbstractPersonRelationTreeControllerDelegate
implements IPersonRelationTreeControllerDelegate
{

	private volatile Collection<PropertySetID> rootPersonIDs;
	
	@Override
	public ObjectID getJDOObjectID(Object jdoObject) {
		return (ObjectID) JDOHelper.getObjectId(jdoObject);
	}
	
	@Override
	public boolean includeObjectIDForLifecycleListener(ObjectID objectID) {
		return true;
	}
	
	@Override
	public void setRootPersonIDs(Collection<PropertySetID> rootPersonIDs) {
		this.rootPersonIDs = rootPersonIDs;
	}
	
	protected Collection<PropertySetID> getRootPersonIDs() {
		return rootPersonIDs;
	}
}
