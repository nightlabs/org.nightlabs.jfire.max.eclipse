package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.entity.editor.EntityEditor;


public class IssueEditor extends EntityEditor{
	/**
	 * The editor id.
	 */
	public static final String EDITOR_ID = IssueEditor.class.getName();

	/* (non-Javadoc)
	 * @see org.nightlabs.base.entityeditor.EntityEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		super.init(site, input);
//		final IssueEditorInput issueEditorInput = (IssueEditorInput) input;
//		Job job = new Job(Messages.getString("org.nightlabs.jfire.issuetracking.issue.editor.IssueEditor.loadingIssueJob.name")) //$NON-NLS-1$
//		{
//			@Override
//			protected IStatus run(ProgressMonitor monitor)
//			throws Exception
//			{
//				final Issue issue = IssueDAO.sharedInstance().getIssue(
//						issueEditorInput.getJDOObjectID(),
//						new String[] { FetchPlan.DEFAULT, Issue.FETCH_GROUP_NAME },
//						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
//				Display.getDefault().asyncExec(new Runnable()
//				{
//					public void run()
//					{
//						setPartName(account.getName().getText());
//						setTitleToolTip(Anchor.getPrimaryKey(account.getOrganisationID(), account.getAnchorTypeID(), account.getAnchorID()));
//					}
//				});
//				return Status.OK_STATUS;
//			}
//		};
//		job.setPriority(Job.SHORT);
//		job.schedule();
	}
}
