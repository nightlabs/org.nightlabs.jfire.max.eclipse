/**
 * 
 */
package org.nightlabs.jfire.issuetracking.admin.ui.project;

import java.util.List;
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
import org.nightlabs.jfire.issuetracking.admin.ui.IssueTrackingAdminPlugin;
import org.nightlabs.jfire.security.User;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class ProjectMemberSection extends ToolBarSectionPart {

	private Label projectManagerNameLabel;
	private UserTable userTable;
	
	public ProjectMemberSection(FormPage page, Composite parent, final ProjectEditorPageController controller) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR, "Project's Members");
		
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		projectManagerNameLabel = new Label(client, SWT.WRAP);
		projectManagerNameLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		projectManagerNameLabel.setText("Project Manager: ");
		
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
										"Project Manager: %s", 
										p == null? "" : p.getName())
						);
						
						Set<User> members = project.getMembers();
						userTable.setInput(members);
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
					IssueTrackingAdminPlugin.getDefault(), 
					ProjectMemberSection.class, 
					"Assign"));
			setToolTipText("Assign Project Manager");
			setText("Assign");
		}

		@Override
		public void run() {
			UserSearchDialog userSearchDialog = new UserSearchDialog(getSection().getShell(), null);
			int returnCode = userSearchDialog.open();
			if (returnCode == Dialog.OK) {
				projectManager = userSearchDialog.getSelectedUser();
				if (projectManager != null) {
					project.setProjectManager(projectManager);
				}
				markDirty();
			}//if
		}		
	}
	
	public class AddMemberAction extends Action {		
		public AddMemberAction() {
			super();
			setId(AssignPMAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingAdminPlugin.getDefault(), 
					ProjectMemberSection.class, 
					"Add"));
			setToolTipText("Add Members");
			setText("Add Members");
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
