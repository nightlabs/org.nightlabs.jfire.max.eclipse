package org.nightlabs.jfire.scripting.ui.tree;


import javax.jdo.JDOHelper;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.jdo.notification.TreeNodeParentResolver;
import org.nightlabs.jfire.scripting.Script;
import org.nightlabs.jfire.scripting.ScriptCategory;


/**
 * @author Fitas Amine - fitas [at] nightlabs [dot] de
 */
public class ScriptRegistryItemParentResolver implements TreeNodeParentResolver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.jdo.notification.TreeNodeParentResolver#getParentObjectID(java.lang.Object)
	 */
	public ObjectID getParentObjectID(Object jdoObject) {
		if(jdoObject instanceof ScriptCategory)	 	
			return  (ObjectID)JDOHelper.getObjectId(((ScriptCategory)jdoObject).getParent());
		else
			return  (ObjectID)JDOHelper.getObjectId(((Script)jdoObject).getParent());
	}
}
