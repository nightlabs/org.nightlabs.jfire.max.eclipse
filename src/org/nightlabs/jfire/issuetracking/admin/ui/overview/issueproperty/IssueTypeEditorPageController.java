package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueTypeDAO;
import org.nightlabs.jfire.issue.id.IssueTypeID;
import org.nightlabs.progress.NullProgressMonitor;

public class IssueTypeEditorPageController extends EntityEditorPageController{

	private IssueTypeID issueTypeID;
	private IssueType issueType;
	
	public IssueTypeEditorPageController(EntityEditor editor)
	{
		super(editor);
	}
	
	public void doLoad(IProgressMonitor monitor) {
		monitor.beginTask("Loading Issue Types....", 100);
		
		IssueTypeEditorInput input = (IssueTypeEditorInput)getEntityEditor().getEditorInput();
		this.issueTypeID = input.getJDOObjectID();

		issueType = 
			IssueTypeDAO.sharedInstance().getIssueType(issueTypeID, IssueTypeTable.FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
		
		monitor.done();
		fireModifyEvent(null, null);
	}

	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
	}
	
	public IssueTypeID getIssueTypeID() {
		return issueTypeID;
	}
	
	public IssueType getIssueType() {
		return issueType;
	}
}
