package org.nightlabs.jfire.issuetracking.ui.projectphase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.II18nTextEditor;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jfire.issue.project.id.ProjectID;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueOptionalWizardPage;

public class CreateProjectPhaseWizardPage extends DynamicPathWizardPage
{
	public CreateProjectPhaseWizardPage(String pageName) {
		super(pageName);
	}

	private Label nameLabel;
	private I18nTextEditor nameText;

	private Label descriptionLabel;
	private I18nTextEditor descriptionText;
	
	private ProjectID projectID;

	@Override
	public Control createPageContents(Composite parent) {
		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		nameLabel = new Label(page, SWT.WRAP);
		nameLabel.setLayoutData(new GridData());
		nameLabel.setText("Subject:");
		
		nameText = new I18nTextEditor(page);
		nameText.addModifyListener(modifyListener);

		descriptionLabel = new Label(page, SWT.WRAP);
		descriptionLabel.setLayoutData(new GridData());
		descriptionLabel.setText("Description:");
		
		descriptionText = new I18nTextEditorMultiLine(page, nameText.getLanguageChooser());		
		descriptionText.addModifyListener(modifyListener);

		return page;
	}

	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			getContainer().updateButtons();
		}
	};
	
	public CreateProjectPhaseWizardPage(ProjectID projectID)
	{
		super(CreateProjectPhaseWizardPage.class.getName(), "Project Phase Page", 
				SharedImages.getWizardPageImageDescriptor(IssueTrackingPlugin.getDefault(), CreateProjectPhaseWizardPage.class));
		this.setDescription("Description");
		this.projectID = projectID;
	}

	@Override
	public void onShow() {
		nameText.forceFocus();
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
		
		if (nameText.getEditText().equals("") || nameText.getI18nText().getText() == null) {
			result = false;
		}
		
		if (descriptionText.getEditText().equals("") || descriptionText.getI18nText().getText() == null) {
			result = false;
		}
		
		return result;
	}
}