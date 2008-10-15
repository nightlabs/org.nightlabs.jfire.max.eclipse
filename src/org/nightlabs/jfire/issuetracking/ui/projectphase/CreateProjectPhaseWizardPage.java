package org.nightlabs.jfire.issuetracking.ui.projectphase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectPhase;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;

public class CreateProjectPhaseWizardPage extends DynamicPathWizardPage
{
	public CreateProjectPhaseWizardPage(String pageName) {
		super(pageName);
	}

	private Label phaseNameLabel;
	private I18nTextEditor phaseNameText;

	private Label descriptionLabel;
	private I18nTextEditor descriptionText;
	
	private Button activeButton;
	private boolean isActive = true;
	
	private Project project;
	private ProjectPhase newProjectPhase;

	@Override
	public Control createPageContents(Composite parent) {
		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		page.getGridLayout().numColumns = 2;
		
		phaseNameLabel = new Label(page, SWT.WRAP);
		phaseNameLabel.setLayoutData(new GridData());
		phaseNameLabel.setText("Phase Name:");
		
		phaseNameText = new I18nTextEditor(page);
		phaseNameText.setI18nText(newProjectPhase.getName(), EditMode.DIRECT);
		phaseNameText.addModifyListener(modifyListener);

		descriptionLabel = new Label(page, SWT.WRAP);
		descriptionLabel.setLayoutData(new GridData());
		descriptionLabel.setText("Description:");
		
		descriptionText = new I18nTextEditorMultiLine(page, phaseNameText.getLanguageChooser());	
		descriptionText.setI18nText(newProjectPhase.getDescription(), EditMode.DIRECT);
		descriptionText.addModifyListener(modifyListener);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumHeight = 100;
		descriptionText.setLayoutData(gridData);

		new Label(page, SWT.NONE).setText("Properties: ");
		
		activeButton = new Button(page, SWT.CHECK);
		activeButton.setText("Active");
		activeButton.setSelection(isActive);
		activeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isActive = activeButton.getSelection();
				newProjectPhase.setActive(isActive);
			}
		});
		
		return page;
	}

	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			getContainer().updateButtons();
		}
	};
	
	public CreateProjectPhaseWizardPage(Project project, ProjectPhase newProjectPhase)
	{
		super(CreateProjectPhaseWizardPage.class.getName(), "Project Phase Page", 
				SharedImages.getWizardPageImageDescriptor(IssueTrackingPlugin.getDefault(), CreateProjectPhaseWizard.class));
		this.setDescription("Description");
		this.project = project;
		this.newProjectPhase = newProjectPhase;
	}

	@Override
	public void onShow() {
		phaseNameText.forceFocus();
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}
	
	@Override
	public boolean isPageComplete()
	{
		boolean result = true;
		setErrorMessage(null);
		
		if (phaseNameText.getEditText().equals("") || phaseNameText.getI18nText().getText() == null) {
			result = false;
		}
		
		if (descriptionText.getEditText().equals("") || descriptionText.getI18nText().getText() == null) {
			result = false;
		}
		
		return result;
	}
}