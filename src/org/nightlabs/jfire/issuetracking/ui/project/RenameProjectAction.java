package org.nightlabs.jfire.issuetracking.ui.project;

import java.util.Locale;

import javax.jdo.FetchPlan;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectDAO;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.project.create.CreateProjectAction;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;

public class RenameProjectAction extends Action {
	private InputDialog dialog;
	private TreeViewer projectTreeViewer;
	public RenameProjectAction(TreeViewer projectTreeViewer) {
		this.projectTreeViewer = projectTreeViewer;
		setId(CreateProjectAction.class.getName());
		setImageDescriptor(SharedImages.getSharedImageDescriptor(
				IssueTrackingPlugin.getDefault(), 
				ProjectAdminTreeComposite.class, 
		"Rename")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.RenameProjectAction.RenameProjectAction.toolTipText")); //$NON-NLS-1$
		setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.RenameProjectAction.RenameProjectAction.text")); //$NON-NLS-1$
	}

	@Override
	public void run() {
		TreeSelection selection = (TreeSelection)projectTreeViewer.getSelection();
		final Project projectToStore = ((ProjectTreeNode)(selection.getFirstElement())).getJdoObject();
		dialog = new InputDialog(RCPUtil.getActiveShell(), Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.RenameProjectAction.dialog.renameProject.title"), Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.RenameProjectAction.dialog.renameProject.description"), projectToStore.getName().getText(), null) { //$NON-NLS-1$ //$NON-NLS-2$
			@Override
			protected void okPressed() {
				try {
					projectToStore.getName().setText(Locale.ENGLISH.getLanguage(), getValue());
					ProjectDAO.sharedInstance().storeProject(projectToStore, false, new String[]{FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
					dialog.close();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			};

			@Override
			protected Control createDialogArea(Composite parent) {
				Control dialogArea = super.createDialogArea(parent);
				return dialogArea;
			}
		};

		if (dialog.open() != Window.OK)
			return;
	}		
}