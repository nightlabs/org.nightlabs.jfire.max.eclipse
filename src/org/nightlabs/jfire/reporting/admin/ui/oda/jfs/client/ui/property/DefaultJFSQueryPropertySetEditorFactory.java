/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property;

import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;

/**
 * The default {@link IJFSQueryPropertySetEditorFactory} that is used when
 * no other matches the {@link ScriptRegistryItemID} of a JFS script.
 * It creates {@link DefaultJFSQueryPropertySetEditor}s.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class DefaultJFSQueryPropertySetEditorFactory extends AbstractJFSQueryPropertySetEditorFactory {


	public DefaultJFSQueryPropertySetEditorFactory() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.IJFSQueryPropertySetEditorFactory#createJFSQueryPropertySetEditor()
	 */
	@Override
	public IJFSQueryPropertySetEditor createJFSQueryPropertySetEditor() {
		return new DefaultJFSQueryPropertySetEditor();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.IJFSQueryPropertySetEditorFactory#matches(org.nightlabs.jfire.scripting.id.ScriptRegistryItemID)
	 */
	@Override
	public boolean matches(ScriptRegistryItemID scriptID) {
		return true;
	}

}
