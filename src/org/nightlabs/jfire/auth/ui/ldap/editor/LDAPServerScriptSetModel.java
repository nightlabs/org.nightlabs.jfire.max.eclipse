package org.nightlabs.jfire.auth.ui.ldap.editor;

import java.util.Map;

import org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.NamedScript;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPScriptSet;
import org.nightlabs.jfire.base.security.integration.ldap.id.LDAPScriptSetID;
import org.nightlabs.jfire.base.security.integration.ldap.scripts.ILDAPScriptProvider;

/**
 * Simple model of {@link LDAPScriptSet} to be used on UI in {@link LDAPServerScriptSetSection}.
 * It just wraps around {@link LDAPScriptSet} and performs some additional data transformation and 
 * verification.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class LDAPServerScriptSetModel {

	private LDAPScriptSet ldapScriptSet;

	public LDAPServerScriptSetModel(LDAPScriptSet ldapScriptSet) {
		this.ldapScriptSet = ldapScriptSet;
	}
	
	/**
	 * Get ID of underlying {@link LDAPScriptSet} object.
	 * 
	 * @return {@link LDAPScriptSetID}
	 */
	public LDAPScriptSetID getLDAPScriptSetID(){
		return LDAPScriptSetID.create(ldapScriptSet.getOrganisationID(), ldapScriptSet.getLdapScriptSetID());
	}
	
	/**
	 * Get content of a script as {@link String} by its name. Script name is one of constants defined in {@link LDAPScriptSetHelper}.
	 * 
	 * @param scriptName script name
	 * @return script's content
	 */
	public String getScriptContentById(String scriptID){
		if (ILDAPScriptProvider.BIND_VARIABLES_SCRIPT_ID.equals(scriptID)){
			return ldapScriptSet.getBindVariablesScript();
		}else if (ILDAPScriptProvider.GET_ENTRY_NAME_SCRIPT_ID.equals(scriptID)){
			return ldapScriptSet.getLdapDNScript();
		}else if (ILDAPScriptProvider.GET_ATTRIBUTE_SET_SCRIPT_ID.equals(scriptID)){
			return ldapScriptSet.getGenerateJFireToLdapAttributesScript();
		}else if (ILDAPScriptProvider.GET_PARENT_ENTRIES_SCRIPT_ID.equals(scriptID)){
			return ldapScriptSet.getGenerateParentLdapEntriesScript();
		}else if (ILDAPScriptProvider.SYNC_TO_JFIRE_SCRIPT_ID.equals(scriptID)){
			return ldapScriptSet.getSyncLdapToJFireScript();
		}else{
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * Sets modified script content to underlying {@link LDAPScriptSet} instance. 
	 * @param namedScriptsLocal 
	 * 
	 * @param namedScripts
	 */
	public void commitScriptContent(Map<String, NamedScript> namedScriptsLocal){
		for (NamedScript script : namedScriptsLocal.values()){
			String scriptID = script.getScriptID();
			String scriptContent = script.getScriptContent();
			if (ILDAPScriptProvider.BIND_VARIABLES_SCRIPT_ID.equals(scriptID)){
				ldapScriptSet.setBindVariablesScript(scriptContent);
			}else if (ILDAPScriptProvider.GET_ENTRY_NAME_SCRIPT_ID.equals(scriptID)){
				ldapScriptSet.setLdapDNScript(scriptContent);
			}else if (ILDAPScriptProvider.GET_ATTRIBUTE_SET_SCRIPT_ID.equals(scriptID)){
				ldapScriptSet.setGenerateJFireToLdapAttributesScript(scriptContent);
			}else if (ILDAPScriptProvider.GET_PARENT_ENTRIES_SCRIPT_ID.equals(scriptID)){
				ldapScriptSet.setGenerateParentLdapEntriesScript(scriptContent);
			}else if (ILDAPScriptProvider.SYNC_TO_JFIRE_SCRIPT_ID.equals(scriptID)){
				ldapScriptSet.setSyncLdapToJFireScript(scriptContent);
			}
		}
	}

}
