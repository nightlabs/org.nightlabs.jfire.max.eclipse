/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.projectphase;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectPhase;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTable;
import org.nightlabs.jfire.issuetracking.ui.issuelink.create.CreateIssueLinkWizard;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectEditorPageController;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class ProjectPhaseSection extends ToolBarSectionPart {

	private ProjectPhaseTable projectPhaseTable;
	
	public ProjectPhaseSection(FormPage page, Composite parent, final ProjectEditorPageController controller) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR, "Project's Phases");
		
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		projectPhaseTable = new ProjectPhaseTable(client, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 200;
		projectPhaseTable.setLayoutData(gd);
		
		getSection().setClient(client);
		
		getToolBarManager().add(new AddProjectPhaseAction());

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
						List<ProjectPhase> phases = project.getProjectPhases();
						projectPhaseTable.setInput(phases);
						
						if (project.getObjectId().equals(Project.PROJECT_ID_DEFAULT)) {
							projectPhaseTable.setEnabled(false);
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
	
	public class AddProjectPhaseAction extends Action {		
		public AddProjectPhaseAction() {
			super();
			setId(AddProjectPhaseAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(), 
					ProjectPhaseSection.class, 
					"Add"));
			setToolTipText("Add Phases");
			setText("Add Phases");
		}

		@Override
		public void run() {
			DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(new CreateProjectPhaseWizard(project.getObjectId()));
			dialog.open();
		}		
	}
}
