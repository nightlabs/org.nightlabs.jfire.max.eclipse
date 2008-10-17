package org.nightlabs.jfire.issuetracking.ui.projectphase;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;

public class ProjectPhaseEditorPage 
extends EntityEditorPageWithProgress 
{
	/**
	 * The id of this page.
	 */
	public static final String ID_PAGE = ProjectPhaseEditorPage.class.getName();

	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link ProjectPhaseEditorPage}. 
	 */
	public static class Factory implements IEntityEditorPageFactory {

		public IFormPage createPage(FormEditor formEditor) {
			return new ProjectPhaseEditorPage(formEditor);
		}
		
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new ProjectPhaseEditorPageController(editor);
		}
	}

//	private ProjectPhaseSection projectPhaseSection;
	
	/**
	 * <p>
	 * This constructor is used by the entity editor
	 * page extension system.
	 * 
	 * @param editor The editor for which to create this
	 * 		form page. 
	 */
	public ProjectPhaseEditorPage(FormEditor editor)
	{
		super(editor, ID_PAGE, "Project Phase");
	}

	private ProjectPhaseEditorPageController controller;
	
	@Override
	protected void addSections(Composite parent) {
		controller = (ProjectPhaseEditorPageController)getPageController();
		
//		projectPhaseSection = new ProjectPhaseSection(this, parent, controller);
//		getManagedForm().addPart(projectPhaseSection);
//		
//		if (controller.isLoaded()) {
//			projectPhaseSection.setProject(controller.getProjectPhase());
//		}
	}

	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
		switchToContent();		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
//				if (projectPhaseSection != null && !projectPhaseSection.getSection().isDisposed()) {
//					projectPhaseSection.setProject(controller.getProject());
//				}
			}
		});
		
	}
	
	@Override
	protected String getPageFormTitle() {
		return "Project Phase";
	}
	
	protected ProjectPhaseEditorPageController getController() {
		return (ProjectPhaseEditorPageController)getPageController();
	}
}
