package org.nightlabs.jfire.issuetracking.admin.ui.project;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;

public class ProjectEditorPage extends EntityEditorPageWithProgress {
	/**
	 * The id of this page.
	 */
	public static final String ID_PAGE = ProjectEditorPage.class.getName();

	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link ProjectEditorPage}. 
	 */
	public static class Factory implements IEntityEditorPageFactory {

		public IFormPage createPage(FormEditor formEditor) {
			return new ProjectEditorPage(formEditor);
		}
		
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new ProjectEditorPageController(editor);
		}
	}

	private ProjectSection projectSection;
	/**
	 * <p>
	 * This constructor is used by the entity editor
	 * page extension system.
	 * 
	 * @param editor The editor for which to create this
	 * 		form page. 
	 */
	public ProjectEditorPage(FormEditor editor)
	{
		super(editor, ID_PAGE, "Project");
	}

	@Override
	protected void addSections(Composite parent) {
		ProjectEditorPageController controller = (ProjectEditorPageController)getPageController();
		
		projectSection = new ProjectSection(this, parent, controller);
		getManagedForm().addPart(projectSection);
		
		if (controller.isLoaded()) {
			projectSection.setProject(controller.getProject());
		}
	}

	@Override
	protected void asyncCallback() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleControllerObjectModified(
			EntityEditorPageControllerModifyEvent modifyEvent) {
		switchToContent();		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				ProjectEditorPageController controller = (ProjectEditorPageController)getPageController();
			}
		});
		
	}
	
	@Override
	protected String getPageFormTitle() {
		return "Project";
	}
}
