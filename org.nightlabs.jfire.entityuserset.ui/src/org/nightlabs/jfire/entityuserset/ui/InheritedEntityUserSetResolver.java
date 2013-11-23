package org.nightlabs.jfire.entityuserset.ui;

import org.nightlabs.inheritance.FieldMetaData;
import org.nightlabs.inheritance.Inheritable;
import org.nightlabs.jfire.entityuserset.EntityUserSet;
import org.nightlabs.jfire.entityuserset.id.EntityUserSetID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public interface InheritedEntityUserSetResolver<Entity> 
{
	/**
	 * Returns the (inherited) {@link EntityUserSetID} from the parent object.
	 * 
	 * @param monitor the ProgressMonitor to display the progress
	 * @return the (inherited) EntityUserSetID from the parent object
	 */
	EntityUserSetID getInheritedEntityUserSetID(ProgressMonitor monitor);
	
	/**
	 * Determines whether the {@link EntityUserSet} is inherited or not.
	 * @return true if the {@link EntityUserSet} is inherited or false if not.
	 */
	boolean isEntityUserSetInherited();
	
	/**
	 * Determines whether the {@link EntityUserSet} is inherited or not.
	 * Implementations must then set the corresponding {@link FieldMetaData} in the {@link Inheritable} object
	 * which contains the managed {@link EntityUserSet} member.
	 * @param inherited 
	 */
	void setEntityUserSetInherited(boolean inherited);	
}
