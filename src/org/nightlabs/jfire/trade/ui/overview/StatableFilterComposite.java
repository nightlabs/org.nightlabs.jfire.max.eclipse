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
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.overview.search.JDOQueryComposite;
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
import org.nightlabs.util.Util;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
@Deprecated
public class StatableFilterComposite
	extends JDOQueryComposite<Statable, StatableQuery>
{
	private DateTimeEdit createDTMin = null;
	private DateTimeEdit createDTMax = null;
	private Button activeButton = null;
	private Button onlyInSelectedStateButton = null;

	private static final String[] FETCH_GROUPS_STATE_DEFINITON = new String[] {
		FetchPlan.DEFAULT,
		StateDefinition.FETCH_GROUP_NAME
	};
 
	public StatableFilterComposite(
		AbstractQueryFilterComposite<Statable, StatableQuery> filterComposite, 
		int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode)
	{
		super(filterComposite, style, layoutMode, layoutDataMode);
		createComposite(this);
	}

	public StatableFilterComposite(AbstractQueryFilterComposite<Statable, StatableQuery> filterComposite,
		int style)
	{
		super(filterComposite, style);
		createComposite(this);
	}
	
	@Override
	protected void createComposite(Composite parent)
	{
		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterComposite.group.text"));		 //$NON-NLS-1$
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Composite wrapper = new XComposite(group, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		wrapper.setLayout(new GridLayout(3, true));
		activeButton = new Button(wrapper, SWT.CHECK);
		activeButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterComposite.activeButton.text")); //$NON-NLS-1$
		activeButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final boolean active = ((Button)e.getSource()).getSelection();
				stateDefinitions.setEnabled(active);
				onlyInSelectedStateButton.setEnabled(active);
				setSearchSectionActive(active);
				
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				if (active)
				{
					getQuery().setOnlyInSelectedState(onlyInSelectedState);
					getQuery().setStateDefinitionID(stateDefinitionID);
				}
				else
				{
					getQuery().setOnlyInSelectedState(false);
					getQuery().setStateDefinitionID(null);
				}
				setUIChangedQuery(false);
			}
		});
		onlyInSelectedStateButton = new Button(wrapper, SWT.CHECK);
		onlyInSelectedStateButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterComposite.onlyInSelectedStateButton.text")); //$NON-NLS-1$
		GridData selectedStateButtonData = new GridData();
		selectedStateButtonData.horizontalSpan = 2;
		onlyInSelectedStateButton.setLayoutData(selectedStateButtonData);
		onlyInSelectedStateButton.setEnabled(false);
		onlyInSelectedStateButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				onlyInSelectedState = onlyInSelectedStateButton.getSelection();
				
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				getQuery().setOnlyInSelectedState(onlyInSelectedState);
				setUIChangedQuery(false);
			}
		});
		
		stateDefinitions = new XComboComposite<StateDefinition>(wrapper, SWT.NONE, labelProvider);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		stateDefinitions.setLayoutData(data);
		stateDefinitions.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent e)
			{
				stateDefinitionID = (StateDefinitionID) JDOHelper.getObjectId(stateDefinitions.getSelectedElement());
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				getQuery().setStateDefinitionID(stateDefinitionID);
				setUIChangedQuery(false);
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
		createDTMin.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent me)
			{
				if (isUpdatingUI())
					return;
				
				createDTMinDate = createDTMin.getDate();
				setUIChangedQuery(true);
				getQuery().setStateCreateDTMin(createDTMinDate);
				setUIChangedQuery(false);
			}
		});
		createDTMin.addActiveChangeListener(new SelectionAdapter() 
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final boolean active = ((Button) e.getSource()).getSelection();
				setSearchSectionActive(active);
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				if (active)
				{
					getQuery().setStateCreateDTMin(createDTMin.getDate());
				}
				else
				{
					getQuery().setStateCreateDTMin(null);
				}
				setUIChangedQuery(false);
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
		createDTMax.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent me)
			{
				if (isUpdatingUI())
					return;
				
				createDTMaxDate = createDTMax.getDate();
				setUIChangedQuery(true);
				getQuery().setStateCreateDTMax(createDTMaxDate);
				setUIChangedQuery(false);
			}
		});
		createDTMax.addActiveChangeListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final boolean active = ((Button) e.getSource()).getSelection();
				setSearchSectionActive(active);
				
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				if (active)
				{
					getQuery().setStateCreateDTMax(createDTMax.getDate());
				}
				else
				{
					getQuery().setStateCreateDTMax(null);
				}
				setUIChangedQuery(false);
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
	public Class<? extends Statable> getStatableClass() {
		return statableClass;
	}
	public void setStatableClass(final Class<? extends Statable> statableClass)
	{
		assert statableClass != null;
		if (statableClass == this.statableClass)
			return;
		
		this.statableClass = statableClass;
		getQuery().setStatableClass(statableClass);
		
		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterComposite.loadProcessDefinitionsJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) {
				String statableClassName = statableClass.getName();
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

	private StateDefinitionID stateDefinitionID;
	private boolean onlyInSelectedState = false;
	private Date createDTMinDate;
	private Date createDTMaxDate;
	
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
		query.setOnlyInSelectedState(false);
		query.setStateCreateDTMin(null);
		query.setStateCreateDTMax(null);
		query.setStateDefinitionID(null);
	}

	@Override
	protected void updateUI(QueryEvent event)
	{
		if (uIChangedQuery())
			return;
		
		if (event.getChangedQuery() == null)
		{
			stateDefinitions.setSelection((StateDefinition) null);
			onlyInSelectedStateButton.setSelection(false);
			activeButton.setSelection(false);
			createDTMin.setTimestamp(Calendar.getInstance().getTimeInMillis());
			createDTMin.setActive(false);
			createDTMax.setTimestamp(Calendar.getInstance().getTimeInMillis());
			createDTMax.setActive(false);
		}
		else
		{
			for (FieldChangeCarrier fieldChange : event.getChangedFields())
			{
				if (StatableQuery.PROPERTY_ONLY_IN_SELECTED_STATE.equals(fieldChange.getPropertyName()))
				{
					onlyInSelectedStateButton.setSelection((Boolean) fieldChange.getNewValue());
				}
				
				if (StatableQuery.PROPERTY_STATE_CREATE_DATE_MAX.equals(fieldChange.getPropertyName()))
				{
					if (fieldChange.getNewValue() == null)
					{
						createDTMax.setTimestamp(Calendar.getInstance().getTimeInMillis());
						createDTMax.setActive(false);
					}
					else
					{
						createDTMax.setDate((Date) fieldChange.getNewValue());
						createDTMax.setActive(true);
					}
				}
				
				if (StatableQuery.PROPERTY_STATE_CREATE_DATE_MIN.equals(fieldChange.getPropertyName()))
				{
					if (fieldChange.getNewValue() == null)
					{
						createDTMin.setTimestamp(Calendar.getInstance().getTimeInMillis());
						createDTMin.setActive(false);
					}
					else
					{
						createDTMin.setDate((Date) fieldChange.getNewValue());
						createDTMin.setActive(true);
					}
				}
				
				if (StatableQuery.PROPERTY_STATE_DEFINITION_ID.equals(fieldChange.getPropertyName()))
				{
					StateDefinitionID tmpID = (StateDefinitionID) fieldChange.getNewValue();
					if (! Util.equals(stateDefinitionID, tmpID))
					{
						if (tmpID == null)
						{
							stateDefinitions.setSelection((StateDefinition) null);
							activeButton.setSelection(false);
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
							} catch (Exception e)
							{
								if (e instanceof RuntimeException)
								{
									throw (RuntimeException) e;
								}
								
								throw new RuntimeException(e);
							}
							stateDefinitions.setSelection(selection);
						}
					}
				}
			} // for (FieldChangeCarrier fieldChange : event.getChangedFields().values())
		} // else (changedQuery != null)
	}
	
}
