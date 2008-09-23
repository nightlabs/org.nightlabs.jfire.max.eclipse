package org.nightlabs.jfire.issuetracking.admin.ui.project;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorPageController;

public class ProjectEditorPage 
extends EntityEditorPageWithProgress 
{
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
	private ProjectMemberSection projectmemberSection;
	
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

	private ProjectEditorPageController controller;
	
	private ScrolledComposite sc;
	@Override
	protected void addSections(Composite parent) {
		controller = (ProjectEditorPageController)getPageController();
		
		projectSection = new ProjectSection(this, parent, controller);
		getManagedForm().addPart(projectSection);
		
		projectmemberSection = new ProjectMemberSection(this, parent, controller);
		getManagedForm().addPart(projectmemberSection);
		
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
				if (projectSection != null && !projectSection.getSection().isDisposed()) {
					projectSection.setProject(controller.getProject());
				}
			}
		});
		
	}
	
	@Override
	protected String getPageFormTitle() {
		return "Project";
	}
	
	protected ProjectEditorPageController getController() {
		return (ProjectEditorPageController)getPageController();
	}
}
