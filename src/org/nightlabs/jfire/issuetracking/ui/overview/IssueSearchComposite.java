/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.JDOQuery;
import org.nightlabs.jdo.ui.JDOQueryComposite;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueTypeDAO;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLabelProvider;
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
	
	private XComboComposite<IssueType> issueTypeCombo;
	private XComboComposite<IssueSeverityType> issueSeverityCombo;
	private XComboComposite<IssuePriority> issuePriorityCombo;
	
	private IssueType selectedIssueType;
	private IssueSeverityType selectedIssueSeverityType;
	private IssuePriority selectedIssuePriority;
	
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
	}

	/**
	 * @param parent
	 * @param style
	 */
	public IssueSearchComposite(Composite parent, int style) {
		super(parent, style);
		createComposite(this);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jdo.ui.JDOQueryComposite#createComposite(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createComposite(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		
		Group group = new Group(parent, getBorderStyle());
		group.setLayout(new GridLayout(6, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(group, SWT.NONE).setText("ID");
		issueIDText = new Text(group, SWT.NONE);
		
		new Label(group, SWT.NONE).setText("Issue Type");
		issueTypeCombo = new XComboComposite<IssueType>(group, SWT.NONE);
		issueTypeCombo.setLabelProvider(labelProvider);
		
		new Label(group, SWT.NONE).setText("Severity");
		issueSeverityCombo = new XComboComposite<IssueSeverityType>(group, SWT.NONE);
		issueSeverityCombo.setLabelProvider(labelProvider);
		
		new Label(group, SWT.NONE).setText("Priority");
		issuePriorityCombo = new XComboComposite<IssuePriority>(group, SWT.NONE);
		issuePriorityCombo.setLabelProvider(labelProvider);
		
		new Label(group, SWT.NONE).setText("Subject");
		subjectText = new Text(group, SWT.NONE);
		
		new Label(group, SWT.NONE).setText("Reporter");
		reporterText = new Text(group, SWT.NONE);
		
		new Label(group, SWT.NONE).setText("Assignee");
		assigneeText = new Text(group, SWT.NONE);
		
		loadProperties();
	}

	private static final String[] FETCH_GROUPS = { IssueType.FETCH_GROUP_THIS, IssueSeverityType.FETCH_GROUP_THIS, IssuePriority.FETCH_GROUP_THIS, FetchPlan.DEFAULT };
	private IssueLabelProvider labelProvider = new IssueLabelProvider();
	private void loadProperties(){
		Job loadJob = new Job("Loading Issue Properties....") {
			@Override
			protected IStatus run(final ProgressMonitor monitor) {
				try {
//					final TradeManager tradeManager =	TradePlugin.getDefault().getTradeManager();
//					final JbpmManager jbpmManager = JbpmManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
					
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
	}
	/* (non-Javadoc)
	 * @see org.nightlabs.jdo.ui.JDOQueryComposite#getJDOQuery()
	 */
	@Override
	public JDOQuery getJDOQuery() {
		return new IssueQuery();
	}

}
