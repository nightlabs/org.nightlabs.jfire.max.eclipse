package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.Collection;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.OverviewEntryEditorInput;
import org.nightlabs.jfire.base.ui.querystore.BaseQueryStoreTableComposite;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.query.store.BaseQueryStore;
import org.nightlabs.jfire.query.store.dao.QueryStoreDAO;
import org.nightlabs.progress.ProgressMonitor;

public class StoredIssueQueryTable
	extends BaseQueryStoreTableComposite // AbstractTableComposite<BaseQueryStore<?, ?>>
{
	public static final String[] FETCHGROUPS_BASEQUERYSTORE = new String[] {
		BaseQueryStore.FETCH_GROUP_OWNER, FetchPlan.DEFAULT
	};
	
	public StoredIssueQueryTable(Composite parent, int style) {
		super(parent, style);

		loadStoredIssueQueries();
	}

	private void loadStoredIssueQueries()
	{
		Job loadSavedIssueQueries = new Job("Loading saved Issue queries.")
		{
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				final Collection<BaseQueryStore<?, ?>> savedIssueQueries = 
					QueryStoreDAO.sharedInstance().getQueryStoresByReturnType(
						Issue.class, true, 
						FETCHGROUPS_BASEQUERYSTORE, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor
						);
				
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{					
						setInput(savedIssueQueries);
					}
				});
				
				return Status.OK_STATUS;
			}
		};
		loadSavedIssueQueries.schedule();
	}

	@Override
	protected void createTableColumns(final TableViewer tableViewer, Table table) {
		TableLayout layout = new TableLayout();

		TableViewerColumn tc = new TableViewerColumn(tableViewer, SWT.NONE);
		tc.getColumn().setText("Query Name");
		layout.addColumnData(new ColumnWeightData(100));
		tc.setLabelProvider(new ColumnLabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				if (!(element instanceof BaseQueryStore<?, ?>))
					return super.getText(element);
				
				final BaseQueryStore<?, ?> store = (BaseQueryStore<?, ?>) element;
				return store.getName().getText();
			}
		});

		table.setLayout(layout);
		table.setHeaderVisible(false);

		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(final DoubleClickEvent e) {
				StructuredSelection s = (StructuredSelection)e.getSelection();
				if (s.isEmpty())
					return;

				final BaseQueryStore<?, ?> selectedQueryStore = getFirstSelectedElement();
				if (selectedQueryStore == null)
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
							final IssueEntryListViewer viewer = (IssueEntryListViewer)part.getEntryViewer();

							viewer.getQueryProvider().loadQueries(selectedQueryStore.getQueryCollection());
							viewer.search();
						}
					});

				} catch (PartInitException e1) {
					throw new RuntimeException(e1);
				}
			}
		});
	}

}
