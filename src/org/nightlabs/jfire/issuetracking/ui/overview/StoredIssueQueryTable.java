package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.Collection;
import java.util.Iterator;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.OverviewEntryEditorInput;
import org.nightlabs.jfire.issue.config.IssueQueryConfigModule;
import org.nightlabs.jfire.issue.config.StoredIssueQuery;
import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.NullProgressMonitor;

public class StoredIssueQueryTable
extends AbstractTableComposite<StoredIssueQuery>
{
	public StoredIssueQueryTable(Composite parent, int style) {
		super(parent, style);

		loadStoredIssueQueries();

		JDOLifecycleManager.sharedInstance().addNotificationListener(
				IssueQueryConfigModule.class, myListener);

		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event)
			{
				JDOLifecycleManager.sharedInstance().removeNotificationListener(
						IssueQueryConfigModule.class, myListener);
			}
		});
	}

	private NotificationListener myListener = new NotificationAdapterJob() {
		public void notify(org.nightlabs.notification.NotificationEvent notificationEvent) {
			for (Iterator<DirtyObjectID> it = notificationEvent.getSubjects().iterator(); it.hasNext(); ) {
				DirtyObjectID dirtyObjectID = it.next();

				switch (dirtyObjectID.getLifecycleState()) {
				case DIRTY:
					loadStoredIssueQueries();
					break;
				case DELETED:
					// - remove the object from the UI
					break;
				}
			}
		}
	};

	private void loadStoredIssueQueries(){
		IssueQueryConfigModule cfMod = (IssueQueryConfigModule)ConfigUtil.getUserCfMod(
				IssueQueryConfigModule.class,
				new String[] {FetchPlan.DEFAULT, IssueQueryConfigModule.FETCH_GROUP_STOREDISSUEQUERRYLIST, StoredIssueQuery.FETCH_GROUP_STOREDISSUEQUERY},
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new NullProgressMonitor());

		final Collection<StoredIssueQuery> storedIssueQueries = cfMod.getStoredIssueQueryList();

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {					
				setInput(storedIssueQueries);
			}
		});
	}

	@Override
	protected void createTableColumns(final TableViewer tableViewer, Table table) {
		TableLayout layout = new TableLayout();

		TableColumn tc = new TableColumn(table, SWT.NONE);
		tc.setResizable(true);
		layout.addColumnData(new ColumnWeightData(100));

		table.setLayout(layout);
		table.setHeaderVisible(false);

		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(final DoubleClickEvent e) {
				StructuredSelection s = (StructuredSelection)e.getSelection();
				if (s.isEmpty())
					return;

				try {
					Entry entry = IssueOverviewRegistry.sharedInstance().getEntryFactory(IssueEntryListFactory.ID).createEntry();
					final IssueEntryListEditor part = (IssueEntryListEditor)RCPUtil.openEditor(
							new OverviewEntryEditorInput(entry), 
							IssueEntryListEditor.EDITOR_ID,
							true
					);

					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							IssueEntryListViewer viewer = (IssueEntryListViewer)part.getEntryViewer();
							viewer.expand();

							StructuredSelection ss = (StructuredSelection)e.getSelection();

							IssueFilterComposite searchComposite = (IssueFilterComposite)viewer.getSearchComposite();
							searchComposite.setStoredIssueQuery((StoredIssueQuery)ss.getFirstElement());

							viewer.search();
						}
					});

				} catch (PartInitException e1) {
					throw new RuntimeException(e1);
				}
			}
		});
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new StoredIssueQueryLabelProvider());
	}

	class StoredIssueQueryLabelProvider
	extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex) 
		{
			if (element instanceof StoredIssueQuery) {
				StoredIssueQuery storedIssueQuery = (StoredIssueQuery) element;
				switch (columnIndex) 
				{
				case(0):
					return storedIssueQuery.getName();
				default:
					return ""; //$NON-NLS-1$
				}
			}
			return null;
		}		
	}
}
