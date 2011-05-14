package org.nightlabs.jfire.auth.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.nightlabs.base.ui.entity.tree.EntityTree;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectController;
import org.nightlabs.jfire.base.ui.login.action.LSDWorkbenchWindowActionDelegate;
import org.nightlabs.jfire.security.dao.UserManagementSystemDAO;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.id.UserManagementSystemID;

/**
 * Action for removing persistent {@link UserManagementSystem}s selected in {@link EntityTree}.
 * TODO: tree should refresh after object is deleted, need add corresponding support in {@link ActiveJDOObjectController}.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class RemoveUserManagementSystemAction extends LSDWorkbenchWindowActionDelegate{
	
	private Collection<UserManagementSystemID> selectedUserManagementSystemIDs;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run(IAction action)	{
		try	{
			if (selectedUserManagementSystemIDs != null
					&& !selectedUserManagementSystemIDs.isEmpty()
					&& MessageDialog.openConfirm(RCPUtil.getActiveShell(), "Delete user management system(s)", "Delete selected user management system(s)?")){
				
				ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(RCPUtil.getActiveShell());
				progressDialog.setOpenOnRun(true);
				progressDialog.run(false, false, new IRunnableWithProgress() {
					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						
						UserManagementSystemDAO.sharedInstance().removeUserManagementSystems(selectedUserManagementSystemIDs, new ProgressMonitorWrapper(monitor));

						RCPUtil.getActiveShell().getDisplay().asyncExec(new Runnable() {
							@Override
							public void run() {
								MessageDialog.openInformation(RCPUtil.getActiveShell(), "Delete user management system(s)", "User management system(s) were removed.");
							}
						});
					}
				});
				
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Tracks selection of {@link UserManagementSystem} objects.
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		action.setEnabled(false);

		if (!(selection instanceof IStructuredSelection)){
			return;
		}

		IStructuredSelection sel = (IStructuredSelection) selection;
		Set<UserManagementSystemID> userManagementSystemIDs = new HashSet<UserManagementSystemID>();

		for (Iterator<?> it = sel.iterator(); it.hasNext(); ) {
			Object o = it.next();
			Object oid = JDOHelper.getObjectId(o);
			if (oid instanceof UserManagementSystemID) {
				userManagementSystemIDs.add((UserManagementSystemID) oid);
			}
		}

		selectedUserManagementSystemIDs = userManagementSystemIDs;
		action.setEnabled(!selectedUserManagementSystemIDs.isEmpty());

		super.selectionChanged(action, selection);
	}
}
