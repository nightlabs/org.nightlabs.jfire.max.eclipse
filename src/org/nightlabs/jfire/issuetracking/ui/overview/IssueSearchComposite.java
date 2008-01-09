package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.DateTimeEdit;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.JDOQuery;
import org.nightlabs.jdo.ui.JDOQueryComposite;
import org.nightlabs.jfire.base.ui.security.UserSearchDialog;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueTypeDAO;
import org.nightlabs.jfire.issue.id.IssuePriorityID;
import org.nightlabs.jfire.issue.id.IssueSeverityTypeID;
import org.nightlabs.jfire.issue.id.IssueTypeID;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLabelProvider;
import org.nightlabs.jfire.organisation.Organisation;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.security.id.UserID;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun 
 *
 */
public class IssueSearchComposite extends JDOQueryComposite {
	private Text issueIDText;
	private Text subjectText;
	private Text reporterText;
	private Text assigneeText;
	
	private List<IssueType> issueTypes;
	private List<IssuePriority> allIssuePriorities;
	private List<IssueSeverityType> allIssueSeverityTypes;
	
	private XComboComposite<IssueType> issueTypeCombo;
	private XComboComposite<IssueSeverityType> issueSeverityCombo;
	private XComboComposite<IssuePriority> issuePriorityCombo;
	
	private IssueType selectedIssueType;
	private IssueSeverityType selectedIssueSeverityType;
	private IssuePriority selectedIssuePriority;
	
	private User selectedReporter;
	private User selectedAssignee;
	
