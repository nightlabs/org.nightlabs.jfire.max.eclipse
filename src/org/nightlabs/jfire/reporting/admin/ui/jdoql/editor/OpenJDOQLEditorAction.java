/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.jdoql.editor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.nightlabs.base.ui.util.RCPUtil;

/**
 * @author Alexander Bieber <alex [AT] nightlabs [DOT] de>
 *
 */
public class OpenJDOQLEditorAction implements IWorkbenchWindowActionDelegate {

	/**
	 * 
	 */
	public OpenJDOQLEditorAction() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		try {
			RCPUtil.openEditor(new JDOQLEditorInput(), JDOQLEditor.class.getName());
//			RCPUtil.openEditor(new FileEditorInput(ResourcesPlugin.getWorkspace().getRoot().getProject("ReportLocalisation").getFile("JFireReportingTrade-Reporting-Invoice-Default-InvoiceLayout/reportMessages.properties")), "com.essiembre.eclipse.rbe.ui.editor.ResourceBundleEditor");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

}
