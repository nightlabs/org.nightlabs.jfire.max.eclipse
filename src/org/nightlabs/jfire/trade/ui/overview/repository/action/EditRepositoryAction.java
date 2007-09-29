package org.nightlabs.jfire.trade.ui.overview.repository.action;

import javax.jdo.JDOHelper;

import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.action.WorkbenchPartSelectionAction;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.trade.ui.repository.editor.RepositoryEditor;
import org.nightlabs.jfire.trade.ui.repository.editor.RepositoryEditorInput;
import org.nightlabs.jfire.transfer.id.AnchorID;

public class EditRepositoryAction
extends WorkbenchPartSelectionAction
{
	public boolean calculateEnabled()
	{
		return !getSelectedObjects().isEmpty();
	}

	public boolean calculateVisible()
	{
		return true;
	}

	@Override
	public void run()
	{
		if (getSelectedObjects().isEmpty())
			return;

		RepositoryEditorInput input = new RepositoryEditorInput((AnchorID) JDOHelper.getObjectId(getSelectedObjects().get(0)));

		try {
			RCPUtil.openEditor(
					input,
					RepositoryEditor.EDITOR_ID);
		} catch (PartInitException e) {
			throw new RuntimeException(e); // escalate = leave any action to our exception-handling registry
		}
	}
}
