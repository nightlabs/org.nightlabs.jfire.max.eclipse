package org.nightlabs.jfire.asterisk.ui.asteriskserver;

import org.eclipse.jface.action.IAction;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.base.ui.login.action.LSDWorkbenchWindowActionDelegate;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class CreateAsteriskServerAction 
extends LSDWorkbenchWindowActionDelegate 
{
	@Override
	public void run(IAction action) {
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(getShell(), new CreateAsteriskServerWizard());
		dialog.open();
	}
}
