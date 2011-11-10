package org.nightlabs.jfire.auth.ui.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.auth.ui.editor.UserManagementSystemEditor;
import org.nightlabs.jfire.auth.ui.editor.UserManagementSystemEditorInput;
import org.nightlabs.jfire.auth.ui.resource.Messages;
import org.nightlabs.jfire.security.dao.UserManagementSystemDAO;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemType;
import org.nightlabs.jfire.security.integration.id.UserManagementSystemID;

/**
 * Wizard for creating and configuring new {@link UserManagementSystem}. Inititally it has a {@link CreateUserManagementSystemWizardHop}
 * which contributes a generic page {@link SelectUserManagementSystemTypePage} for selecting a {@link UserManagementSystemType} of
 * newly created {@link UserManagementSystem}.
 * 
 * When specific {@link UserManagementSystemType} is selected new pages are added to the wizard dynamically which correspond to specific 
 * {@link UserManagementSystem} implementation. These pages are represented by implementations of {@link IUserManagementSystemBuilderHop} 
 * which are contributed by other UserManagementSystem-specific plugins and are registered via <code>org.nightlabs.jfire.auth.ui.userManagementSystemUIMapping</code> 
 * extension point.
 * 
 * Actual creation of specific {@link UserManagementSystem} is also delegated to {@link IUserManagementSystemBuilderHop}
 * implementations via {@link CreateUserManagementSystemWizardHop}. Created instance is then stored by this wizard in {@link #performFinish()}.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class CreateUserManagementSystemWizard extends DynamicPathWizard implements INewWizard{
	
	private CreateUserManagementSystemWizardHop createUserManagementSystemWizardHop;
	
	/**
	 * Default constructor.
	 */
	public CreateUserManagementSystemWizard() {
		setNeedsProgressMonitor(true);
		setWindowTitle(Messages.getString("org.nightlabs.jfire.auth.ui.wizard.CreateUserManagementSystemWizard.windowTitle")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		createUserManagementSystemWizardHop = new CreateUserManagementSystemWizardHop();
		addPage(createUserManagementSystemWizardHop.getEntryPage());
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
						
						UserManagementSystem<?> userManagementSystem = createUserManagementSystemWizardHop.createUserManagementSystem();
						if (userManagementSystem == null){
							return;
						}
						
						@SuppressWarnings("unchecked")
						final Class<? extends UserManagementSystemType<?>> userManegementSystemTypeClass = (Class<? extends UserManagementSystemType<?>>) userManagementSystem.getType().getClass();
						final UserManagementSystemID userManagementSystemID = userManagementSystem.getUserManagementSystemObjectID();
						
						UserManagementSystemDAO.sharedInstance().storeUserManagementSystem(
								userManagementSystem, false, null, 1, new ProgressMonitorWrapper(monitor)
								);
						
						result[0] = true;

						if (!getContainer().getShell().isDisposed()){
							getContainer().getShell().getDisplay().asyncExec(new Runnable(){
								@Override
								public void run(){
									try{
										Editor2PerspectiveRegistry.sharedInstance().openEditor(
												new UserManagementSystemEditorInput(userManagementSystemID, userManegementSystemTypeClass),
												UserManagementSystemEditor.EDITOR_ID
												);
									}catch(Exception e){
										throw new RuntimeException(e);
									}
								}
							});
						}
					}catch(Exception e){
						throw new RuntimeException(e);
					}
				}
			});
		}catch (Exception e){
			throw new RuntimeException(e);
		}
		return result[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// do nothing
	}
	
}
