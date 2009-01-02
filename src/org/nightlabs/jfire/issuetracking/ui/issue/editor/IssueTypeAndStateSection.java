/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.jbpm.JbpmConstants;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectComboComposite;
import org.nightlabs.jfire.jbpm.graph.def.Transition;
import org.nightlabs.jfire.jbpm.ui.state.CurrentStateComposite;
import org.nightlabs.jfire.jbpm.ui.transition.next.NextTransitionComposite;
import org.nightlabs.jfire.jbpm.ui.transition.next.SignalEvent;
import org.nightlabs.jfire.jbpm.ui.transition.next.SignalListener;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class IssueTypeAndStateSection 
extends AbstractIssueEditorGeneralSection 
{
	private Label projectLabel;
	private ProjectComboComposite projectComboComposite;

	private Label issueTypeLabel;
	private Label statusLabel;

	private CurrentStateComposite currentStateComposite;
	private NextTransitionComposite nextTransitionComposite;

	/**
	 * @param section
	 * @param managedForm
	 */
	public IssueTypeAndStateSection(final FormPage page, Composite parent, IssueEditorPageController controller) {
		super(page, parent, controller);

		getSection().setText("Type and Status");

		getClient().getGridLayout().numColumns = 3;
		getClient().getGridLayout().makeColumnsEqualWidth = false;

		// Issue Type
		issueTypeLabel = new Label(getClient(), SWT.WRAP);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		issueTypeLabel.setLayoutData(gd);

		// Project
		projectLabel = new Label(getClient(), SWT.WRAP);
		gd = new GridData();
		gd.horizontalSpan = 2;
		projectLabel.setLayoutData(gd);

		projectComboComposite = new ProjectComboComposite(getClient(), SWT.NONE);
		gd = new GridData();
		gd.horizontalSpan = 1;
		projectComboComposite.setLayoutData(gd);
		projectComboComposite.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent e) {
				getController().getIssue().setProject(projectComboComposite.getSelectedProject());
				markDirty();
			}
		});

		// Status
		statusLabel = new Label(getClient(), SWT.WRAP);
		statusLabel.setText("Status: ");
		gd = new GridData();
		gd.horizontalSpan = 1;
		statusLabel.setLayoutData(gd);

		currentStateComposite = new CurrentStateComposite(getClient(), SWT.WRAP);
		gd = new GridData();
		gd.horizontalSpan = 2;
		currentStateComposite.setLayoutData(gd);

		nextTransitionComposite = new NextTransitionComposite(getClient(), SWT.NONE);
		nextTransitionComposite.addSignalListener(new SignalListener() {
			public void signal(SignalEvent event) {
				if (JbpmConstants.TRANSITION_NAME_ASSIGN.equals(event.getTransition().getJbpmTransitionName()) &&
						getIssue().getAssignee() == null) 
				{
					((IssueEditorGeneralPage)page).getIssueDetailSection().getAssignAction().run();
					nextTransitionComposite.setEnabled(true);
					return;
				}

				if (JbpmConstants.TRANSITION_NAME_UNASSIGN.equals(event.getTransition().getJbpmTransitionName())) {
					((IssueEditorGeneralPage)page).getIssueDetailSection().getUnassignAction().run();
					nextTransitionComposite.setEnabled(true);
					return;
				}

				if (getController().getEntityEditor().isDirty()) {
					if (!MessageDialog.openQuestion(nextTransitionComposite.getShell(), "Save?", "In order to perform a transition, you need to save all modifications. Do you want to save the editor and perform the transition now?")) {
						nextTransitionComposite.setEnabled(true);
						return;
					}
				}

//				if (assignInPossibleTransition(getIssue(), new NullProgressMonitor())) {
//				if (getIssue().getAssignee() == null) {
//				if (!isDirty()) {
//				UserSearchDialog userSearchDialog = new UserSearchDialog(getSection().getShell(), null);
//				int returnCode = userSearchDialog.open();
//				if (returnCode == Dialog.OK) {
//				User assigneeUser = userSearchDialog.getSelectedUser();
//				if (assigneeUser != null) {
//				getIssue().setAssignee(assigneeUser);
//				}
//				}//if							
//				}
//				}
//				}

//				is this commit necessary? I commented it out.
//				commit(true);

//				instead of signalling it here, we save the whole editor
//				signalIssue(event);
				getController().setJbpmTransitionName(event.getTransition().getJbpmTransitionName());
				markDirty();
				getController().getEntityEditor().doSave(new NullProgressMonitor()); // spawns a job anyway - does nothing expensive on the UI thread.
			}
		});

		gd = new GridData();
		gd.horizontalSpan = 3;
		nextTransitionComposite.setLayoutData(gd);
	}

