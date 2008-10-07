package org.nightlabs.jfire.issuetracking.ui.project.create;

import org.eclipse.jface.action.Action;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectAdminTreeComposite;

public class CreateProjectAction 
extends Action 
{
//	private InputDialog dialog;
	public CreateProjectAction() {
		setId(CreateProjectAction.class.getName());
		setImageDescriptor(SharedImages.getSharedImageDescriptor(
				IssueTrackingPlugin.getDefault(), 
				ProjectAdminTreeComposite.class, 
		"Create"));
		setToolTipText("Create Project");
		setText("Create Project");
	}

	@Override
	public void run() {
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(new CreateProjectWizard(null));
		dialog.open();
//		dialog = new InputDialog(RCPUtil.getActiveShell(), "Create Project", "Enter project's name", "Name", null) {
//			@Override
//			protected void okPressed() {
//				try {
//					Project project = new Project(Login.getLogin().getOrganisationID(), IDGenerator.nextID(Project.class));
//					project.getName().setText(Locale.ENGLISH.getLanguage(), getValue());
//
//					ProjectDAO.sharedInstance().storeProject(project, false, new String[]{FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
//					dialog.close();
//				} catch (Exception e) {
//					throw new RuntimeException(e);
//				}
//			};
//
//			@Override
//			protected Control createDialogArea(Composite parent) {
//				Control dialogArea = super.createDialogArea(parent);
//				return dialogArea;
//			}
//		};
//
//		if (dialog.open() != Window.OK)
//			return;
	}		
}