package org.nightlabs.jfire.auth.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.graphics.Point;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.auth.ui.wizard.ImportExportWizard;
import org.nightlabs.jfire.base.ui.login.action.LSDWorkbenchWindowActionDelegate;
import org.nightlabs.jfire.security.integration.UserManagementSystem;

/**
 * Action which opens {@link ImportExportWizard} for manual synchronization of user data between JFire and {@link UserManagementSystem}.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class OpenImportExportWizardAction extends LSDWorkbenchWindowActionDelegate{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run(IAction action)	{
		try	{
			ImportExportWizard wiz = new ImportExportWizard();
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
