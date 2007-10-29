package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.FileSelectionComposite;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.jfire.base.ui.security.UserSearchDialog;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueStatus;
import org.nightlabs.jfire.issue.dao.IssuePriorityDAO;
import org.nightlabs.jfire.issue.dao.IssueSeverityTypeDAO;
import org.nightlabs.jfire.issue.dao.IssueStatusDAO;
import org.nightlabs.jfire.security.User;
import org.nightlabs.progress.ProgressMonitor;

public class IssueCreateComposite extends XComposite{
	
	private List<IssueSeverityType> issueSeverityTypes = new ArrayList<IssueSeverityType>();
	private List<IssueStatus> issueStatus = new ArrayList<IssueStatus>();
	private List<IssuePriority> issuePriorities = new ArrayList<IssuePriority>();
	
	private IssueSeverityType selectedIssueSeverityType;
	private IssueStatus selectedIssueStatus;
	private IssuePriority selectedIssuePriority;
	
	private Label severityLbl;
	private XComboComposite<IssueSeverityType> severityCombo;
	private Label statusLbl;
	private XComboComposite<IssueStatus> statusCombo;
	private Label priorityLbl;
	private XComboComposite<IssuePriority> priorityCombo;

	private Label userLbl;
	private Text userText;
	private Button userButton;
	private Label subjectLabel;
	private I18nTextEditor subjectText;

	private Label fileLabel;
	private FileSelectionComposite fileComposite;
	
	private Label descriptionLabel;
	private I18nTextEditorMultiLine descriptionText;
	
	private Button submitButton;
	
	private User selectedUser;
	
	public IssueCreateComposite(Composite parent, int style) {
		super(parent, style);
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
		
		severityLbl = new Label(this, SWT.NONE);
		severityLbl.setText("Severity: ");
		severityCombo = new XComboComposite<IssueSeverityType>(this, SWT.NONE, labelProvider);
		severityCombo.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedIssueSeverityType = severityCombo.getSelectedElement();
			}
		});
		
		statusLbl = new Label(this, SWT.NONE);
		statusLbl.setText("Status: ");
		statusCombo = new XComboComposite<IssueStatus>(this, SWT.NONE, labelProvider);
		statusCombo.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedIssueStatus = statusCombo.getSelectedElement();
			}
		});
		
		priorityLbl = new Label(this, SWT.NONE);
		priorityLbl.setText("Priority: ");
		priorityCombo = new XComboComposite<IssuePriority>(this, SWT.NONE, labelProvider);
		priorityCombo.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedIssuePriority = priorityCombo.getSelectedElement();
			}
		});
		
		/**********USER**********/
		userLbl = new Label(this, SWT.NONE);
		userLbl.setText("User: ");
		
		XComposite userComposite = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		userComposite.getGridLayout().numColumns = 2;
		
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		userComposite.setLayoutData(gridData);
		
		userText = new Text(userComposite, SWT.BORDER);
		gridData  = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		userText.setLayoutData(gridData);
		
		userButton = new Button(userComposite, SWT.PUSH);
		userButton.setText("Choose User");
		
		userButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				UserSearchDialog userSearchDialog = new UserSearchDialog(getShell(), userText.getText());
				int returnCode = userSearchDialog.open();
				if (returnCode == Dialog.OK) {
					selectedUser = userSearchDialog.getSelectedUser();
//					userID = (UserID) JDOHelper.getObjectId(selectedUser);
					if (selectedUser != null)
						userText.setText(selectedUser.getName());
				}//if
			}
		});
		/************************/
		
		subjectLabel = new Label(this, SWT.NONE);
		subjectLabel.setText("Subject: ");

		subjectText = new I18nTextEditor(this);
		subjectText.setI18nText(null, EditMode.BUFFERED);
		
		descriptionLabel = new Label(this, SWT.NONE);
		descriptionLabel.setText("Description: ");

		descriptionText = new I18nTextEditorMultiLine(this);
		descriptionText.setI18nText(null, EditMode.BUFFERED);

		gridData = new GridData(GridData.FILL_BOTH);
		descriptionText.setLayoutData(gridData);
		
		fileLabel = new Label(this, SWT.NONE);
		fileLabel.setText("Files: ");

		fileComposite = new FileSelectionComposite(this, SWT.NONE, FileSelectionComposite.OPEN_FILE, 
				null, null){
			@Override
			protected void modifyText(ModifyEvent e) {
			}
		}; 
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		fileComposite.setLayoutData(gridData);
		
		Job loadJob = new Job("Loading Issue Severity Types....") {
			@Override
			protected IStatus run(ProgressMonitor monitor) {
				try {
					issueSeverityTypes = IssueSeverityTypeDAO.sharedInstance().getIssueSeverityTypes(monitor);
					issueStatus = IssueStatusDAO.sharedInstance().getIssueStatus(monitor);
					issuePriorities = IssuePriorityDAO.sharedInstance().getIssuePriorities(monitor);
				} catch (Exception e) {
					ExceptionHandlerRegistry.asyncHandleException(e);
					throw new RuntimeException(e);
				}
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						severityCombo.removeAll();
						for (Iterator it = issueSeverityTypes.iterator(); it.hasNext(); ) {
							IssueSeverityType issueSeverityType = (IssueSeverityType) it.next();
							severityCombo.addElement(issueSeverityType);
						}
						severityCombo.selectElementByIndex(1);
					}
				});
				
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						statusCombo.removeAll();
						for (Iterator it = issueStatus.iterator(); it.hasNext(); ) {
							IssueStatus is = (IssueStatus) it.next();
							statusCombo.addElement(is);
						}
						statusCombo.selectElementByIndex(1);
					}
				});
				
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						priorityCombo.removeAll();
						for (Iterator it = issuePriorities.iterator(); it.hasNext(); ) {
							IssuePriority ip = (IssuePriority) it.next();
							priorityCombo.addElement(ip);
						}
						priorityCombo.selectElementByIndex(1);
					}
				});

				return Status.OK_STATUS;
			} 
			
		};
		loadJob.schedule();
		
