package org.nightlabs.jfire.issuetracking.ui.project;

import java.util.List;

import org.eclipse.jface.viewers.TreePath;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOObjectTreeNode;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.id.ProjectID;
import org.nightlabs.jfire.issuetracking.ui.issue.ActiveProjectTreeController;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 *
 */
public class ProjectTreeNode extends JDOObjectTreeNode<ProjectID, Project, ActiveProjectTreeController>
{
	public TreePath getTreePath() {
		List<JDOObjectTreeNode> nodes = getChildNodes();
		if (nodes != null) {
			nodes.add(0, this);
			return new TreePath(nodes.toArray(new JDOObjectTreeNode[0]));
		}
		return new TreePath(new Object[0]);		
	}
}