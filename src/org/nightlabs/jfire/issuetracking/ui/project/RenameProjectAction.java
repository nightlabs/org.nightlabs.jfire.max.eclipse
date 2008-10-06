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
		"Rename"));
		setToolTipText("Rename Project");
		setText("Rename Project");
	}

	@Override
	public void run() {
		TreeSelection selection = (TreeSelection)projectTreeViewer.getSelection();
		final Project projectToStore = ((ProjectTreeNode)(selection.getFirstElement())).getJdoObject();
		dialog = new InputDialog(RCPUtil.getActiveShell(), "Rename Project", "Enter project's name", projectToStore.getName().getText(), null) {
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