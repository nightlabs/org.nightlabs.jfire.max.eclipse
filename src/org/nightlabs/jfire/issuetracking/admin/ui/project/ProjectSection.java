package org.nightlabs.jfire.issuetracking.admin.ui.project;

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
	
	private Collection<ProjectType> projectTypes;
	private ProjectType selectedProjectType;
	
	public ProjectSection(FormPage page, Composite parent, final ProjectEditorPageController controller) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR, "Projects");
		
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());
		
		XComposite client = new XComposite(getSection(), SWT.NONE);

		projectTypeLabel = new Label(client, SWT.WRAP);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		projectTypeLabel.setLayoutData(gd);
		projectTypeLabel.setText("Project Type: ");
		
		projectTypeCombo = new XComboComposite<ProjectType>(client, SWT.NONE);
		projectTypeCombo.setLabelProvider(new ProjectTypeLabelProvider());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		projectTypeCombo.setLayoutData(gd);
		projectTypeCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				controller.getProject().setProjectType(projectTypeCombo.getSelectedElement());
				markDirty();
			}
		});
		
		nameLabel = new Label(client, SWT.WRAP);
		nameLabel.setLayoutData(new GridData());
		nameLabel.setText("Name: ");
		
		nameText = new I18nTextEditor(client);
		nameText.addModifyListener(modifyListener);
		
		descriptionLabel = new Label(client, SWT.WRAP);
		descriptionLabel.setLayoutData(new GridData());
		descriptionLabel.setText("Description:");
		
		descriptionText = new I18nTextEditorMultiLine(client, nameText.getLanguageChooser());		
		descriptionText.addModifyListener(modifyListener);
		
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
//				projectTypeLabel.setText(
//						String.format(
//								"Project Type: %s", 
//								projectType == null? "" : projectType.getName().getText())
//				);
				
				nameText.setI18nText(project.getName(), EditMode.DIRECT);
				descriptionText.setI18nText(project.getDescription(), EditMode.DIRECT);
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