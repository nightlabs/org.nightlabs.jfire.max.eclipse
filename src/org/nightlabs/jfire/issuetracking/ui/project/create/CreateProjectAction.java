package org.nightlabs.jfire.issuetracking.ui.project.create;

import org.eclipse.jface.action.Action;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectAdminTreeComposite;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

public class CreateProjectAction 
extends Action 
{
//	private InputDialog dialog;
	public CreateProjectAction() {
		setId(CreateProjectAction.class.getName());
		setImageDescriptor(SharedImages.getSharedImageDescriptor(
				IssueTrackingPlugin.getDefault(), 
				ProjectAdminTreeComposite.class, 
		"Create")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.create.CreateProjectAction.CreateProjectAction.toolTipText")); //$NON-NLS-1$
		setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.create.CreateProjectAction.CreateProjectAction.text")); //$NON-NLS-1$
	}

	@Override
	public void run() {
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(new CreateProjectWizard(null));
		dialog.open();
	}		
}