//		submitButton = new Button(this, SWT.PUSH);
//		submitButton.setText("Submit");
//		gridData = new GridData(GridData.FILL_BOTH);
//		submitButton.setLayoutData(gridData);
//		
//		submitButton.addSelectionListener(new SelectionAdapter(){
//			@Override
//			public void widgetSelected(SelectionEvent arg0) {
//				IssueDAO id = IssueDAO.sharedInstance();
//				Issue issue = new Issue(selectedIssuePriority, 
//						selectedIssueSeverityType, 
//						selectedIssueStatus, 
//						UserDAO.sharedInstance().getUsers(new String[]{User.FETCH_GROUP_THIS_USER}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()).iterator().next(),
//						null);
//				
//				try {
//					issue.setOrganisationID(Login.getLogin().getOrganisationID());
//					id.createIssueWithoutAttachedDocument(issue, true, new String[]{Issue.FETCH_GROUP_THIS}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
//				} catch (LoginException e) {
//					e.printStackTrace();
//				}
//			}
//		});
		
//		 Category  	
//		 Reproducibility 	
	}
	
	private ILabelProvider labelProvider = new LabelProvider() {
		@Override
		public String getText(Object element) 
		{
			if (element instanceof IssueSeverityType) {
				IssueSeverityType issueSeverityType = (IssueSeverityType) element;
				return issueSeverityType.getIssueSeverityTypeText().getText();
			}
			
			if (element instanceof IssueStatus) {
				IssueStatus issueStatus = (IssueStatus) element;
				return issueStatus.getIssueStatusText().getText();
			}
			
			if (element instanceof IssuePriority) {
				IssuePriority issuePriority = (IssuePriority) element;
				return issuePriority.getIssuePriorityText().getText();
			}
			return super.getText(element);
		}		
	};

	public IssueSeverityType getSelectedIssueSeverityType() {
		return selectedIssueSeverityType;
	}

	public void setSelectedIssueSeverityType(
			IssueSeverityType selectedIssueSeverityType) {
		this.selectedIssueSeverityType = selectedIssueSeverityType;
	}

	public IssueStatus getSelectedIssueStatus() {
		return selectedIssueStatus;
	}

	public void setSelectedIssueStatus(IssueStatus selectedIssueStatus) {
		this.selectedIssueStatus = selectedIssueStatus;
	}

	public IssuePriority getSelectedIssuePriority() {
		return selectedIssuePriority;
	}

	public void setSelectedIssuePriority(IssuePriority selectedIssuePriority) {
		this.selectedIssuePriority = selectedIssuePriority;
	}
	
	public User getSelectedUser() {
		return selectedUser;
	}
	
	public I18nTextEditorMultiLine getDescriptionText() {
		return descriptionText;
	}
	
	public I18nTextEditor getSubjectText() {
		return subjectText;
	}
}
