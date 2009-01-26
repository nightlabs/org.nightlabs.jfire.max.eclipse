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
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
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
				ProjectAdminTreeComposite.class, 
				"Delete")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.DeleteProjectAction.DeleteProjectAction.toolTipText")); //$NON-NLS-1$
		setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.DeleteProjectAction.DeleteProjectAction.text")); //$NON-NLS-1$
	}
	
	@Override
	public void run() {
		boolean confirm = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.DeleteProjectAction.dialog.confirmDelete.title"), Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.DeleteProjectAction.dialog.confirmDelete.description")); //$NON-NLS-1$ //$NON-NLS-2$
		if(confirm) {
			TreeSelection selection = (TreeSelection)projectTreeViewer.getSelection();
			Project project = ((ProjectTreeNode)(selection.getFirstElement())).getJdoObject();
			ProjectDAO.sharedInstance().deleteProject(project.getObjectId(), new NullProgressMonitor());
			projectTreeViewer.refresh();
		}
	}		
}