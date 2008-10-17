package org.nightlabs.jfire.issuetracking.ui.projectphase;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.ui.IEditorInput;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.jfire.issue.project.ProjectPhase;
import org.nightlabs.jfire.issue.project.ProjectPhaseDAO;
import org.nightlabs.jfire.issue.project.id.ProjectPhaseID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

public class ProjectPhaseEditorPageController 
extends ActiveEntityEditorPageController<ProjectPhase>
{
	private ProjectPhaseID projectPhaseID;
	private ProjectPhase projectPhase;
	
	public ProjectPhaseEditorPageController(EntityEditor editor)
	{
		super(editor);
	}
	
	protected ProjectPhaseID getProjectPhaseID() {
		ProjectPhaseEditorInput input = (ProjectPhaseEditorInput) getEntityEditor().getEditorInput();
		return input.getJDOObjectID();
	}
	
	public ProjectPhase getProjectPhase() {
		return getControllerObject();
	}

	@Override
	protected String[] getEntityFetchGroups() {
		return new String[] {
				FetchPlan.DEFAULT,
				ProjectPhase.FETCH_GROUP_NAME,
				ProjectPhase.FETCH_GROUP_DESCRIPTION};
	}

	@Override
	protected ProjectPhase retrieveEntity(ProgressMonitor monitor) {
		ProjectPhase projectPhase = ProjectPhaseDAO.sharedInstance().getProjectPhase(getProjectPhaseID(), getEntityFetchGroups(), getEntityMaxFetchDepth(), monitor);
		return projectPhase;
	}
	
	@Override
	protected IEditorInput createNewInstanceEditorInput() {
		return new ProjectPhaseEditorInput(getProjectPhaseID());
	}

	@Override
	protected ProjectPhase storeEntity(ProjectPhase controllerObject,
			ProgressMonitor monitor) {
		monitor.beginTask("Saving projec phase", 100);
		try {
			ProjectPhaseID projectPhaseID = (ProjectPhaseID) JDOHelper.getObjectId(controllerObject);
			if (projectPhaseID == null)
				throw new IllegalStateException("JDOHelper.getObjectId(controllerObject) returned null for controllerObject=" + controllerObject);
			
			projectPhase = ProjectPhaseDAO.sharedInstance().storeProjectPhase(
						controllerObject, true, getEntityFetchGroups(), getEntityMaxFetchDepth(),
						new SubProgressMonitor(monitor, 50)
				);

			return projectPhase;
		} finally {
			monitor.done();
		}
	}
}
