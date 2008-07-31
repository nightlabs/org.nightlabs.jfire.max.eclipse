package org.nightlabs.jfire.issuetracking.ui.issuehistory;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleEvent;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleListener;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.jdo.notification.JDOLifecycleAdapterJob;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.history.IssueHistory;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.jdo.notification.IJDOLifecycleListenerFilter;
import org.nightlabs.jfire.jdo.notification.JDOLifecycleState;
import org.nightlabs.jfire.jdo.notification.SimpleLifecycleListenerFilter;

/**
 * This composite lists all {@link IssueHistory}s of an issue in a table.
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssueHistoryTable
extends AbstractTableComposite<IssueHistory>
{
	/**
	 * The fetch groups of issue history data.
	 */
	public static final String[] FETCH_GROUPS = new String[] {};

	public IssueHistoryTable(Composite parent, int style)
	{
		super(parent, style);
		
		getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				
			}
		});
		
		JDOLifecycleManager.sharedInstance().addLifecycleListener(myLifecycleListener);
	    addDisposeListener(new DisposeListener() {
	      public void widgetDisposed(DisposeEvent event)
	      {
	        JDOLifecycleManager.sharedInstance().removeLifecycleListener(myLifecycleListener);
	      }
	    });
	    
	    getTableViewer().setComparator(new ViewerComparator() {
			@Override
			public void sort(Viewer viewer, Object[] elements) {
				Arrays.sort(elements, new Comparator<Object>() {
					public int compare(Object object1, Object object2) {
						return -((IssueHistory)object1).getCreateTimestamp().compareTo(((IssueHistory)object2).getCreateTimestamp());
					}
				});
			}
		});
	}		

	private JDOLifecycleListener myLifecycleListener = new JDOLifecycleAdapterJob("Loading Xyz") {
	    private IJDOLifecycleListenerFilter filter = new SimpleLifecycleListenerFilter(
	      Issue.class,
	      true,
	      JDOLifecycleState.NEW);

	    public IJDOLifecycleListenerFilter getJDOLifecycleListenerFilter()
	    {
	      return filter;
	    }

	    public void notify(JDOLifecycleEvent event)
	    {
	    }
	};

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		TableColumn tc;

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText("Date");

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText("Username");
		
		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText("Action");

		WeightedTableLayout layout = new WeightedTableLayout(new int[]{30, 30, 70});
		table.setLayout(layout);
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new IssueHistoryListLabelProvider());
	}

	private static DateFormat dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
	
	class IssueHistoryListLabelProvider
	extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex) 
		{
			if (element instanceof IssueHistory) {
				IssueHistory issueHistory = (IssueHistory) element;
				switch (columnIndex) 
				{
				case(0):
					return dateTimeFormat.format(issueHistory.getCreateTimestamp());
				case(1):
					return issueHistory.getUser().getName();
				case(2):
					return issueHistory.getChange();
				case(3):
				break;
				case(4):
				case(5):
				case(6):
				default:
					return ""; //$NON-NLS-1$
				}
			}
			return null;
		}		
	}

	public void setLoadingStatus()
	{
		super.setInput("Loading message");
	}

	private IssueID issueID;
	public void setIssueHistories(IssueID issueID, Collection<IssueHistory> issueHistories)
	{
		if (issueID == null)
			throw new IllegalArgumentException("issueID == null"); //$NON-NLS-1$

		this.issueID = issueID;
		super.setInput(issueHistories);
	}
	
	@Override
	public void setInput(Object input)
	{
		throw new UnsupportedOperationException("Use setIssueHistories(...) or setLoadingStatus(...) instead!"); //$NON-NLS-1$
	}
}

