package org.nightlabs.jfire.issuetracking.ui.issue.create;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueFileAttachmentComposite;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkAdderComposite;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueFileAttachmentComposite.IssueFileAttachmentCompositeStyle;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class CreateIssueOptionalWizardPage
extends WizardHopPage
{
	//GUI
	private Label linkedObjectLbl;
	private IssueLinkAdderComposite issueLinkAdderComposite;

	private Label fileLabel;
	private IssueFileAttachmentComposite fileComposite;

	private WizardHopPage reminderPage;

	private Issue issue;

	public CreateIssueOptionalWizardPage(Issue issue) {
		super(CreateIssueOptionalWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueOptionalWizardPage.title"), SharedImages.getWizardPageImageDescriptor(IssueTrackingPlugin.getDefault(), CreateIssueWizard.class)); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueOptionalWizardPage.description")); //$NON-NLS-1$
		this.issue = issue;
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		mainComposite.getGridLayout().numColumns = 2;

		linkedObjectLbl = new Label(mainComposite, SWT.NONE);
		linkedObjectLbl.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueOptionalWizardPage.label.linkedObject.text")); //$NON-NLS-1$
		GridData gd = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, true);
		linkedObjectLbl.setLayoutData(gd);

		issueLinkAdderComposite = new IssueLinkAdderComposite(mainComposite, SWT.NONE, true, issue);
		fileLabel = new Label(mainComposite, SWT.NONE);
		fileLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueOptionalWizardPage.label.file.text")); //$NON-NLS-1$
		GridData gd2 = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, true);
		fileLabel.setLayoutData(gd2);

		fileComposite = new IssueFileAttachmentComposite(mainComposite, SWT.NONE, LayoutMode.TIGHT_WRAPPER, IssueFileAttachmentCompositeStyle.withAddRemoveButton);
		fileComposite.setIssue(issue);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		fileComposite.setLayoutData(gridData);

		return mainComposite;
	}

	@Override
	public boolean isPageComplete() {
		if (reminderPage == null) {
			new WizardHop(this);
			reminderPage = new CreateIssueReminderWizardPage(issue);
			getWizardHop().addHopPage(reminderPage);
		}

		return getErrorMessage() == null;
	}

	public IssueLinkAdderComposite getIssueLinkAdderComposite() {
		return issueLinkAdderComposite;
	}
}
