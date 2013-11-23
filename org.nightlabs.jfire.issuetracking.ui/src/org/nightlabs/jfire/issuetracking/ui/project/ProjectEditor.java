package org.nightlabs.jfire.issuetracking.ui.project;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.login.ui.part.ICloseOnLogoutEditorPart;
import org.nightlabs.jfire.base.ui.editlock.EditLockCallback;
import org.nightlabs.jfire.base.ui.editlock.EditLockCarrier;
import org.nightlabs.jfire.base.ui.editlock.EditLockHandle;
import org.nightlabs.jfire.base.ui.editlock.EditLockMan;
import org.nightlabs.jfire.base.ui.editlock.InactivityAction;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditor;
import org.nightlabs.jfire.issue.EditLockTypeIssue;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectDAO;
import org.nightlabs.jfire.issue.project.id.ProjectID;
import org.nightlabs.progress.ProgressMonitor;

public class ProjectEditor extends ActiveEntityEditor
implements ICloseOnLogoutEditorPart
{
	public static final String EDITOR_ID = ProjectEditor.class.getName();

	private static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		Project.FETCH_GROUP_NAME,
		Project.FETCH_GROUP_DESCRIPTION
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
				            ((ProjectEditorInput)getEditorInput()).getJDOObjectID(),
				            "TODO", //$NON-NLS-1$
				            editLockCallback
                         );
	}

	// :: --- [ ~~ ActiveEntiyEditor ] -------------------------------------------------------------------------->>---|
	@Override
	protected String getEditorTitleFromEntity(Object entity) {
		return entity instanceof Project ? ((Project)entity).getName().getText() : null;
	}

	@Override
	protected Object retrieveEntityForEditorTitle(ProgressMonitor monitor) {
		ProjectID projectID = ((ProjectEditorInput)getEditorInput()).getJDOObjectID();
		assert projectID != null;
		return ProjectDAO.sharedInstance().getProject(projectID, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}
	// :: --- [ ~~ ActiveEntiyEditor ] --------------------------------------------------------------------------<<---|



//	private ProjectEditorInput projectEditorInput;
//
//	@Override
//	public void init(IEditorSite site, IEditorInput input) throws PartInitException
//	{
//		super.init(site, input);
//		projectEditorInput = (ProjectEditorInput)input;
//		Job job = new Job(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.ProjectEditor.job.loadingProject.text")) //$NON-NLS-1$
//		{
//			@Override
//			protected IStatus run(ProgressMonitor monitor)
//			throws Exception
//			{
//				final Project project = ProjectDAO.sharedInstance().getProject(
//						projectEditorInput.getJDOObjectID(),
//						FETCH_GROUPS,
//						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
//
//				Display.getDefault().asyncExec(new Runnable()
//				{
//					public void run()
//					{
//						setPartName(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.ProjectEditor.editor.partName.text") + project.getName().getText()); //$NON-NLS-1$
//						setTitleToolTip(project.getDescription().getText());
//					}
//				});
//
//				editLockHandle = EditLockMan.sharedInstance().acquireEditLock(
//						EditLockTypeIssue.EDIT_LOCK_TYPE_ID,
//						(ObjectID)JDOHelper.getObjectId(project),
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