//	/**
//	* The fetch groups of issue data.
//	*/
//	public static final String[] FETCH_GROUPS = new String[] {
//	FetchPlan.DEFAULT, 
//	Issue.FETCH_GROUP_ISSUE_LOCAL,
//	IssueType.FETCH_GROUP_NAME,
//	IssueLocal.FETCH_GROUP_STATE,
//	IssueLocal.FETCH_GROUP_STATES,
//	State.FETCH_GROUP_STATE_DEFINITION,
//	StateDefinition.FETCH_GROUP_NAME};

//	protected void signalIssue(final SignalEvent event) {
//	Job job = new Job("Performing transition") {
//	@Override
//	protected IStatus run(ProgressMonitor monitor)
//	{
//	monitor.beginTask("Performing transition of issue", 100);
//	try {
//	IssueID issueID = (IssueID)JDOHelper.getObjectId(getIssue());
//	IssueManager im = JFireEjbUtil.getBean(IssueManager.class, Login.getLogin().getInitialContextProperties());
////	Issue issue = im.signalIssue((IssueID)JDOHelper.getObjectId(getIssue()), event.getTransition().getJbpmTransitionName(), 
////	true, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//	im.signalIssue(issueID, event.getTransition().getJbpmTransitionName(), false, null, 1);
//	// make sure the object cannot be loaded from the cache anymore (we would load an out-dated version, since the notification from the server is surely not yet here)
//	Cache.sharedInstance().removeByObjectID(issueID, false);
//	monitor.worked(30);
//	getController().load(new SubProgressMonitor(monitor, 70));

////	currentStateComposite.setStatable(issue);
////	nextTransitionComposite.setStatable(issue);
//	} catch (Exception x) {
//	throw new RuntimeException(x);
//	} finally {
//	monitor.done();
//	}
//	return Status.OK_STATUS;
//	}
//	};
//	job.setPriority(Job.SHORT);
//	job.setUser(true);
//	job.schedule();		
//	}

//	protected void signalAssign() {
//	Job job = new Job("Performing transition") {
//	@Override
//	@Implement
//	protected IStatus run(IProgressMonitor monitor)
//	{
//	try {
//	IssueManager im = JFireEjbUtil.getBean(IssueManager.class, Login.getLogin().getInitialContextProperties());
//	Issue issue = im.storeIssue(getController().getIssue(), true, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//	issue = im.signalIssue((IssueID)JDOHelper.getObjectId(issue), JbpmConstants.TRANSITION_NAME_ASSIGN, 
//	true, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

//	currentStateComposite.setStatable(issue);
//	nextTransitionComposite.setStatable(issue);

//	getController().setIssue(issue);
//	} catch (Exception x) {
//	throw new RuntimeException(x);
//	}
//	return Status.OK_STATUS;
//	}
//	};
//	job.setPriority(Job.SHORT);
//	job.setUser(true);
//	job.schedule();
//	}

	protected void doSetIssue(Issue issue) {
		issueTypeLabel.setText(
				String.format(
						"Issue type: %s", 
						issue.getIssueType().getName().getText())
		);

		projectLabel.setText(
				String.format(
				"Project:")
		);

		projectComboComposite.setSelectedProject(issue.getProject());

		currentStateComposite.setStatable(issue);
		nextTransitionComposite.setStatable(issue);		
	}

	public static final String[] FETCH_GROUPS_TRANSITION = {
		FetchPlan.DEFAULT,
		Transition.FETCH_GROUP_NAME
	};

	//disabled for the 0.9.4 release
	/*public static boolean assignInPossibleTransition(Issue issue, ProgressMonitor monitor) {
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
	}*/
}
