package org.nightlabs.jfire.auth.ui.ldap.editor;

import java.util.LinkedHashMap;
import java.util.Map;

import org.nightlabs.jfire.auth.ui.ldap.resource.Messages;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPScriptSet;

/**
 * UI helper class which holds constants with {@link LDAPScriptSet} script names (which are used as script identifiers on UI) and descriptions.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class LDAPScriptSetHelper {

	public static final String SYNC_TO_JFIRE_SCRIPT_NAME = Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.syncToJFireScriptName"); //$NON-NLS-1$
	public static final String GET_PARENT_ENTRIES_SCRIPT_NAME = Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.getParentEntriesScriptName"); //$NON-NLS-1$
	public static final String GET_ATTRIBUTE_SET_SCRIPT_NAME = Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.getAttributeSetScriptName"); //$NON-NLS-1$
	public static final String GET_ENTRY_NAME_SCRIPT_NAME = Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.getEntryNameScriptName"); //$NON-NLS-1$
	public static final String BIND_VARIABLES_SCRIPT_NAME = Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.bindVariablesScriptName"); //$NON-NLS-1$
	
	private static final Map<String, String> scriptsData = new LinkedHashMap<String, String>(5);
	static{
		scriptsData.put(
				BIND_VARIABLES_SCRIPT_NAME, 
				Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.bindVariablesScriptDescription")); //$NON-NLS-1$
		scriptsData.put(
				GET_ENTRY_NAME_SCRIPT_NAME, 
				Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.getEntryNameScriptDescription")); //$NON-NLS-1$
		scriptsData.put(
				GET_ATTRIBUTE_SET_SCRIPT_NAME, 
				Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.getAttributeSetScriptDescription")); //$NON-NLS-1$
		scriptsData.put(
				GET_PARENT_ENTRIES_SCRIPT_NAME, 
				Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.getParentEntriesScriptDescription")); //$NON-NLS-1$
		scriptsData.put(
				SYNC_TO_JFIRE_SCRIPT_NAME, 
				Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.syncToJFireScriptDescription")); //$NON-NLS-1$
	}

	/**
	 * Get all {@link LDAPScriptSet} script names.
	 * 
	 * @return all script names as an {@link Iterable}
	 */
	public static Iterable<String> getAllScriptNames(){
		return scriptsData.keySet();
	}
	
	/**
	 * Get script index (based on its position in internal map) by its name.
	 * 
	 * @param scriptName script name
	 * @return index
	 */
	public static int getScriptNameIndex(String scriptName){
		if (scriptName == null){
			return 0;
		}
		
		int index = 0;
		int i = 0;
		for (String name : getAllScriptNames()){
			if (scriptName.equals(name)){
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
	public static String getScriptDescriptionByName(String scriptName){
		return scriptsData.get(scriptName);
	}

}
