package org.nightlabs.jfire.department.admin.ui.editor;

import org.nightlabs.base.ui.editor.JDOObjectEditorInput;
import org.nightlabs.jfire.department.id.DepartmentID;

/**
 * Editor input for {@link DepartmentEditor}s.
 * 
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class DepartmentEditorInput extends JDOObjectEditorInput<DepartmentID>
{
	/**
	 * Constructor for an existing department.
	 * @param departmentID The department
	 */
	public DepartmentEditorInput(DepartmentID departmentID)
	{
		super(departmentID);
		setName("Editor Input Name");
	}
}