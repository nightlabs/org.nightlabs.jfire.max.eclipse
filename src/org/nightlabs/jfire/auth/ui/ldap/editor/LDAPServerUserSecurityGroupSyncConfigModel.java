package org.nightlabs.jfire.auth.ui.ldap.editor;

import org.nightlabs.jfire.base.security.integration.ldap.LDAPServer;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPUserSecurityGroupSyncConfig;

/**
 * Simple model to be used on UI in {@link LDAPServerUserSecurityGroupSyncConfigComposite}.
 * It just wraps around {@link LDAPUserSecurityGroupSyncConfig}.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class LDAPServerUserSecurityGroupSyncConfigModel {
	
	private LDAPUserSecurityGroupSyncConfig syncConfig;

	/**
	 * Create new {@link LDAPServerUserSecurityGroupSyncConfigModel} with given {@link LDAPUserSecurityGroupSyncConfig}
	 * 
	 * @param syncConfig {@link LDAPUserSecurityGroupSyncConfig} to wrap around
	 */
	public LDAPServerUserSecurityGroupSyncConfigModel(LDAPUserSecurityGroupSyncConfig syncConfig) {
		this.syncConfig = syncConfig;
	}
	
	/**
	 * Get {@link LDAPServer} related to underlying {@link LDAPUserSecurityGroupSyncConfig}
	 * 
	 * @return related {@link LDAPServer}
	 */
	public LDAPServer getLdapServer(){
		return this.syncConfig.getUserManagementSystem();
	}
	
	/**
	 * Get the name of LDAP group from underlying {@link LDAPUserSecurityGroupSyncConfig}
	 * 
	 * @return name of mapped LDAP group as a {@link String}
	 */
	public String getLDAPGroupName(){
		return this.syncConfig.getUserManagementSystemSecurityObject();
	}

	/**
	 * Sets new LDAP group name to underlying {@link LDAPUserSecurityGroupSyncConfig}
	 * 
	 * @param entryName new name to be set, not <code>null</code> and not empty
	 */
	public void setLDAPGroupName(String entryName) {
		this.syncConfig.setLdapGroupName(entryName);
	}
	
}
