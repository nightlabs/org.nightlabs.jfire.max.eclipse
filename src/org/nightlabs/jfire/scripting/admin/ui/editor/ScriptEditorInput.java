package org.nightlabs.jfire.scripting.admin.ui.editor;

import org.nightlabs.base.ui.editor.JDOObjectEditorInput;
import org.nightlabs.jfire.scripting.ScriptRegistryItem;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;

/**
 *
 * @author vince
 *
 */

public class ScriptEditorInput extends JDOObjectEditorInput<ScriptRegistryItemID> {

	public ScriptEditorInput(ScriptRegistryItemID scriptRegistryItemID) {
		this(scriptRegistryItemID, false);
	}

	public ScriptEditorInput(ScriptRegistryItemID scriptRegistryItemID, boolean createUniqueInput) {
		super(scriptRegistryItemID, createUniqueInput);
		setName(
				String.format(
						"Name",  //$NON-NLS-1$
						ScriptRegistryItem.getPrimaryKey(scriptRegistryItemID.organisationID,
								scriptRegistryItemID.scriptRegistryItemType,
								scriptRegistryItemID.scriptRegistryItemID)
				)
		);
	}
}