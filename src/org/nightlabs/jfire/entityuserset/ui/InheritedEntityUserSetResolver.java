package org.nightlabs.jfire.entityuserset.ui;

import org.nightlabs.jfire.entityuserset.id.EntityUserSetID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public interface InheritedEntityUserSetResolver 
{
	EntityUserSetID getInheritedEntityUserSetID(ProgressMonitor monitor);
	
	boolean isEntityUserSetInherited();
	
	void setEntityUserSetInherited(boolean inherited);
}
