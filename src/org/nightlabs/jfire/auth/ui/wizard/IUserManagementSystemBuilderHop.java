package org.nightlabs.jfire.auth.ui.wizard;

import org.nightlabs.base.ui.wizard.IWizardHop;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemType;

/**
 * WizardHops contributed by specific UserManagementSystem plugins should implement this interface. These hops should be registered
 * via <code>org.nightlabs.jfire.auth.ui.userManagementSystemUIMapping</code> extension point. They could contribute wizard pages
 * to {@link CreateUserManagementSystemWizard} and should implement {@link #buildUserManagementSystem(UserManagementSystemType)} for 
 * actual creation and configuration of specific {@link UserManagementSystem}.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public interface IUserManagementSystemBuilderHop extends IWizardHop{

	/**
	 * Creates specific {@link UserManagementSystem} object using given {@link UserManagementSystemType}.
	 * 
	 * @param userManagementSystemType {@link UserManagementSystemType} of newly created {@link UserManagementSystem}
	 * @return created specific {@link UserManagementSystem} instance
	 */
	UserManagementSystem<?> buildUserManagementSystem(UserManagementSystemType<?> userManagementSystemType);
	
}
