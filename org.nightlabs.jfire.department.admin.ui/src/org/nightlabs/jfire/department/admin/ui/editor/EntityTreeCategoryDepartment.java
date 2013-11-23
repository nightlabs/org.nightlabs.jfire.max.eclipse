/**
 * 
 */
package org.nightlabs.jfire.department.admin.ui.editor;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.ui.IEditorInput;
import org.nightlabs.base.ui.entity.tree.IEntityTreeCategory;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.entity.tree.ActiveJDOEntityTreeCategory;
import org.nightlabs.jfire.department.Department;
import org.nightlabs.jfire.department.dao.DepartmentDAO;
import org.nightlabs.jfire.department.id.DepartmentID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * An {@link IEntityTreeCategory} that shows the departments.
 * 
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class EntityTreeCategoryDepartment 
extends ActiveJDOEntityTreeCategory<DepartmentID, Department> {

	public static String[] FETCH_GROUPS_DEPARTMENT = new String[] {
		FetchPlan.DEFAULT, Department.FETCH_GROUP_NAME
	};
	
	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.entity.tree.ActiveJDOEntityTreeCategory#getJDOObjectClass()
	 */
	@Override
	protected Class<Department> getJDOObjectClass() {
		return Department.class;
	}
	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.entity.tree.ActiveJDOEntityTreeCategory#retrieveJDOObjects(java.util.Set, org.nightlabs.progress.ProgressMonitor)
	 */
	@Override
	protected Collection<Department> retrieveJDOObjects(
			Set<DepartmentID> objectIDs, ProgressMonitor monitor) {
		return DepartmentDAO.sharedInstance().getDepartments(objectIDs,
						FETCH_GROUPS_DEPARTMENT, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}

	@Override
	protected Collection<Department> retrieveJDOObjects(ProgressMonitor monitor) {
		return DepartmentDAO.sharedInstance().getDepartments(
				FETCH_GROUPS_DEPARTMENT, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.entity.tree.ActiveJDOEntityTreeCategory#sortJDOObjects(java.util.List)
	 */
	@Override
	protected void sortJDOObjects(List<Department> objects) {
	}
	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.tree.IEntityTreeCategory#createEditorInput(java.lang.Object)
	 */
	@Override
	public IEditorInput createEditorInput(Object o) {
		Department department = (Department)o;
		DepartmentID departmentID = DepartmentID.create(department.getOrganisationID(), department.getDepartmentID());
		return new DepartmentEditorInput(departmentID);
	}
	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.tree.IEntityTreeCategory#createLabelProvider()
	 */
	@Override
	public ITableLabelProvider createLabelProvider() {
		return new TableLabelProvider() {
			@Override
			public String getColumnText(Object element, int columnIndex) {
				switch (columnIndex) {
					case 0:
						if (element instanceof Department)
							return ((Department) element).getName().getText();
						else
							return String.valueOf(element);

					default:
							return ""; //$NON-NLS-1$
				}
			}
			@Override
			public String getText(Object element) {
				if (element instanceof Department)
					return ((Department) element).getName().getText();
				else
					return String.valueOf(element);
			}
		};
	}
}
