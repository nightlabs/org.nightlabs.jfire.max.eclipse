package org.nightlabs.jfire.department.admin.ui.editor;

import javax.jdo.FetchPlan;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.login.ui.part.ICloseOnLogoutEditorPart;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditor;
import org.nightlabs.jfire.department.Department;
import org.nightlabs.jfire.department.dao.DepartmentDAO;
import org.nightlabs.jfire.department.id.DepartmentID;
import org.nightlabs.progress.ProgressMonitor;

public class DepartmentEditor extends ActiveEntityEditor
implements ICloseOnLogoutEditorPart
{
	public static final String EDITOR_ID = DepartmentEditor.class.getName();

//	private DepartmentEditorInput departmentEditorInput;

	private static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		Department.FETCH_GROUP_NAME
	};

	@Override
	protected String getEditorTitleFromEntity(Object entity) {
		return entity instanceof Department ? ((Department)entity).getName().getText() : null;
	}

	@Override
	protected Object retrieveEntityForEditorTitle(ProgressMonitor monitor) {
		DepartmentID departmentID = ((DepartmentEditorInput)getEditorInput()).getJDOObjectID();
		assert departmentID != null;
		return DepartmentDAO.sharedInstance().getDepartment(departmentID, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}

//	@Override
//	public void init(IEditorSite site, IEditorInput input)
//			throws PartInitException {
//		super.init(site, input);
//		departmentEditorInput = (DepartmentEditorInput)input;
//		Job job = new Job("Loading Department.........")
//		{
//			@Override
//			protected IStatus run(ProgressMonitor monitor)
//			throws Exception
//			{
//				final Department department = DepartmentDAO.sharedInstance().getDepartment(
//						departmentEditorInput.getJDOObjectID(),
//						FETCH_GROUPS,
//						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
//				Display.getDefault().asyncExec(new Runnable()
//				{
//					public void run()
//					{
//						setPartName(department.getName().getText());
//						setTitleToolTip(department.getName().getText());
//					}
//				});
//
//				return Status.OK_STATUS;
//			}
//		};
//		job.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
//		job.schedule();
//	}

	@Override
	public void dispose() {
		super.dispose();
	}
}