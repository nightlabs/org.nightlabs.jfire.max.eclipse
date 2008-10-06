package org.nightlabs.jfire.issuetracking.ui.project;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.issuetracking.ui.project.create.CreateProjectAction;

public class ProjectAdminView
extends LSDViewPart
{
	public final static String ID_VIEW = ProjectAdminView.class.getName();

	//Actions
	private CreateProjectAction createProjectAction;

	//Components
	private ProjectAdminTreeComposite projectTreeComposite;

	@Override
	public void createPartContents(Composite parent) {
		projectTreeComposite = new ProjectAdminTreeComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);

		createActions();
		createToolbarButtons();
	}

	@Override
	public boolean canDisplayPart() {
		return Login.isLoggedIn();
	}

	private void createActions() {
		createProjectAction = new CreateProjectAction();
	}
	private void createToolbarButtons() {
		getViewSite().getActionBars().getToolBarManager()
		.add(createProjectAction);
	}
}
