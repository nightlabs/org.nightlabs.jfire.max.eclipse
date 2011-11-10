package org.nightlabs.jfire.auth.ui.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.auth.ui.actions.OpenImportExportWizardAction;
import org.nightlabs.jfire.auth.ui.resource.Messages;
import org.nightlabs.jfire.auth.ui.wizard.ISynchronizationPerformerHop.SyncDirection;
import org.nightlabs.jfire.security.integration.UserManagementSystem;

/**
 * Wizard for running user data import/export between JFire and selected {@link UserManagementSystem}. Inititally it has a {@link ImportExportWizardHop}
 * which contributes a generic page {@link ImportExportConfigurationPage} for selecting a {@link UserManagementSystem} and type of interaction:
 * import or export.
 * 
 * When specific {@link UserManagementSystem} is selected new pages are added to the wizard dynamically which correspond to specific 
 * {@link UserManagementSystem} implementation. These pages are represented by implementations of {@link ISynchronizationPerformerHop} 
 * which are contributed by other UserManagementSystem-specific plugins and are registered via <code>org.nightlabs.jfire.auth.ui.userManagementSystemUIMapping</code> 
 * extension point.
 * 
 * Actual synchronization is also delegated to {@link ISynchronizationPerformerHop} implementation via {@link ImportExportWizardHop}. 
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class ImportExportWizard extends DynamicPathWizard{
	
	private ImportExportWizardHop importExportWizardHop;
	
	/**
	 * Default constructor.
	 */
	public ImportExportWizard() {
		setNeedsProgressMonitor(true);
		setWindowTitle("Select user management system for import or export"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		importExportWizardHop = new ImportExportWizardHop();
		addPage(importExportWizardHop.getEntryPage());
	}
	
	/**
	 * Proceeds to import or export page depending on {@link #selectedUserManagementSystem} and {@link #selectedSyncDirection}
	 * whih were set in corresponding constructor.
	 * 
	 * @param userManagementSystem {@link UserManagementSystem} selected for synchronization
	 * @param syncDirection Direction of synchronization, either import or export
	 */
	public void proceedToSynchronizationPage(UserManagementSystem<?> userManagementSystem, SyncDirection syncDirection){
		if (userManagementSystem != null && syncDirection != null){
			importExportWizardHop.proceedToNextPage(userManagementSystem, syncDirection);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		
		final boolean[] result = new boolean[] {false};
		try{
			getContainer().run(false, false, new IRunnableWithProgress(){
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException{
					try{
						
						final SyncDirection syncDirection = importExportWizardHop.getSyncDirection();
						
						String monitorMessage = SyncDirection.IMPORT.equals(syncDirection) ? Messages.getString("org.nightlabs.jfire.auth.ui.wizard.ImportExportWizard.monitorMessageImport") : Messages.getString("org.nightlabs.jfire.auth.ui.wizard.ImportExportWizard.monitorMessageExport"); //$NON-NLS-1$ //$NON-NLS-2$
						monitor.beginTask(monitorMessage, 2);
						
						importExportWizardHop.performSynchronization();
						
						monitor.worked(1);
						result[0] = true;

						if (!getContainer().getShell().isDisposed()){
							getContainer().getShell().getDisplay().asyncExec(new Runnable(){
								@Override
								public void run(){
									try{
										String msg = SyncDirection.IMPORT.equals(syncDirection) ? Messages.getString("org.nightlabs.jfire.auth.ui.wizard.ImportExportWizard.importCompletedMsg") : Messages.getString("org.nightlabs.jfire.auth.ui.wizard.ImportExportWizard.exportCompletedMsg"); //$NON-NLS-1$ //$NON-NLS-2$
										ImportExportMessageDialog.openSyncInformation(RCPUtil.getActiveShell(), Messages.getString("org.nightlabs.jfire.auth.ui.wizard.ImportExportWizard.syncInformationDlgTitle"), msg); //$NON-NLS-1$
									}catch(Exception e){
										throw new RuntimeException(e);
									}
								}
							});
						}
					}catch(Exception e){
						throw new RuntimeException(e);
					}finally{
						monitor.done();
					}
				}
			});
		}catch (Exception e){
			throw new RuntimeException(e);
		}
		return result[0];
	}

	static class ImportExportMessageDialog extends MessageDialog{

	    public static boolean openSyncInformation(Shell parent, String title, String message) {
	        MessageDialog dialog = new ImportExportMessageDialog(parent, title, null, message, MessageDialog.INFORMATION, new String[]{"OK"}, 0); //$NON-NLS-1$
	        return dialog.open() == 0;
	    }

		public ImportExportMessageDialog(Shell parentShell, String dialogTitle,
				Image dialogTitleImage, String dialogMessage,
				int dialogImageType, String[] dialogButtonLabels,
				int defaultIndex) {
			super(parentShell, dialogTitle, dialogTitleImage, dialogMessage,
					dialogImageType, dialogButtonLabels, defaultIndex);
		}
		
		@Override
		protected boolean customShouldTakeFocus() {
			return false;
		}
		
		@Override
		protected Control createCustomArea(Composite parent) {
			
			Button runAnotherButton = new Button(parent, SWT.PUSH);
			runAnotherButton.setText(Messages.getString("org.nightlabs.jfire.auth.ui.wizard.ImportExportWizard.runAnotherButtonLabel")); //$NON-NLS-1$
			runAnotherButton.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.wizard.ImportExportWizard.runAnotherButtonDescription")); //$NON-NLS-1$
			runAnotherButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					close();
					new OpenImportExportWizardAction().run(null);
				}
			});
			
			return runAnotherButton;
		}
		
	}
}
