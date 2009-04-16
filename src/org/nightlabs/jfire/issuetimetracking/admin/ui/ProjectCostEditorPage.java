package org.nightlabs.jfire.issuetimetracking.admin.ui;

import org.eclipse.swt.SWT;
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
import org.nightlabs.jfire.issuetracking.ui.project.ProjectEditorPageController;

public class ProjectCostEditorPage 
extends EntityEditorPageWithProgress 
{
	/**
	 * The id of this page.
	 */
	public static final String ID_PAGE = ProjectCostEditorPage.class.getName();

	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link ProjectCostEditorPage}. 
	 */
	public static class Factory implements IEntityEditorPageFactory {

		public IFormPage createPage(FormEditor formEditor) {
			return new ProjectCostEditorPage(formEditor);
		}
		
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new ProjectEditorPageController(editor);
		}
	}

	/**
	 * <p>
	 * This constructor is used by the entity editor
	 * page extension system.
	 * 
	 * @param editor The editor for which to create this
	 * 		form page. 
	 */
	public ProjectCostEditorPage(FormEditor editor)
	{
		super(editor, ID_PAGE, "Project Cost");
	}

	private ProjectEditorPageController controller;
//	private ScrolledComposite sc;
	
	//Sections (in order)
	private ProjectCostSection projectCostSection;
	private UserCostSection userCostSection;
	
	@Override
	protected void addSections(Composite parent) {
		controller = (ProjectEditorPageController)getPageController();
		
//		sc = new ScrolledComposite(parent, SWT.H_SCROLL |   
//				  SWT.V_SCROLL);
//		sc.setLayoutData(new GridData(GridData.FILL_BOTH));
		
//		final XComposite mainComposite = new XComposite(sc, SWT.NONE);
		final XComposite mainComposite = new XComposite(parent, SWT.NONE);
		GridLayout layout = (GridLayout)mainComposite.getLayout();
		layout.makeColumnsEqualWidth = true;
		
//		sc.setContent(mainComposite);
//		sc.setExpandHorizontal(true);
//		sc.setExpandVertical(true);

		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		toolkit.decorateFormHeading(getManagedForm().getForm().getForm());
		
		projectCostSection = new ProjectCostSection(this, mainComposite);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		projectCostSection.getSection().setLayoutData(gridData);
		getManagedForm().addPart(projectCostSection);
		
		userCostSection = new UserCostSection(this, mainComposite);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		userCostSection.getSection().setLayoutData(gridData);
		getManagedForm().addPart(userCostSection);
		
		if (controller.isLoaded()) {
		}
	}

	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
		switchToContent();		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
			}
		});
	}
	
	@Override
	protected String getPageFormTitle() {
		return "Project Cost";
	}
	
	protected ProjectEditorPageController getController() {
		return (ProjectEditorPageController)getPageController();
	}
	
	@Override
	protected boolean includeFixForVerticalScrolling() {
		return false;
	}
}