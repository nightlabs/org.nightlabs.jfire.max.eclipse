package org.nightlabs.jfire.issuetimetracking.admin.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.jdo.FetchPlan;

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
import org.nightlabs.jfire.accounting.Price;
import org.nightlabs.jfire.accounting.PriceFragment;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issuetimetracking.ProjectCost;
import org.nightlabs.jfire.issuetimetracking.ProjectCostValue;
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
			return new ProjectCostEditorPageController(editor);
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

	private ProjectCostEditorPageController controller;

	//Sections (in order)
	private ProjectCostSection projectCostSection;
	private UserCostSection userCostSection;

	private ProjectCost projectCost;
	@Override
	protected void addSections(Composite parent) {
		controller = (ProjectCostEditorPageController)getPageController();

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
		projectCostSection.addPropertyChangeListener(ProjectCostSection.PROPERTY_KEY_CURRENCY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				userCostSection.refresh();
			}
		});

		userCostSection = new UserCostSection(this, mainComposite);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		userCostSection.getSection().setLayoutData(gridData);
		getManagedForm().addPart(userCostSection);
	}

	private static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		Project.FETCH_GROUP_MEMBERS,
		ProjectCost.FETCH_GROUP_PROJECT,
		ProjectCost.FETCH_GROUP_CURRENCY,
		ProjectCost.FETCH_GROUP_DEFAULT_COST,
		ProjectCost.FETCH_GROUP_DEFAULT_REVENUE,
		ProjectCostValue.FETCH_GROUP_COST,
		ProjectCostValue.FETCH_GROUP_REVENUE,
		Price.FETCH_GROUP_CURRENCY,
		Price.FETCH_GROUP_FRAGMENTS,
		PriceFragment.FETCH_GROUP_PRICE_FRAGMENT_TYPE
	};

	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
		switchToContent();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (projectCostSection != null && !projectCostSection.getSection().isDisposed()) {
					projectCostSection.setProjectCost(controller.getControllerObject());
				}

				if (userCostSection != null && !userCostSection.getSection().isDisposed()) {
					userCostSection.setProjectCost(controller.getControllerObject());
				}
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