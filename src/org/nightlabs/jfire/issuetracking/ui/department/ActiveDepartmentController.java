package org.nightlabs.jfire.issuetracking.ui.department;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectController;
import org.nightlabs.jfire.issue.project.Department;
import org.nightlabs.jfire.issue.project.DepartmentDAO;
import org.nightlabs.jfire.issue.project.id.DepartmentID;
import org.nightlabs.progress.ProgressMonitor;

public class ActiveDepartmentController 
extends ActiveJDOObjectController<DepartmentID, Department>
{
	private static final String[] FETCH_GROUPS_DEPARTMENT = {
		FetchPlan.DEFAULT,
		Department.FETCH_GROUP_NAME
	};


	@Override
	protected Class<? extends Department> getJDOObjectClass() {
		return Department.class;
	}

	@Override
	protected Collection<Department> retrieveJDOObjects(
			Set<DepartmentID> objectIDs, ProgressMonitor monitor) {
		return DepartmentDAO.sharedInstance().getDepartments(objectIDs, FETCH_GROUPS_DEPARTMENT, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}

	@Override
	protected Collection<Department> retrieveJDOObjects(ProgressMonitor monitor) {
		return DepartmentDAO.sharedInstance().getDepartments(FETCH_GROUPS_DEPARTMENT, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}

	@Override
	protected void sortJDOObjects(List<Department> objects) {
		Collections.sort(objects, new Comparator<Department>() {
			@Override
			public int compare(Department o1, Department o2)
			{
				return ((Department) o1).getName().getText().compareTo(
						((Department) o2).getName().getText());
			}
		});
	}

}
