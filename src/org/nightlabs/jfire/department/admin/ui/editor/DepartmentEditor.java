package org.nightlabs.jfire.department.admin.ui.editor;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.part.ICloseOnLogoutEditorPart;
import org.nightlabs.jfire.department.Department;
import org.nightlabs.jfire.department.dao.DepartmentDAO;
import org.nightlabs.progress.ProgressMonitor;

public class DepartmentEditor extends EntityEditor
implements ICloseOnLogoutEditorPart
{
	public static final String EDITOR_ID = DepartmentEditor.class.getName();
	
	private DepartmentEditorInput departmentEditorInput;
	
	private static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		Department.FETCH_GROUP_NAME
	};
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		departmentEditorInput = (DepartmentEditorInput)input;
		Job job = new Job("Loading Department.........")
		{
			@Override
			protected IStatus run(ProgressMonitor monitor)
			throws Exception
			{
				final Department department = DepartmentDAO.sharedInstance().getDepartment(
						departmentEditorInput.getJDOObjectID(),
						FETCH_GROUPS,
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						setPartName(department.getName().getText());
						setTitleToolTip(department.getName().getText());
					}
				});

				return Status.OK_STATUS;
			}
		};
		job.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		job.schedule();
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}