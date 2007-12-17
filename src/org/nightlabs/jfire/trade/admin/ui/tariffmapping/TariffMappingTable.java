package org.nightlabs.jfire.trade.admin.ui.tariffmapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

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
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.accounting.TariffMapping;
import org.nightlabs.jfire.accounting.dao.TariffMappingDAO;
import org.nightlabs.jfire.accounting.id.TariffID;
import org.nightlabs.jfire.accounting.id.TariffMappingID;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleEvent;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleListener;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.jdo.notification.JDOLifecycleAdapterJob;
import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.jfire.jdo.notification.IJDOLifecycleListenerFilter;
import org.nightlabs.jfire.jdo.notification.JDOLifecycleState;
import org.nightlabs.jfire.jdo.notification.SimpleLifecycleListenerFilter;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

public class TariffMappingTable
extends AbstractTableComposite
{
	private static class MyLabelProvider extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex)
		{
			if (!(element instanceof TariffMapping)) {
				if (columnIndex == 0)
					return String.valueOf(element);

				return ""; //$NON-NLS-1$
			}

			TariffMapping tariffMapping = (TariffMapping) element;

			switch (columnIndex) {
				case 0:
					return tariffMapping.getPartnerTariffOrganisationID();
				case 1:
					return tariffMapping.getPartnerTariff().getName().getText();
				case 2:
					return tariffMapping.getLocalTariff().getName().getText();
				default:
					return ""; //$NON-NLS-1$
			}
		}
	}

	public TariffMappingTable(Composite parent, int style)
	{
		super(parent, style);

		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				unregisterJDOLifecycleListener();
			}
		});
	}

	@Override
	@Implement
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		TableColumn tc;

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.tariffmapping.TariffMappingTable.partnerOrganisationIDTableColumn.text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.tariffmapping.TariffMappingTable.partnerTariffTableColumn.text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.tariffmapping.TariffMappingTable.localTariffTableColumn.text")); //$NON-NLS-1$

		TableLayout tl = new TableLayout();
		tl.addColumnData(new ColumnWeightData(50));
		tl.addColumnData(new ColumnWeightData(50));
		tl.addColumnData(new ColumnWeightData(50));
		table.setLayout(tl);
	}

	@Override
	@Implement
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setLabelProvider(new MyLabelProvider());
		tableViewer.setContentProvider(new TableContentProvider());
	}

	public static final String[] FETCH_GROUPS_TARIFF_MAPPING = {
		FetchPlan.DEFAULT, TariffMapping.FETCH_GROUP_LOCAL_TARIFF, TariffMapping.FETCH_GROUP_PARTNER_TARIFF,
		Tariff.FETCH_GROUP_NAME
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
			// there exist neither subclasses of TariffMapping nor can a TariffMapping be changed - therefore this is sufficient. We might later add a Change-Listener (implicit) in case the name of a Tariff is changed, but currently, this is not very important
			private IJDOLifecycleListenerFilter filter = new SimpleLifecycleListenerFilter(TariffMapping.class, false, new JDOLifecycleState[] { JDOLifecycleState.NEW });
			
			@Implement
			public IJDOLifecycleListenerFilter getJDOLifecycleListenerFilter()
			{
				return filter;
			}

			@Implement
			public void notify(JDOLifecycleEvent event)
			{
				Set<TariffMappingID> tariffMappingIDs = new HashSet<TariffMappingID>();
				for (DirtyObjectID dirtyObjectID : event.getDirtyObjectIDs()) {
					if (dirtyObjectID.getLifecycleState() != JDOLifecycleState.NEW)
						throw new IllegalStateException("Why the hell do I get this DirtyObjectID?!"); //$NON-NLS-1$

					tariffMappingIDs.add((TariffMappingID) dirtyObjectID.getObjectID());
				}

				final List<TariffMapping> _tariffMappings = TariffMappingDAO.sharedInstance().getTariffMappings(
						tariffMappingIDs, FETCH_GROUPS_TARIFF_MAPPING, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new ProgressMonitorWrapper(getProgressMonitor()));
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						tariffMappings.removeAll(_tariffMappings);
						tariffMappings.addAll(_tariffMappings);
						refresh();
					}
				});
			}
		};

		JDOLifecycleManager.sharedInstance().addLifecycleListener(jdoLifecycleListener);
	}

	private Set<TariffMapping> tariffMappings;

	public Set<TariffMapping> getTariffMappings(boolean blockUntilLoaded)
	{
		synchronized (loadingTariffMappingsMutex) {
			if (blockUntilLoaded) {
				while (loadingTariffMappings) {
					try {
						loadingTariffMappingsMutex.wait(60000);
					} catch (InterruptedException e) {
						// ignore
					}
				} // while (loadingTariffMappings) {
			} // if (blockUntilLoaded) {

			if (tariffMappings == null)
				return new HashSet<TariffMapping>(0);

			return new HashSet<TariffMapping>(tariffMappings);
		}
	}

	private boolean loadingTariffMappings = false;
	private Object loadingTariffMappingsMutex = new Object();

	private void setLoadingTariffMappings(boolean value)
	{
		synchronized (loadingTariffMappingsMutex) {
			this.loadingTariffMappings = value;
			loadingTariffMappingsMutex.notifyAll();
		}
	}

	public void loadTariffMappings()
	{
		boolean error1 = true;
		setLoadingTariffMappings(true);
		try {
			tariffMappings = null;
			setInput(Messages.getString("org.nightlabs.jfire.trade.admin.ui.tariffmapping.TariffMappingTable.inputPseudoEntry_loading")); //$NON-NLS-1$
	
			org.nightlabs.base.ui.job.Job job = new org.nightlabs.base.ui.job.Job(Messages.getString("org.nightlabs.jfire.trade.admin.ui.tariffmapping.TariffMappingTable.loadTariffMappingsJob.name")) { //$NON-NLS-1$
				@Override
				@Implement
				protected IStatus run(ProgressMonitor monitor)
				{
					boolean error2 = true;
					try {
	
						final List<TariffMapping> _tariffMappings = TariffMappingDAO.sharedInstance().getTariffMappings(
								FETCH_GROUPS_TARIFF_MAPPING, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		
						Display.getDefault().asyncExec(new Runnable()
						{
							public void run()
							{
								try {
									if (isDisposed())
										return;

									tariffMappings = new HashSet<TariffMapping>(_tariffMappings);
									setInput(tariffMappings);
									registerJDOLifecycleListener();
								} finally {
									setLoadingTariffMappings(false);
								}
							}
						});
		
						error2 = false;
						return Status.OK_STATUS;
					} finally {
						if (error2)
							setLoadingTariffMappings(false);
					}
				}
			};
			job.setPriority(Job.SHORT);
			job.schedule();
			error1 = false;
		} finally {
			if (error1)
				setLoadingTariffMappings(false);
		}
	}

	public void addClientOnlyTariffMapping(TariffMapping tariffMapping)
	{
		if (tariffMappings == null)
			return;

		if (!tariffMappings.contains(tariffMapping)) {
			tariffMappings.add(tariffMapping);
			refresh();
//			setInput(tariffMappings);
		}
	}

	public void removeClientOnlyTariffMappings(Collection<TariffMapping> tariffMappingsToDelete)
	{
		tariffMappings.removeAll(tariffMappingsToDelete);
		refresh();
	}

	public void storeClientOnlyTariffMappingsToServer()
	{
		final ArrayList<TariffMapping> _tariffMappings = new ArrayList<TariffMapping>(tariffMappings);

		org.nightlabs.base.ui.job.Job job = new org.nightlabs.base.ui.job.Job(Messages.getString("org.nightlabs.jfire.trade.admin.ui.tariffmapping.TariffMappingTable.storeTariffMappingsJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor)
			{
				for (TariffMapping tariffMapping : _tariffMappings) {
					if (JDOHelper.getObjectId(tariffMapping) != null) // already on server
						continue;

					TariffID partnerTariffID = (TariffID) JDOHelper.getObjectId(tariffMapping.getPartnerTariff());
					TariffID localTariffID = (TariffID) JDOHelper.getObjectId(tariffMapping.getLocalTariff());

					TariffMappingDAO.sharedInstance().createTariffMapping_new(localTariffID, partnerTariffID, false, null, 1, monitor);
				}

				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						loadTariffMappings();
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();
	}
}
