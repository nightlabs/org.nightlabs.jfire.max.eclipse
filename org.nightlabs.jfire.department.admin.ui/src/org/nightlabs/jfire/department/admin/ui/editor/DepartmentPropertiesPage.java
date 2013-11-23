package org.nightlabs.jfire.department.admin.ui.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.editor.RestorableSectionPart;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.department.Department;

public class DepartmentPropertiesPage extends EntityEditorPageWithProgress {

	/**
	 * The id of this page.
	 */
	public static final String ID_PAGE = DepartmentPropertiesPage.class.getName();
	/**
	 * The editor section.
	 */
	private RestorableSectionPart departmentPropertiesSection;

	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link DepartmentPropertiesPage}.
	 */
	public static class Factory implements IEntityEditorPageFactory {

		public IFormPage createPage(FormEditor formEditor) {
			return new DepartmentPropertiesPage(formEditor);
		}
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new DepartmentEditorPageController(editor);
		}
	}

	/**
	 * Create an instance of DepartmentPropertiesPage.
	 * <p>
	 * This constructor is used by the entity editor
	 * page extension system.
	 *
	 * @param editor The editor for which to create this
	 * 		form page.
	 */
	public DepartmentPropertiesPage(FormEditor editor)
	{
		super(editor, ID_PAGE, "Properties");
	}
	
	private DepartmentPropertiesSection dSection;
	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#addSections(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void addSections(Composite parent) {
		dSection = new DepartmentPropertiesSection(this, parent,"Title");
		getManagedForm().addPart(dSection);
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#handleControllerObjectModified(org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent)
	 */
	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				DepartmentEditorPageController controller = (DepartmentEditorPageController)getPageController();
				Department department = controller.getControllerObject();
				dSection.setDepartment(department);
				switchToContent();
			}
		});
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#getPageFormTitle()
	 */
	@Override
	protected String getPageFormTitle() {
		return "Department";
	}
}
