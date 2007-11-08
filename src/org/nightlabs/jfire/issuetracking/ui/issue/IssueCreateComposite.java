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
import org.nightlabs.jfire.issue.dao.IssueStatusDAO;
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

	private Map<String, Collection<StateDefinition>> stateMap = new HashMap<String, Collection<StateDefinition>>();

	private List<IssueSeverityType> issueSeverityTypes = new ArrayList<IssueSeverityType>();
	private List<IssueStatus> issueStatus = new ArrayList<IssueStatus>();
	private List<IssuePriority> issuePriorities = new ArrayList<IssuePriority>();
//	final Set<StateDefinition> allStateDefinitions = new HashSet<StateDefinition>();

	private String selectedDocumentType;
	private IssueSeverityType selectedIssueSeverityType;
	private StateDefinition selectedState;
	private IssuePriority selectedIssuePriority;

	private Label documentLbl;
	private XComboComposite<String> documentCombo;
	private Label severityLbl;
	private XComboComposite<IssueSeverityType> severityCombo;
	private Label statusLbl;
	private XComboComposite<StateDefinition> statusCombo;
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

		loadStates();
	}

	/**
	 * Create the content for this composite.
	 * @param parent The parent composite
	 */
	protected void createComposite(Composite parent) 
	{
		setLayout(new GridLayout(2, false));

		int textStyle = SWT.READ_ONLY | SWT.BORDER;

		documentLbl = new Label(this, SWT.NONE);
		documentLbl.setText("Document Type: ");
		documentCombo = new XComboComposite<String>(this, SWT.NONE, labelProvider);
		documentCombo.setInput(CollectionUtil.array2ArrayList(documentTypes));
		documentCombo.selectElementByIndex(0);
		documentCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				selectedDocumentType = documentCombo.getSelectedElement();
				statusCombo.removeAll();
				Collection<StateDefinition> states = stateMap.get(selectedDocumentType);
				for(StateDefinition state : states){
					statusCombo.addElement(state);
				}//for
				statusCombo.selectElementByIndex(0);
			}
		});

		statusLbl = new Label(this, SWT.NONE);
		statusLbl.setText("Status: ");
		statusCombo = new XComboComposite<StateDefinition>(this, SWT.NONE, labelProvider);
		statusCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
//				selectedIssueSeverityType = severityCombo.getSelectedElement();
			}
		});

		severityLbl = new Label(this, SWT.NONE);
		severityLbl.setText("Severity: ");
		severityCombo = new XComboComposite<IssueSeverityType>(this, SWT.NONE, labelProvider);
		severityCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				selectedIssueSeverityType = severityCombo.getSelectedElement();
			}
		});

		priorityLbl = new Label(this, SWT.NONE);
		priorityLbl.setText("Priority: ");
		priorityCombo = new XComboComposite<IssuePriority>(this, SWT.NONE, labelProvider);
		priorityCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
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

		Job loadJob = new Job("Loading Issue Properties....") {
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
						severityCombo.selectElementByIndex(0);
						selectedIssueSeverityType = severityCombo.getSelectedElement();
					}
				});

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						priorityCombo.removeAll();
						for (Iterator it = issuePriorities.iterator(); it.hasNext(); ) {
							IssuePriority ip = (IssuePriority) it.next();
							priorityCombo.addElement(ip);
						}
						priorityCombo.selectElementByIndex(0);
						selectedIssuePriority = priorityCombo.getSelectedElement();
					}
				});

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
		return selectedUser;
	}

	public I18nTextEditorMultiLine getDescriptionText() {
		return descriptionText;
	}

	public I18nTextEditor getSubjectText() {
		return subjectText;
	}

	private void loadStates(){
		Job loadJob = new Job("Loading Issue Properties....") {
			@Override
			protected IStatus run(ProgressMonitor monitor) {
				try{
					TradeManager tradeManager =	TradePlugin.getDefault().getTradeManager();
					JbpmManager jbpmManager = JbpmManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();

					for(String type : documentTypes){
						Set<ProcessDefinitionID> processDefinitionIDs = tradeManager.getProcessDefinitionIDs(type);
						String[] PROCESS_DEFINITION_FETCH_GROUPS = new String[] {
								FetchPlan.DEFAULT,
								ProcessDefinition.FETCH_GROUP_THIS_PROCESS_DEFINITION
						};
						Collection<ProcessDefinition> processDefinitions = ProcessDefinitionDAO.sharedInstance().getProcessDefinitions(
								processDefinitionIDs, 
								PROCESS_DEFINITION_FETCH_GROUPS, 
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
								monitor);

						String[] STATE_DEFINITION_FETCH_GROUPS = new String[] {
								FetchPlan.DEFAULT,
								StateDefinition.FETCH_GROUP_NAME
						};

						for (ProcessDefinition processDefinition : processDefinitions) 
						{
							Set<StateDefinitionID> statedDefinitionIDs = jbpmManager.getStateDefinitionIDs(processDefinition);
							Collection<StateDefinition> stateDefinitions = StateDefinitionDAO.sharedInstance().getStateDefintions(
									statedDefinitionIDs, 
									STATE_DEFINITION_FETCH_GROUPS, 
									NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
									monitor);
							stateMap.put(type, stateDefinitions);
//							allStateDefinitions.addAll(stateDefinitions);				
						}//for
					}//for
					return Status.OK_STATUS;
				} catch (Exception e) {
					ExceptionHandlerRegistry.asyncHandleException(e);
					throw new RuntimeException(e);
				}

			} 

		};
		loadJob.setPriority(Job.SHORT);
		loadJob.schedule();
	}
}