	private DateTimeEdit createdTimeEdit;
	private DateTimeEdit updatedTimeEdit;
	
	
	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public IssueSearchComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
		createComposite(this);
		prepareProperties();
	}

	/**
	 * @param parent
	 * @param style
	 */
	public IssueSearchComposite(Composite parent, int style) {
		super(parent, style);
		createComposite(this);
		prepareProperties();
	}

	private void prepareProperties(){
		ISSUE_TYPE_ALL.getName().setText(Locale.ENGLISH.getLanguage(), "All");
		ISSUE_SEVERITY_TYPE_ALL.getIssueSeverityTypeText().setText(Locale.ENGLISH.getLanguage(), "All");
		ISSUE_PRIORITY_ALL.getIssuePriorityText().setText(Locale.ENGLISH.getLanguage(), "All");
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jdo.ui.JDOQueryComposite#createComposite(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createComposite(Composite parent) {
		parent.setLayout(new GridLayout(3, false));

//		new Label(userGroup, SWT.NONE).setText("ID");
//		issueIDText = new Text(userGroup, SWT.NONE);
//		issueIDText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		
//		new Label(userGroup, SWT.NONE).setText("Subject");
//		subjectText = new Text(userGroup, SWT.NONE);
//		subjectText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Group issueTypeGroup = new Group(parent, SWT.NONE);
		issueTypeGroup.setText("Issue Related");
		issueTypeGroup.setLayout(new GridLayout(1, false));
		issueTypeGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		XComposite issueTypeComposite = new XComposite(issueTypeGroup, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		issueTypeComposite.getGridLayout().numColumns = 2;
		
		new Label(issueTypeComposite, SWT.NONE).setText("Issue Type: ");
		issueTypeCombo = new XComboComposite<IssueType>(issueTypeComposite, SWT.NONE);
		issueTypeCombo.setLabelProvider(labelProvider);
		issueTypeCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				selectedIssueType = issueTypeCombo.getSelectedElement();
				
				issueSeverityCombo.removeAll();
				issueSeverityCombo.addElement(ISSUE_SEVERITY_TYPE_ALL);
				for (IssueSeverityType is : selectedIssueType.getIssueSeverityTypes()) {
					issueSeverityCombo.addElement(is);
				}
				issueSeverityCombo.selectElementByIndex(0);
				selectedIssueSeverityType = issueSeverityCombo.getSelectedElement();
				
				issuePriorityCombo.removeAll();
				issuePriorityCombo.addElement(ISSUE_PRIORITY_ALL);
				for (IssuePriority ip : selectedIssueType.getIssuePriorities()) {
					issuePriorityCombo.addElement(ip);
				}
				issuePriorityCombo.selectElementByIndex(0);
				selectedIssuePriority = issuePriorityCombo.getSelectedElement();
			}
		});
		
		new Label(issueTypeComposite, SWT.NONE).setText("Severity: ");
		issueSeverityCombo = new XComboComposite<IssueSeverityType>(issueTypeComposite, SWT.NONE);
		issueSeverityCombo.setLabelProvider(labelProvider);
		issueSeverityCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				selectedIssueSeverityType = issueSeverityCombo.getSelectedElement();
			}
		});
		
		new Label(issueTypeComposite, SWT.NONE).setText("Priority: ");
		issuePriorityCombo = new XComboComposite<IssuePriority>(issueTypeComposite, SWT.NONE);
		issuePriorityCombo.setLabelProvider(labelProvider);
		issuePriorityCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				selectedIssuePriority = issuePriorityCombo.getSelectedElement();
			}
		});
		
		//-----------------------------------------------------------
		Group userGroup = new Group(parent, SWT.NONE);
		userGroup.setText("People Related");
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.verticalSpacing = 10;
		userGroup.setLayout(gridLayout);
		userGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		new Label(userGroup, SWT.NONE).setText("Reporter: ");
		reporterText = new Text(userGroup, SWT.NONE);
		reporterText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		/////////////////////////////////
		Button reporterButton = new Button(userGroup, SWT.PUSH);
		reporterButton.setText("Choose User");

		reporterButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				UserSearchDialog userSearchDialog = new UserSearchDialog(getShell(), reporterText.getText());
				int returnCode = userSearchDialog.open();
				if (returnCode == Dialog.OK) {
					selectedReporter = userSearchDialog.getSelectedUser();
					if (selectedReporter != null)
						reporterText.setText(selectedReporter.getName());
				}//if
			}
		});
		/////////////////////////////////
		
		new Label(userGroup, SWT.NONE).setText("Assignee: ");
		assigneeText = new Text(userGroup, SWT.NONE);
		assigneeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button assigneeButton = new Button(userGroup, SWT.PUSH);
		assigneeButton.setText("Choose User");

		assigneeButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				UserSearchDialog userSearchDialog = new UserSearchDialog(getShell(), assigneeText.getText());
				int returnCode = userSearchDialog.open();
				if (returnCode == Dialog.OK) {
					selectedAssignee = userSearchDialog.getSelectedUser();
					if (selectedAssignee != null)
						assigneeText.setText(selectedAssignee.getName());
				}//if
			}
		});
		//-----------------------------------------------------------
		Group timeGroup = new Group(parent, SWT.NONE);
		timeGroup.setText("Time Related");
		gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 10;
		timeGroup.setLayout(gridLayout);
		timeGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		new Label(timeGroup, SWT.NONE).setText("Created Time: ");
		createdTimeEdit = new DateTimeEdit(
				timeGroup,
				DateFormatter.FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY + DateTimeEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX,
				Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterComposite.createDateMin.caption")); //$NON-NLS-1$
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
		createdTimeEdit.setDate(cal.getTime());
		
		new Label(timeGroup, SWT.NONE).setText("Updated Time: ");
		updatedTimeEdit = new DateTimeEdit(
				timeGroup,
				DateFormatter.FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY + DateTimeEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX,
				Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterComposite.createDateMin.caption")); //$NON-NLS-1$
		cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
		updatedTimeEdit.setDate(cal.getTime());
		
		loadProperties();
	}

	private static final String[] FETCH_GROUPS_ISSUE = { IssueType.FETCH_GROUP_THIS, IssueSeverityType.FETCH_GROUP_THIS, IssuePriority.FETCH_GROUP_THIS, FetchPlan.DEFAULT };
	private IssueLabelProvider labelProvider = new IssueLabelProvider();
	private void loadProperties(){
		Job loadJob = new Job("Loading Issue Properties....") {
			@Override
			protected IStatus run(final ProgressMonitor monitor) {
				try {
					issueTypes = new ArrayList<IssueType>(IssueTypeDAO.sharedInstance().getIssueTypes(FETCH_GROUPS_ISSUE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor));
					allIssuePriorities = new ArrayList<IssuePriority>();
					allIssueSeverityTypes = new ArrayList<IssueSeverityType>();
					
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							issueTypeCombo.removeAll();
							issueTypeCombo.addElement(ISSUE_TYPE_ALL);
							for (Iterator it = issueTypes.iterator(); it.hasNext(); ) {
								IssueType issueType = (IssueType) it.next();
								issueTypeCombo.addElement(issueType);
								for(IssuePriority p : issueType.getIssuePriorities())
									allIssuePriorities.add(p);
								for(IssueSeverityType s : issueType.getIssueSeverityTypes())
									allIssueSeverityTypes.add(s);
							}
							issueTypeCombo.selectElementByIndex(0);
							selectedIssueType = issueTypeCombo.getSelectedElement();
							
							/**************************************************/
							ISSUE_TYPE_ALL.getIssuePriorities().addAll(allIssuePriorities);
							ISSUE_TYPE_ALL.getIssueSeverityTypes().addAll(allIssueSeverityTypes);
							/**************************************************/
							
							issueSeverityCombo.removeAll();
							issueSeverityCombo.addElement(ISSUE_SEVERITY_TYPE_ALL);
							for (IssueSeverityType is : selectedIssueType.getIssueSeverityTypes()) {
								issueSeverityCombo.addElement(is);
							}
							issueSeverityCombo.selectElementByIndex(0);
							selectedIssueSeverityType = issueSeverityCombo.getSelectedElement();
							
							issuePriorityCombo.removeAll();
							issuePriorityCombo.addElement(ISSUE_PRIORITY_ALL);
							for (IssuePriority ip : selectedIssueType.getIssuePriorities()) {
								issuePriorityCombo.addElement(ip);
							}
							issuePriorityCombo.selectElementByIndex(0);
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
	}
	
	private static IssueType ISSUE_TYPE_ALL = new IssueType(Organisation.DEV_ORGANISATION_ID, "Issue_Type_All");
	private static IssueSeverityType ISSUE_SEVERITY_TYPE_ALL = new IssueSeverityType(Organisation.DEV_ORGANISATION_ID, "Issue_Severity_Type_All");
	private static IssuePriority ISSUE_PRIORITY_ALL = new IssuePriority(Organisation.DEV_ORGANISATION_ID, "Issue_Priority_All");
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jdo.ui.JDOQueryComposite#getJDOQuery()
	 */
	@Override
	public JDOQuery getJDOQuery() {
		IssueQuery issueQuery = new IssueQuery();
		
		if(selectedIssueType != null && !selectedIssueType.equals(ISSUE_TYPE_ALL)){
			issueQuery.setIssueTypeID((IssueTypeID) JDOHelper.getObjectId(selectedIssueType));
		}
		
		if(selectedIssueSeverityType != null && !selectedIssueSeverityType.equals(ISSUE_SEVERITY_TYPE_ALL)){
			issueQuery.setIssueSeverityTypeID((IssueSeverityTypeID) JDOHelper.getObjectId(selectedIssueSeverityType));
		}
		
		if(selectedIssuePriority != null && !selectedIssuePriority.equals(ISSUE_PRIORITY_ALL)){
			issueQuery.setIssuePriorityID((IssuePriorityID)JDOHelper.getObjectId(selectedIssuePriority));
		}
		
		if(selectedReporter != null){
			issueQuery.setReporterID((UserID)JDOHelper.getObjectId(selectedReporter));
		}
		
		if(selectedAssignee != null){
			issueQuery.setAssigneeID((UserID)JDOHelper.getObjectId(selectedAssignee));
		}
		
		if(createdTimeEdit.isActive()){
			issueQuery.setCreateTimestamp(createdTimeEdit.getDate());
		}
		
		if(updatedTimeEdit.isActive()){
			issueQuery.setUpdateTimestamp(updatedTimeEdit.getDate());
		}
//		if(selectedIssuePriority != null && !selectedIssuePriority.equals(ISSUE_PRIORITY_ALL)){
//			issueQuery.setIssuePriorityID((IssuePriorityID)JDOHelper.getObjectId(selectedIssuePriority));
//		}
		
		return issueQuery;
	}

}
