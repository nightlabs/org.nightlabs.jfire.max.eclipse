package org.nightlabs.jfire.issuetracking.ui.projectphase;

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
import org.nightlabs.jfire.issue.project.ProjectPhase;
import org.nightlabs.jfire.issue.project.ProjectPhaseDAO;
import org.nightlabs.progress.ProgressMonitor;

public class ProjectPhaseEditor extends EntityEditor
implements ICloseOnLogoutEditorPart
{
	public static final String EDITOR_ID = ProjectPhaseEditor.class.getName();
	
	private ProjectPhaseEditorInput projectPhaseEditorInput;
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		super.init(site, input);
		projectPhaseEditorInput = (ProjectPhaseEditorInput)input;
		Job job = new Job("Loading Project Phase.....")
		{
			@Override
			protected IStatus run(ProgressMonitor monitor)
			throws Exception
			{
				final ProjectPhase projectPhase = ProjectPhaseDAO.sharedInstance().getProjectPhase(
						projectPhaseEditorInput.getJDOObjectID(),
						new String[] { FetchPlan.DEFAULT, ProjectPhase.FETCH_GROUP_NAME, ProjectPhase.FETCH_GROUP_DESCRIPTION},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
			
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						setPartName(projectPhase.getName().getText());
						setTitleToolTip(projectPhase.getDescription().getText());
					}
				});
				
				return Status.OK_STATUS;
			}
		};
		job.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		job.schedule();
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
}