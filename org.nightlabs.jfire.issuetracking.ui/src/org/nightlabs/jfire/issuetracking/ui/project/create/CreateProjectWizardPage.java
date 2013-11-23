package org.nightlabs.jfire.issuetracking.ui.project.create;

import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedListener;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectType;
import org.nightlabs.jfire.issue.project.id.ProjectID;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.issue.ActiveProjectTreeController;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectAdminTreeComposite;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectTreeNode;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectTypeComboComposite;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

public class CreateProjectWizardPage extends DynamicPathWizardPage {
	private Label projectTypeLabel;
	private ProjectTypeComboComposite projectTypeCombo;

	private Label projectNameLabel;
	private I18nTextEditor projectNameText;

	private Label descriptionLabel;
	private I18nTextEditor descriptionText;

	private Button activeButton;
	private boolean isActive = true;

	private Project parentProject;
	private Project newProject;

	//For Tree
	private ProjectTreeNode selectedNode;
	
	@Override
	public Control createPageContents(Composite parent) {
		XComposite page = new XComposite(parent, SWT.NONE,
				LayoutMode.TOP_BOTTOM_WRAPPER, LayoutDataMode.GRID_DATA);
		page.getGridLayout().numColumns = 2;
		page.getGridLayout().verticalSpacing = 10;
		// page.getGridLayout().horizontalSpacing = 10;

		projectTypeLabel = new Label(page, SWT.NONE);
		projectTypeLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.create.CreateProjectWizardPage.label.projectType.text")); //$NON-NLS-1$
		projectTypeCombo = new ProjectTypeComboComposite(page, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		projectTypeCombo.setLayoutData(gridData);
		projectTypeCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				newProject.setProjectType(projectTypeCombo.getSelectedProjectType());
			}
		});
		
		if (parentProject != null) {
			new Label(page, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.create.CreateProjectWizardPage.label.parentProject.text")); //$NON-NLS-1$
			final ProjectAdminTreeComposite projectTree = new ProjectAdminTreeComposite(
					page, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, false);
			projectTree.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					newProject.setParentProject(projectTree.getFirstSelectedElement());
				}
			});
			
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.heightHint = 20;
			
			ActiveProjectTreeController c = (ActiveProjectTreeController)projectTree.getInput();
			c.addJDOTreeNodesChangedListener(new JDOTreeNodesChangedListener<ProjectID, Project, ProjectTreeNode>() {
				@Override
				public void onJDOObjectsChanged(
						JDOTreeNodesChangedEvent<ProjectID, ProjectTreeNode> changedEvent) {
					List<ProjectTreeNode> loadedNodes = changedEvent.getLoadedTreeNodes();
					projectTree.getTreeViewer().expandAll();
				
					for (ProjectTreeNode node : loadedNodes) {
						if (node.getJdoObject().equals(parentProject)) {
							selectedNode = node;
//							projectTree.setSelection(node.getJdoObject()); Doesn't work!!!!!!!!!!!!!
//							projectTree.getTreeViewer().setSelection(new StructuredSelection(node), true);
						}
					}
					
					if (selectedNode != null)
						projectTree.getTreeViewer().setSelection(new StructuredSelection(selectedNode), true);
				}
			});

			projectTree.setLayoutData(gridData);
		}

		Label sep = new Label(page, SWT.SEPARATOR | SWT.HORIZONTAL
//				| SWT.LINE_SOLID);
				);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		sep.setLayoutData(gridData);

		projectNameLabel = new Label(page, SWT.NONE);
		projectNameLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.create.CreateProjectWizardPage.label.projectName.text")); //$NON-NLS-1$

		projectNameText = new I18nTextEditor(page);
		projectNameText.addModifyListener(modifyListener);
		projectNameText.setI18nText(newProject.getName(), EditMode.DIRECT);
		
		descriptionLabel = new Label(page, SWT.WRAP);
		descriptionLabel.setLayoutData(new GridData());
		descriptionLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.create.CreateProjectWizardPage.label.description.text")); //$NON-NLS-1$

		descriptionText = new I18nTextEditorMultiLine(page, projectNameText
				.getLanguageChooser());
		descriptionText.setI18nText(newProject.getDescription(), EditMode.DIRECT);
		descriptionText.addModifyListener(modifyListener);
		gridData = new GridData(GridData.FILL_BOTH);
		descriptionText.setLayoutData(gridData);

		new Label(page, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.create.CreateProjectWizardPage.label.properties.text")); //$NON-NLS-1$

		activeButton = new Button(page, SWT.CHECK);
		activeButton.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.create.CreateProjectWizardPage.button.active.text")); //$NON-NLS-1$
		activeButton.setSelection(isActive);
		activeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isActive = activeButton.getSelection();
				newProject.setActive(isActive);
			}
		});
		gridData = new GridData();
		activeButton.setLayoutData(gridData);

		return page;
	}

	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			getContainer().updateButtons();
		}
	};

	public CreateProjectWizardPage(Project parentProject, Project newProject) {
		super(CreateProjectWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.create.CreateProjectWizardPage.title"), //$NON-NLS-1$
				SharedImages.getWizardPageImageDescriptor(IssueTrackingPlugin
						.getDefault(), CreateProjectWizard.class));
		this.setDescription(Messages.getString("org.nightlabs.jfire.issuetracking.ui.project.create.CreateProjectWizardPage.description")); //$NON-NLS-1$
		this.parentProject = parentProject;
		this.newProject = newProject;
	}

	public I18nTextEditor getProjectNameText() {
		return projectNameText;
	}

	@Override
	public void onShow() {
		projectNameText.forceFocus();
	}
	
//	@Override
//	public boolean canFlipToNextPage() {
//		return isPageComplete();
//	}
//	
	@Override
	public boolean isPageComplete() {
		return !projectNameText.getEditText().isEmpty();
	}

	public ProjectType getSelectedProjectType() {
		return projectTypeCombo.getSelectedProjectType();
	}

	public boolean isActive() {
		return isActive;
	}
}