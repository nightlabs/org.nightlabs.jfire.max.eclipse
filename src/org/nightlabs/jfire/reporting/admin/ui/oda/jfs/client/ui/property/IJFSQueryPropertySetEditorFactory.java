/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property;

import org.eclipse.core.runtime.IExecutableExtension;
import org.nightlabs.jfire.reporting.oda.jfs.JFSQueryPropertySet;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;

/**
 * {@link IJFSQueryPropertySetEditorFactory}s create {@link IJFSQueryPropertySetEditor}s
 * which are used to create GUI that heĺps collecting the {@link JFSQueryPropertySet}
 * of a datasource script.
 * <p>
 * {@link IJFSQueryPropertySetEditorFactory}s are registered as extension to the 
 * point <code>org.nightlabs.jfire.reporting.admin.queryPropertySetEditor</code>.
 * </p>
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public interface IJFSQueryPropertySetEditorFactory extends IExecutableExtension {
	/**
	 * Check if this factory can create an appropriate
	 * {@link IJFSQueryPropertySetEditor} to collect the properties of 
	 * the Script referenced by the given scriptID.
	 * 
	 * @param scriptID The {@link ScriptRegistryItemID} to check.
	 * @return Whether this factory can handle the properties of the referenced script.
	 */
	boolean matches(ScriptRegistryItemID scriptID);
	/**
	 * Create the {@link IJFSQueryPropertySetEditor} of this factory.
	 * @return A new {@link IJFSQueryPropertySetEditor}.
	 */
	IJFSQueryPropertySetEditor createJFSQueryPropertySetEditor();
}
