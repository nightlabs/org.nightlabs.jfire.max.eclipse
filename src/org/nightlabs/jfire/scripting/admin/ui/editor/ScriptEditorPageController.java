package org.nightlabs.jfire.scripting.admin.ui.editor;

import javax.jdo.FetchPlan;

import org.eclipse.ui.IEditorInput;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.jfire.scripting.Script;
import org.nightlabs.jfire.scripting.ScriptParameterSet;
import org.nightlabs.jfire.scripting.ScriptRegistryItem;
import org.nightlabs.jfire.scripting.dao.ScriptRegistryItemDAO;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.progress.ProgressMonitor;

/**
 *
 * @author vince
 *
 */
public class ScriptEditorPageController
extends ActiveEntityEditorPageController<Script>
{
	public ScriptEditorPageController(EntityEditor editor){
		super(editor);
	}

	@Override
	protected IEditorInput createNewInstanceEditorInput() {
		return new ScriptEditorInput(getScriptID(), true);
	}

	@Override
	protected String[] getEntityFetchGroups() {
		return new String[]{ FetchPlan.DEFAULT,
				Script.FETCH_GROUP_NAME,
				Script.FETCH_GROUP_DESCRIPTION,
				ScriptRegistryItem.FETCH_GROUP_PARAMETER_SET,
				ScriptParameterSet.FETCH_GROUP_PARAMETERS};
	}

	@Override
	protected Script retrieveEntity(ProgressMonitor monitor) {
		ScriptEditorInput input = (ScriptEditorInput)getEntityEditor().getEditorInput();
		ScriptRegistryItem item = ScriptRegistryItemDAO.sharedInstance().getScriptRegistryItem(
				input.getJDOObjectID(),
				getEntityFetchGroups(),
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor);



		return (Script)item;
	}

	@Override
	protected Script storeEntity(Script controllerObject, ProgressMonitor monitor) {
		return (Script) ScriptRegistryItemDAO.sharedInstance().storeRegistryItem(
				controllerObject,
				true,
				getEntityFetchGroups(),
				getEntityMaxFetchDepth(),
				monitor
		);
	}

	protected ScriptRegistryItemID getScriptID() {
		ScriptEditorInput input = (ScriptEditorInput) getEntityEditor().getEditorInput();
		return input.getJDOObjectID();
	}

	public Script getScript() {
		return getControllerObject();
	}

	@Override
	public void fireModifyEvent(Object oldObject, Object newObject, boolean resetDirtyState) {
		super.fireModifyEvent(oldObject, newObject, resetDirtyState);
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (getEntityEditor().getActivePageInstance() instanceof ScriptEditorContentPage) {
			ScriptEditorContentPage contentPage = (ScriptEditorContentPage) getEntityEditor().getActivePageInstance();
			contentPage.getManagedForm().dirtyStateChanged();
		}
	}

	@Override
	public void markUndirty() {
		super.markUndirty();
		if (getEntityEditor().getActivePageInstance() instanceof ScriptEditorContentPage) {
			ScriptEditorContentPage contentPage = (ScriptEditorContentPage) getEntityEditor().getActivePageInstance();
			contentPage.getManagedForm().dirtyStateChanged();
		}
	}
}