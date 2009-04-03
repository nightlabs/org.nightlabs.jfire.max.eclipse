package org.nightlabs.jfire.entityuserset.ui;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.entityuserset.id.EntityUserSetID;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class AssignEntityUserSetWizard extends DynamicPathWizard {

	public AssignEntityUserSetWizard() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		return false;
	}
 
	public EntityUserSetID getEntityUserSetID() {
		return null;
	}
}
