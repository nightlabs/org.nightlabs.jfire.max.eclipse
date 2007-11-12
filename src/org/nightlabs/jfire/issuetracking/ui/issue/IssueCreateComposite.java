package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.security.UserSearchDialog;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueStatus;
import org.nightlabs.jfire.issue.dao.IssuePriorityDAO;
import org.nightlabs.jfire.issue.dao.IssueSeverityTypeDAO;
import org.nightlabs.jfire.jbpm.JbpmManager;
import org.nightlabs.jfire.jbpm.JbpmManagerUtil;
import org.nightlabs.jfire.jbpm.dao.ProcessDefinitionDAO;
import org.nightlabs.jfire.jbpm.dao.StateDefinitionDAO;
import org.nightlabs.jfire.jbpm.graph.def.ProcessDefinition;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.jbpm.graph.def.id.ProcessDefinitionID;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.store.ReceptionNote;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.state.id.StateDefinitionID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.CollectionUtil;

public class IssueCreateComposite extends XComposite{

	private String[] documentTypes = new String[]{DeliveryNote.class.getName(), ReceptionNote.class.getName(), 
			Invoice.class.getName(), Offer.class.getName()};

	private Map<String, Collection<StateDefinition>> stateDefinitionMap = new HashMap<String, Collection<StateDefinition>>();

	private List<IssueSeverityType> issueSeverityTypes = new ArrayList<IssueSeverityType>();
//	private List<IssueStatus> issueStatus = new ArrayList<IssueStatus>();
	private List<IssuePriority> issuePriorities = new ArrayList<IssuePriority>();

	private String selectedDocumentType;
	private IssueSeverityType selectedIssueSeverityType;
	private StateDefinition selectedState;
	private IssuePriority selectedIssuePriority;

	private Label documentTypeLbl;
	private XComboComposite<String> documentTypeCombo;
	private Label issueSeverityLbl;
	private XComboComposite<IssueSeverityType> issueSeverityCombo;
	private Label stateDefinitionLbl;
	private XComboComposite<StateDefinition> stateDefinitionCombo;
	private Label issuePriorityLbl;
	private XComboComposite<IssuePriority> issuePriorityCombo;

	private Label reporterLbl;
	private Text reporterText;
	private Button reporterButton;
	
	private Label toUserLbl;
	private Text toUserText;
	private Button toUserButton;
	
	private Label subjectLabel;
	private I18nTextEditor subjectText;

	private Label fileLabel;
	private FileSelectionComposite fileComposite;

	private Label descriptionLabel;
	private I18nTextEditorMultiLine descriptionText;

	private Button submitButton;

	private User selectedReporter;
	private User selectedUndertaker;

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

