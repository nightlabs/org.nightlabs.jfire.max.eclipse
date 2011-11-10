package org.nightlabs.jfire.auth.ui.wizard;

import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.jfire.auth.ui.resource.Messages;
import org.nightlabs.jfire.security.dao.UserManagementSystemTypeDAO;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemType;
import org.nightlabs.progress.ProgressMonitor;

/**
 * WizardHop containing {@link SelectUserManagementSystemTypePage} for selecting specific {@link UserManagementSystemType}.
 * Starts a {@link Job} for loading all existent {@link UserManagementSystemType} persistent instances when constructor is called.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class CreateUserManagementSystemWizardHop extends WizardHop{
	
	private Job loadJob;
	
	private SelectUserManagementSystemTypePage selectTypePage;
	
	private final static String[] FETCH_GROUPS_USER_MANAGEMENT_SYSTEM_TYPES = new String[]{
		UserManagementSystemType.FETCH_GROUP_NAME,
		UserManagementSystemType.FETCH_GROUP_DESCRIPTION,
		FetchPlan.DEFAULT
	};
	
	private final static int FETCH_DEPTH_USER_MANAGEMENT_SYSTEM_TYPES = 3;

	private List<UserManagementSystemType<?>> allUserManagementSystemTypes;
	
	/**
	 * Default constructor. Creates {@link SelectUserManagementSystemTypePage} and starts a {@link Job} for loading all
	 * persistent {@link UserManagementSystemType} objects.
	 */
	public CreateUserManagementSystemWizardHop() {

		// create page with "loading usermanagement system types..." label
		selectTypePage = new SelectUserManagementSystemTypePage();
		setEntryPage(selectTypePage);
		
		loadJob = new Job(Messages.getString("org.nightlabs.jfire.auth.ui.wizard.CreateUserManagementSystemWizardHop.loadUserManagementSystemTypesJobTitle")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				allUserManagementSystemTypes = UserManagementSystemTypeDAO.sharedInstance().getAllUserManagementSystemTypes(
						FETCH_GROUPS_USER_MANAGEMENT_SYSTEM_TYPES, FETCH_DEPTH_USER_MANAGEMENT_SYSTEM_TYPES, monitor);
				
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
							selectTypePage.setUserManagementSystemTypes(allUserManagementSystemTypes);
						}
					});
				}
				super.done(event);
			}
			
		});
		
		loadJob.schedule();
		
	}	
	
	/**
	 * Delegates creation of specific {@link UserManagementSystem} to {@link IUserManagementSystemBuilderHop} implementation.
	 * 
	 * @return created {@link UserManagementSystem} instance
	 */
	public UserManagementSystem<?> createUserManagementSystem(){
		return selectTypePage.getUserManagementSystemBuilderHop().buildUserManagementSystem(selectTypePage.getSelectedUserManagementSystemType());
	}
}
