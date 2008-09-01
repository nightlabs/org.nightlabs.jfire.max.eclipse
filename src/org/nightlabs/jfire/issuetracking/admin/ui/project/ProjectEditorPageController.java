package org.nightlabs.jfire.issuetracking.admin.ui.project;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueResolution;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueTypeDAO;
import org.nightlabs.jfire.issue.id.IssueTypeID;
import org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypeEditorInput;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

public class ProjectEditorPageController extends EntityEditorPageController {

	/**
	 * The fetch groups of issue data.
	 */
	public static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		IssueType.FETCH_GROUP_THIS_ISSUE_TYPE,
		IssueSeverityType.FETCH_GROUP_THIS_ISSUE_SEVERITY_TYPE,
		IssueResolution.FETCH_GROUP_NAME,
		IssuePriority.FETCH_GROUP_NAME};
	
	
	private IssueTypeID issueTypeID;
	private IssueType issueType;
	
	public ProjectEditorPageController(EntityEditor editor)
	{
		super(editor);
	}
	
	public void doLoad(ProgressMonitor monitor) {
		monitor.beginTask("Loading Projects....", 100);
		
		IssueTypeEditorInput input = (IssueTypeEditorInput)getEntityEditor().getEditorInput();
		this.issueTypeID = input.getJDOObjectID();

		issueType = 
			IssueTypeDAO.sharedInstance().getIssueType(issueTypeID, 
					FETCH_GROUPS,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
					new NullProgressMonitor());
		
		monitor.done();
		setLoaded(true); // must be done before fireModifyEvent!
		fireModifyEvent(null, issueType);
	}

	public boolean doSave(ProgressMonitor monitor) {
//		IssueTypeDAO.sharedInstance().storeIssueType(issueType, FETCH_GROUPS,
//					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
//					new NullProgressMonitor());
		return true;
	}
	
	public IssueTypeID getIssueTypeID() {
		return issueTypeID;
	}
	
	public IssueType getIssueType() {
		return issueType;
	}
}
