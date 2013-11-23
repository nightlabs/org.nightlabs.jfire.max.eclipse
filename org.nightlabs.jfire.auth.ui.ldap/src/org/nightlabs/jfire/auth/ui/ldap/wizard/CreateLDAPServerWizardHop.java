package org.nightlabs.jfire.auth.ui.ldap.wizard;

import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.jfire.auth.ui.wizard.CreateUserManagementSystemWizard;
import org.nightlabs.jfire.auth.ui.wizard.IUserManagementSystemBuilderHop;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPScriptSet;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPServer;
import org.nightlabs.jfire.base.security.integration.ldap.connection.ILDAPConnectionParamsProvider;
import org.nightlabs.jfire.base.security.integration.ldap.connection.ILDAPConnectionParamsProvider.AuthenticationMethod;
import org.nightlabs.jfire.base.security.integration.ldap.connection.ILDAPConnectionParamsProvider.EncryptionMethod;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemType;

/**
 * Implementation if {@link IUserManagementSystemBuilderHop} for creating {@link LDAPServer} instances.
 * Contributes two pages to {@link CreateUserManagementSystemWizard} with general {@link LDAPServer} 
 * properties configuration (host, port, name, description, active state, encryption and authentication methods)
 * and advanced configuration (leading state, synchronization properties, base entry name). 
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class CreateLDAPServerWizardHop extends WizardHop implements IUserManagementSystemBuilderHop{
	
	private LDAPServerGeneralConfigWizardPage generalConfigPage;
	private LDAPServerAdvancedConfigWizardPage advancedConfigPage;
	
	/**
	 * Default constructor
	 */
	public CreateLDAPServerWizardHop() {
		generalConfigPage = new LDAPServerGeneralConfigWizardPage();
		advancedConfigPage = new LDAPServerAdvancedConfigWizardPage();
		setEntryPage(generalConfigPage);
		addHopPage(advancedConfigPage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserManagementSystem buildUserManagementSystem(UserManagementSystemType<?> userManagementSystemType) {
		
		LDAPServer ldapServer = (LDAPServer) userManagementSystemType.createUserManagementSystem();
		
		ldapServer.setName(generalConfigPage.getLDAPName());
		ldapServer.setDescription(generalConfigPage.getLDAPDescription());
		ldapServer.setHost(generalConfigPage.getHost());
		ldapServer.setPort(generalConfigPage.getPort());
		ldapServer.setEncryptionMethod(generalConfigPage.getEncryptionMethod());
		ldapServer.setAuthenticationMethod(generalConfigPage.getAuthenticationMethod());
		ldapServer.setActive(generalConfigPage.getActiveState());
		ldapServer.setSyncDN(advancedConfigPage.getSyncDN());
		ldapServer.setSyncPassword(advancedConfigPage.getSyncPassword());
		ldapServer.setLeading(advancedConfigPage.getLeadingState());
		ldapServer.setBaseDN(LDAPScriptSet.BASE_USER_ENTRY_NAME_PLACEHOLDER, advancedConfigPage.getBaseEntryDN(LDAPScriptSet.BASE_USER_ENTRY_NAME_PLACEHOLDER));
		ldapServer.setBaseDN(LDAPScriptSet.BASE_GROUP_ENTRY_NAME_PLACEHOLDER, advancedConfigPage.getBaseEntryDN(LDAPScriptSet.BASE_GROUP_ENTRY_NAME_PLACEHOLDER));
		ldapServer.setAttributeSyncPolicy(advancedConfigPage.getAttributeSyncPolicy());
		
		return ldapServer;
	}
	
	/**
	 * Constructs {@link ILDAPConnectionParamsProvider} with data that was specified on {@link LDAPServerGeneralConfigWizardPage}.
	 * Made protected for package access.
	 * 
	 * @return counstructed {@link ILDAPConnectionParamsProvider}
	 */
	protected ILDAPConnectionParamsProvider getLDAPConnectionParamsProvider(){
		
		if (!generalConfigPage.isPageComplete()){
			return null;
		}
		
		// create local variables to prevent possible invalid thread access later
		final AuthenticationMethod authenticationMethod = generalConfigPage.getAuthenticationMethod();
		final EncryptionMethod encryptionMethod = generalConfigPage.getEncryptionMethod();
		final String host = generalConfigPage.getHost();
		final int port = generalConfigPage.getPort();
		
		return new ILDAPConnectionParamsProvider(){

			@Override
			public AuthenticationMethod getAuthenticationMethod() {
				return authenticationMethod;
			}

			@Override
			public EncryptionMethod getEncryptionMethod() {
				return encryptionMethod;
			}

			@Override
			public String getHost() {
				return host;
			}

			@Override
			public int getPort() {
				return port;
			}

			@Override
			public String getSASLRealm(String bindPrincipal) {
				// TODO 
				return null;
			}
			
		};
	}
	
}
