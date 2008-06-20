/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import java.util.List;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.annotation.Implement;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.security.UserSearchDialog;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueFileAttachment;
import org.nightlabs.jfire.issue.IssueLocal;
import org.nightlabs.jfire.issue.IssueManager;
import org.nightlabs.jfire.issue.IssueManagerUtil;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issue.jbpm.JbpmConstants;
import org.nightlabs.jfire.jbpm.dao.TransitionDAO;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.jbpm.graph.def.Transition;
import org.nightlabs.jfire.jbpm.graph.def.id.StateID;
import org.nightlabs.jfire.jbpm.ui.state.CurrentStateComposite;
import org.nightlabs.jfire.jbpm.ui.transition.next.NextTransitionComposite;
import org.nightlabs.jfire.jbpm.ui.transition.next.SignalEvent;
import org.nightlabs.jfire.jbpm.ui.transition.next.SignalListener;
import org.nightlabs.jfire.security.User;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class IssueTypeAndStateSection extends AbstractIssueEditorGeneralSection {

	private Label issueTypeLabel;
	private Label statusLabel;

	private CurrentStateComposite currentStateComposite;
	private NextTransitionComposite nextTransitionComposite;

	/**
	 * @param section
	 * @param managedForm
	 */
	public IssueTypeAndStateSection(FormPage page, Composite parent, IssueEditorPageController controller) {
		super(page, parent, controller);

		getSection().setText("Type and Status");

		getClient().getGridLayout().numColumns = 3;
		getClient().getGridLayout().makeColumnsEqualWidth = false;

		issueTypeLabel = new Label(getClient(), SWT.WRAP);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		issueTypeLabel.setLayoutData(gd);

		statusLabel = new Label(getClient(), SWT.WRAP);
		statusLabel.setText("Status: ");
		gd = new GridData();
		statusLabel.setLayoutData(gd);

		currentStateComposite = new CurrentStateComposite(getClient(), SWT.NONE);
		gd = new GridData();
		gd.horizontalSpan = 2;
		currentStateComposite.setLayoutData(gd);
		
		nextTransitionComposite = new NextTransitionComposite(getClient(), SWT.NONE);
		nextTransitionComposite.addSignalListener(new SignalListener() {
			public void signal(SignalEvent event) {
				if (assignInPossibleTransition(getIssue(), new NullProgressMonitor())) {
					if (getIssue().getAssignee() == null) {
						if (!isDirty()) {
							UserSearchDialog userSearchDialog = new UserSearchDialog(getSection().getShell(), null);
							int returnCode = userSearchDialog.open();
							if (returnCode == Dialog.OK) {
								User assigneeUser = userSearchDialog.getSelectedUser();
								if (assigneeUser != null) {
									getIssue().setAssignee(assigneeUser);
								}
							}//if							
						}
					}
				}

				commit(true);
				signalIssue(event);
			}
		});
		
		gd = new GridData();
		gd.horizontalSpan = 3;
		nextTransitionComposite.setLayoutData(gd);
	}

	/**
	 * The fetch groups of issue data.
	 */
	public static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT, 
		Issue.FETCH_GROUP_THIS_ISSUE,
		IssueType.FETCH_GROUP_THIS_ISSUE_TYPE,
//		IssueDescription.FETCH_GROUP_THIS_DESCRIPTION, 
//		IssueSubject.FETCH_GROUP_THIS_ISSUE_SUBJECT,
		IssueFileAttachment.FETCH_GROUP_THIS_FILEATTACHMENT,
		IssueSeverityType.FETCH_GROUP_THIS_ISSUE_SEVERITY_TYPE,
//		IssuePriority.FETCH_GROUP_THIS_ISSUE_PRIORITY,
		IssueLocal.FETCH_GROUP_THIS_ISSUE_LOCAL,
		State.FETCH_GROUP_STATE_DEFINITION,
		StateDefinition.FETCH_GROUP_NAME};

	protected void signalIssue(final SignalEvent event) {
		Job job = new Job("Performing transition") {
			@Override
			@Implement
			protected IStatus run(IProgressMonitor monitor)
			{
				try {
					IssueManager im = IssueManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
					Issue issue = im.signalIssue((IssueID)JDOHelper.getObjectId(getIssue()), event.getTransition().getJbpmTransitionName(), 
							true, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

					currentStateComposite.setStatable(issue);
					nextTransitionComposite.setStatable(issue);
				} catch (Exception x) {
					throw new RuntimeException(x);
				}
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.setUser(true);
		job.schedule();		
	}

	protected void signalAssign() {
		Job job = new Job("Performing transition") {
			@Override
			@Implement
			protected IStatus run(IProgressMonitor monitor)
			{
				try {
					IssueManager im = IssueManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
					Issue issue = im.storeIssue(getController().getIssue(), true, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
					issue = im.signalIssue((IssueID)JDOHelper.getObjectId(issue), JbpmConstants.TRANSITION_NAME_ASSIGN, 
							true, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

					currentStateComposite.setStatable(issue);
					nextTransitionComposite.setStatable(issue);

					getController().setIssue(issue);
				} catch (Exception x) {
					throw new RuntimeException(x);
				}
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.setUser(true);
		job.schedule();
	}

	protected void doSetIssue(Issue issue) {
		issueTypeLabel.setText(
				String.format(
						"Issue type: %s", 
						issue.getIssueType().getName().getText())
		);
		currentStateComposite.setStatable(issue);
		nextTransitionComposite.setStatable(issue);		
	}

	public static final String[] FETCH_GROUPS_TRANSITION = {
		FetchPlan.DEFAULT,
		Transition.FETCH_GROUP_NAME
	};

	public static boolean assignInPossibleTransition(Issue issue, ProgressMonitor monitor) {
		State state = issue.getStatableLocal().getState();
		StateID stateID = (StateID) JDOHelper.getObjectId(state);

		// fetch the possible further transitions for the current state
		final List<Transition> transitions = TransitionDAO.sharedInstance().getTransitions(
				stateID, Boolean.TRUE, FETCH_GROUPS_TRANSITION, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

		for (Transition transition : transitions) {
			if (transition.getJbpmTransitionName().equals(JbpmConstants.TRANSITION_NAME_ASSIGN)) {
				return true;
			}
		}
		return false;
	}
}
