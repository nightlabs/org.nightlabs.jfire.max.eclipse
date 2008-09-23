/**
 * 
 */
package org.nightlabs.jfire.issuetracking.admin.ui.project;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.base.ui.security.UserTable;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypePriorityEditWizard;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueDetailSection;

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
		projectManagerNameLabel.setLayoutData(new GridData());
		projectManagerNameLabel.setText("Project Manager: ");
		
		userTable = new UserTable(client, SWT.NONE);
		userTable.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
//				StructuredSelection s = (StructuredSelection)e.getSelection();
//				if (s.isEmpty())
//					return;
//
//				IssuePriority issuePriority = (IssuePriority)s.getFirstElement();
//				IssueTypePriorityEditWizard wizard = new IssueTypePriorityEditWizard(issuePriority, false, null);
//				try {
//					DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
//					dialog.open();
//				} catch (Exception ex) {
//					throw new RuntimeException(ex);
//				}
//				issuePriorityTable.refresh(true);
			}
		});	
		
		getSection().setClient(client);
	}

	private Project project;
	public void setProject(final Project project) {
		this.project = project;
		
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
//				nameText.setI18nText(project.getName(), EditMode.DIRECT);
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
	
	public class AssignPMAction extends Action {		
		public AssignPMAction() {
			super();
			setId(AssignPMAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(), 
					IssueDetailSection.class, 
					"Assign"));
			setToolTipText("Assign");
			setText("Assign");
		}

		@Override
		public void run() {
//			UserSearchDialog userSearchDialog = new UserSearchDialog(getSection().getShell(), null);
//			int returnCode = userSearchDialog.open();
//			if (returnCode == Dialog.OK) {
//				assigneeUser = userSearchDialog.getSelectedUser();
//				if (assigneeUser != null) {
//					issue.setAssignee(assigneeUser);
//					assigneeTextLabel.setText(issue.getAssignee().getName());
//				}
//				markDirty();
//			}//if
		}		
	}
}
