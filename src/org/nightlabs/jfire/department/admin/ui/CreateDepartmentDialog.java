package org.nightlabs.jfire.department.admin.ui;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.department.Department;
import org.nightlabs.jfire.department.dao.DepartmentDAO;
import org.nightlabs.progress.NullProgressMonitor;

public class CreateDepartmentDialog 
extends ResizableTitleAreaDialog
{
	public CreateDepartmentDialog(Shell shell) {
		super(shell, null);
	}

	private CreateDepartmentComposite createDepartmentComposite;
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Create Department");
		setMessage("Create a Department");

		Composite wrapper = new XComposite(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER);
		createDepartmentComposite = new CreateDepartmentComposite(null, wrapper, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		createDepartmentComposite.setLayoutData(gridData);
		return wrapper;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Title");
	}
	
	private static String[] FETCH_GROUP_ISSUE = new String[]{
		FetchPlan.DEFAULT,
		Department.FETCH_GROUP_NAME
	};
	
	@Override
	protected void okPressed() {
		final Department newDepartment = createDepartmentComposite.getCreatingDepartment();
		try {
			Job job = new Job("Setting the default values....") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					final Department department = DepartmentDAO.sharedInstance().storeDepartment(newDepartment, true, FETCH_GROUP_ISSUE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
//					Display.getDefault().asyncExec(new Runnable() {
//						@Override
//						public void run() {
//							DepartmentEditorInput editorInput = new DepartmentEditorInput((DepartmentID)JDOHelper.getObjectId(department));
//							try {
//								Editor2PerspectiveRegistry.sharedInstance().openEditor(editorInput, DepartmentEditor.EDITOR_ID);
//							} catch (Exception e) {
//								throw new RuntimeException(e);
//							}
//						}
//					});
					
					return Status.OK_STATUS;
				}
			};

			job.setPriority(Job.SHORT);
			job.schedule();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		super.okPressed();
	}
}