package org.nightlabs.jfire.trade.ui.overview.account.action;

import javax.jdo.JDOHelper;

import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.action.WorkbenchPartSelectionAction;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.trade.ui.account.editor.AccountEditor;
import org.nightlabs.jfire.trade.ui.account.editor.AccountEditorInput;
import org.nightlabs.jfire.transfer.id.AnchorID;

public class EditAccountAction 
extends WorkbenchPartSelectionAction
{
	public boolean calculateEnabled() {
		return !getSelection().isEmpty();
	}

	public boolean calculateVisible() {
		return true;
	}

	@Override
	public void run() {
		if (getSelectedObjects().isEmpty())
			return;

		AccountEditorInput input = new AccountEditorInput((AnchorID) JDOHelper.getObjectId(getSelectedObjects().get(0)));

		try {
			RCPUtil.openEditor(
					input,
					AccountEditor.EDITOR_ID);
		} catch (PartInitException e) {
			throw new RuntimeException(e); // escalate = leave any action to our exception-handling registry
		}
	}
}
