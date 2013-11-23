package org.nightlabs.jfire.auth.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemSyncEvent;
import org.nightlabs.jfire.security.integration.UserSecurityGroupSyncConfig;
import org.nightlabs.jfire.security.integration.UserSecurityGroupSyncConfigContainer;

/**
 * Interface for delegate classes which are supposed to perform some specific tasks for concrete types of {@link UserSecurityGroupSyncConfig}s
 * in generic modules. Now is used in {@link UserSecurityGroupSyncConfigGenericSection}.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public interface IUserSecurityGroupSyncConfigDelegate {

	/**
	 * Create new {@link UserSecurityGroupSyncConfig} of a specific type.
	 * 
	 * @param syncConfigsContainer {@link UserSecurityGroupSyncConfigContainer} which will contain created {@link UserSecurityGroupSyncConfig}
	 * @param userManagementSystem Specific {@link UserManagementSystem} which will synchronize authorization data based on created {@link UserSecurityGroupSyncConfig}
	 * @return newly created {@link UserSecurityGroupSyncConfig} instance
	 */
	UserSecurityGroupSyncConfig<?, ?> createSyncConfig(
			UserSecurityGroupSyncConfigContainer syncConfigsContainer, UserManagementSystem userManagementSystem);

	/**
	 * Create a concrete type of {@link UserManagementSystemSyncEvent} for sychronization of authorization data.
	 * 
	 * @param syncConfig {@link UserSecurityGroupSyncConfig} instance which holds mapping for synchronization
	 * @param userManagementSystemIsLeading Flag whether {@link UserManagementSystem} which is supposed to be synchronized is leading or not
	 * @return newly created {@link UserManagementSystemSyncEvent}
	 */
	UserManagementSystemSyncEvent createSyncEvent(
			UserSecurityGroupSyncConfig<?, ?> syncConfig, boolean userManagementSystemIsLeading);

	/**
	 * Create a {@link UserSecurityGroupSyncConfigSpecificComposite} to edit specific properties of {@link UserSecurityGroupSyncConfig} instance
	 * 
	 * @param parent Parent {@link Composite}
	 * @param style {@link SWT} style
	 * @param toolkit {@link FormToolkit} to be used for creation of composite's content
	 * @return created {@link UserSecurityGroupSyncConfigSpecificComposite}
	 */
	UserSecurityGroupSyncConfigSpecificComposite createEditorComposite(Composite parent, int style, FormToolkit toolkit);
	
}
