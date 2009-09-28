package org.nightlabs.jfire.scripting.admin.ui.editor;
import javax.jdo.FetchGroup;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditor;
import org.nightlabs.jfire.scripting.Script;
import org.nightlabs.jfire.scripting.ScriptRegistryItem;
import org.nightlabs.jfire.scripting.dao.ScriptRegistryItemDAO;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.progress.ProgressMonitor;

/**
 *
 * @author vince
 *
 */
public class ScriptEditor extends ActiveEntityEditor {

	public static final String ID_EDITOR =ScriptEditor.class.getName();

	@Override
	protected String getEditorTitleFromEntity(Object entity) {
		String text=((Script)entity).getName().getText();
		return text;
	}

	@Override
	protected Object retrieveEntityForEditorTitle(ProgressMonitor monitor) {
		ScriptEditorInput input=(ScriptEditorInput)getEditorInput();
		ScriptRegistryItemID scriptRegistryItemID=input.getJDOObjectID();
		ScriptRegistryItem scriptRegistryItem = ScriptRegistryItemDAO.sharedInstance().getScriptRegistryItem(
				scriptRegistryItemID, new String[]{ FetchGroup.DEFAULT, Script.FETCH_GROUP_NAME }, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		return  scriptRegistryItem;
	}
}