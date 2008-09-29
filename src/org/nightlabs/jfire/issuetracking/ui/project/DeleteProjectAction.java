package org.nightlabs.jfire.issuetracking.ui.project;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectDAO;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.progress.NullProgressMonitor;

public class DeleteProjectAction 
extends Action 
{		
	private TreeViewer projectTreeViewer;
	public DeleteProjectAction(TreeViewer projectTreeViewer) {
		this.projectTreeViewer = projectTreeViewer;
		setId(DeleteProjectAction.class.getName());
		setImageDescriptor(SharedImages.getSharedImageDescriptor(
				IssueTrackingPlugin.getDefault(), 
				ProjectTreeComposite.class, 
				"Delete"));
		setToolTipText("Delete the selected project");
		setText("Delete");
	}
	
	@Override
	public void run() {
		boolean confirm = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Confirm Delete", "Delete this item(s)?");
		if(confirm) {
			TreeSelection selection = (TreeSelection)projectTreeViewer.getSelection();
			Project project = ((ProjectTreeNode)(selection.getFirstElement())).getJdoObject();
			ProjectDAO.sharedInstance().deleteProject(project.getObjectId(), new NullProgressMonitor());
			projectTreeViewer.refresh();
		}
	}		
}