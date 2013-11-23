package org.nightlabs.jfire.issuetracking.ui.project;

import java.util.Collection;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectType;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class ProjectSection 
extends ToolBarSectionPart
{
	private Label projectTypeLabel;
	private ProjectTypeComboComposite projectTypeCombo;
	
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
	
	private Button activeButton;
	
	public ProjectSection(FormPage page, Composite parent, final ProjectEditorPageController controller) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR, Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.ProjectSection.section.text")); //$NON-NLS-1$
		
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		client.setLayout(gl);
		
		projectTypeLabel = new Label(client, SWT.WRAP);
		projectTypeLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.ProjectSection.label.projectType.text")); //$NON-NLS-1$
		
		projectTypeCombo = new ProjectTypeComboComposite(client, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		projectTypeCombo.setLayoutData(gd);
		projectTypeCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				controller.getProject().setProjectType(projectTypeCombo.getSelectedProjectType());
				markDirty();
			}
		});
		
		nameLabel = new Label(client, SWT.WRAP);
		nameLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.ProjectSection.label.name.text")); //$NON-NLS-1$
		
		nameText = new I18nTextEditor(client);
		nameText.addModifyListener(modifyListener);
		
		descriptionLabel = new Label(client, SWT.WRAP);
		descriptionLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.ProjectSection.label.description.text")); //$NON-NLS-1$
		
		descriptionText = new I18nTextEditorMultiLine(client, nameText.getLanguageChooser());		
		descriptionText.addModifyListener(modifyListener);
		
		new Label(client, SWT.NONE).setText(""); //$NON-NLS-1$
		
		activeButton = new Button(client, SWT.CHECK);
		activeButton.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.ProjectSection.button.active.text")); //$NON-NLS-1$
		activeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.getProject().setActive(activeButton.getSelection());
				markDirty();
			}
		});
		
		createdTimeLabel = new Label(client, SWT.WRAP);
		createdTimeLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.ProjectSection.label.createdTime.text")); //$NON-NLS-1$

		createdTimeTextLabel = new Label(client, SWT.NONE);
		createdTimeTextLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		updatedTimeLabel = new Label(client, SWT.WRAP);
		updatedTimeLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.ProjectSection.label.updatedTime.text")); //$NON-NLS-1$

		updatedTimeTextLabel = new Label(client, SWT.NONE);
		updatedTimeTextLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		getSection().setClient(client);
	}
	
	private Project project;
	public void setProject(final Project project) {
		this.project = project;
		
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				ProjectType projectType = project.getProjectType();
				if (projectType != null) {
					projectTypeCombo.setSelectedProjectType(projectType);
				}
				
				nameText.setI18nText(project.getName(), EditMode.DIRECT);
				descriptionText.setI18nText(project.getDescription(), EditMode.DIRECT);
				
				createdTimeTextLabel.setText(project.getCreateTimestamp().toString());
				updatedTimeTextLabel.setText(project.getUpdateTimestamp() == null? "-" : project.getUpdateTimestamp().toString()); //$NON-NLS-1$
				
				activeButton.setSelection(project.isActive());
				
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