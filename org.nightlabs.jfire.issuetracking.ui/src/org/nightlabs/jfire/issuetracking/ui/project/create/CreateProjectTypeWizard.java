package org.nightlabs.jfire.issuetracking.ui.project.create;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.issue.project.id.ProjectID;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectEditor;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectEditorInput;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

public class CreateProjectTypeWizard
extends DynamicPathWizard
{
	private static String[] FETCH_GROUPS_PROJECT_TYPE = {
		FetchPlan.DEFAULT
	};

	private ProjectID projectID;
	private CreateProjectTypeNameWizardPage namePage;

	public CreateProjectTypeWizard(ProjectID projectID) {
		this.projectID = projectID;
	}

	@Override
	public void addPages() {
		namePage = new CreateProjectTypeNameWizardPage(projectID);
		addPage(namePage);
	}

	@Override
	public boolean performFinish()
	{

		Job job = new Job(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.create.CreateProjectTypeWizard.job.storingProjectType.text")) { //$NON-NLS-1$
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{

				Display.getDefault().asyncExec(new Runnable() {
					public void run()
					{
						try {
							RCPUtil.openEditor(
							new ProjectEditorInput(projectID),
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