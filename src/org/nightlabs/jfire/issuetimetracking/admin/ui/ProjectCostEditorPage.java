package org.nightlabs.jfire.issuetimetracking.admin.ui;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.CurrencyConstants;
import org.nightlabs.jfire.accounting.Price;
import org.nightlabs.jfire.accounting.dao.CurrencyDAO;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.id.ProjectID;
import org.nightlabs.jfire.issuetimetracking.ProjectCost;
import org.nightlabs.jfire.issuetimetracking.dao.ProjectCostDAO;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectEditorPageController;
import org.nightlabs.progress.NullProgressMonitor;

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

	//Sections (in order)
	private ProjectCostSection projectCostSection;
	private UserCostSection userCostSection;

	@Override
	protected void addSections(Composite parent) {
		controller = (ProjectEditorPageController)getPageController();

		final XComposite mainComposite = new XComposite(parent, SWT.NONE);
		GridLayout layout = (GridLayout)mainComposite.getLayout();
		layout.makeColumnsEqualWidth = true;

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
			final Project project = controller.getProject();

			Job job = new Job("Creating project costs.............") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					final ProjectCost projectCost = ProjectCostDAO.sharedInstance().getProjectCost(
							ProjectID.create(project.getOrganisationID(), project.getProjectID()), 
							FETCH_GROUPS, 
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
							new NullProgressMonitor());

					if (projectCost != null) {
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								projectCostSection.setProjectCost(projectCost);
							}
						});
					}
					else {
						final ProjectCost pc = ProjectCostDAO.sharedInstance().createProjectCost(
								project, 
								CurrencyDAO.sharedInstance().getCurrency(CurrencyConstants.EUR, new NullProgressMonitor()),
								true,
								FETCH_GROUPS,
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								new NullProgressMonitor());

						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								projectCostSection.setProjectCost(pc);
							}
						});

					}
					return Status.OK_STATUS;
				}
			};
			job.setPriority(Job.SHORT);
			job.schedule();
		}
	}

	private static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		ProjectCost.FETCH_GROUP_COST,
		ProjectCost.FETCH_GROUP_REVENUE,
		Price.FETCH_GROUP_CURRENCY
	};

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