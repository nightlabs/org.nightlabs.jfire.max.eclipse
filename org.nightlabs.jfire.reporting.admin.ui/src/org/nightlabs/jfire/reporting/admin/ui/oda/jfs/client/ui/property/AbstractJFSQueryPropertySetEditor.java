/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property;

import org.nightlabs.jfire.reporting.oda.jfs.JFSQueryPropertySet;

/**
 * Base class for the implementation of {@link IJFSQueryPropertySetEditor}.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public abstract class AbstractJFSQueryPropertySetEditor implements IJFSQueryPropertySetEditor {

	private JFSQueryPropertySet queryPropertySet;
	
	public AbstractJFSQueryPropertySetEditor() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.IJFSQueryPropertySetEditor#init(org.nightlabs.jfire.reporting.oda.jfs.JFSQueryPropertySet)
	 */
	@Override
	public void setJFSQueryPropertySet(JFSQueryPropertySet queryPropertySet) {
		this.queryPropertySet = queryPropertySet;
	}

	/**
	 * @return The {@link JFSQueryPropertySet} set with {@link #setJFSQueryPropertySet(JFSQueryPropertySet)}.
	 */
	protected JFSQueryPropertySet getQueryPropertySet() {
		return queryPropertySet;
	}
}
