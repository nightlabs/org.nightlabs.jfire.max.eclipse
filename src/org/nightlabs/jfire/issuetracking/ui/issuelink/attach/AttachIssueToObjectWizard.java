package org.nightlabs.jfire.issuetracking.ui.issuelink.attach;

import java.lang.reflect.InvocationTargetException;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditor;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorInput;
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
		setWindowTitle("Attach Issue(s) to Objects");
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
		Issue.FETCH_GROUP_ISSUE_LINKS,
		IssueLinkType.FETCH_GROUP_NAME,
		Issue.FETCH_GROUP_ISSUE_ASSIGNEE,
		Issue.FETCH_GROUP_ISSUE_REPORTER,
		Issue.FETCH_GROUP_ISSUE_PRIORITY,
		Issue.FETCH_GROUP_ISSUE_SEVERITY_TYPE,
		Issue.FETCH_GROUP_ISSUE_RESOLUTION,
		Issue.FETCH_GROUP_SUBJECT,
		Issue.FETCH_GROUP_STATE,
		Issue.FETCH_GROUP_STATES};
	
	@Override
	public boolean performFinish() {
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor _monitor) throws InvocationTargetException, InterruptedException {
					IssueLinkType selectedIssueLinkType = selectIssueLinkTypePage.getSelectedIssueLinkType();
					
					Issue issue = selectIssueWizardPage.getIssue();
					if (JDOHelper.getObjectId(issue) == null) {
						try {
							issue.setReporter((Login.getLogin().getUser(new String[]{User.FETCH_GROUP_NAME}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new org.eclipse.core.runtime.NullProgressMonitor()));
						} catch (LoginException e) {
							throw new RuntimeException(e);
						}
						issue = IssueDAO.sharedInstance().storeIssue(issue, true, FETCH_GROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
					}
					else {
						issue = IssueDAO.sharedInstance().getIssue((IssueID)JDOHelper.getObjectId(issue), FETCH_GROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
					}
					issue.createIssueLink(selectedIssueLinkType, attachedObject);
					
					Issue createdIssue = IssueDAO.sharedInstance().storeIssue(issue, true, FETCH_GROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
					IssueEditorInput issueEditorInput = new IssueEditorInput((IssueID)JDOHelper.getObjectId(createdIssue));
					try {
						Editor2PerspectiveRegistry.sharedInstance().openEditor(issueEditorInput, IssueEditor.EDITOR_ID);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
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
