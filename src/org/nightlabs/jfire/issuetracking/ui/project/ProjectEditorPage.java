package org.nightlabs.jfire.issuetracking.ui.project;

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
import org.nightlabs.jfire.issuetracking.ui.projectphase.ProjectPhaseSection;

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
	private ProjectPhaseSection projectPhaseSection;
	private ProjectMemberSection projectMemberSection;
	
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
		
		sc = new ScrolledComposite(parent, SWT.H_SCROLL |   
				  SWT.V_SCROLL);
		sc.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		final XComposite c = new XComposite(sc, SWT.NONE);
		GridLayout layout = (GridLayout)c.getLayout();
		layout.makeColumnsEqualWidth = true;

		sc.setContent(c);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		projectSection = new ProjectSection(this, c, controller);
		getManagedForm().addPart(projectSection);
		
		projectPhaseSection = new ProjectPhaseSection(this, c, controller);
		getManagedForm().addPart(projectPhaseSection);
		
		projectMemberSection = new ProjectMemberSection(this, c, controller);
		getManagedForm().addPart(projectMemberSection);
		
		if (controller.isLoaded()) {
			projectSection.setProject(controller.getProject());
			projectPhaseSection.setProject(controller.getProject());
			projectMemberSection.setProject(controller.getProject());
		}
	}

	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
		switchToContent();		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (projectSection != null && !projectSection.getSection().isDisposed()) {
					projectSection.setProject(controller.getProject());
				}
				
				if (projectPhaseSection != null && !projectPhaseSection.getSection().isDisposed()) {
					projectPhaseSection.setProject(controller.getProject());
				}
				
				if (projectMemberSection != null && !projectMemberSection.getSection().isDisposed()) {
					projectMemberSection.setProject(controller.getProject());
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
	
	@Override
	protected boolean includeFixForVerticalScrolling() {
		return false;
	}
}
