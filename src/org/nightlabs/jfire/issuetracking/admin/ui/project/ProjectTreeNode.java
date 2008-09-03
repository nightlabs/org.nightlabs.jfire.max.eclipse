package org.nightlabs.jfire.issuetracking.admin.ui.project;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectDAO;
import org.nightlabs.jfire.issuetracking.admin.ui.IssueTrackingAdminPlugin;
import org.nightlabs.progress.NullProgressMonitor;

public class ProjectTreeNode 
{
	private Project object;
	private ProjectTreeNode parent;
	private List<ProjectTreeNode> childNodes = new LinkedList<ProjectTreeNode>();
	
	protected boolean childrenLoaded = false;
	
	private TreeViewer treeViewer = null;
	
	public ProjectTreeNode(TreeViewer treeViewer, Project object){
		this.treeViewer = treeViewer;
		this.object = object;
	}
	
	public void addChild(ProjectTreeNode child)
	{
		childNodes.add(child);
	}
	
	public void removeChild(ProjectTreeNode child)
	{
		childNodes.remove(child);
	}
	
	public String getLabel()
	{
		return object.getName().getText();
	}

	public ProjectTreeNode[] getChildren()
	{
		if(!childrenLoaded){
			loadChildren();
		}//if
		return childNodes.toArray(new ProjectTreeNode[0]);
	}

	public boolean hasChildren()
	{
		return true;
	}
	
	private static String[] FETCH_GROUPS = new String[]{ Project.FETCH_GROUP_SUBPROJECTS};
	
	public void loadChildren() {
		if(isChildrenLoaded())
			return;
		
		childNodes.clear();
		
		if(object != null){
			object = ProjectDAO.sharedInstance().getProject(object.getObjectId(), FETCH_GROUPS, 1, new NullProgressMonitor());
			Collection<Project> p = object.getSubProjects();
			for(Project po : p) {
				childNodes.add(new ProjectTreeNode(treeViewer, po));
			}//for
			setChildrenLoaded(true);
		}//if
		else {
			try {
				Collection<Project> projects = ProjectDAO.sharedInstance().getRootProjects(Login.getLogin().getOrganisationID());
				for (Project p : projects) {
					childNodes.add(new ProjectTreeNode(treeViewer, p));
				}
			} catch (LoginException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public ImageDescriptor getImageDescriptor() {
		return IssueTrackingAdminPlugin.getImageDescriptor("icons/city.png"); //$NON-NLS-1$
	}

	public I18nText getName() {
		return object.getName();
	}
	
	protected void setObject(Project obj){
		this.object = obj;
	}
	
	public Object getObject(){
		return object;
	}
	
	public void setParent(ProjectTreeNode parent){
		this.parent = parent;
	}
	
	public ProjectTreeNode getParent(){
		return parent;
	}
	
	public boolean isChildrenLoaded() {
		return childrenLoaded;
	}
	
	public void setChildrenLoaded(boolean isChildrenLoaded){
		this.childrenLoaded = isChildrenLoaded;
	}
	
	public ProjectTreeNode findNode(ProjectTreeNode node){
		if(getObject().equals(node)){
			return this;
		}
		else{
			ProjectTreeNode[] nodes = getChildren();
			if(nodes != null){
				for(ProjectTreeNode iNode : nodes){
					if(iNode.equals(node)){
						return iNode;
					}//if
				}//for
			}//if
		}//else
		
		return null;
	}
}