		documentTypeLbl = new Label(this, SWT.NONE);
		documentTypeLbl.setText("Document Type: ");
		documentTypeCombo = new XComboComposite<String>(this, SWT.NONE, labelProvider);
		documentTypeCombo.setInput(CollectionUtil.array2ArrayList(documentTypes));
		documentTypeCombo.selectElementByIndex(0);
		documentTypeCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				selectedDocumentType = documentTypeCombo.getSelectedElement();
				stateDefinitionCombo.removeAll();
				Collection<StateDefinition> states = stateDefinitionMap.get(selectedDocumentType);
				for(StateDefinition state : states){
					stateDefinitionCombo.addElement(state);
				}//for
				stateDefinitionCombo.selectElementByIndex(0);
			}
		});

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
		selectedReporter = Login.sharedInstance().getUser(new String[]{User.FETCH_GROUP_THIS_USER}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
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
						reporterText.setText(selectedUndertaker.getName());
				}//if
			}
		});
		/*******************************/
		/**********Assigned To**********/
		toUserLbl = new Label(this, SWT.NONE);
		toUserLbl.setText("Assigned To: ");

		XComposite toUserComposite = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		toUserComposite.getGridLayout().numColumns = 2;

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		toUserComposite.setLayoutData(gridData);

		toUserText = new Text(toUserComposite, SWT.BORDER);
		gridData  = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		toUserText.setLayoutData(gridData);

		toUserButton = new Button(toUserComposite, SWT.PUSH);
		toUserButton.setText("Choose User");

		toUserButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				UserSearchDialog userSearchDialog = new UserSearchDialog(getShell(), toUserText.getText());
				int returnCode = userSearchDialog.open();
				if (returnCode == Dialog.OK) {
					selectedUndertaker = userSearchDialog.getSelectedUser();
//					userID = (UserID) JDOHelper.getObjectId(selectedUser);
					if (selectedUndertaker != null)
						toUserText.setText(selectedUndertaker.getName());
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

		Job loadJob = new Job("Loading Issue Properties....") {
			@Override
			protected IStatus run(final ProgressMonitor monitor) {
				try {
					final TradeManager tradeManager =	TradePlugin.getDefault().getTradeManager();
					final JbpmManager jbpmManager = JbpmManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
					
					issueSeverityTypes = IssueSeverityTypeDAO.sharedInstance().getIssueSeverityTypes(monitor);
//					issueStatus = IssueStatusDAO.sharedInstance().getIssueStatus(monitor);
					issuePriorities = IssuePriorityDAO.sharedInstance().getIssuePriorities(monitor);

					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							issueSeverityCombo.removeAll();
							for (Iterator it = issueSeverityTypes.iterator(); it.hasNext(); ) {
								IssueSeverityType issueSeverityType = (IssueSeverityType) it.next();
								issueSeverityCombo.addElement(issueSeverityType);
							}
							issueSeverityCombo.selectElementByIndex(0);
							selectedIssueSeverityType = issueSeverityCombo.getSelectedElement();
						}
					});

					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							issuePriorityCombo.removeAll();
							for (Iterator it = issuePriorities.iterator(); it.hasNext(); ) {
								IssuePriority ip = (IssuePriority) it.next();
								issuePriorityCombo.addElement(ip);
							}
							issuePriorityCombo.selectElementByIndex(0);
							selectedIssuePriority = issuePriorityCombo.getSelectedElement();
						}
					});
					
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							for(final String type : documentTypes){
								try {
									Set<ProcessDefinitionID> processDefinitionIDs = tradeManager.getProcessDefinitionIDs(type);
									String[] PROCESS_DEFINITION_FETCH_GROUPS = new String[] {
											FetchPlan.DEFAULT,
											ProcessDefinition.FETCH_GROUP_THIS_PROCESS_DEFINITION
									};
									Collection<ProcessDefinition> processDefinitions;

									processDefinitions = ProcessDefinitionDAO.sharedInstance().getProcessDefinitions(
											processDefinitionIDs, 
											PROCESS_DEFINITION_FETCH_GROUPS, 
											NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
											monitor);

									final String[] STATE_DEFINITION_FETCH_GROUPS = new String[] {
											FetchPlan.DEFAULT,
											StateDefinition.FETCH_GROUP_NAME
									};


									for (ProcessDefinition processDefinition : processDefinitions){
										Set<StateDefinitionID> statedDefinitionIDs;
										try {
											statedDefinitionIDs = jbpmManager.getStateDefinitionIDs(processDefinition);
											Collection<StateDefinition> stateDefinitions = StateDefinitionDAO.sharedInstance().getStateDefintions(
													statedDefinitionIDs, 
													STATE_DEFINITION_FETCH_GROUPS, 
													NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
													monitor);
											stateDefinitionMap.put(type, stateDefinitions);
//											allStateDefinitions.addAll(stateDefinitions);		
										} catch (Exception e) {
											ExceptionHandlerRegistry.asyncHandleException(e);
											throw new RuntimeException(e);
										}
									}//for

									documentTypeCombo.selectElementByIndex(0);
									selectedDocumentType = documentTypeCombo.getSelectedElement();
									
									stateDefinitionCombo.removeAll();
									Collection<StateDefinition> states = stateDefinitionMap.get(selectedDocumentType);
									for(StateDefinition state : states){
										stateDefinitionCombo.addElement(state);
									}//for
									stateDefinitionCombo.selectElementByIndex(0);
								}//try
								catch (Exception e1) {
									ExceptionHandlerRegistry.asyncHandleException(e1);
									throw new RuntimeException(e1);
								}
							}//for
						}//run
					});
					
					return Status.OK_STATUS;
				}catch (Exception e1) {
					ExceptionHandlerRegistry.asyncHandleException(e1);
					throw new RuntimeException(e1);
				}
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

	public User getSelectedUser() {
		return selectedUndertaker;
	}

	public I18nTextEditorMultiLine getDescriptionText() {
		return descriptionText;
	}

	public I18nTextEditor getSubjectText() {
		return subjectText;
	}
}
