package org.nightlabs.jfire.auth.ui.ldap.editor;

import java.util.Map;

import org.nightlabs.jfire.base.security.integration.ldap.LDAPScriptSet;

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

	/**
	 * Class for holding different script data in a convinient way.
	 * 
	 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
	 *
	 */
	public static class NamedScript {
		
		private String scriptName;
		private String scriptContent;
		private String scriptDescription;
		
		public NamedScript(String name, String content){
			this.scriptName = name;
			this.scriptContent = content;
		}
		
		public String getScriptName() {
			return scriptName;
		}
		
		public String getScriptContent() {
			return scriptContent;
		}
		
		public void setScriptContent(String scriptContent) {
			this.scriptContent = scriptContent;
		}
		
		public void setScriptDescription(String scriptDescription) {
			this.scriptDescription = scriptDescription;
		}
		
		public String getScriptDescription() {
			return scriptDescription;
		}
	}

	public LDAPServerScriptSetModel(LDAPScriptSet ldapScriptSet) {
		this.ldapScriptSet = ldapScriptSet;
	}
	
	/**
	 * Get content of a script as {@link String} by its name. Script name is one of constants defined in {@link LDAPScriptSetHelper}.
	 * 
	 * @param scriptName script name
	 * @return script's content
	 */
	public String getScriptContentByName(String scriptName){
		if (LDAPScriptSetHelper.BIND_VARIABLES_SCRIPT_NAME.equals(scriptName)){
			return ldapScriptSet.getBindVariablesScript();
		}else if (LDAPScriptSetHelper.GET_ENTRY_NAME_SCRIPT_NAME.equals(scriptName)){
			return ldapScriptSet.getLdapDNScript();
		}else if (LDAPScriptSetHelper.GET_ATTRIBUTE_SET_SCRIPT_NAME.equals(scriptName)){
			return ldapScriptSet.getGenerateJFireToLdapAttributesScript();
		}else if (LDAPScriptSetHelper.GET_PARENT_ENTRIES_SCRIPT_NAME.equals(scriptName)){
			return ldapScriptSet.getGenerateParentLdapEntriesScript();
		}else if (LDAPScriptSetHelper.SYNC_TO_JFIRE_SCRIPT_NAME.equals(scriptName)){
			return ldapScriptSet.getSyncLdapToJFireScript();
		}else{
			return "";
		}
	}

	/**
	 * Sets modified script content to underlying {@link LDAPScriptSet} instance. 
	 * 
	 * @param namedScripts
	 */
	public void commitScriptContent(Map<String, NamedScript> namedScripts){
		for (String scriptName : LDAPScriptSetHelper.getAllScriptNames()){
			String scriptContent = namedScripts.get(scriptName).getScriptContent();
			if (LDAPScriptSetHelper.BIND_VARIABLES_SCRIPT_NAME.equals(scriptName)){
				ldapScriptSet.setBindVariablesScript(scriptContent);
			}else if (LDAPScriptSetHelper.GET_ENTRY_NAME_SCRIPT_NAME.equals(scriptName)){
				ldapScriptSet.setLdapDNScript(scriptContent);
			}else if (LDAPScriptSetHelper.GET_ATTRIBUTE_SET_SCRIPT_NAME.equals(scriptName)){
				ldapScriptSet.setGenerateJFireToLdapAttributesScript(scriptContent);
			}else if (LDAPScriptSetHelper.GET_PARENT_ENTRIES_SCRIPT_NAME.equals(scriptName)){
				ldapScriptSet.setGenerateParentLdapEntriesScript(scriptContent);
			}else if (LDAPScriptSetHelper.SYNC_TO_JFIRE_SCRIPT_NAME.equals(scriptName)){
				ldapScriptSet.setSyncLdapToJFireScript(scriptContent);
			}
		}
	}

}
