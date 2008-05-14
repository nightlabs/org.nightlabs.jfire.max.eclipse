package org.nightlabs.jfire.trade.ui;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.login.LoginStateListener;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.GeneralEditor;

/**
 * @author Fitas Amine - fitas at nightlabs dot de
 */

public class LoginStateListenerForGeneralEditor
implements LoginStateListener
{


	@Override
	public void loginStateBeforeChange(int loginState, IAction action)
	{		
		// TODO: is also called at application shutdown (e.g. classloading configuration has changed)
		// and leads to the fact that the AbstractApplication$ExitThread kills the app after 60s
		// and therefore the application do NOT restarts.
		if (loginState == Login.LOGINSTATE_LOGGED_OUT)
			closeAllEditors();	

	
	}

	
	
	@Override
	public void loginStateChanged(int loginState, IAction action)
	{		

		
	}


	private void closeAllEditors()
	{
//		Display.getDefault().syncExec(new Runnable()
//		{
//			public void run()
//			{

				IWorkbench wb = PlatformUI.getWorkbench();
				IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
				IWorkbenchPage page = win.getActivePage();
				IEditorReference[] references = page.getEditorReferences();

				for (IEditorReference reference : references) {
					IEditorPart editor = reference.getEditor(false);
					if (editor instanceof GeneralEditor) {

						if(editor != null)
							page.closeEditor(editor, true);
					}

				}
//
//			}
//		});
	}


}
