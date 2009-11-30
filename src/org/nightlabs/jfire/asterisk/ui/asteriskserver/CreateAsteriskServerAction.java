package org.nightlabs.jfire.asterisk.ui.asteriskserver;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class CreateAsteriskServerAction 
implements IViewActionDelegate 
{
	public static final String ID = CreateAsteriskServerAction.class.getName();
	
	private IViewPart viewPart;
	@Override
	public void init(IViewPart viewPart) {
		this.viewPart = viewPart;
	}

	@Override
	public void run(IAction action) {
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(viewPart.getSite().getShell(), new CreateAsteriskServerWizard());
		dialog.open();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}
}
