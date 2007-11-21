package org.nightlabs.jfire.issuetracking.ui.issue;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.FileListSelectionComposite;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.security.UserSearchDialog;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueFileAttachment;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueTypeDAO;
import org.nightlabs.jfire.security.User;
import org.nightlabs.progress.ProgressMonitor;

public class IssueEditComposite 
extends XComposite{
	private List<IssueType> issueTypes = new ArrayList<IssueType>();
	private List<IssueSeverityType> issueSeverityTypes = new ArrayList<IssueSeverityType>();
	private List<IssuePriority> issuePriorities = new ArrayList<IssuePriority>();

	private IssueType selectedIssueType;
	private IssueSeverityType selectedIssueSeverityType;
	private IssuePriority selectedIssuePriority;

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
	private Text assigntoUserText;
	private Button assigntoUserButton;

	private Label subjectLabel;
	private I18nTextEditor subjectText;

	private Label fileLabel;
	private FileListSelectionComposite fileListSelectionComposite;

	private Label descriptionLabel;
	private I18nTextEditorMultiLine descriptionText;

	private User selectedReporter;
	private User selectedAssigntoUser;
	
	private Issue issue;
	
	private IssueLabelProvider labelProvider = new IssueLabelProvider();

	private static final String[] FETCH_GROUPS_ISSUE_TYPE = { 
		IssueType.FETCH_GROUP_THIS, 
		IssueSeverityType.FETCH_GROUP_THIS, IssueSeverityType.FETCH_GROUP_NAME, 
		IssuePriority.FETCH_GROUP_THIS, IssuePriority.FETCH_GROUP_NAME, 
		FetchPlan.DEFAULT 
	};
	
	public IssueEditComposite(Issue issue, Composite parent, int style) {
		super(parent, style);
		this.issue = issue;
		
		createComposite(this);
	}

	/**
	 * Create the content for this composite.
	 * @param parent The parent composite
	 */
	protected void createComposite(Composite parent) 
	{
		setLayout(new GridLayout(2, false));

		int textStyle = SWT.READ_ONLY | SWT.BORDER;

		issueTypeLbl = new Label(this, SWT.NONE);
		issueTypeLbl.setText("Issue Type: ");
		issueTypeCombo = new XComboComposite<IssueType>(this, SWT.NONE, labelProvider);

		List<IssueType> issueTypeList = new ArrayList<IssueType>();
		issueTypeCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				selectedIssueType = issueTypeCombo.getSelectedElement();
				
				issueSeverityCombo.removeAll();
				for (IssueSeverityType is : selectedIssueType.getSeverityTypes()) {
					issueSeverityCombo.addElement(is);
				}
				issueSeverityCombo.selectElementByIndex(0);
				selectedIssueSeverityType = issueSeverityCombo.getSelectedElement();
				
				issuePriorityCombo.removeAll();
				for (IssuePriority ip : selectedIssueType.getPriorities()) {
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
			}
		});

		issuePriorityLbl = new Label(this, SWT.NONE);
		issuePriorityLbl.setText("Priority: ");
		issuePriorityCombo = new XComboComposite<IssuePriority>(this, SWT.NONE, labelProvider);
		issuePriorityCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				selectedIssuePriority = issuePriorityCombo.getSelectedElement();
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

		reporterText = new Text(reporterComposite, SWT.BORDER);
		gridData  = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		reporterText.setLayoutData(gridData);
		selectedReporter = issue.getReporter();
		reporterText.setText(selectedReporter.getName());

		reporterButton = new Button(reporterComposite, SWT.PUSH);
		reporterButton.setText("Choose User");

		reporterButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				UserSearchDialog userSearchDialog = new UserSearchDialog(getShell(), reporterText.getText());
				int returnCode = userSearchDialog.open();
				if (returnCode == Dialog.OK) {
					selectedReporter = userSearchDialog.getSelectedUser();
//					userID = (UserID) JDOHelper.getObjectId(selectedUser);
					if (selectedReporter != null)
						reporterText.setText(selectedReporter.getName());
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

		assigntoUserText = new Text(toUserComposite, SWT.BORDER);
		gridData  = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		selectedAssigntoUser = issue.getAssigntoUser();
		assigntoUserText.setText(selectedAssigntoUser.getName());
		assigntoUserText.setLayoutData(gridData);

		assigntoUserButton = new Button(toUserComposite, SWT.PUSH);
		assigntoUserButton.setText("Choose User");

		assigntoUserButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				UserSearchDialog userSearchDialog = new UserSearchDialog(getShell(), assigntoUserText.getText());
				int returnCode = userSearchDialog.open();
				if (returnCode == Dialog.OK) {
					selectedAssigntoUser = userSearchDialog.getSelectedUser();
//					userID = (UserID) JDOHelper.getObjectId(selectedUser);
					if (selectedAssigntoUser != null)
						assigntoUserText.setText(selectedAssigntoUser.getName());
				}//if
			}
		});
		/************************/

		subjectLabel = new Label(this, SWT.NONE);
		subjectLabel.setText("Subject: ");

		subjectText = new I18nTextEditor(this);
		subjectText.setI18nText(issue.getSubject(), EditMode.BUFFERED);

		descriptionLabel = new Label(this, SWT.NONE);
		descriptionLabel.setText("Description: ");

		descriptionText = new I18nTextEditorMultiLine(this);
		descriptionText.setI18nText(issue.getDescription(), EditMode.BUFFERED);

		gridData = new GridData(GridData.FILL_BOTH);
		descriptionText.setLayoutData(gridData);

		fileLabel = new Label(this, SWT.NONE);
		fileLabel.setText("Files: ");

		fileListSelectionComposite = new FileListSelectionComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		List<IssueFileAttachment> iList = issue.getFileList();
		Map<String, InputStream> iMap = new HashMap<String, InputStream>();
		for(IssueFileAttachment ia : iList){
			iMap.put(ia.getFileName(), ia.createFileAttachmentInputStream());
		}
		fileListSelectionComposite.setInputStreamMap(iMap);

		Job loadJob = new Job("Loading Issue Properties....") {
			@Override
			protected IStatus run(final ProgressMonitor monitor) {
				try {
					issueTypes = new ArrayList<IssueType>(IssueTypeDAO.sharedInstance().getIssueTypes(FETCH_GROUPS_ISSUE_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor));

					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							issueTypeCombo.removeAll();
							for (Iterator it = issueTypes.iterator(); it.hasNext(); ) {
								IssueType issueType = (IssueType) it.next();
								issueTypeCombo.addElement(issueType);
							}
							issueTypeCombo.selectElement(issue.getIssueType());
							selectedIssueType = issueTypeCombo.getSelectedElement();

							issueSeverityCombo.removeAll();
							for (IssueSeverityType is : selectedIssueType.getSeverityTypes()) {
								issueSeverityCombo.addElement(is);
							}
							issueSeverityCombo.selectElement(issue.getSeverityType());
							selectedIssueSeverityType = issueSeverityCombo.getSelectedElement();

							issuePriorityCombo.removeAll();
							for (IssuePriority ip : selectedIssueType.getPriorities()) {
								issuePriorityCombo.addElement(ip);
							}
							issuePriorityCombo.selectElement(issue.getPriority());
							selectedIssuePriority = issuePriorityCombo.getSelectedElement();
						}
					});
				}catch (Exception e1) {
					ExceptionHandlerRegistry.asyncHandleException(e1);
					throw new RuntimeException(e1);
				}

				return Status.OK_STATUS;
			} 
		};
		loadJob.setPriority(Job.SHORT);
		loadJob.schedule();

//		Category  	
//		Reproducibility 	
	}

	public IssueSeverityType getSelectedIssueSeverityType() {
		return selectedIssueSeverityType;
	}

	public void setSelectedIssueSeverityType(
			IssueSeverityType selectedIssueSeverityType) {
		this.selectedIssueSeverityType = selectedIssueSeverityType;
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

	public User getSelectedAssigntoUser() {
		return selectedAssigntoUser;
	}

	public I18nTextEditorMultiLine getDescriptionText() {
		return descriptionText;
	}

	public I18nTextEditor getSubjectText() {
		return subjectText;
	}
}
