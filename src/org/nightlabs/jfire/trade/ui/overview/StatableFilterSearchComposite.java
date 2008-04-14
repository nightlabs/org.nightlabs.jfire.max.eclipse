package org.nightlabs.jfire.trade.ui.overview;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
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
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.jbpm.JbpmManager;
import org.nightlabs.jfire.jbpm.JbpmManagerUtil;
import org.nightlabs.jfire.jbpm.dao.ProcessDefinitionDAO;
import org.nightlabs.jfire.jbpm.dao.StateDefinitionDAO;
import org.nightlabs.jfire.jbpm.graph.def.ProcessDefinition;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.jbpm.graph.def.id.ProcessDefinitionID;
import org.nightlabs.jfire.jbpm.query.StatableQuery;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.state.id.StateDefinitionID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

/**
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class StatableFilterSearchComposite
	extends AbstractQueryFilterComposite<Statable, StatableQuery>
{
	private DateTimeEdit createDTMin;
	private DateTimeEdit createDTMax;
	private Button stateDefinitionActiveButton;
	private Button onlyInSelectedStateButton;
	private StateDefinitionID stateDefinitionID;
	private boolean onlyInSelectedState = false;
	private Date createDTMinDate;
	private Date createDTMaxDate;
	
	private static final String[] FETCH_GROUPS_STATE_DEFINITON = new String[] {
		FetchPlan.DEFAULT,
		StateDefinition.FETCH_GROUP_NAME
	};
 
	/**
	 * Creates a new {@link AbstractQueryFilterComposite}.
	 * <p><b>Note</b>: The caller has to call {@link #createComposite(Composite)} to create the UI! <br />
	 * 	This is not done in this constructor to omit problems with fields that are not only declared,
	 * 	but also initialised. If these fields are used inside {@link #createComposite(Composite)}
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
		LayoutDataMode layoutDataMode, QueryProvider<Statable, ? super StatableQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
		createComposite(this);
	}

	/**
	 * Delegates to {@link StatableFilterComposite#StatableFilterComposite(AbstractQueryFilterComposite, int, XComposite.LayoutMode, LayoutDataMode)}
	 */
	public StatableFilterSearchComposite(Composite parent, int style,
		QueryProvider<Statable, ? super StatableQuery> queryProvider)
	{
		super(parent, style, queryProvider);
		createComposite(this);
	}

	@Override
	public Class<StatableQuery> getQueryClass()
	{
		return StatableQuery.class;
	}

	@Override
	protected void createComposite(Composite parent)
	{
		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterComposite.group.text"));		 //$NON-NLS-1$
		group.setLayout(new GridLayout(4, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		XComposite wrapper = new XComposite(group, SWT.NONE, LayoutMode.TIGHT_WRAPPER,
			LayoutDataMode.GRID_DATA_HORIZONTAL, 2);
		wrapper.getGridLayout().makeColumnsEqualWidth = true;
		wrapper.getGridData().horizontalSpan = 2;
		stateDefinitionActiveButton = new Button(wrapper, SWT.CHECK);
		stateDefinitionActiveButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterComposite.activeButton.text")); //$NON-NLS-1$
		stateDefinitionActiveButton.addSelectionListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				if (active)
				{
					if (stateDefinitionID == null)
					{
						setInitialValue(true);
						getQuery().setOnlyInSelectedState(onlyInSelectedState);
						getQuery().setStateDefinitionID(stateDefinitionID);
						setInitialValue(false);
					}
					else
					{
						getQuery().setOnlyInSelectedState(onlyInSelectedState);
						getQuery().setStateDefinitionID(stateDefinitionID);
					}
				}
				else
				{
					getQuery().setOnlyInSelectedState(false);
					getQuery().setStateDefinitionID(null);
				}
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
				onlyInSelectedState = onlyInSelectedStateButton.getSelection();
				getQuery().setOnlyInSelectedState(onlyInSelectedState);
			}
		});
		
		stateDefinitions = new XComboComposite<StateDefinition>(wrapper, getBorderStyle(), labelProvider);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		stateDefinitions.setLayoutData(data);
		stateDefinitions.setEnabled(false);
		stateDefinitions.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent e)
			{
				stateDefinitionID = (StateDefinitionID) JDOHelper.getObjectId(stateDefinitions.getSelectedElement());
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
				createDTMinDate = createDTMin.getDate();
				getQuery().setStateCreateDTMin(createDTMinDate);
			}
		});
		createDTMin.addActiveChangeListener(new ButtonSelectionListener() 
		{
			@Override
			protected void handleSelection(boolean active)
			{
				if (active)
				{
					if (createDTMinDate == null)
					{
						setInitialValue(true);
						// for consistency we need to update the field according to the initial value of
						// the date edit composites.
						createDTMinDate = createDTMin.getDate();
						getQuery().setStateCreateDTMin(createDTMinDate);
						setInitialValue(false);
					}
					else
					{
						getQuery().setStateCreateDTMin(createDTMinDate);
					}
				}
				else
				{
					getQuery().setStateCreateDTMin(null);
				}
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
				createDTMaxDate = createDTMax.getDate();
				getQuery().setStateCreateDTMax(createDTMaxDate);
			}
		});
		createDTMax.addActiveChangeListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				if (active)
				{
					if (createDTMaxDate == null)
					{
						setInitialValue(true);
						// for consistency we need to update the field according to the initial value of
						// the date edit composites.
						createDTMaxDate = createDTMax.getDate();
						getQuery().setStateCreateDTMax(createDTMaxDate);
						setInitialValue(false);
					}
					else
					{
						getQuery().setStateCreateDTMax(createDTMaxDate);
					}
				}
				else
				{
					getQuery().setStateCreateDTMax(null);
				}
			}
		});
	}

	private XComboComposite<StateDefinition> stateDefinitions;
	private ILabelProvider labelProvider = new LabelProvider() {
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
	
	private Class<? extends Statable> statableClass;
	
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
		
		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterComposite.loadProcessDefinitionsJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) {
				try {
					TradeManager tradeManager =	TradePlugin.getDefault().getTradeManager();
					JbpmManager jbpmManager = JbpmManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
					
					Set<ProcessDefinitionID> processDefinitionIDs = tradeManager.getProcessDefinitionIDs(statableClassName);
					String[] PROCESS_DEFINITION_FETCH_GROUPS = new String[] {
							FetchPlan.DEFAULT,
							ProcessDefinition.FETCH_GROUP_THIS_PROCESS_DEFINITION
					};
					Collection<ProcessDefinition> processDefinitions = ProcessDefinitionDAO.sharedInstance().getProcessDefinitions(
							processDefinitionIDs,
							PROCESS_DEFINITION_FETCH_GROUPS,
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							monitor);
					
					final Set<StateDefinition> allStateDefinitions = new HashSet<StateDefinition>();
					for (ProcessDefinition processDefinition : processDefinitions)
					{
						Set<StateDefinitionID> statedDefinitionIDs = jbpmManager.getStateDefinitionIDs(processDefinition);
						Collection<StateDefinition> stateDefinitions = StateDefinitionDAO.sharedInstance().getStateDefintions(
								statedDefinitionIDs,
								FETCH_GROUPS_STATE_DEFINITON,
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								monitor);
						allStateDefinitions.addAll(stateDefinitions);
					}
					
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							if (stateDefinitions == null || stateDefinitions.isDisposed())
								return;

							stateDefinitions.addElements(allStateDefinitions);
						}
					});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
	
	@Override
	protected void resetSearchQueryValues(StatableQuery query)
	{
		query.setOnlyInSelectedState(onlyInSelectedState);
		query.setStateCreateDTMin(createDTMinDate);
		query.setStateCreateDTMax(createDTMaxDate);
		query.setStateDefinitionID(stateDefinitionID);
	}

	@Override
	protected void unsetSearchQueryValues(StatableQuery query)
	{
		if (! stateDefinitionActiveButton.getSelection())
		{
			stateDefinitionID = null;
			onlyInSelectedState = false;
		}
		if (! createDTMax.isActive())
		{
			createDTMaxDate = null;
		}
		if (! createDTMin.isActive())
		{
			createDTMinDate = null;
		}
		
		query.setOnlyInSelectedState(false);
		query.setStateCreateDTMin(null);
		query.setStateCreateDTMax(null);
		query.setStateDefinitionID(null);
	}

	@Override
	protected void updateUI(QueryEvent event)
	{
		if (event.getChangedQuery() == null)
		{
			onlyInSelectedState = false;
			onlyInSelectedStateButton.setSelection(false);
			stateDefinitionID = null;
			stateDefinitions.setSelection((StateDefinition) null);
			setSearchSectionActive(stateDefinitionActiveButton, false);

			createDTMinDate = null;
			createDTMin.setTimestamp(Calendar.getInstance().getTimeInMillis());
			if (createDTMin.isActive())
			{
				createDTMin.setActive(false);
				setSearchSectionActive(false);
			}

			createDTMaxDate = null;
			createDTMax.setTimestamp(Calendar.getInstance().getTimeInMillis());
			if (createDTMax.isActive())
			{
				createDTMax.setActive(false);
				setSearchSectionActive(false);
			}
		}
		else
		{
			for (FieldChangeCarrier fieldChange : event.getChangedFields())
			{
				boolean active = isInitialValue();
				if (StatableQuery.PROPERTY_ONLY_IN_SELECTED_STATE.equals(fieldChange.getPropertyName()))
				{
					onlyInSelectedStateButton.setSelection((Boolean) fieldChange.getNewValue());
				}
				
				if (StatableQuery.PROPERTY_STATE_CREATE_DATE_MAX.equals(fieldChange.getPropertyName()))
				{
					Date maxDate = (Date) fieldChange.getNewValue();
					createDTMax.setDate(maxDate);
					
					active |= maxDate != null;
					if (createDTMax.isActive() != active)
					{
						createDTMax.setActive(active);
						setSearchSectionActive(active);
					}
				}
				
				if (StatableQuery.PROPERTY_STATE_CREATE_DATE_MIN.equals(fieldChange.getPropertyName()))
				{
					Date minDate = (Date) fieldChange.getNewValue();
					createDTMin.setDate(minDate);
					active |= minDate != null;
					if (createDTMin.isActive() != active)
					{
						createDTMin.setActive(active);
						setSearchSectionActive(active);
					}
				}
				
				if (StatableQuery.PROPERTY_STATE_DEFINITION_ID.equals(fieldChange.getPropertyName()))
				{
					StateDefinitionID tmpID = (StateDefinitionID) fieldChange.getNewValue();
					if (tmpID == null)
					{
						stateDefinitions.setSelection((StateDefinition) null);
					}
					else
					{
						StateDefinition selection;
						try
						{
							selection = StateDefinitionDAO.sharedInstance().getStateDefintions(
								Collections.singleton(stateDefinitionID), FETCH_GROUPS_STATE_DEFINITON, 
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
						stateDefinitions.setSelection(selection);
					}
					
					active |= tmpID != null;
					stateDefinitions.setEnabled(active);
					onlyInSelectedStateButton.setEnabled(active);
					setSearchSectionActive(stateDefinitionActiveButton, active);
//					initialValue = false;
				}
			} // for (FieldChangeCarrier fieldChange : event.getChangedFields().values())
		} // else (changedQuery != null)
	}
	
}
