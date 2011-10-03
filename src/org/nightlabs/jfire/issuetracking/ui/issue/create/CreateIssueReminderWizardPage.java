package org.nightlabs.jfire.issuetracking.ui.issue.create;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.DateTimeControl;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueUserComposite;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueUserComposite.Orientation;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.l10n.IDateFormatter;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class CreateIssueReminderWizardPage
extends WizardHopPage
{
	//GUI
	private DateTimeControl deadlineDateTime;
	private IssueUserComposite assigneeComposite;

	private Issue issue;

	public CreateIssueReminderWizardPage(Issue issue) {
		super(CreateIssueReminderWizardPage.class.getName(),
				Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueReminderWizardPage.title"), //$NON-NLS-1$
				SharedImages.getWizardPageImageDescriptor(IssueTrackingPlugin.getDefault(), CreateIssueWizard.class));
		setDescription(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueReminderWizardPage.description")); //$NON-NLS-1$
		this.issue = issue;
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		mainComposite.getGridLayout().numColumns = 2;

		Label deadlineLabel = new Label(mainComposite, SWT.NONE);
		deadlineLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueReminderWizardPage.label.deadline.text")); //$NON-NLS-1$

		Composite deadlineWrapper = new XComposite(mainComposite, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL, 2);
		deadlineDateTime = new DateTimeControl(deadlineWrapper, SWT.NONE, IDateFormatter.FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY);
		deadlineDateTime.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		deadlineDateTime.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				issue.setDeadlineTimestamp(deadlineDateTime.getDate());
			}
		});
		deadlineDateTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				issue.setDeadlineTimestamp(deadlineDateTime.getDate());
			}
		});
//		Button removeDeadlineButton = new Button(deadlineWrapper, SWT.NONE);
//		removeDeadlineButton.setImage(SharedImages.DELETE_16x16.createImage());
//		removeDeadlineButton.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent selectionevent) {
//				deadlineDateTime.setDate(null);
//				issue.setDeadlineTimestamp(null);
//			}
//		});

		Label assigneeLabel = new Label(mainComposite, SWT.NONE);
		assigneeLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueReminderWizardPage.label.assignee.text")); //$NON-NLS-1$
		assigneeComposite = new IssueUserComposite(mainComposite, SWT.NONE,
				LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL, null, Orientation.LEFT);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		assigneeComposite.setLayoutData(gd);
		assigneeComposite.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent modifyevent) {
				issue.setAssignee(assigneeComposite.getSelectedUser());
			}
		});

		return mainComposite;
	}

	@Override
	public boolean isPageComplete() {
		return getErrorMessage() == null;
	}
}