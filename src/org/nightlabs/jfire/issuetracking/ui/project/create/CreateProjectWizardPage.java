package org.nightlabs.jfire.issuetracking.ui.project.create;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.language.I18nTextEditorTable;
import org.nightlabs.base.ui.language.II18nTextEditor;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jfire.issue.project.ProjectType;
import org.nightlabs.jfire.issue.project.id.ProjectID;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectTypeComboComposite;

public class CreateProjectWizardPage
extends DynamicPathWizardPage 
{
	private Label projectTypeLabel;
	private ProjectTypeComboComposite projectTypeCombo;
	
	private Label projectNameLabel;
	
	private I18nTextBuffer projectNameBuffer;
	private I18nTextEditorTable projectNameEditor;

	private ProjectID parentProjectID;
	
	@Override
	public Control createPageContents(Composite parent) {
		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TOP_BOTTOM_WRAPPER, LayoutDataMode.GRID_DATA);
		page.getGridLayout().numColumns = 2;
		
		projectTypeLabel = new Label(page, SWT.NONE);
		projectTypeLabel.setText("Project Type: ");
		projectTypeCombo = new ProjectTypeComboComposite(page, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		projectTypeCombo.setLayoutData(gridData);
		
		projectNameLabel = new Label(page, SWT.NONE);
		projectNameLabel.setText("Project Name: ");
		projectNameBuffer = new I18nTextBuffer();
		projectNameEditor = new I18nTextEditorTable(page);
		projectNameEditor.setI18nText(projectNameBuffer);
		projectNameEditor.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				getWizard().getContainer().updateButtons();
			}
		});
		gridData = new GridData(GridData.FILL_BOTH);
		projectNameEditor.setLayoutData(gridData);

//		Button activeButton = new Button(page, SWT.CHECK);
//		activeButton.setText("Active");
//		gridData = new GridData();
//		gridData.horizontalAlignment = GridData.END;
//		activeButton.setLayoutData(gridData);
		
//		Button advanceButton = new Button(page, SWT.PUSH);
//		advanceButton.setText("Advance");
		
		return page;
	}

	public CreateProjectWizardPage(ProjectID projectID) {
		super(CreateProjectWizardPage.class.getName(), "Project Page",
				SharedImages.getWizardPageImageDescriptor(IssueTrackingPlugin
						.getDefault(), CreateProjectWizard.class));
		this.setDescription("Description");
		
		this.parentProjectID = projectID;
	}

	public II18nTextEditor getProjectTypeNameEditor() {
		return projectNameEditor;
	}

	public I18nTextBuffer getProjectTypeNameBuffer() {
		return projectNameBuffer;
	}

	@Override
	public boolean isPageComplete() {
		return !projectNameBuffer.isEmpty();
	}
	
	public ProjectType getSelectedProjectType() {
		return projectTypeCombo.getSelectedProjectType();
	}
}