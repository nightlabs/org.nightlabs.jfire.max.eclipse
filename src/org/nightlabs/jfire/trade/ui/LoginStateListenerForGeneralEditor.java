package org.nightlabs.jfire.trade.ui;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.nightlabs.base.ui.login.LoginState;
import org.nightlabs.jfire.base.ui.login.LoginStateChangeEvent;
import org.nightlabs.jfire.base.ui.login.LoginStateListener;
import org.nightlabs.jfire.base.ui.login.part.LoginStateListenerForCloseOnLogoutEditorParts;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.IArticleContainerEditor;

/**
 * @author Fitas Amine - fitas at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 * @deprecated replaced by {@link LoginStateListenerForCloseOnLogoutEditorParts}
 */
public class LoginStateListenerForGeneralEditor
implements LoginStateListener
{
	@Override
	public void loginStateChanged(LoginStateChangeEvent event)
	{		
		if (event.getNewLoginState() == LoginState.ABOUT_TO_LOG_OUT)
			closeAllEditors();
	}

	private void closeAllEditors()
	{
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = win.getActivePage();
		IEditorReference[] references = page.getEditorReferences();

		for (IEditorReference reference : references) {
			IEditorPart editor = reference.getEditor(false);
			if (editor instanceof IArticleContainerEditor)
				page.closeEditor(editor, true);
		}
	}
}
