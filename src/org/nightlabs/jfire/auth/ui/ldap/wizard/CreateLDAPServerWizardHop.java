package org.nightlabs.jfire.auth.ui.ldap.wizard;

import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.jfire.auth.ui.wizard.CreateUserManagementSystemWizard;
import org.nightlabs.jfire.auth.ui.wizard.IUserManagementSystemBuilderHop;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPServer;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemType;

/**
 * Implementation if {@link IUserManagementSystemBuilderHop} for creating {@link LDAPServer} instances.
 * Contributes two pages to {@link CreateUserManagementSystemWizard} with general {@link LDAPServer} 
 * properties configuration (host, port, name, description, active state, encryption and authentication methods)
 * and advanced configuration (leading state, synchronization properties). 
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
		
		return ldapServer;
	}
	
}
