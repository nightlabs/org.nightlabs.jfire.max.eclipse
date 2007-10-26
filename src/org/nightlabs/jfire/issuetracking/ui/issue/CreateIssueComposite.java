package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.FileSelectionComposite;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.security.UserSearchComposite;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueStatus;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.dao.IssuePriorityDAO;
import org.nightlabs.jfire.issue.dao.IssueSeverityTypeDAO;
import org.nightlabs.jfire.issue.dao.IssueStatusDAO;
import org.nightlabs.jfire.security.SecurityReflector;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.security.dao.UserDAO;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.xml.NLDOMUtil;

public class CreateIssueComposite extends XComposite{
	
	private List<IssueSeverityType> issueSeverityTypes = new ArrayList<IssueSeverityType>();
	private List<IssueStatus> issueStatus = new ArrayList<IssueStatus>();
	private List<IssuePriority> issuePriorities = new ArrayList<IssuePriority>();
	
	private IssueSeverityType selectedIssueSeverityType;
	private IssueStatus selectedIssueStatus;
	private IssuePriority selectedIssuePriority;
	
	private Label descriptionLabel;
	private I18nTextEditorMultiLine descriptionText;
	
	public CreateIssueComposite(Composite parent, int style) {
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
		
		Label severityLbl = new Label(this, SWT.NONE);
		severityLbl.setText("Severity: ");
		final XComboComposite<IssueSeverityType> severityCombo = new XComboComposite<IssueSeverityType>(this, SWT.NONE, labelProvider);
		severityCombo.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				selectedIssueSeverityType = severityCombo.getSelectedElement();
			}
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedIssueSeverityType = severityCombo.getSelectedElement();
			}
		});
		
		Label statusLbl = new Label(this, SWT.NONE);
		statusLbl.setText("Status: ");
		final XComboComposite<IssueStatus> statusCombo = new XComboComposite<IssueStatus>(this, SWT.NONE, labelProvider);
		statusCombo.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				selectedIssueStatus = statusCombo.getSelectedElement();
			}
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedIssueStatus = statusCombo.getSelectedElement();
			}
		});
		
		Label priorityLbl = new Label(this, SWT.NONE);
		priorityLbl.setText("Priority: ");
		final XComboComposite<IssuePriority> priorityCombo = new XComboComposite<IssuePriority>(this, SWT.NONE, labelProvider);
		priorityCombo.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				selectedIssuePriority = priorityCombo.getSelectedElement();
			}
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedIssuePriority = priorityCombo.getSelectedElement();
			}
		});
		/**********USER**********/
		Label userLbl = new Label(this, SWT.NONE);
		userLbl.setText("User: ");
		
		UserSearchComposite uc = new UserSearchComposite(this, SWT.NONE);
		/************************/
		
		Label subjectLabel = new Label(this, SWT.NONE);
		subjectLabel.setText("Subject: ");

		I18nTextEditor subjectText = new I18nTextEditor(this);
		subjectText.setI18nText(null, EditMode.BUFFERED);
		
		descriptionLabel = new Label(this, SWT.NONE);
		descriptionLabel.setText("Description: ");

		descriptionText = new I18nTextEditorMultiLine(this);
		descriptionText.setI18nText(null, EditMode.BUFFERED);

		Label fileLabel = new Label(this, SWT.NONE);
		fileLabel.setText("Files: ");

		FileSelectionComposite fileComposite = new FileSelectionComposite(this, SWT.NONE, FileSelectionComposite.OPEN_FILE, 
				null, null){
			@Override
			protected void modifyText(ModifyEvent e) {
				
			}
		}; 
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		descriptionText.setLayoutData(gridData);
		
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
		
		Button submitButton = new Button(this, SWT.PUSH);
		submitButton.setText("Submit");
		
		submitButton.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void widgetSelected(SelectionEvent arg0) {
				IssueDAO id = IssueDAO.sharedInstance();
				Issue issue = new Issue(selectedIssuePriority, 
						selectedIssueSeverityType, 
						selectedIssueStatus, 
						UserDAO.sharedInstance().getUsers(new String[]{User.FETCH_GROUP_THIS_USER}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()).iterator().next(),
						null);
				
				try {
					issue.setOrganisationID(Login.getLogin().getOrganisationID());
					id.createIssueWithoutAttachedDocument(issue, true, new String[]{Issue.FETCH_GROUP_THIS}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
				} catch (LoginException e) {
					e.printStackTrace();
				}
			}
		});
		
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
}
