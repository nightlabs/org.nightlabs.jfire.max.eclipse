package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.base.ui.jdo.tree.ActiveJDOObjectTreeController;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectDAO;
import org.nightlabs.jfire.issue.project.ProjectParentResolver;
import org.nightlabs.jfire.issue.project.id.ProjectID;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectTreeNode;
import org.nightlabs.jfire.jdo.notification.TreeNodeParentResolver;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 *
 */
public class ActiveProjectTreeController extends ActiveJDOObjectTreeController<ProjectID, Project, ProjectTreeNode>
{
	public static final String[] FETCH_GROUPS_PROJECT = {
		FetchPlan.DEFAULT, Project.FETCH_GROUP_NAME, Project.FETCH_GROUP_SUBPROJECTS, Project.FETCH_GROUP_PARENT_PROJECT
	};

	@Override
	protected ProjectTreeNode createNode()
	{
		return new ProjectTreeNode();
	}

	@Override
	protected Collection<Project> retrieveChildren(ProjectID parentID, Project parent, ProgressMonitor monitor)
	{
		if (parentID != null) {
			Project project = ProjectDAO.sharedInstance().getProject(
					parentID, FETCH_GROUPS_PROJECT, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
			Collection<Project> res = project.getSubProjects();
			return res;
		}

		Collection<Project> projects = ProjectDAO.sharedInstance().getRootProjects(Login.sharedInstance().getOrganisationID(), FETCH_GROUPS_PROJECT, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		return projects;
	}

	@Override
	protected Collection<Project> retrieveJDOObjects(Set<ProjectID> objectIDs, ProgressMonitor monitor)
	{
		Collection<Project> res = ProjectDAO.sharedInstance().getProjects(
				objectIDs, new String[]{Project.FETCH_GROUP_PARENT_PROJECT, Project.FETCH_GROUP_NAME}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		return res;
	}

	@Override
	protected void sortJDOObjects(List<Project> objects)
	{
		Collections.sort(objects);
	}

	@Override
	protected TreeNodeParentResolver createTreeNodeParentResolver()
	{
		return new ProjectParentResolver();
	}

	@Override
	protected Class<Project> getJDOObjectClass()
	{
		return Project.class;
	}
}