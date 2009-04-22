/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.project;

import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.base.ui.security.UserSearchDialog;
import org.nightlabs.jfire.base.ui.security.UserTable;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.jfire.security.User;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class ProjectMemberSection extends ToolBarSectionPart {

	private Label projectManagerNameLabel;
	private UserTable userTable;
	
	public ProjectMemberSection(FormPage page, Composite parent, final ProjectEditorPageController controller) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR, Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.ProjectMemberSection.section.text")); //$NON-NLS-1$
		
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		projectManagerNameLabel = new Label(client, SWT.WRAP);
		projectManagerNameLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		projectManagerNameLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.ProjectMemberSection.label.projectManagerName.text")); //$NON-NLS-1$
		
		userTable = new UserTable(client, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 200;
		userTable.setLayoutData(gd);
		
		getSection().setClient(client);
		
		getToolBarManager().add(new AssignPMAction());
		getToolBarManager().add(new AddMemberAction());

		updateToolBarManager();
	}

	private Project project;
	public void setProject(final Project project) {
		this.project = project;
		
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						User p = project.getProjectManager();
						projectManagerNameLabel.setText(
								String.format(
										Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.ProjectMemberSection.label.projectManagerName.fill.text"),  //$NON-NLS-1$
										p == null? "" : p.getName()) //$NON-NLS-1$
						);
						
						Set<User> members = project.getMembers();
						userTable.setInput(members);
						
						if (project.getObjectId().equals(Project.PROJECT_ID_DEFAULT)) {
							userTable.setEnabled(false);
							getToolBarManager().getControl().setEnabled(false);
						}
					}
				});
			}
		});
		
	}
	
	public Project getProject() {
		return project;
	}
	
	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent arg0) {
			markDirty();
		}
	};
	
	private User projectManager;
	public class AssignPMAction extends Action {		
		public AssignPMAction() {
			super();
			setId(AssignPMAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(), 
					ProjectMemberSection.class, 
					"Assign")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.ProjectMemberSection.AssignPMAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.ProjectMemberSection.AssignPMAction.text")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			UserSearchDialog userSearchDialog = new UserSearchDialog(getSection().getShell(), null);
			int returnCode = userSearchDialog.open();
			if (returnCode == Dialog.OK) {
				projectManager = userSearchDialog.getSelectedUser();
				if (projectManager != null) {
					project.setProjectManager(projectManager);
					project.addMember(projectManager);
				}
				setProject(project);
				markDirty();
			}//if
		}		
	}
	
	public class AddMemberAction extends Action {		
		public AddMemberAction() {
			super();
			setId(AddMemberAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(), 
					ProjectMemberSection.class, 
					"Add")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.ProjectMemberSection.AddMemberAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.ProjectMemberSection.AddMemberAction.text")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			UserSearchDialog userSearchDialog = new UserSearchDialog(getSection().getShell(), null);
			int returnCode = userSearchDialog.open();
			if (returnCode == Dialog.OK) {
				User user = userSearchDialog.getSelectedUser();
				if (user != null) {
					project.addMember(user);
				}
				userTable.setInput(project.getMembers());
				markDirty();
			}//if
		}		
	}
}
