package org.nightlabs.jfire.issuetracking.ui.project.create;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.language.I18nTextEditorTable;
import org.nightlabs.base.ui.language.II18nTextEditor;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jfire.issue.project.id.ProjectID;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;

public class CreateProjectTypeNameWizardPage extends DynamicPathWizardPage
{
	public CreateProjectTypeNameWizardPage(String pageName) {
		super(pageName);
	}

	private I18nTextBuffer projectTypeNameBuffer;
	private II18nTextEditor projectTypeNameEditor;

	private ProjectID projectID;

	@Override
	public Control createPageContents(Composite parent) {
		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		new Label(page, SWT.NONE).setText("Project Name");
		projectTypeNameBuffer = new I18nTextBuffer();
		projectTypeNameEditor = new I18nTextEditorTable(page);
		projectTypeNameEditor.setI18nText(projectTypeNameBuffer);
		projectTypeNameEditor.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0)
			{
				getWizard().getContainer().updateButtons();
			}
		});

		return page;
	}

	public CreateProjectTypeNameWizardPage(ProjectID projectID)
	{
		super(CreateProjectTypeNameWizardPage.class.getName(), "Project Page", 
				SharedImages.getWizardPageImageDescriptor(IssueTrackingPlugin.getDefault(), CreateProjectTypeNameWizardPage.class));
		this.setDescription("Description");
		this.projectID = projectID;
	}

	public II18nTextEditor getVoucherTypeNameEditor()
	{
		return projectTypeNameEditor;
	}

	public I18nTextBuffer getProjectTypeNameBuffer()
	{
		return projectTypeNameBuffer;
	}

	@Override
	public boolean isPageComplete()
	{
		return !projectTypeNameBuffer.isEmpty();
	}
}