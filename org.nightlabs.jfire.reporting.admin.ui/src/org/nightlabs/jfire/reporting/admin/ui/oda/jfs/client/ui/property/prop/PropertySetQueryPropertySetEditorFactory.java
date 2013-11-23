/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.prop;

import org.nightlabs.jfire.reporting.ReportingScriptConstants;
import org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.AbstractJFSQueryPropertySetEditorFactory;
import org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.IJFSQueryPropertySetEditor;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PropertySetQueryPropertySetEditorFactory extends AbstractJFSQueryPropertySetEditorFactory {

	public PropertySetQueryPropertySetEditorFactory() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.IJFSQueryPropertySetEditorFactory#createJFSQueryPropertySetEditor()
	 */
	@Override
	public IJFSQueryPropertySetEditor createJFSQueryPropertySetEditor() {
		return new PropertySetQueryPropertySetEditor();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.IJFSQueryPropertySetEditorFactory#matches(org.nightlabs.jfire.scripting.id.ScriptRegistryItemID)
	 */
	@Override
	public boolean matches(ScriptRegistryItemID scriptID) {
		return scriptID.equals(ReportingScriptConstants.SCRIPT_REGISTRY_ITEM_ID_PROPERTY_SET);
	}

}
