package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.jdo.tree.ActiveJDOObjectTreeController;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectDAO;
import org.nightlabs.jfire.issue.project.ProjectParentResolver;
import org.nightlabs.jfire.issue.project.id.ProjectID;
import org.nightlabs.jfire.jdo.notification.TreeNodeParentResolver;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 *
 */
public class ActiveProjectTreeController extends ActiveJDOObjectTreeController<ProjectID, Project, ProjectTreeNode>
{
	public static final String[] FETCH_GROUPS_PROJECT = {
		FetchPlan.DEFAULT, Project.FETCH_GROUP_NAME, Project.FETCH_GROUP_SUBPROJECTS, Project.FETCH_GROUP_PARENT_PROJECT
	};

	@Implement
	@Override
	protected ProjectTreeNode createNode()
	{
		return new ProjectTreeNode();
	}

	@Implement
	@Override
	protected Collection<Project> retrieveChildren(ProjectID parentID, Project parent, IProgressMonitor monitor)
	{
		if (parentID != null) {
			Project project = ProjectDAO.sharedInstance().getProject(
					parentID, FETCH_GROUPS_PROJECT, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new ProgressMonitorWrapper(monitor));
			Collection<Project> res = project.getSubProjects();
			return res;
		}
		
		return ProjectDAO.sharedInstance().getRootProjects(Login.sharedInstance().getOrganisationID(), FETCH_GROUPS_PROJECT, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new ProgressMonitorWrapper(monitor));
	}

	@Implement
	@Override
	protected Collection<Project> retrieveJDOObjects(Set<ProjectID> objectIDs, IProgressMonitor monitor)
	{
		Collection<Project> res = ProjectDAO.sharedInstance().getProjects(
				objectIDs, new String[]{Project.FETCH_GROUP_PARENT_PROJECT, Project.FETCH_GROUP_NAME}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new ProgressMonitorWrapper(monitor));
		return res;
	}

	@Implement
	@Override
	protected void sortJDOObjects(List<Project> objects)
	{
		// no need to sort now - later maybe
	}

	@Implement
	@Override
	protected TreeNodeParentResolver createTreeNodeParentResolver()
	{
		return new ProjectParentResolver();
	}

	@Implement
	@Override
	protected Class<Project> getJDOObjectClass()
	{
		return Project.class;
	}
}