package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import javax.jdo.FetchPlan;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.login.ui.part.ICloseOnLogoutEditorPart;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditor;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueTypeDAO;
import org.nightlabs.jfire.issue.id.IssueTypeID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 *
 */
public class IssueTypeEditor extends ActiveEntityEditor
implements ICloseOnLogoutEditorPart
{
	public static final String EDITOR_ID = IssueTypeEditor.class.getName();


	@Override
	protected String getEditorTitleFromEntity(Object entity) {
		return entity instanceof IssueType ? ((IssueType)entity).getName().getText() : null;
	}

	@Override
	protected Object retrieveEntityForEditorTitle(ProgressMonitor monitor) {
		IssueTypeID issueTypeID = ((IssueTypeEditorInput)getEditorInput()).getJDOObjectID();
		assert issueTypeID != null;
		return IssueTypeDAO.sharedInstance().getIssueType(issueTypeID, new String[] { FetchPlan.DEFAULT, IssueType.FETCH_GROUP_NAME }, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}


//	private IssueTypeEditorInput issueTypeEditorInput;
//	@Override
//	public void init(IEditorSite site, IEditorInput input) throws PartInitException
//	{
//		super.init(site, input);
//		issueTypeEditorInput = (IssueTypeEditorInput)input;
//		Job job = new Job(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypeEditor.job.loadIssueTypes.text")) //$NON-NLS-1$
//		{
//			@Override
//			protected IStatus run(ProgressMonitor monitor)
//			throws Exception
//			{
//				final IssueType issueType = IssueTypeDAO.sharedInstance().getIssueType(
//						issueTypeEditorInput.getJDOObjectID(),
//						new String[] { FetchPlan.DEFAULT, IssueType.FETCH_GROUP_THIS_ISSUE_TYPE },
//						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
//				Display.getDefault().asyncExec(new Runnable()
//				{
//					public void run()
//					{
//						setPartName(issueType.getName().getText());
//						setTitleToolTip(issueType.getIssueTypeID());
//					}
//				});
//				return Status.OK_STATUS;
//			}
//		};
//		job.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
//		job.schedule();
//	}
}