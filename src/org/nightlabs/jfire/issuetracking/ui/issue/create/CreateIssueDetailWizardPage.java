package org.nightlabs.jfire.issuetracking.ui.issue.create;

import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueResolution;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueTypeDAO;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLabelProvider;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectComboComposite;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

/**
 * Displays the user-interface page to assist in creating a new {@link Issue}.
 * Default options for important fields should always be made available on this page.
 *
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 * @author Khaireel Mohamed - khaireel at nightlabs dot de
 */
public class CreateIssueDetailWizardPage
extends WizardHopPage
{
	//GUI
	private Label subjectLabel;
	private I18nTextEditor subjectText;

	private Label descriptionLabel;
	private I18nTextEditorMultiLine descriptionText;

	private Label issueTypeLbl;
	private XComboComposite<IssueType> issueTypeCombo;
	private Label issueSeverityLbl;
	private XComboComposite<IssueSeverityType> issueSeverityCombo;
	private Label issuePriorityLbl;
	private XComboComposite<IssuePriority> issuePriorityCombo;

	private ProjectComboComposite projectComboComposite;
	private IssueLabelProvider issueLabelProvider = new IssueLabelProvider();

	//Used objects
	private List<IssueType> issueTypes;

	private IssueType selectedIssueType;
	private IssueSeverityType selectedIssueSeverityType;
	private IssuePriority selectedIssuePriority;
	private Project selectedProject;

	private Issue issue;

	private static final String[] ISSUE_TYPE_FETCH_GROUPS = {
		FetchPlan.DEFAULT,
		IssueType.FETCH_GROUP_NAME,
		IssueType.FETCH_GROUP_ISSUE_SEVERITY_TYPES,
		IssueType.FETCH_GROUP_ISSUE_PRIORITIES,
		IssueType.FETCH_GROUP_ISSUE_RESOLUTIONS,
		IssueSeverityType.FETCH_GROUP_NAME,
		IssuePriority.FETCH_GROUP_NAME
	};


	public CreateIssueDetailWizardPage(Issue issue) {
		super(CreateIssueDetailWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueDetailWizardPage.title"), SharedImages.getWizardPageImageDescriptor(IssueTrackingPlugin.getDefault(), CreateIssueWizard.class)); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueDetailWizardPage.description")); //$NON-NLS-1$
		this.issue = issue;
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE, LayoutMode.TOP_BOTTOM_WRAPPER, LayoutDataMode.GRID_DATA);
		mainComposite.getGridLayout().numColumns = 6;

		new Label(mainComposite, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueDetailWizardPage.label.project.text")); //$NON-NLS-1$
		projectComboComposite = new ProjectComboComposite(mainComposite, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 5;
		projectComboComposite.setLayoutData(gridData);
		projectComboComposite.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent e) {
				selectedProject = projectComboComposite.getSelectedProject();
				issue.setProject(selectedProject);
				getContainer().updateButtons();
			}
		});

		// Subject & Description
		subjectLabel = new Label(mainComposite, SWT.NONE);
		subjectLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueDetailWizardPage.label.subject.text")); //$NON-NLS-1$

		subjectText = new I18nTextEditor(mainComposite);
		subjectText.setI18nText(issue.getSubject(), EditMode.DIRECT);
		subjectText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				getContainer().updateButtons();
				if (subjectText.getEditText().equals("")) { //$NON-NLS-1$
					setErrorMessage(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueDetailWizardPage.errorMessage.subjectNotEmpty.text")); //$NON-NLS-1$
				}
			}
		});

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 5;
		subjectText.setLayoutData(gridData);

		descriptionLabel = new Label(mainComposite, SWT.NONE);
		descriptionLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueDetailWizardPage.label.description.text")); //$NON-NLS-1$

		descriptionText = new I18nTextEditorMultiLine(mainComposite, subjectText.getLanguageChooser());
		descriptionText.setI18nText(issue.getDescription(), EditMode.DIRECT);
		descriptionText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				getContainer().updateButtons();
				if (descriptionText.getEditText().equals("")) { //$NON-NLS-1$
					setErrorMessage(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueDetailWizardPage.errorMessage.descriptionNotEmpty.text")); //$NON-NLS-1$
				}
			}
		});

		gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumHeight = 100;
		gridData.horizontalSpan = 6;
		descriptionText.setLayoutData(gridData);

		// Properties
		issueTypeLbl = new Label(mainComposite, SWT.NONE);
		issueTypeLbl.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueDetailWizardPage.label.issueType.text")); //$NON-NLS-1$
		issueTypeCombo = new XComboComposite<IssueType>(mainComposite, SWT.NONE | SWT.READ_ONLY, issueLabelProvider);
		issueTypeCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				selectedIssueType = issueTypeCombo.getSelectedElement();
				issue.setIssueType(selectedIssueType);

				issueSeverityCombo.removeAll();
				for (IssueSeverityType is : selectedIssueType.getIssueSeverityTypes()) {
					issueSeverityCombo.addElement(is);
				}
				issueSeverityCombo.selectElementByIndex(0);
				selectedIssueSeverityType = issueSeverityCombo.getSelectedElement();

				issuePriorityCombo.removeAll();
				for (IssuePriority ip : selectedIssueType.getIssuePriorities()) {
					issuePriorityCombo.addElement(ip);
				}
				issuePriorityCombo.selectElementByIndex(0);
				selectedIssuePriority = issuePriorityCombo.getSelectedElement();
			}
		});

		issueSeverityLbl = new Label(mainComposite, SWT.NONE);
		issueSeverityLbl.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueDetailWizardPage.label.severity.text")); //$NON-NLS-1$
		issueSeverityCombo = new XComboComposite<IssueSeverityType>(mainComposite, SWT.NONE | SWT.READ_ONLY, issueLabelProvider);

		issueSeverityCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				selectedIssueSeverityType = issueSeverityCombo.getSelectedElement();
				issue.setIssueSeverityType(selectedIssueSeverityType);
			}
		});

		issuePriorityLbl = new Label(mainComposite, SWT.NONE);
		issuePriorityLbl.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueDetailWizardPage.label.priority.text")); //$NON-NLS-1$
		issuePriorityCombo = new XComboComposite<IssuePriority>(mainComposite, SWT.NONE | SWT.READ_ONLY, issueLabelProvider);
		issuePriorityCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				selectedIssuePriority = issuePriorityCombo.getSelectedElement();
				issue.setIssuePriority(selectedIssuePriority);
			}
		});

		XComposite dateTimeComposite = new XComposite(mainComposite, SWT.NONE);
		dateTimeComposite.getGridLayout().numColumns = 3;
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 6;
		dateTimeComposite.setLayoutData(gridData);

		// ---> Loading Data
		Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueDetailWizardPage.job.loadingIssueProp.text")) { //$NON-NLS-1$
			@Override
			protected IStatus run(final ProgressMonitor monitor) throws Exception
			{
				issueTypes = IssueTypeDAO.sharedInstance().getAllIssueTypes(ISSUE_TYPE_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						// IssueType
						issueTypeCombo.removeAll();
						IssueType defaultIssueType = null;
						for (IssueType issueType : issueTypes) {
							if (issueType.getIssueTypeID().equals(IssueType.DEFAULT_ISSUE_TYPE_ID))
								defaultIssueType = issueType;
							issueTypeCombo.addElement(issueType);
						}

						if (defaultIssueType != null)
							issueTypeCombo.selectElement(defaultIssueType);
						else
							issueTypeCombo.selectElementByIndex(0);

						selectedIssueType = issueTypeCombo.getSelectedElement();
						issue.setIssueType(selectedIssueType);

						// IssueSeverity
						issueSeverityCombo.removeAll();
						IssueSeverityType defaultIssueSeverityType = null;
						if (selectedIssueType != null) {
							for (IssueSeverityType is : selectedIssueType.getIssueSeverityTypes()) {
								if (is.getIssueSeverityTypeID().equals(Issue.DEFAULT_ISSUE_SEVERITY_TYPE_ID))
									defaultIssueSeverityType = is;
								issueSeverityCombo.addElement(is);
							}

							if (defaultIssueSeverityType != null)
								issueSeverityCombo.selectElement(defaultIssueSeverityType);
							else
								issueSeverityCombo.selectElementByIndex(0);
						}
						selectedIssueSeverityType = issueSeverityCombo.getSelectedElement();
						issue.setIssueSeverityType(selectedIssueSeverityType);

						// IssuePriority
						issuePriorityCombo.removeAll();
						IssuePriority defaultIssuePriority = null;
						if (selectedIssueType != null) {
							for (IssuePriority ip : selectedIssueType.getIssuePriorities()) {
								if (ip.getIssuePriorityID().equals(Issue.DEFAULT_ISSUE_PRIORITY_ID))
									defaultIssuePriority = ip;
								issuePriorityCombo.addElement(ip);
							}

							if (defaultIssueSeverityType != null)
								issuePriorityCombo.selectElement(defaultIssuePriority);
							else
								issuePriorityCombo.selectElementByIndex(0);
						}
						selectedIssuePriority = issuePriorityCombo.getSelectedElement();
						issue.setIssuePriority(selectedIssuePriority);


						//IssueResolution
						for (IssueResolution ir : selectedIssueType.getIssueResolutions()) {
							if (ir.getIssueResolutionID().equals(Issue.DEFAULT_ISSUE_RESOLUTION_ID))
								issue.setIssueResolution(ir);
						}
					}
				});

				return Status.OK_STATUS;
			}
		};
		loadJob.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		loadJob.schedule();

		return mainComposite;
	}

	@Override
	public void onShow() {
		subjectText.forceFocus();
	}

	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}

	private WizardHopPage optionalPage;

	@Override
	public boolean isPageComplete() {
		boolean result = true;
		setErrorMessage(null);

		if (subjectText.getEditText().equals("") || subjectText.getI18nText().getText() == null) { //$NON-NLS-1$
			result = false;
		}

		if (descriptionText.getEditText().equals("") || descriptionText.getI18nText().getText() == null) { //$NON-NLS-1$
			result = false;
		}

		if (result == true && optionalPage == null) {
			new WizardHop(this);
			optionalPage = new CreateIssueOptionalWizardPage(issue);
			getWizardHop().addHopPage(optionalPage);
		}

		return result;
	}

	public I18nTextEditorMultiLine getDescriptionText() {
		return descriptionText;
	}

	public I18nTextEditor getSubjectText() {
		return subjectText;
	}
}