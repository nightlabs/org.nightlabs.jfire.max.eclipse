package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueTypeDAO;
import org.nightlabs.progress.ProgressMonitor;

public class IssueTypeEditor extends EntityEditor{
	public static final String EDITOR_ID = IssueTypeEditor.class.getName();
	private IssueTypeEditorInput issueTypeEditorInput;
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		super.init(site, input);
		issueTypeEditorInput = (IssueTypeEditorInput)input;
		Job job = new Job("Loading Issue Type.....")
		{
			@Override
			protected IStatus run(ProgressMonitor monitor)
			throws Exception
			{
				final IssueType issueType = IssueTypeDAO.sharedInstance().getIssueType(
						issueTypeEditorInput.getJDOObjectID(),
						new String[] { FetchPlan.DEFAULT, IssueType.FETCH_GROUP_THIS_ISSUE_TYPE },
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						setPartName(issueType.getName().getText());
						setTitleToolTip(issueType.getIssueTypeID());
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		job.schedule();
	}
}