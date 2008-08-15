/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property;

import org.nightlabs.jfire.reporting.oda.jfs.JFSQueryPropertySet;

/**
 * Table element used by {@link JFSQueryPropertySetTable}.
 * It holds the name and value of a property in a {@link JFSQueryPropertySet}
 * and also the information whether the property is refrenced
 * in the meta-data of the {@link JFSQueryPropertySet}.
 *  
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class JFSQueryPropertySetTableEntry {
	private boolean fromMetaData;
	private boolean required;
	private String name;
	private String value;
	
	/**
	 * Create a new {@link JFSQueryPropertySetTableEntry}.
	 * 
	 * @param fromMetaData Whether this entry comes from the meta-data.
	 * @param required If the property of this entry is required.
	 * @param name The name of the property.
	 */
	public JFSQueryPropertySetTableEntry(boolean fromMetaData, boolean required, String name) {
		super();
		this.fromMetaData = fromMetaData;
		this.required = required;
		this.name = name;
	}
	/**
	 * @return The value of the property of this entry.
	 */
	public String getValue() {
		return value;
	}
	/**
	 * Set the name of the property of this entry.
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Set the value of the property of this entry.
	 * @param value The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return Whether the property of this entry came from the meta-data.
	 */
	public boolean isFromMetaData() {
		return fromMetaData;
	}
	/**
	 * @return Whether the property of this entry is reqired. 
	 */
	public boolean isRequired() {
		return required;
	}
	/**
	 * @return The name of the property of this entry.
	 */
	public String getName() {
		return name;
	}
}