package org.nightlabs.jfire.issuetracking.ui.project;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.ui.IEditorInput;
import org.nightlabs.base.ui.editor.JDOObjectEditorInput;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectDAO;
import org.nightlabs.jfire.issue.project.ProjectType;
import org.nightlabs.jfire.issue.project.id.ProjectID;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/** 
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 */
public class ProjectEditorPageController 
extends ActiveEntityEditorPageController<Project>
{
	private Project project;

	public ProjectEditorPageController(EntityEditor editor)
	{
		super(editor);
		this.projectID = (ProjectID) ((JDOObjectEditorInput<?>)editor.getEditorInput()).getJDOObjectID();
	}

	public Project getProject() {
		return getControllerObject();
	}

	@Override
	protected String[] getEntityFetchGroups() {
		return new String[] {
				FetchPlan.DEFAULT,
				Project.FETCH_GROUP_PROJECT_TYPE,
				Project.FETCH_GROUP_NAME,
				ProjectType.FETCH_GROUP_NAME,
				Project.FETCH_GROUP_DESCRIPTION,
				Project.FETCH_GROUP_PROJECT_MANAGER,
				Project.FETCH_GROUP_MEMBERS};
	}

	private ProjectID projectID;
	
	@Override
	protected Project retrieveEntity(ProgressMonitor monitor) {
		Project project = ProjectDAO.sharedInstance().getProject(projectID, getEntityFetchGroups(), getEntityMaxFetchDepth(), monitor);
		return project;
	}

	@Override
	protected IEditorInput createNewInstanceEditorInput() {
		return new ProjectEditorInput(projectID);
	}

	@Override
	protected Project storeEntity(Project controllerObject,
			ProgressMonitor monitor) {
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.ProjectEditorPageController.monitor.savingProject.text"), 100); //$NON-NLS-1$
		try {
			ProjectID projectID = (ProjectID) JDOHelper.getObjectId(controllerObject);
			if (projectID == null)
				throw new IllegalStateException("JDOHelper.getObjectId(controllerObject) returned null for controllerObject=" + controllerObject); //$NON-NLS-1$

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
