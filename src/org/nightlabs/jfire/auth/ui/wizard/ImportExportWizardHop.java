package org.nightlabs.jfire.auth.ui.wizard;

import java.util.List;

import javax.jdo.FetchPlan;
import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.jfire.auth.ui.wizard.ISynchronizationPerformerHop.SyncDirection;
import org.nightlabs.jfire.security.dao.UserManagementSystemDAO;
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

	private List<UserManagementSystem> allUserManagementSystems;
	
	/**
	 * Default constructor. Creates {@link ImportExportConfigurationPage} and starts a {@link Job} for loading all
	 * persistent {@link UserManagementSystem} objects.
	 */
	public ImportExportWizardHop() {

		// create page with "loading usermanagement systems..." label
		configPage = new ImportExportConfigurationPage();
		setEntryPage(configPage);
		
		loadJob = new Job("loading all user management systems") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				allUserManagementSystems = UserManagementSystemDAO.sharedInstance().getAllUserManagementSystems(
						FETCH_GROUPS_USER_MANAGEMENT_SYSTEM, FETCH_DEPTH_USER_MANAGEMENT_SYSTEM, monitor);
				
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
							configPage.setUserManagementSystems(allUserManagementSystems);
						}
					});
				}
				super.done(event);
			}
			
		});
		
		loadJob.schedule();
		
	}	
	
	/**
	 * Delegates running synchronization to specific {@link ISynchronizationPerformerHop} implementation.
	 * 
	 * @throws LoginException
	 * @throws UserManagementSystemCommunicationException
	 */
	public void performSynchronization() throws LoginException, UserManagementSystemCommunicationException{
		configPage.getSynchronizationHop().performSynchronization(configPage.getSelectedUserManagementSystem(), configPage.getSyncDirection());
	}
	
	/**
	 * Get selected action: import or export.
	 * 
	 * @return selected {@link SyncDirection}
	 */
	public SyncDirection getSyncDirection(){
		return configPage.getSyncDirection();
	}
}
