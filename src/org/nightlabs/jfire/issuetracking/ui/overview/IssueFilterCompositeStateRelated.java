package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueTypeDAO;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.jfire.jbpm.JbpmManagerRemote;
import org.nightlabs.jfire.jbpm.dao.StateDefinitionDAO;
import org.nightlabs.jfire.jbpm.graph.def.ProcessDefinition;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinitionName;
import org.nightlabs.jfire.jbpm.graph.def.id.ProcessDefinitionID;
import org.nightlabs.jfire.jbpm.graph.def.id.StateDefinitionID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueFilterCompositeStateRelated
extends AbstractQueryFilterComposite<IssueQuery>
{
	private static final Logger logger = Logger.getLogger(IssueFilterCompositeStateRelated.class);

	private Button processDefinitionActiveButton;

	private XComboComposite<ProcessDefinition> processDefinitionsCombo;
	private ILabelProvider processDefinitionLabelProvider = new LabelProvider() {
		@Override
		public String getText(Object element)
		{
			if (element instanceof ProcessDefinition) {
				ProcessDefinition processDefinition = (ProcessDefinition) element;
				return processDefinition.getProcessDefinitionID();
			}
			return super.getText(element);
		}
	};

	private DummyProcessDefinition ALL_PROCESS_DEFINITION = new DummyProcessDefinition();
	private class DummyProcessDefinition extends ProcessDefinition {
		@SuppressWarnings("deprecation")
		public DummyProcessDefinition() {
		}

		@Override
		public String getProcessDefinitionID() {
			return "All";
		}
	}

	private Button stateDefinitionActiveButton;
	private XComboComposite<StateDefinition> stateDefinitionsCombo;
	private ILabelProvider stateDefinitionLabelProvider = new LabelProvider() {
		@Override
		public String getText(Object element)
		{
			if (element instanceof StateDefinition) {
				StateDefinition stateDefinition = (StateDefinition) element;
				return stateDefinition.getName().getText();
			}

			return super.getText(element);
		}
	};

	private DummyStateDefinition ALL_STATE_DEFINITION = new DummyStateDefinition();
	private class DummyStateDefinition extends StateDefinition {
		@SuppressWarnings("deprecation")
		public DummyStateDefinition() {
		}

		@Override
		public StateDefinitionName getName() {
			StateDefinitionName stateDefinitionName = new StateDefinitionName(this);
			stateDefinitionName.setText(Locale.ENGLISH, "All");
			return stateDefinitionName;
		}
	}

	/**
	 * @param parent
	 *          The parent to instantiate this filter into.
	 * @param style
	 *          The style to apply.
	 * @param layoutMode
	 *          The layout mode to use. See {@link XComposite.LayoutMode}.
	 * @param layoutDataMode
	 *          The layout data mode to use. See {@link XComposite.LayoutDataMode}.
	 * @param queryProvider
	 *          The queryProvider to use. It may be <code>null</code>, but the caller has to
	 *          ensure, that it is set before {@link #getQuery()} is called!
	 */
	public IssueFilterCompositeStateRelated(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode,
			QueryProvider<? super IssueQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
		createComposite();
	}

	/**
	 * @param parent
	 *          The this to instantiate this filter into.
	 * @param style
	 *          The style to apply.
	 * @param queryProvider
	 *          The queryProvider to use. It may be <code>null</code>, but the caller has to
	 *          ensure, that it is set before {@link #getQuery()} is called!
	 */
	public IssueFilterCompositeStateRelated(Composite parent, int style,
			QueryProvider<? super IssueQuery> queryProvider)
	{
		super(parent, style, queryProvider);
		createComposite();
	}

	@Override
	public Class<IssueQuery> getQueryClass() {
		return IssueQuery.class;
	}

	@Override
	protected void createComposite()
	{
		this.setLayout(new GridLayout(3, false));

		XComposite mainComposite = new XComposite(this, SWT.NONE,
				LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		mainComposite.getGridLayout().numColumns = 2;

		Group processDefinitionGroup = new Group(mainComposite, SWT.NONE);
		processDefinitionGroup.setText("Process Definition");
		processDefinitionGroup.setLayout(new GridLayout(2, false));
		processDefinitionGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		processDefinitionActiveButton = new Button(processDefinitionGroup, SWT.CHECK);
		processDefinitionActiveButton.setText("Active");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		processDefinitionActiveButton.setLayoutData(gridData);
		processDefinitionActiveButton.addSelectionListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				getQuery().setFieldEnabled(IssueQuery.FieldName.processDefinitionID, active);
			}
		});
		new Label(processDefinitionGroup, SWT.NONE).setText("Issue Type");
		processDefinitionsCombo = new XComboComposite<ProcessDefinition>(processDefinitionGroup, getBorderStyle(), stateDefinitionLabelProvider);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		processDefinitionsCombo.setLayoutData(gridData);
		processDefinitionsCombo.setLabelProvider(processDefinitionLabelProvider);
		processDefinitionsCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent e)
			{
				ProcessDefinition selectedProcessDefinition = processDefinitionsCombo.getSelectedElement();
				boolean isSelectAll = selectedProcessDefinition.equals(ALL_PROCESS_DEFINITION);
				if (isSelectAll)
					getQuery().setProcessDefinitionID(null);
				else {
					getQuery().setProcessDefinitionID((ProcessDefinitionID) JDOHelper.getObjectId(selectedProcessDefinition));
					if (! getQuery().isFieldEnabled(IssueQuery.FieldName.processDefinitionID))
						getQuery().setFieldEnabled(IssueQuery.FieldName.processDefinitionID, true);
				}

				stateDefinitionsCombo.removeAll();
				stateDefinitionsCombo.addElement(ALL_STATE_DEFINITION);
				stateDefinitionsCombo.selectElementByIndex(0);

				List<StateDefinition> stateDefinitionList = processDefinition2StateDefinitions.get(selectedProcessDefinition);
				if (stateDefinitionList == null || stateDefinitionList.isEmpty())
					return;
				stateDefinitionsCombo.addElements(stateDefinitionList);
			}
		});

		Group stateDefinitionGroup = new Group(mainComposite, SWT.NONE);
		stateDefinitionGroup.setText("State Definition");
		stateDefinitionGroup.setLayout(new GridLayout(2, false));
		stateDefinitionGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		stateDefinitionActiveButton = new Button(stateDefinitionGroup, SWT.CHECK);
		stateDefinitionActiveButton.setText("Active");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		stateDefinitionActiveButton.setLayoutData(gridData);
		stateDefinitionActiveButton.addSelectionListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				getQuery().setFieldEnabled(IssueQuery.FieldName.processDefinitionID, active);
			}
		});
		new Label(stateDefinitionGroup, SWT.NONE).setText("State");
		stateDefinitionsCombo = new XComboComposite<StateDefinition>(stateDefinitionGroup, getBorderStyle(), stateDefinitionLabelProvider);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		stateDefinitionsCombo.setLayoutData(gridData);
		stateDefinitionsCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent e)
			{
				StateDefinition selectedStateDefinition = stateDefinitionsCombo.getSelectedElement();
				boolean isSelectAll = selectedStateDefinition.equals(ALL_STATE_DEFINITION);
				if (isSelectAll)
					getQuery().setJbpmNodeName(null);
				else {
					getQuery().setJbpmNodeName(selectedStateDefinition.getJbpmNodeName());
					if (! getQuery().isFieldEnabled(IssueQuery.FieldName.jbpmNodeName))
						getQuery().setFieldEnabled(IssueQuery.FieldName.jbpmNodeName, true);
				}
			}
		});

		loadProperties();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void updateUI(QueryEvent event, List<FieldChangeCarrier> changedFields)
	{
		for (FieldChangeCarrier changedField : event.getChangedFields())
		{
			if (IssueQuery.FieldName.processDefinitionID.equals(changedField.getPropertyName()))
			{
				ProcessDefinitionID newProcessDefinitionID = (ProcessDefinitionID) changedField.getNewValue();
				if (newProcessDefinitionID == null)
				{

				}
				else
				{
					for (ProcessDefinition processDefinition : processDefinitionsCombo.getElements()) {
						if (JDOHelper.getObjectId(processDefinition).equals(newProcessDefinitionID)) {
							processDefinitionsCombo.setSelection(processDefinition);
							break;
						}
					}
				}
			}
			else if (getEnableFieldName(IssueQuery.FieldName.processDefinitionID).equals(
					changedField.getPropertyName()))
			{
				Boolean active = (Boolean) changedField.getNewValue();
				setSearchSectionActive(active);
				if (!active) {
					getQuery().setProcessDefinitionID(null);
				}
			}
		} // for (FieldChangeCarrier changedField : event.getChangedFields())
	}

	private static final Set<String> fieldNames;
	static
	{
		fieldNames = new HashSet<String>(1);
		fieldNames.add(IssueQuery.FieldName.jbpmNodeName);
		fieldNames.add(IssueQuery.FieldName.processDefinitionID);
	}

	@Override
	protected Set<String> getFieldNames()
	{
		return fieldNames;
	}

	/**
	 * Group ID for storing active states in the query.
	 */
	public static final String FILTER_GROUP_ID = "IssueFilterCompositeStateRelated"; //$NON-NLS-1$

	@Override
	protected String getGroupID()
	{
		return FILTER_GROUP_ID;
	}


	private Map<ProcessDefinition, List<StateDefinition>> processDefinition2StateDefinitions = new HashMap<ProcessDefinition, List<StateDefinition>>();

	private void loadProperties() {
		Job fillStateComboJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterComposite.loadProcessDefinitionsJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) {
				try {
					JbpmManagerRemote jbpmManager = JFireEjb3Factory.getRemoteBean(JbpmManagerRemote.class, Login.getLogin().getInitialContextProperties());

					String[] FETCH_GROUPS_ISSUE_TYPE = new String[] {
							FetchPlan.DEFAULT,
							IssueType.FETCH_GROUP_NAME,
							IssueType.FETCH_GROUP_PROCESS_DEFINITION
					};

					IssueTypeDAO issueTypeDAO = IssueTypeDAO.sharedInstance();
					Collection<IssueType> issueTypes =
						issueTypeDAO.getAllIssueTypes(FETCH_GROUPS_ISSUE_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

					String[] FETCH_GROUPS_STATE_DEFINITON = new String[] {
						FetchPlan.DEFAULT,
						StateDefinition.FETCH_GROUP_NAME
					};

					for (IssueType issueType : issueTypes) {
						ProcessDefinition processDefinition = issueType.getProcessDefinition();
						Set<StateDefinitionID> statedDefinitionIDs =
							jbpmManager.getStateDefinitionIDs(issueType.getProcessDefinition());
						Collection<StateDefinition> stateDefinitions = StateDefinitionDAO.sharedInstance().getStateDefintions(
								statedDefinitionIDs,
								FETCH_GROUPS_STATE_DEFINITON,
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								monitor);
						processDefinition2StateDefinitions.put(processDefinition, new ArrayList<StateDefinition>(stateDefinitions));
					}


					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (processDefinitionsCombo == null || processDefinitionsCombo.isDisposed() ||
									stateDefinitionsCombo == null || stateDefinitionsCombo.isDisposed())
								return;

							Set<ProcessDefinition> processDefinitions = processDefinition2StateDefinitions.keySet();
							processDefinitionsCombo.addElement(ALL_PROCESS_DEFINITION);
							processDefinitionsCombo.addElements(processDefinitions);
							if (!processDefinitions.isEmpty()) {
								ProcessDefinition firstProcessDefinition = processDefinitions.iterator().next();
								List<StateDefinition> stateDefinitionList = processDefinition2StateDefinitions.get(firstProcessDefinition);
								stateDefinitionsCombo.addElement(ALL_STATE_DEFINITION);
								stateDefinitionsCombo.addElements(stateDefinitionList);
								if (!stateDefinitionList.isEmpty()) {
									stateDefinitionsCombo.selectElementByIndex(0);
								}
							}
							processDefinitionsCombo.selectElementByIndex(0);
						}
					});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				return Status.OK_STATUS;
			}
		};
		fillStateComboJob.schedule();
	}
}