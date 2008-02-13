package org.nightlabs.jfire.trade.ui.overview;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.nightlabs.base.ui.composite.ComboComposite;
import org.nightlabs.base.ui.composite.DateTimeEdit;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.JDOQuery;
import org.nightlabs.jdo.ui.JDOQueryComposite;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.jbpm.JbpmManager;
import org.nightlabs.jfire.jbpm.JbpmManagerUtil;
import org.nightlabs.jfire.jbpm.dao.ProcessDefinitionDAO;
import org.nightlabs.jfire.jbpm.dao.StateDefinitionDAO;
import org.nightlabs.jfire.jbpm.graph.def.ProcessDefinition;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.jbpm.graph.def.id.ProcessDefinitionID;
import org.nightlabs.jfire.jbpm.query.StatableQuery;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.state.id.StateDefinitionID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class StatableFilterComposite
extends JDOQueryComposite
{
	private DateTimeEdit createDTMin = null;
	private DateTimeEdit createDTMax = null;
	private StatableQuery statableQuery = null;
	private Button activeButton = null;
	private Button onlyInSelectedStateButton = null;
	
	public StatableFilterComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode)
	{
		super(parent, style, layoutMode, layoutDataMode);
		createComposite(this);
	}

	public StatableFilterComposite(Composite parent, int style) {
		super(parent, style);
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
		wrapper.setLayout(new GridLayout(2, true));
		activeButton = new Button(wrapper, SWT.CHECK);
		activeButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterComposite.activeButton.text")); //$NON-NLS-1$
		activeButton.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				stateDefinitions.setEnabled(((Button)e.getSource()).getSelection());
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		onlyInSelectedStateButton = new Button(wrapper, SWT.CHECK);
		onlyInSelectedStateButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterComposite.onlyInSelectedStateButton.text")); //$NON-NLS-1$
		onlyInSelectedStateButton.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		stateDefinitions = new ComboComposite<StateDefinition>(
				wrapper, SWT.NONE, labelProvider);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		stateDefinitions.setLayoutData(data);
		stateDefinitions.setEnabled(false);
		
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
	}

	private ComboComposite<StateDefinition> stateDefinitions;
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
	
	@Override
	public JDOQuery getJDOQuery()
	{
		statableQuery = new StatableQuery(getStatableClass());
		StateDefinitionID stateDefinitionID = (StateDefinitionID) JDOHelper.getObjectId(stateDefinitions.getSelectedElement());
		if (statableQuery != null)
		{
			if (activeButton.getSelection() && stateDefinitionID != null)
				statableQuery.setStateDefinitionID(stateDefinitionID);

			if (onlyInSelectedStateButton.getSelection())
				statableQuery.setOnlyInSelectedState(onlyInSelectedStateButton.getSelection());
			
			if (createDTMax.isActive())
				statableQuery.setStateCreateDTMax(createDTMax.getDate());
			
			if (createDTMin.isActive())
				statableQuery.setStateCreateDTMin(createDTMin.getDate());
		}
		return statableQuery;
	}
		
	private Class statableClass;
	public Class getStatableClass() {
		return statableClass;
	}
	public void setStatableClass(final Class statableClass)
	{
		this.statableClass = statableClass;
		if (statableQuery == null)
			statableQuery = new StatableQuery(statableClass);
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
					
					String[] STATE_DEFINITION_FETCH_GROUPS = new String[] {
							FetchPlan.DEFAULT,
							StateDefinition.FETCH_GROUP_NAME
					};
					final Set<StateDefinition> allStateDefinitions = new HashSet<StateDefinition>();
					for (ProcessDefinition processDefinition : processDefinitions)
					{
						Set<StateDefinitionID> statedDefinitionIDs = jbpmManager.getStateDefinitionIDs(processDefinition);
						Collection<StateDefinition> stateDefinitions = StateDefinitionDAO.sharedInstance().getStateDefintions(
								statedDefinitionIDs,
								STATE_DEFINITION_FETCH_GROUPS,
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								monitor);
						allStateDefinitions.addAll(stateDefinitions);
					}
					
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (stateDefinitions.isDisposed())
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
	
}
