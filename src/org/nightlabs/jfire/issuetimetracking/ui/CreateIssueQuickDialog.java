package org.nightlabs.jfire.issuetimetracking.ui;

import java.util.Timer;
import java.util.TimerTask;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLocal;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.dao.IssueTypeDAO;
import org.nightlabs.jfire.issue.id.IssueTypeID;
import org.nightlabs.jfire.issuetimetracking.ui.resource.Messages;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.progress.NullProgressMonitor;

public class CreateIssueQuickDialog
extends ResizableTitleAreaDialog
{
	public CreateIssueQuickDialog(Shell shell) {
		super(shell, null);
	}

	private static final String DEFAULT_MESSAGE = Messages.getString("org.nightlabs.jfire.issuetimetracking.ui.CreateIssueQuickDialog.message"); //$NON-NLS-1$

	private QuickCreateIssueComposite quickCreateComposite;
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(Messages.getString("org.nightlabs.jfire.issuetimetracking.ui.CreateIssueQuickDialog.title")); //$NON-NLS-1$
		setMessage(DEFAULT_MESSAGE);

		Composite wrapper = new XComposite(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER) {
			@Override
			public boolean setFocus() {
				return getButton(OK).forceFocus();
			}
		};
		quickCreateComposite = new QuickCreateIssueComposite(this, wrapper, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		quickCreateComposite.setLayoutData(gridData);
		return wrapper;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("org.nightlabs.jfire.issuetimetracking.ui.CreateIssueQuickDialog.dialogTitle")); //$NON-NLS-1$
	}

	private static String[] FETCH_GROUP_ISSUE = new String[]{
		FetchPlan.DEFAULT,
		Issue.FETCH_GROUP_STATE,
		Issue.FETCH_GROUP_STATES,
		Issue.FETCH_GROUP_ISSUE_LOCAL,
		Issue.FETCH_GROUP_ISSUE_TYPE,
		IssueLocal.FETCH_GROUP_STATE,
		IssueLocal.FETCH_GROUP_STATES,
		Statable.FETCH_GROUP_STATE,
		StatableLocal.FETCH_GROUP_STATE,
		State.FETCH_GROUP_STATE_DEFINITION,
	};

	private Issue newIssue;
	@Override
	protected void okPressed() {
		newIssue = quickCreateComposite.getCreatingIssue();
		getButton(OK).setEnabled(false);
		try {
			Job job = new Job(Messages.getString("org.nightlabs.jfire.issuetimetracking.ui.CreateIssueQuickDialog.settingValueJob")) { //$NON-NLS-1$
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					String organisationID = Login.sharedInstance().getOrganisationID();

					final IssueTypeID issueTypeID = IssueTypeID.create(organisationID, IssueType.DEFAULT_ISSUE_TYPE_ID);
					IssueType issueType = IssueTypeDAO.sharedInstance().getIssueType(issueTypeID, new String[] {IssueType.FETCH_GROUP_NAME}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new org.nightlabs.progress.NullProgressMonitor());
					newIssue.setIssueType(issueType);
					newIssue.getPropertySet().deflate();

					newIssue = IssueDAO.sharedInstance().storeIssue(newIssue, true, FETCH_GROUP_ISSUE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
					quickCreateComposite.initData();
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							if (newIssue != null)
								setMessage(Messages.getString("org.nightlabs.jfire.issuetimetracking.ui.CreateIssueQuickDialog.afterSavingMessage")); //$NON-NLS-1$

							Timer timer = new Timer();
							TimerTask timerTask = new TimerTask(){
								@Override
								public void run() {
									Display.getDefault().asyncExec(new Runnable() {
										@Override
										public void run() {
											setMessage(DEFAULT_MESSAGE);
										}
									});
								}
							};
							timer.schedule(timerTask, 1000 * 2);

							quickCreateComposite.initUI();
						}
					});

					return Status.OK_STATUS;
				}
			};

			job.setPriority(Job.SHORT);
			job.schedule();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	protected Button createButton(Composite parent, int id, String label,
			boolean defaultButton) {
		Button button = super.createButton(parent, id, label, defaultButton);
		if (id == OK)
			button.setEnabled(false);
		return button;
	}

	public void setOKButtonEnabled(boolean enabled) {
		getButton(OK).setEnabled(enabled);
	}
}