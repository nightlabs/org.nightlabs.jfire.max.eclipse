package org.nightlabs.jfire.issuetracking.ui.projectphase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectPhase;

public class CreateProjectPhaseWizard
extends DynamicPathWizard
{
	private Project project;
	private CreateProjectPhaseWizardPage projectPhasePage;
	
	private ProjectPhase newProjectPhase;

	public CreateProjectPhaseWizard(Project project) {
		this.project = project;
		
		newProjectPhase = new ProjectPhase(IDGenerator.getOrganisationID(), Long.toString(IDGenerator.nextID(ProjectPhase.class)));
	}

	@Override
	public void addPages() {
		projectPhasePage = new CreateProjectPhaseWizardPage(project, newProjectPhase);
		addPage(projectPhasePage);
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
				project.addProjectPhase(newProjectPhase);
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
	
	public ProjectPhase getNewProjectPhase() {
		return newProjectPhase;
	}
}
