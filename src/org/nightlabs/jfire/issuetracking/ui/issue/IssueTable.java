package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.Collection;

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
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleEvent;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleListener;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.jdo.notification.JDOLifecycleAdapterJob;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueStatus;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.jbpm.graph.def.ProcessDefinition;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.jdo.notification.IJDOLifecycleListenerFilter;
import org.nightlabs.jfire.jdo.notification.JDOLifecycleState;
import org.nightlabs.jfire.jdo.notification.SimpleLifecycleListenerFilter;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * This composite lists all {@link Issue}s of an issue in a table.
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssueTable
extends AbstractTableComposite<Issue>
{
	private IssueID issueID;

	/**
	 * The fetch groups of issue data.
	 */
	public static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT, Issue.FETCH_GROUP_THIS, 
		Issue.FETCH_GROUP_DESCRIPTION, 
		Issue.FETCH_GROUP_SUBJECT, 
		IssueSeverityType.FETCH_GROUP_THIS,
		IssuePriority.FETCH_GROUP_THIS,
		IssueStatus.FETCH_GROUP_THIS,
		StateDefinition.FETCH_GROUP_NAME};

	public IssueTable(Composite parent, int style)
	{
		super(parent, style);
		
		loadIssues();
		
		getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				StructuredSelection s = (StructuredSelection)e.getSelection();
				if (s.isEmpty())
					return;

				Issue issue = (Issue)s.getFirstElement();
				try {
					IssueViewDialog d = new IssueViewDialog(getShell(), issue);
					d.open();
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		});
		
		JDOLifecycleManager.sharedInstance().addLifecycleListener(myLifecycleListener);
	    addDisposeListener(new DisposeListener() {
	      public void widgetDisposed(DisposeEvent event)
	      {
	        JDOLifecycleManager.sharedInstance().removeLifecycleListener(myLifecycleListener);
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
	      loadIssues();
	    }
	};

	private void loadIssues(){
		final Collection<Issue> issues = IssueDAO.sharedInstance().getIssues(IssueTable.FETCH_GROUPS,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new NullProgressMonitor());
		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {					
				setIssues(null, issues);
			}
		});
	}
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		TableColumn tc;
		TableLayout layout = new TableLayout();

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText("DocumentID");
		layout.addColumnData(new ColumnWeightData(30));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText("Category");
		layout.addColumnData(new ColumnWeightData(30));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText("Subject");
		layout.addColumnData(new ColumnWeightData(20));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText("Description");
		layout.addColumnData(new ColumnWeightData(20));
		
		tc = new TableColumn(table, SWT.LEFT);
		tc.setText("Severity");
		layout.addColumnData(new ColumnWeightData(20));
		
		tc = new TableColumn(table, SWT.LEFT);
		tc.setText("Priority");
		layout.addColumnData(new ColumnWeightData(20));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText("Status");
		layout.addColumnData(new ColumnWeightData(20));
		
		table.setLayout(layout);
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new IssueListLabelProvider());
	}

	public IssueID getIssueID()
	{
		return issueID;
	}

	private IssueID currentIssueID;

	public void setIssues(IssueID currentIssueID, Collection<Issue> issues)
	{
//		if (currentIssueID == null)
//			throw new IllegalArgumentException("currentIssueID == null"); //$NON-NLS-1$

		this.currentIssueID = currentIssueID;
		super.setInput(issues);
	}

	class IssueListLabelProvider
	extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex) 
		{
			if (element instanceof Issue) {
				Issue issue = (Issue) element;
				switch (columnIndex) 
				{
				case(0):
					if (issue.getIssueID() != null)
						return issue.getIssueID();
				break;
				case(1):
				break;
				case(2):
					if (issue.getSubject() != null)
						return issue.getSubject().getText();
					break;
				case(3):
					if (issue.getDescription() != null)
						return issue.getDescription().getText();
//					return issue.getCreateTimestamp().toString();
//					if (issue.getUpdateTimestamp() != null)
//						return issue.getUpdateTimestamp().toString();
				break;
				case(4):
					if(issue.getSeverityType() != null)
						return issue.getSeverityType().getIssueSeverityTypeText().getText();
					break;
				case(5):
					if(issue.getPriority() != null)
						return issue.getPriority().getIssuePriorityText().getText();
					break;
				case(6):
					if(issue.getStateDefinition() != null)
						return issue.getStateDefinition().getName().getText();
					break;
				default:
					return ""; //$NON-NLS-1$
				}
			}
			return null;
		}		
	}

	public void setLoadingStatus()
	{
		this.currentIssueID = null;
		super.setInput("Loading message");
	}

	@Override
	public void setInput(Object input)
	{
		throw new UnsupportedOperationException("Use setIssues(...) or setLoadingStatus(...) instead!"); //$NON-NLS-1$
	}
}
