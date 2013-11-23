package org.nightlabs.jfire.auth.ui.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserSecurityGroupSyncConfig;
import org.nightlabs.jfire.security.integration.UserSecurityGroupSyncConfigContainer;
import org.nightlabs.jfire.security.integration.id.UserManagementSystemID;

/**
 * Simple model to be used on UI in {@link UserSecurityGroupSyncConfigGenericSection}.
 * It just wraps around {@link UserSecurityGroupSyncConfigContainer} and performs some additional data transformation and verification.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class UserSecurityGroupSyncConfigGenericModel{

	private UserSecurityGroupSyncConfigContainer syncConfigsContainer;
	
	/**
	 * Constructs a new {@link UserSecurityGroupSyncConfigGenericModel}.
	 * Throws {@link IllegalArgumentException} if <code>null</code> is passed as argument.
	 * 
	 * @param syncConfigContainer {@link UserSecurityGroupSyncConfigContainer} container instance to wrap around
	 */
	public UserSecurityGroupSyncConfigGenericModel(UserSecurityGroupSyncConfigContainer syncConfigContainer){
		if (syncConfigContainer == null){
			throw new IllegalArgumentException("Argument can't be null!"); //$NON-NLS-1$
		}
		this.syncConfigsContainer = syncConfigContainer;
	}

	/**
	 * Add new {@link UserSecurityGroupSyncConfig} to underlying {@link UserSecurityGroupSyncConfigContainer}
	 * 
	 * @param syncConfig {@link UserSecurityGroupSyncConfig} instance to be added to container
	 * @return <code>true</code> if {@link UserSecurityGroupSyncConfig} was successfully added, <code>false</code> otherwise 
	 */
	public boolean addSyncConfig(UserSecurityGroupSyncConfig<?, ?> syncConfig){
		if (syncConfig != null){
			return this.syncConfigsContainer.addSyncConfig(syncConfig);
		}
		return false;
	}

	/**
	 * Removes given {@link UserSecurityGroupSyncConfig} from uderlying {@link UserSecurityGroupSyncConfigContainer}
	 * 
	 * @param syncConfig {@link UserSecurityGroupSyncConfig} instance to be removed
	 * @return <code>true</code> if {@link UserSecurityGroupSyncConfig} was successfully removed, <code>false</code> otherwise
	 */
	public boolean removeSyncConfig(UserSecurityGroupSyncConfig<?, ?> syncConfig){
		return this.syncConfigsContainer.removeSyncConfig(syncConfig);
	}
	
	/**
	 * Removes a bunch of {@link UserSecurityGroupSyncConfig}s from underlying {@link UserSecurityGroupSyncConfigContainer}
	 * by given {@link Collection} of {@link UserManagementSystem}s.
	 * 
	 * @param userManagementSystems {@link Collection} of {@link UserManagementSystem}s which relate to {@link UserSecurityGroupSyncConfig}s to be removed
	 */
	public void removeSyncConfigsForUserManagementSystems(Collection<UserManagementSystem> userManagementSystems){
		this.syncConfigsContainer.removeSyncConfigs(new HashSet<UserSecurityGroupSyncConfig<?,?>>(
					getSyncConfigsForUserManagementSystems(userManagementSystems)));
	}
	
	/**
	 * Get underlying {@link UserSecurityGroupSyncConfigContainer}. This instance is NOT intended to be edited directly.
	 * 
	 * @return {@link UserSecurityGroupSyncConfigContainer} instance
	 */
	public UserSecurityGroupSyncConfigContainer getSyncConfigsContainer() {
		return syncConfigsContainer;
	}

	/**
	 * Get a {@link UserSecurityGroupSyncConfig} from underlying {@link UserSecurityGroupSyncConfigContainer} by given {@link UserManagementSystem},
	 * delegates to {@link UserSecurityGroupSyncConfigContainer#getSyncConfigForUserManagementSystem(org.nightlabs.jfire.security.integration.id.UserManagementSystemID)}
	 * 
	 * @param userManagementSystem {@link UserManagementSystem} which relate to needed {@link UserSecurityGroupSyncConfig}
	 * @return found {@link UserSecurityGroupSyncConfig} or <code>null</code> if nothing found
	 */
	public UserSecurityGroupSyncConfig<?, ?> getSyncConfigForUserManagementSystem(UserManagementSystem userManagementSystem){
		return this.syncConfigsContainer.getSyncConfigForUserManagementSystem(userManagementSystem.getUserManagementSystemObjectID());
	}

	/**
	 * Get a {@link Collection} of {@link UserSecurityGroupSyncConfig} from underlying {@link UserSecurityGroupSyncConfigContainer}
	 * by given {@link UserManagementSystem}s, delegates to {@link #getSyncConfigForUserManagementSystem(UserManagementSystem)} for
	 * every {@link UserManagementSystem} in given {@link Collection}.
	 * 
	 * @param userManagementSystems {@link Collection} of {@link UserManagementSystem}s which relate to needed {@link UserSecurityGroupSyncConfig}s
	 * @return {@link Collection} of found {@link UserSecurityGroupSyncConfig}s or an empty {@link Collection}
	 */
	public Collection<UserSecurityGroupSyncConfig<?, ?>> getSyncConfigsForUserManagementSystems(Collection<UserManagementSystem> userManagementSystems){
		Collection<UserSecurityGroupSyncConfig<?, ?>> syncConfigs = new ArrayList<UserSecurityGroupSyncConfig<?,?>>();
		for (UserManagementSystem userManagementSystem : userManagementSystems){
			UserSecurityGroupSyncConfig<?, ?> syncConfig = getSyncConfigForUserManagementSystem(userManagementSystem);
			if (syncConfig != null){
				syncConfigs.add(syncConfig);
			}
		}
		if (!syncConfigs.isEmpty()){
			return syncConfigs;
		}else{
			return Collections.emptyList();
		}
	}

	/**
	 * Checks whether a {@link UserSecurityGroupSyncConfig} exists in underlying {@link UserSecurityGroupSyncConfigContainer}
	 * for given {@link UserManagementSystem}. Simply delegates to {@link #getSyncConfigForUserManagementSystem(UserManagementSystem)}
	 * and checks whether its result is not a <code>null</code>/
	 * 
	 * @param userManagementSystem {@link UserManagementSystem} which relates to needed {@link UserSecurityGroupSyncConfig}
	 * @return <code>true</code> if {@link UserSecurityGroupSyncConfig} exists for given {@link UserManagementSystem} inside underlying {@link UserSecurityGroupSyncConfigContainer}
	 */
	public boolean syncConfigExistsForUserManagementSystem(UserManagementSystem userManagementSystem){
		return getSyncConfigForUserManagementSystem(userManagementSystem) != null;
	}
	
	/**
	 * Convinient method for setting syncEnabled flag to a bunch of {@link UserSecurityGroupSyncConfig}s which are found by
	 * given {@link Collection} of {@link UserManagementSystem}s.
	 * 
	 * @param userManagementSystems {@link UserManagementSystem}s which relate to {@link UserSecurityGroupSyncConfig}s
	 * @param syncEnabled Flag to be set
	 */
	public void setSyncEnabledForUserManagementSystems(Collection<UserManagementSystem> userManagementSystems, boolean syncEnabled){
		if (userManagementSystems != null){
			for (UserManagementSystem userManagementSystem : userManagementSystems) {
				UserSecurityGroupSyncConfig<?, ?> syncConfig = getSyncConfigForUserManagementSystem(userManagementSystem);
				if (syncConfig != null){
					syncConfig.setSyncEnabled(syncEnabled);
				}
			}
		}
	}
	
	/**
	 * Get all {@link UserManagementSystem}s which are related to {@link UserSecurityGroupSyncConfig}s inside underlying {@link UserSecurityGroupSyncConfigContainer}
	 * 
	 * @return {@link Collection} of {@link UserManagementSystem}s or an empty {@link Collection}
	 */
	public Collection<UserManagementSystem> getAllRelatedUserManagementSystems(){
		Collection<UserManagementSystem> allRelatedSystems = new ArrayList<UserManagementSystem>();
		for (UserSecurityGroupSyncConfig<?, ?> syncConfig : this.syncConfigsContainer.getSyncConfigs()){
			allRelatedSystems.add(syncConfig.getUserManagementSystem());
		}
		return allRelatedSystems;
	}
	
	/**
	 * Get IDs of all {@link UserManagementSystem}s which are related to {@link UserSecurityGroupSyncConfig}s inside underlying {@link UserSecurityGroupSyncConfigContainer}
	 * 
	 * @return {@link Set} of {@link UserManagementSystemID}s or an empty {@link Set}
	 */
	public Set<UserManagementSystemID> getAllRelatedUserManagementSystemIDs(){
		Set<UserManagementSystemID> allRelatedSystemIDs = new HashSet<UserManagementSystemID>();
		for (UserSecurityGroupSyncConfig<?, ?> syncConfig : this.syncConfigsContainer.getSyncConfigs()){
			allRelatedSystemIDs.add(syncConfig.getUserManagementSystem().getUserManagementSystemObjectID());
		}
		if (!allRelatedSystemIDs.isEmpty()){
			return allRelatedSystemIDs;
		}else{
			return Collections.emptySet();
		}
	}

}