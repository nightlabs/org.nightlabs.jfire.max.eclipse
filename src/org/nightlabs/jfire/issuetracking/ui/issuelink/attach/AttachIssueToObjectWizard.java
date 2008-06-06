package org.nightlabs.jfire.issuetracking.ui.issuelink.attach;

import java.lang.reflect.InvocationTargetException;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssueLinkType;

public class AttachIssueToObjectWizard 
extends DynamicPathWizard
{
	private Object attachedObject;
	private SelectIssueLinkTypeWizardPage selectIssueLinkTypePage;
	
	private Issue selectedIssue;
	
	public AttachIssueToObjectWizard(Object attachedObject) {
		this.attachedObject = attachedObject;
		setWindowTitle("Attach Issue(s) to Objects");
	}

	@Override
	public void addPages() {
		selectIssueLinkTypePage = new SelectIssueLinkTypeWizardPage(attachedObject);
		addPage(selectIssueLinkTypePage);
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
		Issue.FETCH_GROUP_THIS_ISSUE,
		IssueLink.FETCH_GROUP_LINKED_OBJECT};
	
	@Override
	public boolean performFinish() {
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor _monitor) throws InvocationTargetException, InterruptedException {
					IssueLinkType selectedIssueLinkType = selectIssueLinkTypePage.getIssueLinkType();
//					Issue selectedIssue = selectIssuePage.getSelectedIssue();
//					Issue completedIssue = IssueDAO.sharedInstance().getIssue((IssueID)JDOHelper.getObjectId(selectedIssue), FETCH_GROUP,  NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
					
//					completedIssue.createIssueLink(selectedIssueLinkType, attachedObject);
//					Issue issue = IssueDAO.sharedInstance().storeIssue(completedIssue, true, FETCH_GROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
//					IssueEditorInput editorInput = new IssueEditorInput((IssueID)JDOHelper.getObjectId(issue));
//					try {
//						Editor2PerspectiveRegistry.sharedInstance().openEditor(editorInput, IssueEditor.EDITOR_ID);
//					} catch (Exception e) {
//						throw new RuntimeException(e);
//					}
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
