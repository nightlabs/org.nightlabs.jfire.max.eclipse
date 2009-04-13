package org.nightlabs.jfire.issuetracking.ui.issue.create;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLocal;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.dao.IssueTypeDAO;
import org.nightlabs.jfire.issue.id.IssueTypeID;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.progress.NullProgressMonitor;

public class CreateIssueQuickDialog 
extends ResizableTitleAreaDialog
{
	public CreateIssueQuickDialog(Shell shell) {
		super(shell, Messages.RESOURCE_BUNDLE);
	}

	private QuickCreateIssueComposite quickCreateComposite;
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Create Issue");
		setMessage("Create an Issue");

		Composite wrapper = new XComposite(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER) {
			@Override
			public boolean setFocus() {
				return getButton(OK).forceFocus();
			}
		};
		quickCreateComposite = new QuickCreateIssueComposite(wrapper, SWT.NONE);
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
		newShell.setText("Title");
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
		try {
			Job job = new Job("Setting the default values....") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					String organisationID = Login.sharedInstance().getOrganisationID();
				
					final IssueTypeID issueTypeID = IssueTypeID.create(organisationID, IssueType.DEFAULT_ISSUE_TYPE_ID);				
					IssueType issueType = IssueTypeDAO.sharedInstance().getIssueType(issueTypeID, new String[] {IssueType.FETCH_GROUP_NAME}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new org.nightlabs.progress.NullProgressMonitor());
					newIssue.setIssueType(issueType);
					
					newIssue = IssueDAO.sharedInstance().storeIssue(newIssue, true, FETCH_GROUP_ISSUE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
					
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							setMessage("New issue has been created. Please press cancel to close the dialog.");
							quickCreateComposite.initData();
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
}