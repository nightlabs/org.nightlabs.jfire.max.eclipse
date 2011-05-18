package org.nightlabs.jfire.auth.ui.ldap.editor;

import java.util.LinkedHashMap;
import java.util.Map;

import org.nightlabs.jfire.base.security.integration.ldap.LDAPScriptSet;

/**
 * UI helper class which holds constants with {@link LDAPScriptSet} script names (which are used as script identifiers on UI) and descriptions.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class LDAPScriptSetHelper {

	public static final String SYNC_TO_JFIRE_SCRIPT_NAME = "Sync to JFire script";
	public static final String GET_PARENT_ENTRIES_SCRIPT_NAME = "Get parent entries script";
	public static final String GET_ATTRIBUTE_SET_SCRIPT_NAME = "Get attribute set script";
	public static final String GET_ENTRY_NAME_SCRIPT_NAME = "Get entry name script";
	public static final String BIND_VARIABLES_SCRIPT_NAME = "Bind variables script";
	
	private static final Map<String, String> scriptsData = new LinkedHashMap<String, String>(5);
	static{
		scriptsData.put(
				BIND_VARIABLES_SCRIPT_NAME, 
				"Used for binding script variables to values taken from JFire objects (e.g. User and Person)." +
				"These variables are used afterwards in other scripts. Java objects for User and Person " +
				"are available inside this script. NOT supposed to return any values.");
		scriptsData.put(
				GET_ENTRY_NAME_SCRIPT_NAME, 
				"Used for generating a String with an LDAP entry Distingueshed Name which is built usign User and/or Person object's data. " +
				"Should return a String with an LDAP entry DN on the last line of the script.");
		scriptsData.put(
				GET_ATTRIBUTE_SET_SCRIPT_NAME, 
				"Used for generating a LDAPAttributeSet with attributes names and values which are then passed to LDAP modidifcation calls." +
				"Such modifications happen during synchronization of user data from JFire to LDAP directory when JFire is a leading system." +
				"This script makes use of variables from bind variables script. Should return LDAPAttributeSet on the last line of the script.");
		scriptsData.put(
				GET_PARENT_ENTRIES_SCRIPT_NAME, 
				"Used for generating a List with names of LDAP entries which are parents to all LDAP user entries which should be synchronized. " +
				"These parent entries are queried during synchronization when LDAPServer is a leading system. Should return an ArrayList of Strings " +
				"with entries names on the last line of the script.");
		scriptsData.put(
				SYNC_TO_JFIRE_SCRIPT_NAME, 
				"Used for storing data into JFire objects (User and/or Person) during synchronization when LDAPServer is a leading system. " +
				"It makes use of several java objects : allAtributes - LDAPAttributeSet with all attributes of LDAP entry to be synchronized, " +
				"pm - PersistenceManager, organisationID - the ID of JFire organisation, newPersonID - value returned by " +
				"IDGenerator.nextID(PropertySet.class) used when new Person object is created, logger - org.slf4j.Logger for debug purposes. " +
				"Should return persisted object (either User or Person) on the last line of the script.");
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
