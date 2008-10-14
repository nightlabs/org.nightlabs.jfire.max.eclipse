package org.nightlabs.jfire.issuetracking.ui.project.create;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditorTable;
import org.nightlabs.base.ui.language.II18nTextEditor;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jfire.issue.project.ProjectType;
import org.nightlabs.jfire.issue.project.id.ProjectID;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectAdminTreeComposite;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectTypeComboComposite;

public class CreateProjectWizardPage
extends DynamicPathWizardPage 
{
	private Label projectTypeLabel;
	private ProjectTypeComboComposite projectTypeCombo;
	
	private Label projectNameLabel;
	private I18nTextEditor projectNameText;

	private Label descriptionLabel;
	private I18nTextEditor descriptionText;

	private Button activeButton;
	private boolean isActive = true;
	
	private ProjectID parentProjectID;
	
	@Override
	public Control createPageContents(Composite parent) {
		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TOP_BOTTOM_WRAPPER, LayoutDataMode.GRID_DATA);
		page.getGridLayout().numColumns = 2;
		page.getGridLayout().verticalSpacing = 10;
//		page.getGridLayout().horizontalSpacing = 10;
		
		projectTypeLabel = new Label(page, SWT.NONE);
		projectTypeLabel.setText("Project Type: ");
		projectTypeCombo = new ProjectTypeComboComposite(page, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		projectTypeCombo.setLayoutData(gridData);

		Label sep = new Label(page, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_SOLID);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		sep.setLayoutData(gridData);

//		if (parentProjectID != null) {
//			new Label(parent, SWT.NONE).setText("Parent Project: ");
//			ProjectAdminTreeComposite projectTree = new ProjectAdminTreeComposite(parent, SWT.NONE);
//		}
		projectNameLabel = new Label(page, SWT.NONE);
		projectNameLabel.setText("Project Name: ");

		projectNameText = new I18nTextEditor(page);
		projectNameText.addModifyListener(modifyListener);
		
		descriptionLabel = new Label(page, SWT.WRAP);
		descriptionLabel.setLayoutData(new GridData());
		descriptionLabel.setText("Description:");
		
		descriptionText = new I18nTextEditorMultiLine(page, projectNameText.getLanguageChooser());		
		descriptionText.addModifyListener(modifyListener);
		gridData = new GridData(GridData.FILL_BOTH);
		descriptionText.setLayoutData(gridData);
		
		new Label(page, SWT.NONE).setText("Properties: ");
		
		activeButton = new Button(page, SWT.CHECK);
		activeButton.setText("Active");
		activeButton.setSelection(isActive);
		activeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isActive = activeButton.getSelection();
			}
		});
		gridData = new GridData();
		activeButton.setLayoutData(gridData);
		
		return page;
	}

	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			getContainer().updateButtons();
		}
	};
	
	public CreateProjectWizardPage(ProjectID projectID) {
		super(CreateProjectWizardPage.class.getName(), "Project Page",
				SharedImages.getWizardPageImageDescriptor(IssueTrackingPlugin
						.getDefault(), CreateProjectWizard.class));
		this.setDescription("Description");
		this.parentProjectID = projectID;
	}

	public I18nTextEditor getProjectNameText() {
		return projectNameText;
	}
	
	@Override
	public boolean isPageComplete() {
		return !projectNameText.getEditText().isEmpty();
	}
	
	public ProjectType getSelectedProjectType() {
		return projectTypeCombo.getSelectedProjectType();
	}
	
	public boolean isActive() {
		return isActive;
	}
}