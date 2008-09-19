package org.nightlabs.jfire.issuetracking.admin.ui.project;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectDAO;
import org.nightlabs.jfire.issue.project.id.ProjectID;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

public class ProjectEditorPageController extends EntityEditorPageController {

	/**
	 * The fetch groups of issue data.
	 */
	public static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		Project.FETCH_GROUP_NAME,
		Project.FETCH_GROUP_DESCRIPTION,
		Project.FETCH_GROUP_PARENT_PROJECT,
		Project.FETCH_GROUP_SUBPROJECTS};
	
	
	private ProjectID projectID;
	private Project project;
	
	public ProjectEditorPageController(EntityEditor editor)
	{
		super(editor);
	}
	
	public void doLoad(ProgressMonitor monitor) {
		monitor.beginTask("Loading Project....", 100);
		
		ProjectEditorInput input = (ProjectEditorInput)getEntityEditor().getEditorInput();
		this.projectID = input.getJDOObjectID();

		project = 
			ProjectDAO.sharedInstance().getProject(projectID, 
					FETCH_GROUPS,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
					new NullProgressMonitor());
		
		monitor.done();
		setLoaded(true); // must be done before fireModifyEvent!
		fireModifyEvent(null, project);
	}

	public boolean doSave(ProgressMonitor monitor) {
		ProjectDAO.sharedInstance().storeProject(project, false, FETCH_GROUPS,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
					new NullProgressMonitor());
		return true;
	}
	
	public ProjectID getProjectID() {
		return projectID;
	}
	
	public Project getProject() {
		return project;
	}
}
