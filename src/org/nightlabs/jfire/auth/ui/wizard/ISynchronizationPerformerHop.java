package org.nightlabs.jfire.auth.ui.wizard;

import javax.security.auth.login.LoginException;

import org.nightlabs.base.ui.wizard.IWizardHop;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemCommunicationException;

/**
 * WizardHops for {@link ImportExportWizard} contributed by specific UserManagementSystem plugins should implement this interface. 
 * These hops should be registered via <code>org.nightlabs.jfire.auth.ui.userManagementSystemUIMapping</code> extension point. 
 * They contribute wizard pages to {@link ImportExportWizard} and should implement {@link #configurePages(UserManagementSystem, SyncDirection)} 
 * for showing approriate wizad page either for import or for export and {@link #performSynchronization(UserManagementSystem, SyncDirection)}
 * for running actual synchronization for specific {@link UserManagementSystem}.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public interface ISynchronizationPerformerHop extends IWizardHop{
	
	public enum SyncDirection{
		IMPORT,
		EXPORT
	}
	
	/**
	 * This method is called when {@link ISynchronizationPerformerHop} implementation is initialized to configure what page should be 
	 * shown depending on {@link SyncDirection}.
	 * 
	 * @param userManagementSystem
	 * @param syncDirection
	 */
	void configurePages(UserManagementSystem userManagementSystem, SyncDirection syncDirection);

	/**
	 * This method is called by {@link ImportExportWizardHop} when {@link ImportExportWizard#performFinish()} is executed. Actual synchronization 
	 * should be run here for specific {@link UserManagementSystem}.
	 * 
	 * @param userManagementSystem
	 * @param syncDirection
	 * @throws LoginException
	 * @throws UserManagementSystemCommunicationException
	 */
	void performSynchronization(UserManagementSystem userManagementSystem, SyncDirection syncDirection) throws LoginException, UserManagementSystemCommunicationException;

}
