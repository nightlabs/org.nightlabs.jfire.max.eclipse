package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.editlock.EditLockCallback;
import org.nightlabs.jfire.base.ui.editlock.EditLockCarrier;
import org.nightlabs.jfire.base.ui.editlock.EditLockHandle;
import org.nightlabs.jfire.base.ui.editlock.EditLockMan;
import org.nightlabs.jfire.base.ui.editlock.InactivityAction;
import org.nightlabs.jfire.issue.EditLockTypeIssue;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueComment;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueResolution;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.security.User;
import org.nightlabs.progress.ProgressMonitor;


public class IssueEditor extends EntityEditor{
	
	public static final String EDITOR_ID = IssueEditor.class.getName();
	
	private static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		Issue.FETCH_GROUP_THIS_ISSUE,
//		Issue.FETCH_GROUP_SUBJECT,
//		Issue.FETCH_GROUP_ISSUE_LINKS,
//		Issue.FETCH_GROUP_ISSUE_FILELIST,
//		Issue.FETCH_GROUP_DESCRIPTION,
//		Issue.FETCH_GROUP_ISSUE_PRIORITY,
//		Issue.FETCH_GROUP_ISSUE_SEVERITY_TYPE,
//		Issue.FETCH_GROUP_ISSUE_TYPE,
//		Issue.FETCH_GROUP_ISSUE_RESOLUTION,
//		Issue.FETCH_GROUP_ISSUE_REPORTER,
//		Issue.FETCH_GROUP_ISSUE_ASSIGNEE,
//		Issue.FETCH_GROUP_ISSUE_COMMENT,
//		Issue.FETCH_GROUP_STATE,
//		Issue.FETCH_GROUP_STATES,
//		Issue.FETCH_GROUP_ISSUE_LOCAL,
//		Issue.FETCH_GROUP_PROPERTY_SET,
//		IssueType.FETCH_GROUP_NAME,
//		IssueType.FETCH_GROUP_ISSUE_PRIORITIES,
//		IssueType.FETCH_GROUP_ISSUE_SEVERITY_TYPES,
//		IssueType.FETCH_GROUP_ISSUE_RESOLUTIONS,
		IssueType.FETCH_GROUP_THIS_ISSUE_TYPE,
		IssuePriority.FETCH_GROUP_NAME,
		IssueSeverityType.FETCH_GROUP_NAME,
		IssueResolution.FETCH_GROUP_THIS_ISSUE_RESOLUTION,
		IssueComment.FETCH_GROUP_THIS_COMMENT,
		IssueLink.FETCH_GROUP_THIS_ISSUE_LINK,
		IssueLink.FETCH_GROUP_LINKED_OBJECT,
		IssueLink.FETCH_GROUP_LINKED_OBJECT_CLASS,
		Statable.FETCH_GROUP_STATE,
		StatableLocal.FETCH_GROUP_STATE,
		State.FETCH_GROUP_STATE_DEFINITION,
		StateDefinition.FETCH_GROUP_NAME,
		User.FETCH_GROUP_NAME,
		IssueLinkType.FETCH_GROUP_NAME,
	};
	
	private EditLockHandle editLockHandle;
	
	private IssueEditorInput issueEditorInput;
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		super.init(site, input);
		issueEditorInput = (IssueEditorInput)input;
		Job job = new Job("Loading Issue Type.....")
		{
			@Override
			protected IStatus run(ProgressMonitor monitor)
			throws Exception
			{
				final Issue issue = IssueDAO.sharedInstance().getIssue(
						issueEditorInput.getJDOObjectID(),
						FETCH_GROUPS,
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						setPartName("ID: " + issue.getIssueID());
						setTitleToolTip(issue.getSubject().getText());
					}
				});
				
				editLockHandle = EditLockMan.sharedInstance().acquireEditLock(
						EditLockTypeIssue.EDIT_LOCK_TYPE_ID, 
						(ObjectID)JDOHelper.getObjectId(issue), 
						"TODO",
						new EditLockCallback() {
					@Override
					public InactivityAction getEditLockAction(EditLockCarrier editLockCarrier) {
						return InactivityAction.REFRESH_LOCK;
					}
				}, getSite().getShell(), monitor);
				
				return Status.OK_STATUS;
			}
		};
		job.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		job.schedule();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		editLockHandle.release();
	}
}
