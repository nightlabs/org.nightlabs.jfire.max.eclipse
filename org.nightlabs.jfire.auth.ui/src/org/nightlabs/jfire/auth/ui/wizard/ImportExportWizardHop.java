package org.nightlabs.jfire.auth.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.FetchPlan;
import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.jfire.auth.ui.resource.Messages;
import org.nightlabs.jfire.auth.ui.wizard.ISynchronizationPerformerHop.SyncDirection;
import org.nightlabs.jfire.security.dao.UserManagementSystemDAO;
import org.nightlabs.jfire.security.integration.SynchronizableUserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemCommunicationException;
import org.nightlabs.jfire.security.integration.UserManagementSystemType;
import org.nightlabs.progress.ProgressMonitor;

/**
 * WizardHop containing {@link ImportExportConfigurationPage} for selecting {@link UserManagementSystem} and action type.
 * Starts a {@link Job} for loading all existent {@link UserManagementSystem} persistent instances when constructor is called.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class ImportExportWizardHop extends WizardHop{
	
	private Job loadJob;
	
	private ImportExportConfigurationPage configPage;
	
	private static final String[] FETCH_GROUPS_USER_MANAGEMENT_SYSTEM = {
		UserManagementSystem.FETCH_GROUP_NAME,
		UserManagementSystem.FETCH_GROUP_TYPE,
		UserManagementSystemType.FETCH_GROUP_NAME,
		FetchPlan.DEFAULT
		};

	// UserManagementSystem -> UserManagementSystemType -> UserManagementSystemTypeName -> names
	private static final int FETCH_DEPTH_USER_MANAGEMENT_SYSTEM = 4;

	private List<SynchronizableUserManagementSystem<?>> allSyncUserManagementSystems;
	
	/**
	 * Default constructor. Creates {@link ImportExportConfigurationPage} and starts a {@link Job} for loading all
	 * persistent {@link UserManagementSystem} objects.
	 */
	public ImportExportWizardHop() {

		// create page with "loading usermanagement systems..." label
		configPage = new ImportExportConfigurationPage();
		setEntryPage(configPage);

		loadJob = new Job(Messages.getString("org.nightlabs.jfire.auth.ui.wizard.ImportExportWizardHop.loadAllUserManagementSystemsJobTitle")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				List<UserManagementSystem> allUserManagementSystems = UserManagementSystemDAO.sharedInstance().getAllUserManagementSystems(
						FETCH_GROUPS_USER_MANAGEMENT_SYSTEM, FETCH_DEPTH_USER_MANAGEMENT_SYSTEM, monitor);
				allSyncUserManagementSystems = new ArrayList<SynchronizableUserManagementSystem<?>>();
				for (UserManagementSystem userManagementSystem : allUserManagementSystems) {
					if (userManagementSystem instanceof SynchronizableUserManagementSystem){
						allSyncUserManagementSystems.add((SynchronizableUserManagementSystem<?>) userManagementSystem);
					}
				}
				
				return Status.OK_STATUS;
			}
		};
		
		loadJob.addJobChangeListener(new JobChangeAdapter(){
			@Override
			public void done(IJobChangeEvent event) {
				if (Status.OK_STATUS == event.getResult()){
					getWizard().getContainer().getShell().getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							configPage.setUserManagementSystems(allSyncUserManagementSystems);
						}
					});
				}
				super.done(event);
			}
			
		});
		
		loadJob.schedule();
		
	}
	
	/**
	 * Sets selected {@link UserManagementSystem} and {@link SyncDirection} so {@link ImportExportConfigurationPage} 
	 * and calls it to proceed to the next page.
	 * 
	 * @param userManagementSystem {@link UserManagementSystem} selected for synchronization
	 * @param syncDirection Direction of synchronization, either import or export
	 */
	public void proceedToNextPage(SynchronizableUserManagementSystem<?> userManagementSystem, SyncDirection syncDirection) {
		configPage.proceedToNextPage(userManagementSystem, syncDirection);
	}

	/**
	 * Delegates running synchronization to specific {@link ISynchronizationPerformerHop} implementation.
	 * 
	 * @param monitor {@link ProgressMonitor} to be used
	 * @throws LoginException
	 * @throws UserManagementSystemCommunicationException
	 */
	public void performSynchronization(ProgressMonitor monitor) throws LoginException, UserManagementSystemCommunicationException{
		configPage.getSynchronizationHop().performSynchronization(configPage.getSelectedUserManagementSystem(), configPage.getSyncDirection(), monitor);
	}
	
	/**
	 * Get selected action: import or export.
	 * 
	 * @return selected {@link SyncDirection}
	 */
	public SyncDirection getSyncDirection(){
		return configPage.getSyncDirection();
	}
	
	/**
	 * Get {@link UserManagementSystem} selected for import/export on {@link ImportExportConfigurationPage}
	 * 
	 * @return selected {@link UserManagementSystem}
	 */
	public SynchronizableUserManagementSystem<?> getSelectedUserManagementSystem(){
		return configPage.getSelectedUserManagementSystem();
	}
}
