package org.nightlabs.jfire.auth.ui.ldap.editor;

import javax.jdo.FetchPlan;
import javax.jdo.JDODetachedFieldAccessException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.auth.ui.editor.IUserSecurityGroupSyncConfigDelegate;
import org.nightlabs.jfire.auth.ui.editor.UserSecurityGroupSyncConfigSpecificComposite;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPServer;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPUserSecurityGroupSyncConfig;
import org.nightlabs.jfire.base.security.integration.ldap.sync.LDAPSyncEvent;
import org.nightlabs.jfire.base.security.integration.ldap.sync.LDAPSyncEvent.FetchEventTypeDataUnit;
import org.nightlabs.jfire.base.security.integration.ldap.sync.LDAPSyncEvent.SendEventTypeDataUnit;
import org.nightlabs.jfire.security.UserSecurityGroup;
import org.nightlabs.jfire.security.dao.UserManagementSystemDAO;
import org.nightlabs.jfire.security.id.UserSecurityGroupID;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemSyncEvent;
import org.nightlabs.jfire.security.integration.UserManagementSystemType;
import org.nightlabs.jfire.security.integration.UserManagementSystemSyncEvent.SyncEventGenericType;
import org.nightlabs.jfire.security.integration.UserSecurityGroupSyncConfig;
import org.nightlabs.jfire.security.integration.UserSecurityGroupSyncConfigContainer;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.util.CollectionUtil;

/**
 * LDAP implementation of {@link IUserSecurityGroupSyncConfigDelegate}, creates {@link LDAPUserSecurityGroupSyncConfig},
 * {@link LDAPSyncEvent} and {@link LDAPServerUserSecurityGroupSyncConfigComposite}.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class LDAPServerUserSecurityGroupSyncConfigDelegate implements IUserSecurityGroupSyncConfigDelegate {

	private LDAPServerUserSecurityGroupSyncConfigComposite ldapSyncConfigComposite;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserSecurityGroupSyncConfig<?, ?> createSyncConfig(
			UserSecurityGroupSyncConfigContainer syncConfigsContainer, UserManagementSystem userManagementSystem) {
		if (!(userManagementSystem instanceof LDAPServer)){
			throw new IllegalArgumentException(
					"UserManagementSystem must be an instance of LDAPServer! Instead it is " + userManagementSystem!=null ? userManagementSystem.getClass().getName() : null);
		}
		LDAPServer ldapServer = (LDAPServer) userManagementSystem;
		try{
			ldapServer.getLdapScriptSet();
		}catch(JDODetachedFieldAccessException e){
			// Apparently we need this inside LDAPUserSecurityGroupSyncConfigLifecycleListener.postCreate() when executing
			// LDAPSyncInvocation which binds the connection generating entryDN from current User with the help of LDAPScriptSet.
			// Thing is that new LDAPUserSecurityGroupSyncConfig instance (attached in P_NEW state) in postCreate() has a detached 
			// userManagementSystem reference for some reason instead of an attached one. Denis.
			ldapServer = (LDAPServer) UserManagementSystemDAO.sharedInstance().getUserManagementSystem(
					ldapServer.getUserManagementSystemObjectID(), new String[]{
						FetchPlan.DEFAULT,
						UserManagementSystem.FETCH_GROUP_NAME,
						UserManagementSystem.FETCH_GROUP_TYPE,
						UserManagementSystemType.FETCH_GROUP_NAME,
						LDAPServer.FETCH_GROUP_LDAP_SCRIPT_SET
					}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
		}
		return new LDAPUserSecurityGroupSyncConfig(syncConfigsContainer, ldapServer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserManagementSystemSyncEvent createSyncEvent(
			UserSecurityGroupSyncConfig<?, ?> syncConfig, boolean userManagementSystemIsLeading) {
		if (!(syncConfig instanceof LDAPUserSecurityGroupSyncConfig)){
			throw new IllegalArgumentException("syncConfig must be an instance of LDAPUserSecurityGroupSyncConfig!");
		}
		LDAPSyncEvent syncEvent = new LDAPSyncEvent(
				userManagementSystemIsLeading ? SyncEventGenericType.FETCH_AUTHORIZATION : SyncEventGenericType.SEND_AUTHORIZATION);
		if (userManagementSystemIsLeading){
			syncEvent.setFetchEventTypeDataUnits(CollectionUtil.createArrayList(
					new FetchEventTypeDataUnit((String) syncConfig.getUserManagementSystemSecurityObject())));
		}else{
			UserSecurityGroup userSecurityGroup = syncConfig.getUserSecurityGroup();
			syncEvent.setSendEventTypeDataUnits(CollectionUtil.createArrayList(
					new SendEventTypeDataUnit(
							UserSecurityGroupID.create(userSecurityGroup.getOrganisationID(), userSecurityGroup.getUserSecurityGroupID()))));
		}
		return syncEvent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserSecurityGroupSyncConfigSpecificComposite createEditorComposite(Composite parent, int style, FormToolkit toolkit) {
		if (ldapSyncConfigComposite == null){
			ldapSyncConfigComposite = new LDAPServerUserSecurityGroupSyncConfigComposite(parent, style, toolkit);
		}
		return ldapSyncConfigComposite;
	}

}
