package org.nightlabs.jfire.issuetracking.ui.issuelink.attach;

import java.lang.reflect.InvocationTargetException;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditor;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorInput;
import org.nightlabs.progress.NullProgressMonitor;

public class AttachIssueToObjectWizard 
extends DynamicPathWizard
{
	private Object attachedObject;
	private SelectIssueAndIssueLinkTypePage selectIssueAndIssueLinkTypePage;
	
	public AttachIssueToObjectWizard(Object attachedObject) {
		this.attachedObject = attachedObject;
		setWindowTitle("Attach Issue(s) to Objects");
	}

	@Override
	public void addPages() {
		selectIssueAndIssueLinkTypePage = new SelectIssueAndIssueLinkTypePage(attachedObject);
		addPage(selectIssueAndIssueLinkTypePage);
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
					IssueLinkType selectedIssueLinkType = selectIssueAndIssueLinkTypePage.getIssueLinkType();
					Issue selectedIssue = selectIssueAndIssueLinkTypePage.getSelectedIssue();
					Issue completedIssue = IssueDAO.sharedInstance().getIssue((IssueID)JDOHelper.getObjectId(selectedIssue), FETCH_GROUP,  NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
					Object attachedObject = selectIssueAndIssueLinkTypePage.getAttachedObject();
					
					completedIssue.createIssueLink(selectedIssueLinkType, attachedObject);
					Issue issue = IssueDAO.sharedInstance().storeIssue(completedIssue, true, FETCH_GROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
					IssueEditorInput editorInput = new IssueEditorInput((IssueID)JDOHelper.getObjectId(issue));
					try {
						Editor2PerspectiveRegistry.sharedInstance().openEditor(editorInput, IssueEditor.EDITOR_ID);
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
}
