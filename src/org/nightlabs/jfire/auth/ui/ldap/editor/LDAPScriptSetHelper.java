package org.nightlabs.jfire.auth.ui.ldap.editor;

import java.util.LinkedHashMap;

import org.nightlabs.jfire.auth.ui.ldap.resource.Messages;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPScriptSet;
import org.nightlabs.jfire.base.security.integration.ldap.scripts.ILDAPScriptProvider;

/**
 * UI helper class which holds constants with {@link LDAPScriptSet} script names (which are used as script identifiers on UI) and descriptions.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class LDAPScriptSetHelper {

	/**
	 * Class for holding different script data in a convinient way.
	 * 
	 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
	 *
	 */
	public static class NamedScript implements Cloneable{
		
		private String scriptID;
		private String scriptName;
		private String scriptContent;
		private String scriptDescription;

		public NamedScript(String id, String name, String description){
			this.scriptID = id;
			this.scriptName = name;
			this.scriptDescription = description;
		}
		
		public String getScriptID() {
			return scriptID;
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
		
		@Override
		public NamedScript clone(){
			NamedScript clonedScript = new NamedScript(this.scriptID, this.scriptName, this.scriptDescription);
			clonedScript.setScriptContent(this.scriptContent);
			return clonedScript;
		}
	}

	private static LinkedHashMap<String, NamedScript> ldapScriptData = new LinkedHashMap<String, LDAPScriptSetHelper.NamedScript>(5);
	static{
		ldapScriptData.put(
				ILDAPScriptProvider.BIND_VARIABLES_SCRIPT_ID,
				new NamedScript(
						ILDAPScriptProvider.BIND_VARIABLES_SCRIPT_ID, 
						Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.bindVariablesScriptName"), 
						Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.bindVariablesScriptDescription")));
		ldapScriptData.put(
				ILDAPScriptProvider.GET_ATTRIBUTE_SET_SCRIPT_ID,
				new NamedScript(
						ILDAPScriptProvider.GET_ATTRIBUTE_SET_SCRIPT_ID, 
						Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.getAttributeSetScriptName"), 
						Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.getAttributeSetScriptDescription")));
		ldapScriptData.put(
				ILDAPScriptProvider.GET_ENTRY_NAME_SCRIPT_ID,
				new NamedScript(
						ILDAPScriptProvider.GET_ENTRY_NAME_SCRIPT_ID, 
						Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.getEntryNameScriptName"), 
						Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.getEntryNameScriptDescription")));
		ldapScriptData.put(
				ILDAPScriptProvider.GET_PARENT_ENTRIES_SCRIPT_ID,
				new NamedScript(
						ILDAPScriptProvider.GET_PARENT_ENTRIES_SCRIPT_ID, 
						Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.getParentEntriesScriptName"), 
						Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.getParentEntriesScriptDescription")));
		ldapScriptData.put(
				ILDAPScriptProvider.SYNC_TO_JFIRE_SCRIPT_ID,
				new NamedScript(
						ILDAPScriptProvider.SYNC_TO_JFIRE_SCRIPT_ID, 
						Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.syncToJFireScriptName"), 
						Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.syncToJFireScriptDescription")));
	}

	/**
	 * Get all {@link LDAPScriptSet} script wrapped as {@link NamedScript}.
	 * 
	 * @return all {@link NamedScript}s as an {@link Iterable}
	 */
	public static Iterable<NamedScript> getNamedScripts(){
		return ldapScriptData.values();
	}
	
	/**
	 * Get script index (based on its position in internal map) by its name.
	 * 
	 * @param scriptName script name
	 * @return index
	 */
	public static int getScriptIndexByID(String scriptID){
		if (scriptID == null){
			return 0;
		}
		
		int index = 0;
		int i = 0;
		for (String id : ldapScriptData.keySet()){
			if (scriptID.equals(id)){
				index = i;
				break;
			}
			i++;
		}
		return index;
	}
	
	/**
	 * Get script description by its name.
	 * 
	 * @param scriptName script name
	 * @return script description
	 */
	public static String getScriptDescriptionByID(String scriptID){
		if (ldapScriptData.get(scriptID) != null){
			return ldapScriptData.get(scriptID).getScriptDescription();
		}
		return "";
	}

}
