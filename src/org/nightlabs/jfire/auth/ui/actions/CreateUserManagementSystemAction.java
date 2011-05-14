package org.nightlabs.jfire.auth.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.graphics.Point;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.auth.ui.wizard.CreateUserManagementSystemWizard;
import org.nightlabs.jfire.base.ui.login.action.LSDWorkbenchWindowActionDelegate;
import org.nightlabs.jfire.security.integration.UserManagementSystem;

/**
 * Action which opens {@link CreateUserManagementSystemWizard} for creating and configuring new {@link UserManagementSystem}.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class CreateUserManagementSystemAction extends LSDWorkbenchWindowActionDelegate{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run(IAction action)	{
		try	{
			CreateUserManagementSystemWizard wiz = new CreateUserManagementSystemWizard();
			DynamicPathWizardDialog dynamicPathWizardDialog = new DynamicPathWizardDialog(wiz.getShell(), wiz) {
				@Override
				protected Point getInitialSize() {
					return new Point(780,650);
				}
			};
			dynamicPathWizardDialog.open();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
