package org.nightlabs.jfire.issuetracking.ui.projectphase;

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

public class CreateProjectPhaseWizardPage extends DynamicPathWizardPage
{
	public CreateProjectPhaseWizardPage(String pageName) {
		super(pageName);
	}

	private I18nTextBuffer projectPhaseNameBuffer;
	private II18nTextEditor projectPhaseNameEditor;

	private ProjectID projectID;

	@Override
	public Control createPageContents(Composite parent) {
		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		new Label(page, SWT.NONE).setText("Project Phase Name");
		projectPhaseNameBuffer = new I18nTextBuffer();
		projectPhaseNameEditor = new I18nTextEditorTable(page);
		projectPhaseNameEditor.setI18nText(projectPhaseNameBuffer);
		projectPhaseNameEditor.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0)
			{
				getWizard().getContainer().updateButtons();
			}
		});

		return page;
	}

	public CreateProjectPhaseWizardPage(ProjectID projectID)
	{
		super(CreateProjectPhaseWizardPage.class.getName(), "Project Phase Page", 
				SharedImages.getWizardPageImageDescriptor(IssueTrackingPlugin.getDefault(), CreateProjectPhaseWizardPage.class));
		this.setDescription("Description");
		this.projectID = projectID;
	}

	public II18nTextEditor getVoucherPhaseNameEditor()
	{
		return projectPhaseNameEditor;
	}

	public I18nTextBuffer getProjectPhaseNameBuffer()
	{
		return projectPhaseNameBuffer;
	}

	@Override
	public boolean isPageComplete()
	{
		return !projectPhaseNameBuffer.isEmpty();
	}
}