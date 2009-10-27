package org.nightlabs.jfire.trade.ui.overview;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.nightlabs.base.ui.composite.DateTimeEdit;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.jbpm.JbpmManagerRemote;
import org.nightlabs.jfire.jbpm.dao.ProcessDefinitionDAO;
import org.nightlabs.jfire.jbpm.dao.StateDefinitionDAO;
import org.nightlabs.jfire.jbpm.graph.def.ProcessDefinition;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.jbpm.graph.def.id.ProcessDefinitionID;
import org.nightlabs.jfire.jbpm.graph.def.id.StateDefinitionID;
import org.nightlabs.jfire.jbpm.query.StatableQuery;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.Util;

/**
 *
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class StatableFilterSearchComposite
	extends AbstractQueryFilterComposite<StatableQuery>
{
	private DateTimeEdit createDTMin;
	private DateTimeEdit createDTMax;
	private Button stateDefinitionActiveButton;
	private Button onlyInSelectedStateButton;
	private Button notInSelectedStateButton;
	private Map<ProcessDefinition, List<StateDefinition>> processDefinition2StateDefinitions;

	private static final String[] FETCH_GROUPS_STATE_DEFINITON = new String[] {
		FetchPlan.DEFAULT,
		StateDefinition.FETCH_GROUP_NAME
	};

	private static final String[] FETCH_GROUPS_PROCESS_DEFINITON = new String[] {
		FetchPlan.DEFAULT
	};

	/**
	 * Creates a new {@link AbstractQueryFilterComposite}.
	 * <p><b>Note</b>: The caller has to call {@link #createComposite()} to create the UI! <br />
	 * 	This is not done in this constructor to omit problems with fields that are not only declared,
	 * 	but also initialised. If these fields are used inside {@link #createComposite()}
	 * 	or new values are assigned to them, one of the following two things may happen:
	 *  <ul>
	 *  	<li>The value assigned to that field is overridden by the initialisation value that is
	 *  			assigned after this constructor is finished</li>
	 *  	<li>The referenced value is not yet properly initialised, because the initialisation is
	 *  			done after the constructor finishes, and hence results in an unexpected exception.</li>
	 *  </ul>
	 * </p>
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
	public StatableFilterSearchComposite(Composite parent, int style, LayoutMode layoutMode,
		LayoutDataMode layoutDataMode, QueryProvider<? super StatableQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
		processDefinition2StateDefinitions = new HashMap<ProcessDefinition, List<StateDefinition>>();
		createComposite();
	}

	/**
	 * Delegates to {@link StatableFilterComposite#StatableFilterComposite(AbstractQueryFilterComposite, int, XComposite.LayoutMode, LayoutDataMode)}
	 */
	public StatableFilterSearchComposite(Composite parent, int style,
		QueryProvider<? super StatableQuery> queryProvider)
	{
		super(parent, style, queryProvider);
		processDefinition2StateDefinitions = new HashMap<ProcessDefinition, List<StateDefinition>>();
		createComposite();
	}

	@Override
	public Class<StatableQuery> getQueryClass()
	{
		return StatableQuery.class;
	}

	@Override
	protected void createComposite()
	{
		Group group = new Group(this, SWT.NONE);
		group.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterComposite.group.text"));		 //$NON-NLS-1$
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		XComposite wrapper = new XComposite(group, SWT.NONE, LayoutMode.TIGHT_WRAPPER,
			LayoutDataMode.GRID_DATA_HORIZONTAL, 3);
		wrapper.getGridLayout().makeColumnsEqualWidth = true;
		wrapper.getGridData().horizontalSpan = 2;
		stateDefinitionActiveButton = new Button(wrapper, SWT.CHECK);
		stateDefinitionActiveButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterComposite.activeButton.text")); //$NON-NLS-1$
		stateDefinitionActiveButton.addSelectionListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				getQuery().setFieldEnabled(StatableQuery.FieldName.stateDefinitionID, active);
			}
		});
		onlyInSelectedStateButton = new Button(wrapper, SWT.CHECK);
		onlyInSelectedStateButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterComposite.onlyInSelectedStateButton.text")); //$NON-NLS-1$
		GridData selectedStateButtonData = new GridData();
