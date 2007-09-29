package org.nightlabs.jfire.trade.admin.ui.customergroupmapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleEvent;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleListener;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.jdo.notification.JDOLifecycleAdapterJob;
import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.jfire.jdo.notification.IJDOLifecycleListenerFilter;
import org.nightlabs.jfire.jdo.notification.JDOLifecycleState;
import org.nightlabs.jfire.jdo.notification.SimpleLifecycleListenerFilter;
import org.nightlabs.jfire.trade.CustomerGroup;
import org.nightlabs.jfire.trade.CustomerGroupMapping;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.id.CustomerGroupID;
import org.nightlabs.jfire.trade.id.CustomerGroupMappingID;
import org.nightlabs.jfire.trade.ui.customergroupmapping.CustomerGroupMappingDAO;

public class CustomerGroupMappingTable
extends AbstractTableComposite
{
	private static class MyLabelProvider extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex)
		{
			if (!(element instanceof CustomerGroupMapping)) {
				if (columnIndex == 0)
					return String.valueOf(element);

				return ""; //$NON-NLS-1$
			}

			CustomerGroupMapping customerGroupMapping = (CustomerGroupMapping) element;

			switch (columnIndex) {
				case 0:
					return customerGroupMapping.getPartnerCustomerGroupOrganisationID();
				case 1:
					return customerGroupMapping.getPartnerCustomerGroup().getName().getText();
				case 2:
					return customerGroupMapping.getLocalCustomerGroup().getName().getText();
				default:
					return ""; //$NON-NLS-1$
			}
		}
	}

	public CustomerGroupMappingTable(Composite parent, int style)
	{
		super(parent, style);

		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				unregisterJDOLifecycleListener();
			}
		});
	}

	@Implement
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		TableColumn tc;

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.customergroupmapping.CustomerGroupMappingTable.partnerOrganisationIDTableColumn.text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.customergroupmapping.CustomerGroupMappingTable.partnerCustomerGroupTableColumn.text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.customergroupmapping.CustomerGroupMappingTable.localCustomerGroupTableColumn.text")); //$NON-NLS-1$

		TableLayout tl = new TableLayout();
		tl.addColumnData(new ColumnWeightData(50));
		tl.addColumnData(new ColumnWeightData(50));
		tl.addColumnData(new ColumnWeightData(50));
		table.setLayout(tl);
	}

	@Implement
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setLabelProvider(new MyLabelProvider());
		tableViewer.setContentProvider(new TableContentProvider());
	}

	public static final String[] FETCH_GROUPS_TARIFF_MAPPING = {
		FetchPlan.DEFAULT, CustomerGroupMapping.FETCH_GROUP_LOCAL_CUSTOMER_GROUP, CustomerGroupMapping.FETCH_GROUP_PARTNER_CUSTOMER_GROUP,
		CustomerGroup.FETCH_GROUP_NAME
	};

	private JDOLifecycleListener jdoLifecycleListener = null;

	protected synchronized void unregisterJDOLifecycleListener()
	{
		if (jdoLifecycleListener == null)
			return;

		JDOLifecycleManager.sharedInstance().removeLifecycleListener(jdoLifecycleListener);
		jdoLifecycleListener = null;
	}

	protected synchronized void registerJDOLifecycleListener()
	{
		if (jdoLifecycleListener != null)
			return;

		jdoLifecycleListener = new JDOLifecycleAdapterJob() {
			// there exist neither subclasses of CustomerGroupMapping nor can a CustomerGroupMapping be changed - therefore this is sufficient. We might later add a Change-Listener (implicit) in case the name of a CustomerGroup is changed, but currently, this is not very important
			private IJDOLifecycleListenerFilter filter = new SimpleLifecycleListenerFilter(CustomerGroupMapping.class, false, new JDOLifecycleState[] { JDOLifecycleState.NEW });
			
			@Implement
			public IJDOLifecycleListenerFilter getJDOLifecycleListenerFilter()
			{
				return filter;
			}

			@Implement
			public void notify(JDOLifecycleEvent event)
			{
				Set<CustomerGroupMappingID> customerGroupMappingIDs = new HashSet<CustomerGroupMappingID>();
				for (DirtyObjectID dirtyObjectID : event.getDirtyObjectIDs()) {
					if (dirtyObjectID.getLifecycleState() != JDOLifecycleState.NEW)
						throw new IllegalStateException("Why the hell do I get this DirtyObjectID?!"); //$NON-NLS-1$

					customerGroupMappingIDs.add((CustomerGroupMappingID) dirtyObjectID.getObjectID());
				}

				final List<CustomerGroupMapping> _customerGroupMappings = CustomerGroupMappingDAO.sharedInstance().getCustomerGroupMappings(customerGroupMappingIDs, FETCH_GROUPS_TARIFF_MAPPING, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, getProgressMonitor());
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						customerGroupMappings.removeAll(_customerGroupMappings);
						customerGroupMappings.addAll(_customerGroupMappings);
						refresh();
					}
				});
			}
		};

		JDOLifecycleManager.sharedInstance().addLifecycleListener(jdoLifecycleListener);
	}

	private Set<CustomerGroupMapping> customerGroupMappings;

	public Set<CustomerGroupMapping> getCustomerGroupMappings(boolean blockUntilLoaded)
	{
		synchronized (loadingCustomerGroupMappingsMutex) {
			if (blockUntilLoaded) {
				while (loadingCustomerGroupMappings) {
					try {
						loadingCustomerGroupMappingsMutex.wait(60000);
					} catch (InterruptedException e) {
						// ignore
					}
				} // while (loadingCustomerGroupMappings) {
			} // if (blockUntilLoaded) {

			if (customerGroupMappings == null)
				return new HashSet<CustomerGroupMapping>(0);

			return new HashSet<CustomerGroupMapping>(customerGroupMappings);
		}
	}

	private boolean loadingCustomerGroupMappings = false;
	private Object loadingCustomerGroupMappingsMutex = new Object();

	private void setLoadingCustomerGroupMappings(boolean value)
	{
		synchronized (loadingCustomerGroupMappingsMutex) {
			this.loadingCustomerGroupMappings = value;
			loadingCustomerGroupMappingsMutex.notifyAll();
		}
	}

	public void loadCustomerGroupMappings()
	{
		boolean error1 = true;
		setLoadingCustomerGroupMappings(true);
		try {
			customerGroupMappings = null;
			setInput(Messages.getString("org.nightlabs.jfire.trade.admin.ui.customergroupmapping.CustomerGroupMappingTable.inputPseudoEntry_loading")); //$NON-NLS-1$
	
			Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.admin.ui.customergroupmapping.CustomerGroupMappingTable.loadCustomerGroupMappingsJob.name")) { //$NON-NLS-1$
				@Implement
				protected IStatus run(IProgressMonitor monitor)
				{
					boolean error2 = true;
					try {
	
						final List<CustomerGroupMapping> _customerGroupMappings = CustomerGroupMappingDAO.sharedInstance().getCustomerGroupMappings(
								FETCH_GROUPS_TARIFF_MAPPING, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		
						Display.getDefault().asyncExec(new Runnable()
						{
							public void run()
							{
								try {
									if (isDisposed())
										return;

									customerGroupMappings = new HashSet<CustomerGroupMapping>(_customerGroupMappings);
									setInput(customerGroupMappings);
									registerJDOLifecycleListener();
								} finally {
									setLoadingCustomerGroupMappings(false);
								}
							}
						});
		
						error2 = false;
						return Status.OK_STATUS;
					} finally {
						if (error2)
							setLoadingCustomerGroupMappings(false);
					}
				}
			};
			job.setPriority(Job.SHORT);
			job.schedule();
			error1 = false;
		} finally {
			if (error1)
				setLoadingCustomerGroupMappings(false);
		}
	}

	public void addClientOnlyCustomerGroupMapping(CustomerGroupMapping customerGroupMapping)
	{
		if (customerGroupMappings == null)
			return;

		if (!customerGroupMappings.contains(customerGroupMapping)) {
			customerGroupMappings.add(customerGroupMapping);
			refresh();
//			setInput(customerGroupMappings);
		}
	}

	public void removeClientOnlyCustomerGroupMappings(Collection<CustomerGroupMapping> customerGroupMappingsToDelete)
	{
		customerGroupMappings.removeAll(customerGroupMappingsToDelete);
		refresh();
	}

	public void storeClientOnlyCustomerGroupMappingsToServer()
	{
		final ArrayList<CustomerGroupMapping> _customerGroupMappings = new ArrayList<CustomerGroupMapping>(customerGroupMappings);

		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.admin.ui.customergroupmapping.CustomerGroupMappingTable.storeCustomerGroupMappingsJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				for (CustomerGroupMapping customerGroupMapping : _customerGroupMappings) {
					if (JDOHelper.getObjectId(customerGroupMapping) != null) // already on server
						continue;

					CustomerGroupID partnerCustomerGroupID = (CustomerGroupID) JDOHelper.getObjectId(customerGroupMapping.getPartnerCustomerGroup());
					CustomerGroupID localCustomerGroupID = (CustomerGroupID) JDOHelper.getObjectId(customerGroupMapping.getLocalCustomerGroup());

					CustomerGroupMappingDAO.sharedInstance().createCustomerGroupMapping(partnerCustomerGroupID, localCustomerGroupID, false, null, 1, monitor);
				}

				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						loadCustomerGroupMappings();
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();
	}
}
