package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.ArrayList;
import java.util.Collection;
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
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.security.UserSearchDialog;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueTypeDAO;
import org.nightlabs.jfire.jbpm.JbpmManager;
import org.nightlabs.jfire.jbpm.JbpmManagerUtil;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.progress.ProgressMonitor;

public class IssueEditComposite 
extends XComposite{
	private Map<String, Collection<StateDefinition>> stateDefinitionMap = new HashMap<String, Collection<StateDefinition>>();

	private List<IssueType> issueTypes = new ArrayList<IssueType>();
	private List<IssueSeverityType> issueSeverityTypes = new ArrayList<IssueSeverityType>();
//	private List<IssueStatus> issueStatus = new ArrayList<IssueStatus>();
	private List<IssuePriority> issuePriorities = new ArrayList<IssuePriority>();

	private IssueType selectedIssueType;
	private IssueSeverityType selectedIssueSeverityType;
	private StateDefinition selectedState;
	private IssuePriority selectedIssuePriority;

	private Label issueTypeLbl;
	private XComboComposite<IssueType> issueTypeCombo;
	private Label documentTypeLbl;
	private Label documentTypeTextLbl;
	private Label issueSeverityLbl;
	private XComboComposite<IssueSeverityType> issueSeverityCombo;
	private Label stateDefinitionLbl;
	private XComboComposite<StateDefinition> stateDefinitionCombo;
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
	private FileSelectionComposite fileComposite;

	private Label descriptionLabel;
	private I18nTextEditorMultiLine descriptionText;

	private Button submitButton;

	private User selectedReporter;
	private User selectedAssigntoUser;
	
	private Issue issue;

	private static final String[] FETCH_GROUPS = { IssueType.FETCH_GROUP_THIS, FetchPlan.DEFAULT };
	
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

		List issueTypeList = new ArrayList<IssueType>();
		issueTypeCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				selectedIssueType = issueTypeCombo.getSelectedElement();
			}
		});
		
		documentTypeLbl = new Label(this, SWT.NONE);
		documentTypeLbl.setText("Document Type: ");
		documentTypeTextLbl = new Label(this, SWT.NONE);
		documentTypeTextLbl.setText(issue.getStateDefinition().getProcessDefinitionID());
		
		stateDefinitionLbl = new Label(this, SWT.NONE);
		stateDefinitionLbl.setText("State: ");
		stateDefinitionCombo = new XComboComposite<StateDefinition>(this, SWT.NONE, labelProvider);
		stateDefinitionCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				selectedState = stateDefinitionCombo.getSelectedElement();
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

		fileComposite = new FileSelectionComposite(this, SWT.NONE, FileSelectionComposite.OPEN_FILE, 
				null, null){
			@Override
			protected void modifyText(ModifyEvent e) {
			}
		}; 
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		fileComposite.setLayoutData(gridData);
//		fileComposite.setFileText(issue.getFileName());
		fileComposite.getFileTextControl().setEditable(false);

		Job loadJob = new Job("Loading Issue Properties....") {
			@Override
			protected IStatus run(final ProgressMonitor monitor) {
				try {
					final TradeManager tradeManager =	TradePlugin.getDefault().getTradeManager();
					final JbpmManager jbpmManager = JbpmManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
					
					issueTypes = new ArrayList<IssueType>(IssueTypeDAO.sharedInstance().getIssueTypes(FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor));
					
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							issueTypeCombo.removeAll();
							for (Iterator it = issueTypes.iterator(); it.hasNext(); ) {
								IssueType issueType = (IssueType) it.next();
								issueTypeCombo.addElement(issueType);
							}
							issueTypeCombo.selectElementByIndex(0);
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
					
//					Display.getDefault().asyncExec(new Runnable() {
//						public void run() {
//							for(final IssueDocumentType type : IssueDocumentType.values()){
//								try {
//									Set<ProcessDefinitionID> processDefinitionIDs = tradeManager.getProcessDefinitionIDs(type.c().getName());
//									String[] PROCESS_DEFINITION_FETCH_GROUPS = new String[] {
//											FetchPlan.DEFAULT,
//											ProcessDefinition.FETCH_GROUP_THIS_PROCESS_DEFINITION
//									};
//									Collection<ProcessDefinition> processDefinitions;
//
//									processDefinitions = ProcessDefinitionDAO.sharedInstance().getProcessDefinitions(
//											processDefinitionIDs, 
//											PROCESS_DEFINITION_FETCH_GROUPS, 
//											NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
//											monitor);
//
//									final String[] STATE_DEFINITION_FETCH_GROUPS = new String[] {
//											FetchPlan.DEFAULT,
//											StateDefinition.FETCH_GROUP_NAME
//									};
//
//
//									for (ProcessDefinition processDefinition : processDefinitions){
//										Set<StateDefinitionID> statedDefinitionIDs;
//										try {
//											statedDefinitionIDs = jbpmManager.getStateDefinitionIDs(processDefinition);
//											Collection<StateDefinition> stateDefinitions = StateDefinitionDAO.sharedInstance().getStateDefintions(
//													statedDefinitionIDs, 
//													STATE_DEFINITION_FETCH_GROUPS, 
//													NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
//													monitor);
//											stateDefinitionMap.put(type.c(), stateDefinitions);
//										} catch (Exception e) {
//											ExceptionHandlerRegistry.asyncHandleException(e);
//											throw new RuntimeException(e);
//										}
//									}//for
//
//									documentTypeCombo.selectElementByIndex(0);
//									selectedDocumentType = documentTypeCombo.getSelectedElement();
//
//									stateDefinitionCombo.removeAll();
//									Collection<StateDefinition> states = stateDefinitionMap.get(selectedDocumentType);
//									for(StateDefinition state : states){
//										stateDefinitionCombo.addElement(state);
//									}//for
//									stateDefinitionCombo.selectElementByIndex(0);
//
//									selectedState = stateDefinitionCombo.getSelectedElement();
//								}//try
//								catch (Exception e1) {
//									ExceptionHandlerRegistry.asyncHandleException(e1);
//									throw new RuntimeException(e1);
//								}
//							}//for
//						}//run
//					});
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

	private ILabelProvider labelProvider = new LabelProvider() {
		@Override
		public String getText(Object element) 
		{
			if (element instanceof StateDefinition) {
				StateDefinition issueSeverityType = (StateDefinition) element;
				return issueSeverityType.getName().getText();
			}

			if (element instanceof IssueSeverityType) {
				IssueSeverityType issueSeverityType = (IssueSeverityType) element;
				return issueSeverityType.getIssueSeverityTypeText().getText();
			}

//			if (element instanceof IssueStatus) {
//				IssueStatus issueStatus = (IssueStatus) element;
//				return issueStatus.getIssueStatusText().getText();
//			}

			if (element instanceof IssuePriority) {
				IssuePriority issuePriority = (IssuePriority) element;
				return issuePriority.getIssuePriorityText().getText();
			}

			if (element instanceof Class) {
				Class c = (Class) element;
				return c.getSimpleName();
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