//		selectedStateButtonData.horizontalSpan = 2;
		onlyInSelectedStateButton.setLayoutData(selectedStateButtonData);
		onlyInSelectedStateButton.setEnabled(false);
		onlyInSelectedStateButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final Boolean onlyInSelectedState = onlyInSelectedStateButton.getSelection();
				getQuery().setOnlyInSelectedState(onlyInSelectedState);
			}
		});

		notInSelectedStateButton = new Button(wrapper, SWT.CHECK);
		notInSelectedStateButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterSearchComposite.button.text.notInSelectedState")); //$NON-NLS-1$
		GridData notSelectedStateButtonData = new GridData();
//		selectedStateButtonData.horizontalSpan = 2;
		notInSelectedStateButton.setLayoutData(notSelectedStateButtonData);
		notInSelectedStateButton.setEnabled(false);
		notInSelectedStateButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final Boolean notInSelectedState = notInSelectedStateButton.getSelection();
				getQuery().setNotInSelectedState(notInSelectedState);
			}
		});

		processDefinitionsCombo = new XComboComposite<ProcessDefinition>(wrapper, SWT.READ_ONLY | getBorderStyle(), processDefinitionLabelProvider);
		GridData data2 = new GridData(GridData.FILL_HORIZONTAL);
		data2.horizontalSpan = 1;
		processDefinitionsCombo.setLayoutData(data2);
		processDefinitionsCombo.setEnabled(false);
		processDefinitionsCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent e)
			{
				ProcessDefinition selectedProcessDefinition = processDefinitionsCombo.getSelectedElement();
				List<StateDefinition> stateDefinitions = processDefinition2StateDefinitions.get(selectedProcessDefinition);
				stateDefinitionsCombo.removeAll();
				stateDefinitionsCombo.addElements(stateDefinitions);
				if (!stateDefinitionsCombo.getElements().isEmpty())
					stateDefinitionsCombo.selectElementByIndex(0);
			}
		});

		stateDefinitionsCombo = new XComboComposite<StateDefinition>(wrapper, SWT.READ_ONLY | getBorderStyle(), stateDefinitionLabelProvider);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		stateDefinitionsCombo.setLayoutData(data);
		stateDefinitionsCombo.setEnabled(false);
		stateDefinitionsCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent e)
			{
				final StateDefinitionID stateDefinitionID = (StateDefinitionID)
					JDOHelper.getObjectId(stateDefinitionsCombo.getSelectedElement());

				getQuery().setStateDefinitionID(stateDefinitionID);
			}
		});

		createDTMin = new DateTimeEdit(
				group,
				DateFormatter.FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY + DateTimeEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX,
				Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterComposite.createDateMin.caption")); //$NON-NLS-1$
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
		createDTMin.setDate(cal.getTime());
		createDTMin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createDTMin.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent me)
			{
				final Date createDTMinDate = createDTMin.getDate();
				getQuery().setStateCreateDTMin(createDTMinDate);
			}
		});
		createDTMin.addActiveChangeListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				getQuery().setFieldEnabled(StatableQuery.FieldName.stateCreateDTMin, active);
			}
		});

		createDTMax = new DateTimeEdit(
				group,
				DateFormatter.FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY + DateTimeEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX,
				Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterComposite.createDateMax.caption"));		 //$NON-NLS-1$
		cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));
		createDTMax.setDate(cal.getTime());
		createDTMax.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createDTMax.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent me)
			{
				final Date createDTMaxDate = createDTMax.getDate();
				getQuery().setStateCreateDTMax(createDTMaxDate);
			}
		});
		createDTMax.addActiveChangeListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				getQuery().setFieldEnabled(StatableQuery.FieldName.stateCreateDTMax, active);
			}
		});
	}

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


	private Class<? extends Statable> statableClass;

	private Job fillStateComboJob;

	/**
	 * Sets the class implementing {@link Statable} for which the states shall be retrieved and used
	 * for filtering.
	 *
	 * @param statableClass a class implementing Statable
	 */
	public void setStatableClass(Class<? extends Statable> statableClass)
	{
		assert statableClass != null;
		if (statableClass == this.statableClass)
			return;

		this.statableClass = statableClass;
		getQuery().setStatableClass(statableClass);
		final String statableClassName = statableClass.getName();

		fillStateComboJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterComposite.loadProcessDefinitionsJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) {
				try {
					TradeManagerRemote tradeManager = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
					JbpmManagerRemote jbpmManager = JFireEjb3Factory.getRemoteBean(JbpmManagerRemote.class, Login.getLogin().getInitialContextProperties());

					// TODO: add workflow selection so that only the processdefiniton for the given workflow
					Set<ProcessDefinitionID> processDefinitionIDs = tradeManager.getProcessDefinitionIDs(statableClassName);
					String[] PROCESS_DEFINITION_FETCH_GROUPS = new String[] {
							FetchPlan.DEFAULT,
							ProcessDefinition.FETCH_GROUP_THIS_PROCESS_DEFINITION
					};
					final Collection<ProcessDefinition> processDefinitions = ProcessDefinitionDAO.sharedInstance().getProcessDefinitions(
							processDefinitionIDs,
							PROCESS_DEFINITION_FETCH_GROUPS,
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							monitor);

					for (ProcessDefinition processDefinition : processDefinitions)
					{
						Set<StateDefinitionID> statedDefinitionIDs = jbpmManager.getStateDefinitionIDs(processDefinition);
						Collection<StateDefinition> stateDefinitions = StateDefinitionDAO.sharedInstance().getStateDefintions(
								statedDefinitionIDs,
								FETCH_GROUPS_STATE_DEFINITON,
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								monitor);
						// TODO: sort stateDefinitions
						processDefinition2StateDefinitions.put(processDefinition, new ArrayList<StateDefinition>(stateDefinitions));
					}


					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (stateDefinitionsCombo == null || stateDefinitionsCombo.isDisposed())
								return;

							processDefinitionsCombo.addElements(processDefinitions);
							if (!processDefinitions.isEmpty()) {
								ProcessDefinition firstProcessDefinition = processDefinitions.iterator().next();
								processDefinitionsCombo.selectElement(firstProcessDefinition);
								List<StateDefinition> stateDefinitionList = processDefinition2StateDefinitions.get(firstProcessDefinition);
								stateDefinitionsCombo.addElements(stateDefinitionList);
								if (!stateDefinitionList.isEmpty()) {
									stateDefinitionsCombo.selectElementByIndex(0);
								}
							}

							if (deferredSelectedStateDefinition != null) {
								stateDefinitionsCombo.selectElement(deferredSelectedStateDefinition);
								deferredSelectedStateDefinition = null;
							}
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

	private static final String STATABLE_GROUP_ID = "StatableFilterGroup"; //$NON-NLS-1$
	private static final Set<String> fieldNames;
	static
	{
		fieldNames = new HashSet<String>();
		fieldNames.add(StatableQuery.FieldName.onlyInSelectedState);
		fieldNames.add(StatableQuery.FieldName.notInSelectedState);
		fieldNames.add(StatableQuery.FieldName.stateCreateDTMax);
		fieldNames.add(StatableQuery.FieldName.stateCreateDTMin);
		fieldNames.add(StatableQuery.FieldName.stateDefinitionID);
	}

	@Override
	protected Set<String> getFieldNames()
	{
		return fieldNames;
	}

	@Override
	protected String getGroupID()
	{
		return STATABLE_GROUP_ID;
	}

	private StateDefinition deferredSelectedStateDefinition = null;
	private ProcessDefinition deferredProcessDefinition = null;

	@Override
	protected void updateUI(QueryEvent event, List<FieldChangeCarrier> changedFields)
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("This method must be invoked on the SWT UI thread!"); //$NON-NLS-1$

		for (FieldChangeCarrier fieldChange : changedFields)
		{
			if (StatableQuery.FieldName.onlyInSelectedState.equals(fieldChange.getPropertyName()))
			{
				onlyInSelectedStateButton.setSelection((Boolean) fieldChange.getNewValue());
			}
			else if (StatableQuery.FieldName.notInSelectedState.equals(fieldChange.getPropertyName()))
			{
				notInSelectedStateButton.setSelection((Boolean) fieldChange.getNewValue());
			}
			else if (StatableQuery.FieldName.stateCreateDTMax.equals(fieldChange.getPropertyName()))
			{
				Date maxDate = (Date) fieldChange.getNewValue();
				createDTMax.setDate(maxDate);
			}
			else if (getEnableFieldName(StatableQuery.FieldName.stateCreateDTMax).equals(
					fieldChange.getPropertyName()))
			{
				final Boolean active = (Boolean) fieldChange.getNewValue();
				createDTMax.setActive(active);
				setSearchSectionActive(active);
			}
			else if (StatableQuery.FieldName.stateCreateDTMin.equals(fieldChange.getPropertyName()))
			{
				Date minDate = (Date) fieldChange.getNewValue();
				createDTMin.setDate(minDate);
			}
			else if (getEnableFieldName(StatableQuery.FieldName.stateCreateDTMin).equals(
					fieldChange.getPropertyName()))
			{
				final Boolean active = (Boolean) fieldChange.getNewValue();
				createDTMin.setActive(active);
				setSearchSectionActive(active);
			}
			else if (StatableQuery.FieldName.stateDefinitionID.equals(fieldChange.getPropertyName()))
			{
				StateDefinitionID tmpID = (StateDefinitionID) fieldChange.getNewValue();
				if (tmpID == null)
				{
					stateDefinitionsCombo.setSelection((StateDefinition) null);
				}
				else
				{
					StateDefinition selection;
					try
					{
						selection = StateDefinitionDAO.sharedInstance().getStateDefintions(
							Collections.singleton(tmpID), FETCH_GROUPS_STATE_DEFINITON,
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
						).iterator().next();
					}
					catch (Exception e)
					{
						if (e instanceof RuntimeException)
						{
							throw (RuntimeException) e;
						}

						throw new RuntimeException(e);
					}

					stateDefinitionsCombo.setSelection(selection);
					if (!Util.equals(selection, stateDefinitionsCombo.getSelectedElement()))
						deferredSelectedStateDefinition = selection;
				}
			}
			else if (getEnableFieldName(StatableQuery.FieldName.stateDefinitionID).equals(
					fieldChange.getPropertyName()))
			{
				final Boolean active = (Boolean) fieldChange.getNewValue();
				stateDefinitionsCombo.setEnabled(active);
				onlyInSelectedStateButton.setEnabled(active);
				notInSelectedStateButton.setEnabled(active);
				processDefinitionsCombo.setEnabled(active);
				setSearchSectionActive(stateDefinitionActiveButton, active);
			}

			else if (StatableQuery.FieldName.processDefinitionID.equals(fieldChange.getPropertyName()))
			{
				ProcessDefinitionID tmpID = (ProcessDefinitionID) fieldChange.getNewValue();
				if (tmpID == null)
				{
					processDefinitionsCombo.setSelection((ProcessDefinition) null);
				}
				else
				{
					ProcessDefinition selection;
					try
					{
						selection = ProcessDefinitionDAO.sharedInstance().getProcessDefinitions(
							Collections.singleton(tmpID), FETCH_GROUPS_PROCESS_DEFINITON,
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
						).iterator().next();
					}
					catch (Exception e)
					{
						if (e instanceof RuntimeException)
						{
							throw (RuntimeException) e;
						}

						throw new RuntimeException(e);
					}

					processDefinitionsCombo.setSelection(selection);
					if (!Util.equals(selection, processDefinitionsCombo.getSelectedElement()))
						deferredProcessDefinition = selection;
				}
			}
			else if (getEnableFieldName(StatableQuery.FieldName.processDefinitionID).equals(
					fieldChange.getPropertyName()))
			{
				final Boolean active = (Boolean) fieldChange.getNewValue();
				stateDefinitionsCombo.setEnabled(active);
				onlyInSelectedStateButton.setEnabled(active);
				notInSelectedStateButton.setEnabled(active);
				processDefinitionsCombo.setEnabled(active);
				setSearchSectionActive(stateDefinitionActiveButton, active);
			}

		} // for (FieldChangeCarrier fieldChange : event.getChangedFields().values())
	}
}
