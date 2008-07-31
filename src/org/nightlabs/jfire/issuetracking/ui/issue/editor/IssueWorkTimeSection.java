package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.security.UserSearchDialog;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueFileAttachment;
import org.nightlabs.jfire.issue.IssueWorkTimeRange;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueFileAttachmentSection.AddFileToolbarAction;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueFileAttachmentSection.DownloadFileToolbarAction;
import org.nightlabs.jfire.security.User;

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

	private WorkTimeListAction workTimeListAction;
	
	private static DateFormat dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

	/**
	 * @param section
	 * @param managedForm
	 */
	public IssueWorkTimeSection(FormPage page, Composite parent, final IssueEditorPageController controller) {
		super(page, parent, controller);
		getClient().getGridLayout().numColumns = 3;
		getClient().getGridLayout().makeColumnsEqualWidth = false;
		getSection().setText("Work Time");

		new Label(getClient(), SWT.NONE).setText("Status: ");

		statusLabel = new Label(getClient(), SWT.NONE);
		statusLabel.setText(" - ");

		startStopButton = new Button(getClient(), SWT.NONE);
		startStopButton.setText("");
		startStopButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				if (issue.isStarted()) { //End
					issue.endWorking(new Date());
					markDirty();
					getController().getEntityEditor().doSave(new NullProgressMonitor()); // spawns a job anyway - does nothing expensive on the UI thread.
				}

				else {	//Start
					if (issue.getAssignee() == null) {
						UserSearchDialog userSearchDialog = new UserSearchDialog(getSection().getShell(), null);
						int returnCode = userSearchDialog.open();
						if (returnCode == Dialog.OK) {
							issue.setAssignee(userSearchDialog.getSelectedUser());
						}
					}
					
					issue.startWorking(new Date());
					
					markDirty();
					getController().getEntityEditor().doSave(new NullProgressMonitor()); // spawns a job anyway - does nothing expensive on the UI thread.
				}
			}
		});

		new Label(getClient(), SWT.NONE).setText("Start Time: ");
		startTimeLabel = new Label(getClient(), SWT.NONE);
		startTimeLabel.setText("");
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		startTimeLabel.setLayoutData(gd);

		new Label(getClient(), SWT.NONE).setText("Stop Time: ");
		endTimeLabel = new Label(getClient(), SWT.NONE);
		endTimeLabel.setText("");
		gd = new GridData();
		gd.horizontalSpan = 2;
		endTimeLabel.setLayoutData(gd);
		
		workTimeListAction = new WorkTimeListAction();
		
		getToolBarManager().add(workTimeListAction);
		
		updateToolBarManager();
	}

	@Override
	protected void doSetIssue(Issue newIssue) {
		this.issue = newIssue;

		if (issue.isStarted()) {
			startStopButton.setText("Stop");
			try {
				boolean canStop = issue.getLastestIssueWorkTimeRange().getUser().equals(Login.getLogin().getUser(new String[]{User.FETCH_GROUP_NAME}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()));
				startStopButton.setEnabled(canStop);
			} catch (LoginException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else 
			startStopButton.setText("Start");

		IssueWorkTimeRange workTime = issue.getLastestIssueWorkTimeRange();
		if (workTime != null) {
			statusLabel.setText(issue.isStarted() ? "Working" : "Stopped");
			startTimeLabel.setText(workTime.getFrom() == null ? "" : dateTimeFormat.format(workTime.getFrom()));
			endTimeLabel.setText(workTime.getTo() == null ? "" : dateTimeFormat.format(workTime.getTo()));

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
			"List"));
			setToolTipText("List all work times");
			setText("List");
		}

		@Override
		public void run() 
		{
		}		
	}
}
