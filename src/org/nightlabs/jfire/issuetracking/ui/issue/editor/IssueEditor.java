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
import org.nightlabs.jfire.issue.IssueSubject;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;


public class IssueEditor extends EntityEditor{
	
	public static final String EDITOR_ID = IssueEditor.class.getName();
	
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
						new String[] { FetchPlan.DEFAULT, Issue.FETCH_GROUP_THIS, IssueSubject.FETCH_GROUP_THIS },
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						setPartName("ID: " + issue.getIssueID());
						setTitleToolTip(issue.getSubject().getText());
					}
				});
				
				editLockHandle = EditLockMan.sharedInstance().acquireEditLock(EditLockTypeIssue.EDIT_LOCK_TYPE_ID, (ObjectID)JDOHelper.getObjectId(issue), "TODO", // TODO description //$NON-NLS-1$
//						null,
						new EditLockCallback() {
					@Override
					public InactivityAction getEditLockAction(EditLockCarrier editLockCarrier) {
						return InactivityAction.REFRESH_LOCK;
					}
				}, getSite().getShell(), new NullProgressMonitor());
				
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
