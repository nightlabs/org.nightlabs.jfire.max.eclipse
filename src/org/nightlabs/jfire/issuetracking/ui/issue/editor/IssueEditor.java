package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.base.login.ui.part.ICloseOnLogoutEditorPart;
import org.nightlabs.jfire.base.ui.editlock.EditLockCallback;
import org.nightlabs.jfire.base.ui.editlock.EditLockCarrier;
import org.nightlabs.jfire.base.ui.editlock.EditLockHandle;
import org.nightlabs.jfire.base.ui.editlock.EditLockMan;
import org.nightlabs.jfire.base.ui.editlock.InactivityAction;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditor;
import org.nightlabs.jfire.issue.EditLockTypeIssue;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.progress.ProgressMonitor;


public class IssueEditor extends ActiveEntityEditor
implements ICloseOnLogoutEditorPart
{

	public static final String EDITOR_ID = IssueEditor.class.getName();

	private static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		Issue.FETCH_GROUP_SUBJECT,
		Issue.FETCH_GROUP_ISSUE_FILELIST,
	};

	private final EditLockCallback editLockCallback = new EditLockCallback() {
		@Override
		public InactivityAction getEditLockAction(EditLockCarrier editLockCarrier) {
			return InactivityAction.REFRESH_LOCK;
		}

		@Override
		public void doDiscardAndRelease() {
			close(false);
		}

		@Override
		public void doSaveAndRelease() {
			doSave(new NullProgressMonitor());
			close(false);
		}
	};

	private EditLockHandle editLockHandle;


	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		super.init(site, input);
		editLockHandle = EditLockMan.sharedInstance().acquireEditLockAsynchronously(
				            EditLockTypeIssue.EDIT_LOCK_TYPE_ID,
				            ((IssueEditorInput)getEditorInput()).getJDOObjectID(),
				            "TODO", //$NON-NLS-1$
				            editLockCallback
                         );
	}

	// :: --- [ ~~ ActiveEntiyEditor ] -------------------------------------------------------------------------->>---|
	@Override
	protected String getEditorTitleFromEntity(Object entity) {
//		return entity instanceof Issue ? ((Issue)entity).getSubject().getText() : null;
		if (entity instanceof Issue) {
			Issue issue = (Issue)entity;

			// Note: It seems that having the (rigid and stable) ID of the Issue being displayed on the
			//       title tab is quite useful. Kai
			return "(ID:" + ObjectIDUtil.longObjectIDFieldToString(issue.getIssueID()) + ") " + issue.getSubject().getText();
		}

		return null;
	}
	
	@Override
	protected String getEditorTooltipFromEntity(Object entity) {
		return getEditorTitleFromEntity(entity);
	}

	@Override
	protected Object retrieveEntityForEditorTitle(ProgressMonitor monitor) {
		IssueID issueID = ((IssueEditorInput)getEditorInput()).getJDOObjectID();
		assert issueID != null;
		return IssueDAO.sharedInstance().getIssue(issueID, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}
	// :: --- [ ~~ ActiveEntiyEditor ] --------------------------------------------------------------------------<<---|


//	@Override
//	public void init(IEditorSite site, IEditorInput input) throws PartInitException
//	{
//		super.init(site, input);
//		issueEditorInput = (IssueEditorInput)input;
//		Job job = new Job(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditor.job.loadingIssueType.text")) //$NON-NLS-1$
//		{
//			@Override
//			protected IStatus run(ProgressMonitor monitor)
//			throws Exception
//			{
//				final Issue issue = IssueDAO.sharedInstance().getIssue(
//						issueEditorInput.getJDOObjectID(),
//						FETCH_GROUPS,
//						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
//				Display.getDefault().asyncExec(new Runnable()
//				{
//					public void run()
//					{
//						setPartName(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditor.editor.partName.text") + issue.getIssueIDAsString()); //$NON-NLS-1$
//						setTitleToolTip(issue.getSubject().getText());
//					}
//				});
//
//				editLockHandle = EditLockMan.sharedInstance().acquireEditLock(
//						EditLockTypeIssue.EDIT_LOCK_TYPE_ID,
//						(ObjectID)JDOHelper.getObjectId(issue),
//						"TODO", //$NON-NLS-1$
//						new EditLockCallback() {
//							@Override
//							public InactivityAction getEditLockAction(EditLockCarrier editLockCarrier) {
//								return InactivityAction.REFRESH_LOCK;
//							}
//						}, getSite().getShell(), monitor);
//
//				return Status.OK_STATUS;
//			}
//		};
//		job.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
//		job.schedule();
//	}

	@Override
	public void dispose() {
		super.dispose();
		editLockHandle.release();
	}
}
