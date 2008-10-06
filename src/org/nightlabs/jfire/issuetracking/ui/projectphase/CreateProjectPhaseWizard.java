package org.nightlabs.jfire.issuetracking.ui.projectphase;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.issue.project.id.ProjectID;

public class CreateProjectPhaseWizard
extends DynamicPathWizard
{
	private static String[] FETCH_GROUPS_PROJECT = {
		FetchPlan.DEFAULT
	};

	private ProjectID projectID;
	private CreateProjectPhaseWizardPage page;

	public CreateProjectPhaseWizard(ProjectID projectID) {
		this.projectID = projectID;
	}

	@Override
	public void addPages() {
		page = new CreateProjectPhaseWizardPage(projectID);
		addPage(page);
	}

	@Override
	@Implement
	public boolean performFinish()
	{

		Job job = new Job("Creating Project Phase") {
			@Override
			@Implement
			protected IStatus run(IProgressMonitor monitor)
			{

//				Display.getDefault().asyncExec(new Runnable() {
//					public void run()
//					{
//						try {
//							RCPUtil.openEditor(
//							new ProjectEditorInput(projectID),
//							ProjectEditor.EDITOR_ID);
//						} catch (Exception e) {
//							throw new RuntimeException(e);
//						}
//					}
//				});
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.setPriority(Job.SHORT);
		job.schedule();
		return true;
	}
}
