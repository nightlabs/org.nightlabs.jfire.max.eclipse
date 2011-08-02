package org.nightlabs.jfire.auth.ui.ldap.editor;

import org.nightlabs.i18n.I18nText;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPServer;
import org.nightlabs.jfire.base.security.integration.ldap.connection.ILDAPConnectionParamsProvider.AuthenticationMethod;
import org.nightlabs.jfire.base.security.integration.ldap.connection.ILDAPConnectionParamsProvider.EncryptionMethod;

/**
 * Simple model of {@link LDAPServer} to be used on UI in {@link LDAPServerGeneralConfigSection}.
 * It just wraps around {@link LDAPServer} and performs some additional data transformation and 
 * verification.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class LDAPServerGeneralConfigModel{

	private LDAPServer ldapServer;
	
	/**
	 * Constructs a new {@link LDAPServerGeneralConfigModel}.
	 * Throws {@link IllegalArgumentException} if <code>null</code> is passed as argument.
	 * 
	 * @param ldapServer Instance being edited, can't be <code>null</code>
	 */
	public LDAPServerGeneralConfigModel(LDAPServer ldapServer){
		if (ldapServer == null){
			throw new IllegalArgumentException("LDAPServer can't be null!"); //$NON-NLS-1$
		}
		this.ldapServer = ldapServer;
	}
	
	/**
	 * Get the name of {@link LDAPServer}
	 * 
	 * @return name as {@link I18nText}
	 */
	public I18nText getName(){
		return ldapServer.getName();
	}
	
	/**
	 * Set new name to {@link LDAPServer}
	 * 
	 * @param i18nText
	 */
	public void setName(I18nText i18nText){
		ldapServer.setName(i18nText);
	}
	
	/**
	 * Get the description of {@link LDAPServer}
	 * 
	 * @return description as {@link I18nText}
	 */
	public I18nText getDescription(){
		return ldapServer.getDescription();
	}
	
	/**
	 * Set new description to {@link LDAPServer}
	 * 
	 * @param i18nText
	 */
	public void setDescription(I18nText i18nText){
		ldapServer.setDescription(i18nText);
	}
	
	/**
	 * Get host of {@link LDAPServer}
	 * 
	 * @return host
	 */
	public String getHost(){
		return ldapServer.getHost();
	}
	
	/**
	 * Set host to {@link LDAPServer}
	 * 
	 * @param host
	 */
	public void setHost(String host){
		if (host != null && !host.isEmpty()){
			ldapServer.setHost(host);
		}
	}
	
	/**
	 * Get port of {@link LDAPServer}
	 * 
	 * @return port as {@link String}
	 */
	public String getPort(){
		return ""+ldapServer.getPort(); //$NON-NLS-1$
	}
	
	/**
	 * Set port to {@link LDAPServer} if it could be parsed as {@link Integer}.
	 * Otherwise do nothing.
	 * 
	 * @param port
	 */
	public void setPort(String port){
		try{
			ldapServer.setPort(Integer.parseInt(port));
		}catch(NumberFormatException e){
			// do nothing
		}
	}
	
	/**
	 * Get {@link LDAPServer} active state
	 * 
	 * @return <code>true</code> if {@link LDAPServer} is active and used for login
	 */
	public boolean isActive(){
		return ldapServer.isActive();
	}
	
	/**
	 * Modify {@link LDAPServer} active state 
	 * 
	 * @param isActive
	 */
	public void setActive(boolean isActive){
		ldapServer.setActive(isActive);
	}

	/**
	 * Get {@link EncryptionMethod} of {@link LDAPServer}
	 * 
	 * @return name of {@link EncryptionMethod}
	 */
	public String getEncryptionMethod(){
		return ldapServer.getEncryptionMethod().stringValue();
	}
	
	/**
	 * Set {@link EncryptionMethod} to {@link LDAPServer}
	 * 
	 * @param encryptionMethodName
	 */
	public void setEncryptionMethod(String encryptionMethodName){
		EncryptionMethod value = EncryptionMethod.findEncryptionMethodByStringValue(encryptionMethodName);
		if (value != null){
			ldapServer.setEncryptionMethod(value);
		}
	}

	/**
	 * Get {@link AuthenticationMethod} os {@link LDAPServer}
	 * 
	 * @return name of {@link AuthenticationMethod}
	 */
	public String getAuthenticationMethod(){
		return ldapServer.getAuthenticationMethod().stringValue();
	}

	/**
	 * Set {@link AuthenticationMethod} to {@link LDAPServer}
	 * 
	 * @param authMethodName
	 */
	public void setAuthenticationMethod(String authMethodName){
		AuthenticationMethod value = AuthenticationMethod.findAuthenticationMethodByStringValue(authMethodName);
		if (value != null){
			ldapServer.setAuthenticationMethod(value);
		}
	}

}