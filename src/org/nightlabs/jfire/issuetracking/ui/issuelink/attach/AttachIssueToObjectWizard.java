package org.nightlabs.jfire.issuetracking.ui.issuelink.attach;

import java.lang.reflect.InvocationTargetException;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.MessageBox;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issue.issuemarker.IssueMarker;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.security.User;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class AttachIssueToObjectWizard
extends DynamicPathWizard
{
	private Object attachedObject;
	private AttachIssueSelectIssueLinkTypeWizardPage selectIssueLinkTypePage;
	private SelectIssueWizardPage selectIssueWizardPage;

	private Issue selectedIssue;

	public AttachIssueToObjectWizard(Object attachedObject) {
		this.attachedObject = attachedObject;
		setWindowTitle(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.attach.AttachIssueToObjectWizard.title")); //$NON-NLS-1$
	}

	@Override
	public void addPages() {
		selectIssueLinkTypePage = new AttachIssueSelectIssueLinkTypeWizardPage(attachedObject);
		addPage(selectIssueLinkTypePage);

		selectIssueWizardPage = new SelectIssueWizardPage(attachedObject);
		addPage(selectIssueWizardPage);
	}

	private static String[] FETCH_GROUP = new String[]{
		FetchPlan.DEFAULT,
		Issue.FETCH_GROUP_ISSUE_TYPE,
		Issue.FETCH_GROUP_SUBJECT,
		Issue.FETCH_GROUP_DESCRIPTION,
		Issue.FETCH_GROUP_ISSUE_SEVERITY_TYPE,
		Issue.FETCH_GROUP_ISSUE_PRIORITY,
		Statable.FETCH_GROUP_STATE,
		Issue.FETCH_GROUP_ISSUE_LOCAL,
		StatableLocal.FETCH_GROUP_STATE,
		State.FETCH_GROUP_STATE_DEFINITION,
		IssueType.FETCH_GROUP_NAME,
		IssueSeverityType.FETCH_GROUP_NAME,
		IssuePriority.FETCH_GROUP_NAME,
		StateDefinition.FETCH_GROUP_NAME,
		Issue.FETCH_GROUP_ISSUE_MARKERS,
		IssueMarker.FETCH_GROUP_NAME,
		IssueMarker.FETCH_GROUP_ICON_16X16_DATA,
		Issue.FETCH_GROUP_ISSUE_LINKS,
		IssueLinkType.FETCH_GROUP_NAME,
		Issue.FETCH_GROUP_ISSUE_ASSIGNEE,
		Issue.FETCH_GROUP_ISSUE_REPORTER,
		Issue.FETCH_GROUP_ISSUE_RESOLUTION,
		Issue.FETCH_GROUP_ISSUE_COMMENTS,
		Issue.FETCH_GROUP_ISSUE_FILELIST,
		Issue.FETCH_GROUP_STATE,
		Issue.FETCH_GROUP_STATES,
	};

	@Override
	public boolean performFinish() {
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor _monitor) throws InvocationTargetException, InterruptedException {
					//Issue Link Type
					IssueLinkType selectedIssueLinkType = selectIssueLinkTypePage.getSelectedIssueLinkType();

					Issue createdIssue = null;

					//Checking if the issue is new.
					Issue issue = selectIssueWizardPage.getIssue();

					if (JDOHelper.getObjectId(issue) == null) {
						try {
							User reporter = Login.getLogin().getUser(new String[]{User.FETCH_GROUP_NAME}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new org.eclipse.core.runtime.NullProgressMonitor());
							issue.setReporter(reporter);
						} catch (LoginException e) {
							throw new RuntimeException(e);
						}

						createdIssue = IssueDAO.sharedInstance().storeIssue(issue, true, FETCH_GROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
					}
					else {
						//Issue Link
						issue = IssueDAO.sharedInstance().getIssue((IssueID)JDOHelper.getObjectId(issue), FETCH_GROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
						IssueLink issueLink = issue.createIssueLink(selectedIssueLinkType, attachedObject);
						if (issueLink == null) {
							MessageBox msg = new MessageBox(getShell());
							msg.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.attach.AttachIssueToObjectWizard.messageBox.hasLinkAlready.text")); //$NON-NLS-1$
							if (msg.open() == 1) {
								return;
							}
						}

						//Store Issue
						createdIssue = IssueDAO.sharedInstance().storeIssue(issue, true, FETCH_GROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
					}



					// Open the editor <-- Do we immediately do this after establishing a link to an Issue?
					//                     Now that we've restricted ourselves to only open an Issue page in the Issue perspective, as apposed to previously
					//                     opening the Issue page in the same view as the current Order, it feels a bit strange to lost track of what was
					//                     done previously. See follow-up suggestion below. Kai
//					IssueEditorInput issueEditorInput = new IssueEditorInput((IssueID)JDOHelper.getObjectId(createdIssue));
//					try {
//						Editor2PerspectiveRegistry.sharedInstance().openEditor(issueEditorInput, IssueEditor.EDITOR_ID);
//					} catch (Exception e) {
//						throw new RuntimeException(e);
//					}

					// TODO [Follow-up suggestion]: Kai
					// --> Upon successfully establishing a link to an Issue (either a new Issue or an existing one), simply refresh the IssueTable
					//     displaying all the linked Issue to this particular Order.
					// --> Suppose one wishes to see the related Issue after creating the link, one simply double-clicks on corresponding item
					//     in the IssueTable.
					// --> Of course, not withstanding any (possible) complication(s), the newly linked Issue in the IssueTable shall immediately
					//     be highlighted/given focus/etc.
					//
					setSelectedIssue(createdIssue);

				}
			});
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}


		return true;
	}

	public void setSelectedIssue(Issue issue) {
		this.selectedIssue = issue;
	}

	public Issue getSelectedIssue() {
		return selectedIssue;
	}

	@Override
	public boolean canFinish() {
		return getContainer().getCurrentPage().isPageComplete();
	}
}
