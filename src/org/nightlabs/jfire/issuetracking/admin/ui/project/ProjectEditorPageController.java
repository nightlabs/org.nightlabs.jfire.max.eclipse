package org.nightlabs.jfire.issuetracking.admin.ui.project;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.ui.IEditorInput;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectDAO;
import org.nightlabs.jfire.issue.project.id.ProjectID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

public class ProjectEditorPageController 
extends ActiveEntityEditorPageController<Project>
{
	private ProjectID projectID;
	private Project project;
	
	public ProjectEditorPageController(EntityEditor editor)
	{
		super(editor);
	}
	
	protected ProjectID getProjectID() {
		ProjectEditorInput input = (ProjectEditorInput) getEntityEditor().getEditorInput();
		return input.getJDOObjectID();
	}
	
	public Project getProject() {
		return getControllerObject();
	}

	@Override
	protected String[] getEntityFetchGroups() {
		return new String[] {
				FetchPlan.DEFAULT,
				Project.FETCH_GROUP_NAME,
				Project.FETCH_GROUP_DESCRIPTION};
//				Project.FETCH_GROUP_PARENT_PROJECT,
//				Project.FETCH_GROUP_SUBPROJECTS
	}

	@Override
	protected Project retrieveEntity(ProgressMonitor monitor) {
		Project project = ProjectDAO.sharedInstance().getProject(getProjectID(), getEntityFetchGroups(), getEntityMaxFetchDepth(), monitor);
		return project;
		
	}
	
	@Override
	protected IEditorInput createNewInstanceEditorInput() {
		return new ProjectEditorInput(getProjectID());
	}

	@Override
	protected Project storeEntity(Project controllerObject,
			ProgressMonitor monitor) {
		monitor.beginTask("Saving project", 100);
		try {
			ProjectID projectID = (ProjectID) JDOHelper.getObjectId(controllerObject);
			if (projectID == null)
				throw new IllegalStateException("JDOHelper.getObjectId(controllerObject) returned null for controllerObject=" + controllerObject);
			
			project = ProjectDAO.sharedInstance().storeProject(
						controllerObject, true, getEntityFetchGroups(), getEntityMaxFetchDepth(),
						new SubProgressMonitor(monitor, 50)
				);

			return project;
		} finally {
			monitor.done();
		}
	}
}
