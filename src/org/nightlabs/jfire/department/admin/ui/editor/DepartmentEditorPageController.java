package org.nightlabs.jfire.department.admin.ui.editor;

import javax.jdo.FetchPlan;

import org.apache.log4j.Logger;
import org.nightlabs.base.ui.editor.JDOObjectEditorInput;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.jfire.department.Department;
import org.nightlabs.jfire.department.dao.DepartmentDAO;
import org.nightlabs.jfire.department.id.DepartmentID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * A controller that loads a department
 *
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class DepartmentEditorPageController extends ActiveEntityEditorPageController<Department>
{

	private static final String[] FETCH_GROUPS_DEPARTMENT = new String[] {
		FetchPlan.DEFAULT,
		Department.FETCH_GROUP_NAME,
		Department.FETCH_GROUP_DESCRIPTION}
	;

	private static final long serialVersionUID = -1651161683093714800L;

	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(DepartmentEditorPageController.class);

	/**
	 * The department id.
	 */
	private DepartmentID departmentID;

	/**
	 * Create an instance of this controller for
	 * an {@link DepartmentEditor} and load the data.
	 */
	public DepartmentEditorPageController(EntityEditor editor)
	{
		super(editor);
		this.departmentID = (DepartmentID) ((JDOObjectEditorInput<?>)editor.getEditorInput()).getJDOObjectID();
	}

	@Override
	protected Department retrieveEntity(ProgressMonitor monitor) {
		monitor.beginTask("Begin Task", 1);
		try {
			if(departmentID != null) {
				// load department
				Department department = DepartmentDAO.sharedInstance().getDepartment(
						departmentID, getEntityFetchGroups(),
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new SubProgressMonitor(monitor, 1)
				);
				monitor.worked(1);
				return department;
			}
			return null;
		} catch(Exception e) {
			throw new RuntimeException(e);
		} finally {
			monitor.done();
		}
	}
	
	@Override
	protected Department storeEntity(Department controllerObject, ProgressMonitor monitor) {
		monitor.beginTask("Loading.............", 6);
		try	{
			monitor.worked(1);
			return DepartmentDAO.sharedInstance().storeDepartment(
					controllerObject, true, getEntityFetchGroups(),
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 5)
			);
		} catch(Exception e) {
			monitor.setCanceled(true);
			throw new RuntimeException(e);
		} finally {
			monitor.done();
		}
	}

	@Override
	protected String[] getEntityFetchGroups() {
		return FETCH_GROUPS_DEPARTMENT;
	}
}
