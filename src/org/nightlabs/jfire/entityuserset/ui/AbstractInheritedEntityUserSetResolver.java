package org.nightlabs.jfire.entityuserset.ui;


/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public abstract class AbstractInheritedEntityUserSetResolver<Entity> 
implements InheritedEntityUserSetResolver<Entity> 
{
	private boolean entityUserSetInherited;
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.entityuserset.ui.InheritedEntityUserSetResolver#isEntityUserSetInherited()
	 */
	@Override
	public boolean isEntityUserSetInherited() {
		return entityUserSetInherited;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.entityuserset.ui.InheritedEntityUserSetResolver#setEntityUserSetInherited(boolean)
	 */
	@Override
	public void setEntityUserSetInherited(boolean inherited) {
		this.entityUserSetInherited = inherited;
	}

}
