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
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectDAO;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.progress.NullProgressMonitor;

public class CreateSubProjectAction 
extends Action 
{
	private InputDialog dialog;
	private TreeViewer projectTreeViewer;
	public CreateSubProjectAction(TreeViewer projectTreeViewer) {
		this.projectTreeViewer = projectTreeViewer;
		setId(CreateSubProjectAction.class.getName());
		setImageDescriptor(SharedImages.getSharedImageDescriptor(
				IssueTrackingPlugin.getDefault(), 
				ProjectTreeComposite.class, 
		"Create"));
		setToolTipText("Create Sub Project");
		setText("Create Sub Project");
	}

	@Override
	public void run() {
		dialog = new InputDialog(RCPUtil.getActiveShell(), "Create Sub Project", "Enter project's name", "Name", null) {
			@Override
			protected void okPressed() {
				try {
					TreeSelection selection = (TreeSelection)projectTreeViewer.getSelection();
					Project projectToStore = ((ProjectTreeNode)(selection.getFirstElement())).getJdoObject();
					Project project = new Project(Login.getLogin().getOrganisationID(), IDGenerator.nextID(Project.class));
					project.getName().setText(Locale.ENGLISH.getLanguage(), getValue());

					projectToStore = ProjectDAO.sharedInstance().getProject(
							projectToStore.getObjectId(), new String[]{Project.FETCH_GROUP_NAME, Project.FETCH_GROUP_SUBPROJECTS}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
					projectToStore.addSubProject(project);
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