package org.nightlabs.jfire.issuetracking.ui.issue.create;

import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.security.UserSearchDialog;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueTypeDAO;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueFileAttachmentComposite;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLabelProvider;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkAdderComposite;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.security.User;
import org.nightlabs.progress.ProgressMonitor;

public class CreateIssueComposite
extends XComposite
{
	private List<IssueType> issueTypes;

	private IssueType selectedIssueType;
	private IssueSeverityType selectedIssueSeverityType;
	private StateDefinition selectedState;
	private IssuePriority selectedIssuePriority;

	private Label linkedObjectLbl;
	private IssueLinkAdderComposite issueLinkAdderComposite;
	
	private Label issueTypeLbl;
	private XComboComposite<IssueType> issueTypeCombo;
	private Label issueSeverityLbl;
	private XComboComposite<IssueSeverityType> issueSeverityCombo;
	private Label issuePriorityLbl;
	private XComboComposite<IssuePriority> issuePriorityCombo;

	private Label reporterLbl;
	private Text reporterText;
	private Button reporterButton;

	private Label assigntoUserLbl;
	private Text assignToUserText;
	private Button assignToUserButton;

	private Label subjectLabel;
	private I18nTextEditor subjectText;

	private Label fileLabel;
	private IssueFileAttachmentComposite fileComposite;

	private Label descriptionLabel;
	private I18nTextEditorMultiLine descriptionText;

	private User selectedReporter;
	private User selectedAssignToUser;

	private static final String[] FETCH_GROUPS = {
		IssueType.FETCH_GROUP_THIS_ISSUE_TYPE,
		IssueSeverityType.FETCH_GROUP_THIS_ISSUE_SEVERITY_TYPE,
		IssuePriority.FETCH_GROUP_NAME,
		FetchPlan.DEFAULT
		};

	private IssueLabelProvider labelProvider = new IssueLabelProvider();
	
	private Issue issue;
	private CreateIssueWizardPage createIssueWizardPage;
	
	public CreateIssueComposite(CreateIssueWizardPage createIssueWizardPage, Composite parent, int style, Issue issue) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		
		this.issue = issue;
		this.createIssueWizardPage = createIssueWizardPage;
		createComposite(this);
	}

	/**
	 * Create the content for this composite.
	 * @param parent The parent composite
	 */
	protected void createComposite(Composite parent) 
	{
		getGridLayout().numColumns = 2;

		linkedObjectLbl = new Label(this, SWT.NONE);
		linkedObjectLbl.setText("Linked object");
		
		issueLinkAdderComposite = new IssueLinkAdderComposite(this, SWT.NONE, true, issue);
		
		issueTypeLbl = new Label(this, SWT.NONE);
		issueTypeLbl.setText("Issue Type: ");
		issueTypeCombo = new XComboComposite<IssueType>(this, SWT.NONE, labelProvider);
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

		issueSeverityLbl = new Label(this, SWT.NONE);
		issueSeverityLbl.setText("Severity: ");
		issueSeverityCombo = new XComboComposite<IssueSeverityType>(this, SWT.NONE, labelProvider);
		issueSeverityCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				selectedIssueSeverityType = issueSeverityCombo.getSelectedElement();
				issue.setIssueSeverityType(selectedIssueSeverityType);
			}
		});

		issuePriorityLbl = new Label(this, SWT.NONE);
		issuePriorityLbl.setText("Priority: ");
		issuePriorityCombo = new XComboComposite<IssuePriority>(this, SWT.NONE, labelProvider);
		issuePriorityCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				selectedIssuePriority = issuePriorityCombo.getSelectedElement();
				issue.setIssuePriority(selectedIssuePriority);
			}
		});

		/**********Reporter**********/
		reporterLbl = new Label(this, SWT.NONE);
		reporterLbl.setText("Reporter: ");

		XComposite reporterComposite = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		reporterComposite.getGridLayout().numColumns = 2;

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		reporterComposite.setLayoutData(gridData);

		reporterText = new Text(reporterComposite, SWT.BORDER | SWT.READ_ONLY);
		gridData  = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		reporterText.setLayoutData(gridData);
		selectedReporter = Login.sharedInstance().getUser(new String[]{User.FETCH_GROUP_NAME}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
		issue.setReporter(selectedReporter);
		reporterText.setText(selectedReporter.getName());

		reporterButton = new Button(reporterComposite, SWT.PUSH);
		reporterButton.setText("Choose User");

		reporterButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				UserSearchDialog userSearchDialog = new UserSearchDialog(getShell(), selectedReporter == null ? "" : selectedReporter.getUserID());
				int returnCode = userSearchDialog.open();
				if (returnCode == Window.OK) {
					selectedReporter = userSearchDialog.getSelectedUser();
					reporterText.setText(selectedReporter == null ? "" : selectedReporter.getName());
					issue.setReporter(selectedReporter);
				}//if
			}
		});
		/*******************************/
		/**********Assigned To**********/
		assigntoUserLbl = new Label(this, SWT.NONE);
		assigntoUserLbl.setText("Assigned To: ");

		XComposite toUserComposite = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		toUserComposite.getGridLayout().numColumns = 2;

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		toUserComposite.setLayoutData(gridData);

		assignToUserText = new Text(toUserComposite, SWT.BORDER | SWT.READ_ONLY);
		gridData  = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		assignToUserText.setLayoutData(gridData);

		assignToUserButton = new Button(toUserComposite, SWT.PUSH);
		assignToUserButton.setText("Choose User");

		assignToUserButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				UserSearchDialog userSearchDialog = new UserSearchDialog(getShell(), selectedAssignToUser == null ? "" : selectedAssignToUser.getUserID());
				int returnCode = userSearchDialog.open();
				if (returnCode == Window.OK) {
					selectedAssignToUser = userSearchDialog.getSelectedUser();
					assignToUserText.setText(selectedAssignToUser == null ? "" : selectedAssignToUser.getName());
					issue.setAssignee(selectedAssignToUser);
				}//if
			}
		});

		subjectLabel = new Label(this, SWT.NONE);
		subjectLabel.setText("Subject: ");

		subjectText = new I18nTextEditor(this);
		subjectText.setI18nText(issue.getSubject(), EditMode.DIRECT);
		subjectText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				createIssueWizardPage.updatePageComplete();
			}
		});

		descriptionLabel = new Label(this, SWT.NONE);
		descriptionLabel.setText("Description: ");

		descriptionText = new I18nTextEditorMultiLine(this);
		descriptionText.setI18nText(issue.getDescription(), EditMode.DIRECT);
		descriptionText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				createIssueWizardPage.updatePageComplete();
			}
		});
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumHeight = 100;
		descriptionText.setLayoutData(gridData);

		fileLabel = new Label(this, SWT.NONE);
		fileLabel.setText("Files: ");

		fileComposite = new IssueFileAttachmentComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER, issue);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumHeight = 80;
		fileComposite.setLayoutData(gridData);

		Job loadJob = new Job("Loading Issue Properties....") {
			@Override
			protected IStatus run(final ProgressMonitor monitor) throws Exception
			{
				issueTypes = IssueTypeDAO.sharedInstance().getAllIssueTypes(FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						issueTypeCombo.removeAll();
						for (IssueType issueType : issueTypes) {
							issueTypeCombo.addElement(issueType);
						}
						issueTypeCombo.selectElementByIndex(0);
						selectedIssueType = issueTypeCombo.getSelectedElement();
						issue.setIssueType(selectedIssueType);

						issueSeverityCombo.removeAll();
						for (IssueSeverityType is : selectedIssueType.getIssueSeverityTypes()) {
							issueSeverityCombo.addElement(is);
						}
						issueSeverityCombo.selectElementByIndex(0);
						selectedIssueSeverityType = issueSeverityCombo.getSelectedElement();
						issue.setIssueSeverityType(selectedIssueSeverityType);

						issuePriorityCombo.removeAll();
						for (IssuePriority ip : selectedIssueType.getIssuePriorities()) {
							issuePriorityCombo.addElement(ip);
						}
						issuePriorityCombo.selectElementByIndex(0);
						selectedIssuePriority = issuePriorityCombo.getSelectedElement();
						issue.setIssuePriority(selectedIssuePriority);
					}
				});

				return Status.OK_STATUS;
			} 
		};
		loadJob.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		loadJob.schedule();
	}

	public IssueSeverityType getSelectedIssueSeverityType() {
		return selectedIssueSeverityType;
	}

	public void setSelectedIssueSeverityType(
			IssueSeverityType selectedIssueSeverityType) {
		this.selectedIssueSeverityType = selectedIssueSeverityType;
	}

	public StateDefinition getSelectedState() {
		return selectedState;
	}

	public void setSelectedState(StateDefinition selectedState) {
		this.selectedState = selectedState;
	}

	public IssuePriority getSelectedIssuePriority() {
		return selectedIssuePriority;
	}

	public void setSelectedIssuePriority(IssuePriority selectedIssuePriority) {
		this.selectedIssuePriority = selectedIssuePriority;
	}

	public User getSelectedReporter() {
		return selectedReporter;
	}

	public User getSelectedAssignToUser() {
		return selectedAssignToUser;
	}

	public I18nTextEditorMultiLine getDescriptionText() {
		return descriptionText;
	}

	public I18nTextEditor getSubjectText() {
		return subjectText;
	}

	public IssueType getSelectedIssueType(){
		return selectedIssueType;
	}

	public IssueLinkAdderComposite getIssueLinkAdderComposite() {
		return issueLinkAdderComposite;
	}
	
	@Override
	public boolean setFocus() {
		return subjectText.setFocus();
	}
}
