package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import java.text.DateFormat;
import java.util.Date;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.base.ui.composite.CalendarDateTimeEditLookupDialog;
import org.nightlabs.base.ui.composite.DateTimeControl;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.base.ui.security.UserSearchDialog;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueWorkTimeRange;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.jfire.security.User;
import org.nightlabs.l10n.DateFormatProvider;
import org.nightlabs.l10n.DateFormatter;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueWorkTimeSection 
extends AbstractIssueEditorGeneralSection 
{
	private Issue issue;

	private Label statusLabel;
	private Button startStopButton;

	private Label startTimeLabel;
	private Label endTimeLabel;
	private DateTimeControl deadlineDateTimeEdit;
	
	private WorkTimeListAction workTimeListAction;

	private static DateFormat dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
	
	private boolean isLoginAssignee = false;

	/**
	 * @param section
	 * @param managedForm
	 */
	public IssueWorkTimeSection(FormPage page, Composite parent, final IssueEditorPageController controller) {
		super(page, parent, controller);
		getClient().getGridLayout().numColumns = 3;
		getClient().getGridLayout().makeColumnsEqualWidth = false;
		getSection().setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeSection.section.text")); //$NON-NLS-1$

		new Label(getClient(), SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeSection.label.status.text")); //$NON-NLS-1$

		statusLabel = new Label(getClient(), SWT.NONE);
		statusLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeSection.label.statusText.text")); //$NON-NLS-1$

		startStopButton = new Button(getClient(), SWT.NONE);
		startStopButton.setText(""); //$NON-NLS-1$
		startStopButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				User newAssignee = null;
				if (issue.isStarted()) { //End
					if (!isLoginAssignee) { //Warning if the login is not the assignee
						boolean confirmStopWorking = MessageDialog.openConfirm(
								getSection().getShell(), 
								Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeSection.dialog.stopWorking.title"),  //$NON-NLS-1$
								Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeSection.dialog.stopWorking.description")); //$NON-NLS-1$
						if (!confirmStopWorking)
							return;
					}
				}
				else {	//Start
					if (issue.getAssignee() == null) {
						UserSearchDialog userSearchDialog = new UserSearchDialog(getSection().getShell(), null);
						int returnCode = userSearchDialog.open();
						if (returnCode == Dialog.OK) {
							newAssignee = userSearchDialog.getSelectedUser();
//							issue.setAssignee(userSearchDialog.getSelectedUser());
						}
						else
							return;
					}
				}
				
				//Show time choosing dialog
				CalendarDateTimeEditLookupDialog timeDialog = new CalendarDateTimeEditLookupDialog(getSection().getShell(), DateFormatProvider.DATE_LONG | DateFormatProvider.TIME);
				Date selectedTime = null;
				boolean successful;
				do {
					successful = true;
					selectedTime = null;
					if (timeDialog.open() == Dialog.OK) {
						selectedTime = timeDialog.getDate().getTime();
						long timeMSec = selectedTime.getTime();
						timeMSec /= 60000L;
						timeMSec *= 60000L;
						selectedTime = new Date(timeMSec);

						if (!issue.isStarted()) {
							if (newAssignee != null)
								issue.setAssignee(newAssignee);

							issue.startWorking(selectedTime);
						}
						else {
							if (selectedTime.before(issue.getLastIssueWorkTimeRange().getFrom())) {
								MessageDialog.openInformation(
										getSection().getShell(), 
										"Stop time is not correct!!",
										"The stop time should not be the time before the start time. Please select again."
								);
								successful = false;
								selectedTime = null;
							}
							else {
								if (newAssignee != null)
									issue.setAssignee(newAssignee);
								
								issue.endWorking(selectedTime);
							}
						}
					}
				} while (!successful);
				
				if (selectedTime != null) {
					markDirty();
					getController().getEntityEditor().doSave(new NullProgressMonitor()); // spawns a job anyway - does nothing expensive on the UI thread.
				}
			}
		});

		new Label(getClient(), SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeSection.label.startTime.text")); //$NON-NLS-1$
		
		startTimeLabel = new Label(getClient(), SWT.NONE);
		startTimeLabel.setText(""); //$NON-NLS-1$
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		startTimeLabel.setLayoutData(gd);

		new Label(getClient(), SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeSection.label.stopTime.text")); //$NON-NLS-1$
		endTimeLabel = new Label(getClient(), SWT.NONE);
		endTimeLabel.setText(""); //$NON-NLS-1$
		gd = new GridData();
		gd.horizontalSpan = 2;
		endTimeLabel.setLayoutData(gd);
		
		new Label(getClient(), SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeSection.label.deadlineTime.text")); //$NON-NLS-1$
		deadlineDateTimeEdit =new DateTimeControl(getClient(), false, SWT.NONE, DateFormatter.FLAGS_DATE_LONG);
		deadlineDateTimeEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.getIssue().setDeadlineTimestamp(deadlineDateTimeEdit.getDate());
				markDirty();
				deadlineDateTimeEdit.pack(true);
			}
		});
		deadlineDateTimeEdit.addClearSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.getIssue().setDeadlineTimestamp(deadlineDateTimeEdit.getDate());
				markDirty();
			}
		});
		
		gd = new GridData();
		gd.horizontalSpan = 2;
		deadlineDateTimeEdit.setLayoutData(gd);

		workTimeListAction = new WorkTimeListAction();

		getToolBarManager().add(workTimeListAction);

		updateToolBarManager();
	}

	@Override
	protected void doSetIssue(Issue newIssue) {
		this.issue = newIssue;

		if (issue.isStarted()) {
			startStopButton.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeSection.button.startStopButton.stop.text")); //$NON-NLS-1$

			Job job = new Job(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeSection.job.checkUser.text")) { //$NON-NLS-1$
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						isLoginAssignee = issue.getAssignee().equals(
								Login.getLogin().getUser(new String[]{User.FETCH_GROUP_NAME}, 
										NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()));
//						Display.getDefault().asyncExec(new Runnable() {
//							public void run() {
//								try {
//									startStopButton.setEnabled(canStop);
//								}
//								catch (Exception e) {
//									throw new RuntimeException(e);
//								}
//							}
//						});
					}
					catch (Exception e) {
						throw new RuntimeException(e);
					}
					monitor.done();
					return Status.OK_STATUS;
				}
			};
			
			job.schedule();
		}
		else 
			startStopButton.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeSection.button.startStopButton.start.text")); //$NON-NLS-1$

		deadlineDateTimeEdit.setDate(issue.getDeadlineTimestamp());
		deadlineDateTimeEdit.pack(true);
		
		IssueWorkTimeRange workTime = issue.getLastIssueWorkTimeRange();
		if (workTime != null) {
			statusLabel.setText(issue.isStarted() ? Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeSection.label.statusText.working.text") : Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeSection.label.statusText.stopped.text")); //$NON-NLS-1$ //$NON-NLS-2$
			startTimeLabel.setText(workTime.getFrom() == null ? "" : dateTimeFormat.format(workTime.getFrom())); //$NON-NLS-1$
			endTimeLabel.setText(workTime.getTo() == null ? "" : dateTimeFormat.format(workTime.getTo())); //$NON-NLS-1$
			
			statusLabel.pack(true);
			startStopButton.pack(true);
			startTimeLabel.pack(true);
			endTimeLabel.pack(true);
			
			getClient().pack();
		}
	}

	public class WorkTimeListAction 
	extends Action 
	{		
		public WorkTimeListAction() 
		{
			super();
			setId(WorkTimeListAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(), 
					IssueWorkTimeSection.class, 
			"List")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeSection.WorkTimeListAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeSection.WorkTimeListAction.text")); //$NON-NLS-1$
		}

		@Override
		public void run() 
		{
			Dialog dialog = new Dialog(getSection().getShell()) {

				@Override
				protected void setShellStyle(int newShellStyle) {
					super.setShellStyle(getShellStyle() | SWT.RESIZE);
				}

				@Override
				protected Control createDialogArea(Composite parent) {
					Composite container = (Composite) super.createDialogArea(parent);

					IssueWorkTimeRangeTable t = new IssueWorkTimeRangeTable(container, SWT.NONE);
					t.setIssueWorkTimeRanges((IssueID)JDOHelper.getObjectId(issue), issue.getIssueWorkTimeRanges());

					t.pack();

					getShell().setMinimumSize(700, 400);
					return container;
				}

				@Override
				protected void createButtonsForButtonBar(Composite parent) {
					createButton(parent, 
							IDialogConstants.CLOSE_ID,
							Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeSection.button.close.text"),  //$NON-NLS-1$
							false);
				}

				@Override
				protected void buttonPressed(int id) {
					if (id == IDialogConstants.CLOSE_ID){
						close();
					}
				}
			};

			dialog.open();
		}		
	}
}
