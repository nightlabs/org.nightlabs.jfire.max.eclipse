package org.nightlabs.jfire.issuetracking.ui.project.create;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectDAO;
import org.nightlabs.jfire.issue.project.id.ProjectID;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectEditor;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectEditorInput;
import org.nightlabs.progress.NullProgressMonitor;

public class CreateProjectWizard
extends DynamicPathWizard
{
	private static String[] FETCH_GROUPS_PROJECT_TYPE = {
		FetchPlan.DEFAULT
	};

	private ProjectID parentProjectID;
	private CreateProjectWizardPage projectPage;

	public CreateProjectWizard(ProjectID parentProjectID) {
		this.parentProjectID = parentProjectID;
	}

	@Override
	public void addPages() {
		projectPage = new CreateProjectWizardPage(parentProjectID);
		addPage(projectPage);
	}

	@Override
	@Implement
	public boolean performFinish()
	{
		Job job = new Job("Storing Project") {
			@Override
			@Implement
			protected IStatus run(IProgressMonitor monitor)
			{

				Display.getDefault().asyncExec(new Runnable() {
					public void run()
					{
						try {
							Project projectToStore = new Project(Login.getLogin().getOrganisationID(), IDGenerator.nextID(Project.class));
							projectToStore.getName().copyFrom(projectPage.getProjectNameText().getI18nText());
							projectToStore.setProjectType(projectPage.getSelectedProjectType());
							projectToStore.setActive(projectPage.isActive());
							
							Project storedProject = ProjectDAO.sharedInstance().storeProject(projectToStore, true, new String[]{FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());

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
}