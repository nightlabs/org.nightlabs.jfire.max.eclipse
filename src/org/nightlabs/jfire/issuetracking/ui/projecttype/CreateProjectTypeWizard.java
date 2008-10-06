package org.nightlabs.jfire.issuetracking.ui.projecttype;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.issue.project.id.ProjectID;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectEditor;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectEditorInput;

public class CreateProjectTypeWizard
extends DynamicPathWizard
{
	private static String[] FETCH_GROUPS_PROJECT_TYPE = {
		FetchPlan.DEFAULT
	};

	private ProjectID parentProjectID;
	private CreateProjectTypeNameWizardPage namePage;

	public CreateProjectTypeWizard(ProjectID parentProjectID) {
		this.parentProjectID = parentProjectID;
	}

	@Override
	public void addPages() {
		namePage = new CreateProjectTypeNameWizardPage(parentProjectID);
		addPage(namePage);
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
							RCPUtil.openEditor(
							new ProjectEditorInput(parentProjectID),
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
