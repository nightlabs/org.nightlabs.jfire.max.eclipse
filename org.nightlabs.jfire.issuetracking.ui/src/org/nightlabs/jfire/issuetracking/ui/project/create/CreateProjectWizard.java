package org.nightlabs.jfire.issuetracking.ui.project.create;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectDAO;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectEditor;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectEditorInput;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;

public class CreateProjectWizard
extends DynamicPathWizard
implements INewWizard
{
	private static String[] FETCH_GROUPS_PROJECT_TYPE = {
		FetchPlan.DEFAULT
	};

	private Project parentProject;
	private Project newProject;
	
	private CreateProjectWizardPage projectPage;

	public CreateProjectWizard() {
		this(null);
	}
	
	public CreateProjectWizard(Project parentProject) {
		this.parentProject = parentProject;
		
		newProject = new Project(IDGenerator.getOrganisationID(), IDGenerator.nextID(Project.class));
	}

	@Override
	public void addPages() {
		projectPage = new CreateProjectWizardPage(parentProject, newProject);
		addPage(projectPage);
	}

	@Override
	public boolean performFinish()
	{
		Job job = new Job(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.create.CreateProjectWizard.job.storingProject.text")) { //$NON-NLS-1$
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{

				Display.getDefault().asyncExec(new Runnable() {
					public void run()
					{
						try {
							Project storedProject = ProjectDAO.sharedInstance().storeProject(newProject, true, new String[]{FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());

							RCPUtil.openEditor(
									new ProjectEditorInput(storedProject.getObjectId()),
									ProjectEditor.EDITOR_ID);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.setPriority(Job.SHORT);
		job.schedule();
		return true;
	}

	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {
		// do nothing!!
	}
}