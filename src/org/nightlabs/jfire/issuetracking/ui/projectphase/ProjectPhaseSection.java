/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.projectphase;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectPhase;
import org.nightlabs.jfire.issue.project.id.ProjectPhaseID;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectEditorPageController;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class ProjectPhaseSection extends ToolBarSectionPart {

	private ProjectPhaseTable projectPhaseTable;
	
	public ProjectPhaseSection(FormPage page, Composite parent, final ProjectEditorPageController controller) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR, Messages.getString("org.nightlabs.jfire.issuetracking.ui.projectphase.ProjectPhaseSection.title")); //$NON-NLS-1$
		
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		projectPhaseTable = new ProjectPhaseTable(client, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 200;
		projectPhaseTable.setLayoutData(gd);
		
		projectPhaseTable.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				StructuredSelection s = (StructuredSelection)e.getSelection();
				if (s.isEmpty())
					return;

				ProjectPhase selectedProjectPhase = (ProjectPhase)s.getFirstElement();

				if (selectedProjectPhase.getObjectId() == null) {
					MessageDialog.openError(getSection().getShell(), 
							Messages.getString("org.nightlabs.jfire.issuetracking.ui.projectphase.ProjectPhaseSection.dialog.saveProject.title"),  //$NON-NLS-1$
							Messages.getString("org.nightlabs.jfire.issuetracking.ui.projectphase.ProjectPhaseSection.dialog.saveProject.description")); //$NON-NLS-1$
					return;
				}
				
				ProjectPhaseEditorInput projectPhaseEditorInput = 
					new ProjectPhaseEditorInput(ProjectPhaseID.create(selectedProjectPhase.getOrganisationID(), selectedProjectPhase.getProjectPhaseID()));
				try {
					RCPUtil.openEditor(projectPhaseEditorInput, ProjectPhaseEditor.EDITOR_ID);
				} catch (PartInitException e1) {
					throw new RuntimeException(e1);
				}
			}
		});
		
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
					"Add")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.projectphase.ProjectPhaseSection.AddProjectPhaseAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.projectphase.ProjectPhaseSection.AddProjectPhaseAction.text")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			CreateProjectPhaseWizard projectPhaseWizard = new CreateProjectPhaseWizard(project) {
				@Override
				public boolean performFinish() {
					projectPhaseTable.addElement(getNewProjectPhase());
					markDirty();
					return super.performFinish();
				}
			};
			DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(projectPhaseWizard);
			dialog.open();
		}		
	}
}
