package org.nightlabs.jfire.issuetracking.admin.ui.project;

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
import org.nightlabs.jfire.base.ui.login.part.ICloseOnLogoutEditorPart;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectDAO;
import org.nightlabs.progress.ProgressMonitor;

public class ProjectEditor extends EntityEditor
implements ICloseOnLogoutEditorPart
{
	public static final String EDITOR_ID = ProjectEditor.class.getName();
	private ProjectEditorInput projectEditorInput;
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		super.init(site, input);
		projectEditorInput = (ProjectEditorInput)input;
		Job job = new Job("Loading Project.....")
		{
			@Override
			protected IStatus run(ProgressMonitor monitor)
			throws Exception
			{
				final Project project = ProjectDAO.sharedInstance().getProject(
						projectEditorInput.getJDOObjectID(),
						new String[] { FetchPlan.DEFAULT, Project.FETCH_GROUP_NAME, Project.FETCH_GROUP_DESCRIPTION},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						setPartName(project.getName().getText());
						setTitleToolTip(project.getDescription().getText());
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		job.schedule();
	}
}