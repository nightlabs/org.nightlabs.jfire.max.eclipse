package org.nightlabs.jfire.issuetracking.admin.ui.project;

import java.util.Locale;

import javax.jdo.FetchPlan;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectDAO;
import org.nightlabs.jfire.issuetracking.admin.ui.IssueTrackingAdminPlugin;
import org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypeEditorPageController;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class ProjectSection 
extends ToolBarSectionPart
{
	private ProjectTreeComposite projectTreeComposite;

	private CreateProjectAction createProjectAction;
	private DeleteProjectAction deleteProjectAction;
	
	public ProjectSection(FormPage page, Composite parent, final IssueTypeEditorPageController controller) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR, "Severity Types");
		getSection().setText("Project");
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());
		
		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1; 
		
		projectTreeComposite = new ProjectTreeComposite(
				client, AbstractTreeComposite.DEFAULT_STYLE_SINGLE);
		projectTreeComposite.getGridData().grabExcessHorizontalSpace = true;
		projectTreeComposite.getGridData().heightHint = 80;
		
		getSection().setClient(client);
		
		createProjectAction = new CreateProjectAction();
		deleteProjectAction = new DeleteProjectAction();
		
		getToolBarManager().add(createProjectAction);
		getToolBarManager().add(deleteProjectAction);
		
		updateToolBarManager();
	}
	
	public class CreateProjectAction extends Action {
		private InputDialog dialog;
		public CreateProjectAction() {
			setId(CreateProjectAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingAdminPlugin.getDefault(), 
					ProjectSection.class, 
			"Create"));
			setToolTipText("Create Project");
			setText("Create");
		}

		@Override
		public void run() {
			dialog = new InputDialog(RCPUtil.getActiveShell(), "Create Project", "Enter project's name", "Name", null) {
				@Override
				protected void okPressed() {
					try {
						Project project = new Project(Login.getLogin().getOrganisationID(), IDGenerator.nextID(Project.class));
						project.getName().setText(Locale.ENGLISH.getLanguage(), getValue());
						ProjectDAO.sharedInstance().storeProject(project, false, new String[]{FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
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
	
	public class DeleteProjectAction extends Action {		
		public DeleteProjectAction() {
			setId(DeleteProjectAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingAdminPlugin.getDefault(), 
					ProjectSection.class, 
			"Delete"));
			setToolTipText("Delete Project(s)");
			setText("Delete");
		}

		@Override
		public void run() {
//			issueLinkAdderComposite.getIssueLinkTable().removeIssueLinkTableItems(issueLinkAdderComposite.getIssueLinkTable().getSelectedElements());
		}		
	}
}