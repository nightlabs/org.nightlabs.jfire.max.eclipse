package org.nightlabs.jfire.issuetracking.ui.project;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.base.login.ui.part.LSDViewPart;
import org.nightlabs.jfire.issue.project.Project;
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
		projectTreeComposite = new ProjectAdminTreeComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL, true);
		projectTreeComposite.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				Project project = projectTreeComposite.getFirstSelectedElement();
				
				try {
					RCPUtil.openEditor(new ProjectEditorInput(project.getObjectId()),
							ProjectEditor.EDITOR_ID);
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		});

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
