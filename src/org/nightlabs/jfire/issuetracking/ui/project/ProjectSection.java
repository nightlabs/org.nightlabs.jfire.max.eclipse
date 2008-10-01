package org.nightlabs.jfire.issuetracking.ui.project;

import java.util.Collection;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectType;
import org.nightlabs.jfire.issue.project.ProjectTypeDAO;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class ProjectSection 
extends ToolBarSectionPart
{
	private Label projectTypeLabel;
	private XComboComposite<ProjectType> projectTypeCombo;
	private Label nameLabel;
	private I18nTextEditor nameText;
	private Label descriptionLabel;
	private I18nTextEditor descriptionText;
	
	private Label createdTimeLabel;
	private Label createdTimeTextLabel;

	private Label updatedTimeLabel;
	private Label updatedTimeTextLabel;
	
	private Collection<ProjectType> projectTypes;
	private ProjectType selectedProjectType;
	
	public ProjectSection(FormPage page, Composite parent, final ProjectEditorPageController controller) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR, "Project");
		
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		client.setLayout(gl);
		
		projectTypeLabel = new Label(client, SWT.WRAP);
		projectTypeLabel.setText("Project Type: ");
		
		projectTypeCombo = new XComboComposite<ProjectType>(client, SWT.NONE);
		projectTypeCombo.setLabelProvider(new ProjectTypeLabelProvider());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		projectTypeCombo.setLayoutData(gd);
		projectTypeCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				controller.getProject().setProjectType(projectTypeCombo.getSelectedElement());
				markDirty();
			}
		});
		
		nameLabel = new Label(client, SWT.WRAP);
		nameLabel.setText("Name: ");
		
		nameText = new I18nTextEditor(client);
		nameText.addModifyListener(modifyListener);
		
		descriptionLabel = new Label(client, SWT.WRAP);
		descriptionLabel.setText("Description: ");
		
		descriptionText = new I18nTextEditorMultiLine(client, nameText.getLanguageChooser());		
		descriptionText.addModifyListener(modifyListener);
	
		createdTimeLabel = new Label(client, SWT.WRAP);
		createdTimeLabel.setText("Created Time: ");

		createdTimeTextLabel = new Label(client, SWT.NONE);
		createdTimeTextLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		updatedTimeLabel = new Label(client, SWT.WRAP);
		updatedTimeLabel.setText("Updated Time: ");

		updatedTimeTextLabel = new Label(client, SWT.NONE);
		updatedTimeTextLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		getSection().setClient(client);
		
		//Loading Data
		Job loadJob = new Job("Loading Project Types....") {
			@Override
			protected IStatus run(final ProgressMonitor monitor) throws Exception
			{
				projectTypes = 
					ProjectTypeDAO.sharedInstance().getProjectTypes(new String[] {FetchPlan.DEFAULT, ProjectType.FETCH_GROUP_NAME}, 
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						projectTypeCombo.removeAll();
						for (ProjectType projectType : projectTypes) {
							projectTypeCombo.addElement(projectType);
						}
						selectedProjectType = projectTypeCombo.getSelectedElement();
					}
				});

				return Status.OK_STATUS;
			} 
		};
		loadJob.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		loadJob.schedule();
	}
	
	private Project project;
	public void setProject(final Project project) {
		this.project = project;
		
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				ProjectType projectType = project.getProjectType();
				if (projectType != null) {
					projectTypeCombo.selectElement(projectType);
				}
				
				nameText.setI18nText(project.getName(), EditMode.DIRECT);
				descriptionText.setI18nText(project.getDescription(), EditMode.DIRECT);
				
				createdTimeTextLabel.setText(project.getCreateTimestamp().toString());
				updatedTimeTextLabel.setText(project.getUpdateTimestamp() == null? "-" : project.getUpdateTimestamp().toString());
				
				if (project.getObjectId().equals(Project.PROJECT_ID_DEFAULT)) {
					projectTypeCombo.setEnabled(false);
					nameText.setEnabled(false);
					descriptionText.setEnabled(false);
				}
			}
		});
		
	}
	
	public Project getProject() {
		return project;
	}
	
	public ProjectType getSelectedProjectType() {
		return selectedProjectType;
	}
	
	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent arg0) {
			markDirty();
		}
	};
	
	public class ProjectTypeLabelProvider extends LabelProvider{
		@Override
		public String getText(Object element) 
		{
			if (element instanceof ProjectType) {
				ProjectType projectType = (ProjectType) element;
				return projectType.getName().getText();
			}

			return super.getText(element);
		}		
	}
}