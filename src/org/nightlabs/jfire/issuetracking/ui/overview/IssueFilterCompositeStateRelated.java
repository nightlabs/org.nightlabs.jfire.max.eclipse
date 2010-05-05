package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.i18n.I18nText;
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
	private String baseName = "org.nightlabs.jfire.issuetracking.ui.resource.messages"; //$NON-NLS-1$
	private ClassLoader loader = IssueFilterCompositeStateRelated.class.getClassLoader();

	private XComboComposite<ProcessDefinition> processDefinitionsCombo;
	private ILabelProvider processDefinitionLabelProvider = new LabelProvider() {
		@Override
		public String getText(Object element)
		{
			if (element instanceof DummyProcessDefinition) {
				DummyProcessDefinition dDefinition = (DummyProcessDefinition) element;
				return dDefinition.getName().getText();
			}
			if (element instanceof ProcessDefinition) {
				ProcessDefinition processDefinition = (ProcessDefinition) element;
				return processDefinition.getProcessDefinitionID();
			}

			return super.getText(element);
		}
	};

	private DummyProcessDefinition ALL_PROCESS_DEFINITION = new DummyProcessDefinition();
	private class DummyProcessDefinition extends ProcessDefinition {
		private class DummyProcessDefinitionName extends I18nText {
			protected Map<String, String> names = new HashMap<String, String>();
			@Override
			protected String getFallBackValue(String languageID) {
				return "All"; //$NON-NLS-1$
			}

			@Override
			protected Map<String, String> getI18nMap() {
				return names;
			}
		}
		private DummyProcessDefinitionName name;
		@SuppressWarnings("deprecation")
		public DummyProcessDefinition() {
			name = new DummyProcessDefinitionName();
			name.readFromProperties(baseName, loader,
			"org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositeStateRelated.processDefinition.all"); //$NON-NLS-1$
		}

		@Override
		public String getProcessDefinitionID() {
			return "All"; //$NON-NLS-1$
		}

		public DummyProcessDefinitionName getName() {
			return name;
		}
	}

	private XComboComposite<StateDefinition> stateDefinitionsCombo;
	private ILabelProvider stateDefinitionLabelProvider = new LabelProvider() {
		@Override
		public String getText(Object element)
		{
			if (element instanceof StateDefinition) {
				StateDefinition stateDefinition = (StateDefinition) element;
				return (stateDefinition.getProcessDefinitionID() == null?"": stateDefinition.getProcessDefinitionID() + ":" ) + stateDefinition.getName().getText(); //$NON-NLS-1$ //$NON-NLS-2$
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
			stateDefinitionName.readFromProperties(baseName, loader,
			"org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositeStateRelated.stateDefinition.all"); //$NON-NLS-1$

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

		new Label(mainComposite, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositeStateRelated.issueTypeLabel")); //$NON-NLS-1$
		processDefinitionsCombo = new XComboComposite<ProcessDefinition>(mainComposite, SWT.NONE | SWT.READ_ONLY, stateDefinitionLabelProvider);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		processDefinitionsCombo.setLayoutData(gridData);
		processDefinitionsCombo.setLabelProvider(processDefinitionLabelProvider);
		processDefinitionsCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent e)
			{
				stateDefinitionsCombo.selectElement(ALL_STATE_DEFINITION);
				stateDefinitionsComboSelected();

				ProcessDefinition selectedProcessDefinition = processDefinitionsCombo.getSelectedElement();
				boolean isSelectAll = selectedProcessDefinition.equals(ALL_PROCESS_DEFINITION);
				if (isSelectAll) {
					getQuery().setProcessDefinitionID(null);
					getQuery().setFieldEnabled(IssueQuery.FieldName.processDefinitionID, false);
				}
				else {
					getQuery().setProcessDefinitionID((ProcessDefinitionID) JDOHelper.getObjectId(selectedProcessDefinition));
					getQuery().setFieldEnabled(IssueQuery.FieldName.processDefinitionID, true);
				}

				stateDefinitionsCombo.removeAll();
				stateDefinitionsCombo.addElement(ALL_STATE_DEFINITION);
				stateDefinitionsCombo.selectElementByIndex(0);

				List<StateDefinition> stateDefinitionList = processDefinition2StateDefinitions.get(selectedProcessDefinition);
				if (selectedProcessDefinition.equals(ALL_PROCESS_DEFINITION)) {
					for (List<StateDefinition> l : processDefinition2StateDefinitions.values()) {
						stateDefinitionsCombo.addElements(l);
					}
				}
				else {
					stateDefinitionsCombo.addElements(stateDefinitionList);
				}
			}
		});

		new Label(mainComposite, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositeStateRelated.stateLabel")); //$NON-NLS-1$
		stateDefinitionsCombo = new XComboComposite<StateDefinition>(mainComposite, SWT.NONE | SWT.READ_ONLY, stateDefinitionLabelProvider);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		stateDefinitionsCombo.setLayoutData(gridData);
		stateDefinitionsCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent e)
			{
				stateDefinitionsComboSelected();
			}
		});

		loadProperties();
	}

	private void stateDefinitionsComboSelected()
	{
		StateDefinition selectedStateDefinition = stateDefinitionsCombo.getSelectedElement();
		boolean isSelectAll = selectedStateDefinition.equals(ALL_STATE_DEFINITION);
		if (isSelectAll) {
			getQuery().setJbpmNodeName(null);
			getQuery().setFieldEnabled(IssueQuery.FieldName.jbpmNodeName, false);
		}
		else {
			getQuery().setJbpmNodeName(selectedStateDefinition.getJbpmNodeName());
			getQuery().setFieldEnabled(IssueQuery.FieldName.jbpmNodeName, true);
		}
	}

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
					processDefinitionsCombo.setSelection(ALL_PROCESS_DEFINITION);
				}
				else
				{
					for (ProcessDefinition processDefinition : processDefinitionsCombo.getElements()) {
						if (processDefinition != ALL_PROCESS_DEFINITION)
							if (JDOHelper.getObjectId(processDefinition).equals(newProcessDefinitionID)) {
								processDefinitionsCombo.setSelection(processDefinition);
								this.selectedProcessDefinition = processDefinition;
								break;
							}
					}
				}
			}
			else if (getEnableFieldName(IssueQuery.FieldName.processDefinitionID).equals(changedField.getPropertyName()))
			{
				boolean isActive = (Boolean) changedField.getNewValue();
				setSearchSectionActive(isActive);
				if (!isActive) {
					getQuery().setProcessDefinitionID(null);
				}
			}
			else if (IssueQuery.FieldName.jbpmNodeName.equals(changedField.getPropertyName()))
			{
				String newJbpmNodeName = (String) changedField.getNewValue();
				if (newJbpmNodeName == null)
				{
					stateDefinitionsCombo.setSelection(ALL_STATE_DEFINITION);
				}
				else
				{
					for (StateDefinition stateDefinition : stateDefinitionsCombo.getElements()) {
						if (stateDefinition.getJbpmNodeName() != null && stateDefinition.getJbpmNodeName().equals(newJbpmNodeName)) {
							stateDefinitionsCombo.setSelection(stateDefinition);
							this.selectedStateDefinition = stateDefinition;
							break;
						}
					}
				}
			}
			else if (getEnableFieldName(IssueQuery.FieldName.jbpmNodeName).equals(changedField.getPropertyName()))
			{
				boolean isActive = (Boolean) changedField.getNewValue();
				setSearchSectionActive(isActive);
				if (!isActive) {
					getQuery().setJbpmNodeName(null);
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

	private ProcessDefinition selectedProcessDefinition;
	private StateDefinition selectedStateDefinition;

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

					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							if (processDefinitionsCombo == null || processDefinitionsCombo.isDisposed() ||
									stateDefinitionsCombo == null || stateDefinitionsCombo.isDisposed())
								return;

							Set<ProcessDefinition> processDefinitions = processDefinition2StateDefinitions.keySet();
							processDefinitionsCombo.addElement(ALL_PROCESS_DEFINITION);
							processDefinitionsCombo.addElements(processDefinitions);
							stateDefinitionsCombo.addElement(ALL_STATE_DEFINITION);

							if (!processDefinitions.isEmpty()) {
								ProcessDefinition firstProcessDefinition = processDefinitions.iterator().next();
								List<StateDefinition> stateDefinitionList = processDefinition2StateDefinitions.get(firstProcessDefinition);
								stateDefinitionsCombo.addElements(stateDefinitionList);
								if (!stateDefinitionList.isEmpty()) {
									stateDefinitionsCombo.selectElementByIndex(0);
								}
							}
							processDefinitionsCombo.selectElementByIndex(0);

							if (selectedProcessDefinition != null) {
								processDefinitionsCombo.setSelection(selectedProcessDefinition);
								selectedProcessDefinition = null;
							}

							if (selectedStateDefinition != null) {
								stateDefinitionsCombo.setSelection(selectedStateDefinition);
								selectedStateDefinition = null;
							}
						}
					});
				} catch (Exception e) {
					ExceptionHandlerRegistry.asyncHandleException(e);
					throw new RuntimeException(e);
				}
				return Status.OK_STATUS;
			}
		};
		fillStateComboJob.schedule();
	}
}