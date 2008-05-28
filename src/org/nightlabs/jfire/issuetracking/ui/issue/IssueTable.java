package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.Arrays;
import java.util.Comparator;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssueTable
extends AbstractTableComposite<Issue>
{
//	private Map<IssueID, Issue> issueID2issue = new HashMap<IssueID, Issue>();

	/**
	 * The fetch groups of issue data.
	 */
	public static final String[] FETCH_GROUPS_ISSUE = new String[] {
		FetchPlan.DEFAULT,
		Issue.FETCH_GROUP_ISSUE_TYPE,
		IssueType.FETCH_GROUP_NAME,
		Issue.FETCH_GROUP_SUBJECT,
		Issue.FETCH_GROUP_DESCRIPTION,
		IssueLink.FETCH_GROUP_THIS_ISSUE_LINK,
		Issue.FETCH_GROUP_ISSUE_SEVERITY_TYPE,
		IssueSeverityType.FETCH_GROUP_NAME,
		Issue.FETCH_GROUP_ISSUE_PRIORITY,
		IssuePriority.FETCH_GROUP_NAME,
		Statable.FETCH_GROUP_STATE,
		Issue.FETCH_GROUP_ISSUE_LOCAL,
		StatableLocal.FETCH_GROUP_STATE,
		State.FETCH_GROUP_STATE_DEFINITION,
		StateDefinition.FETCH_GROUP_NAME,

//		Issue.FETCH_GROUP_THIS_ISSUE,
//		IssueType.FETCH_GROUP_THIS_ISSUE_TYPE,
//		IssueDescription.FETCH_GROUP_THIS_DESCRIPTION, 
//		IssueSubject.FETCH_GROUP_THIS_ISSUE_SUBJECT,
//		IssueFileAttachment.FETCH_GROUP_THIS_FILEATTACHMENT,
//		IssueSeverityType.FETCH_GROUP_THIS_ISSUE_SEVERITY_TYPE,
//		IssuePriority.FETCH_GROUP_THIS_ISSUE_PRIORITY,
//		IssueLocal.FETCH_GROUP_THIS_ISSUE_LOCAL,
//		Statable.FETCH_GROUP_STATE,
//		Statable.FETCH_GROUP_STATES,
//		StatableLocal.FETCH_GROUP_STATE,
//		StatableLocal.FETCH_GROUP_STATES,
//		State.FETCH_GROUP_STATE_DEFINITION,
//		State.FETCH_GROUP_STATABLE,
//		StateDefinition.FETCH_GROUP_NAME
	};

	public IssueTable(Composite parent, int style)
	{
		super(parent, style);

//		loadIssues();

//		JDOLifecycleManager.sharedInstance().addLifecycleListener(newIssueListener);
//		JDOLifecycleManager.sharedInstance().addNotificationListener(Issue.class, changedIssueListener);
//
//		addDisposeListener(new DisposeListener() {
//			public void widgetDisposed(DisposeEvent event)
//			{
//				JDOLifecycleManager.sharedInstance().removeLifecycleListener(newIssueListener);
//				JDOLifecycleManager.sharedInstance().removeNotificationListener(Issue.class, changedIssueListener);
//			}
//		});

		getTableViewer().setComparator(new ViewerComparator() {
			@Override
			public void sort(Viewer viewer, Object[] elements) {
				if (! (elements instanceof Issue[]))
					return;
				
				Arrays.sort((Issue[])elements, new Comparator<Issue>() {
					public int compare(Issue issue1, Issue issue2) {
						return -issue1.getCreateTimestamp().compareTo(issue2.getCreateTimestamp());
					}
				});
			}
		});
	}
	
//	private JDOLifecycleListener newIssueListener = new JDOLifecycleAdapterJob("Loading Issue") {
//		private SimpleLifecycleListenerFilter filter = new SimpleLifecycleListenerFilter(Issue.class,
//			true, JDOLifecycleState.NEW);
//
//		public IJDOLifecycleListenerFilter getJDOLifecycleListenerFilter()
//		{
//			return filter;
//		}
//
//		public void notify(JDOLifecycleEvent event)
//		{
//			Set<IssueID> issueIDs = new HashSet<IssueID>();
//			for (DirtyObjectID dirtyObjectID : event.getDirtyObjectIDs())
//				issueIDs.add((IssueID) dirtyObjectID.getObjectID());
//
//			// we should filter the new issueIDs against the conditions defined by the UI (i.e. the QueryMap that is managed by IssueEntryListViewer).
//			// TODO or even better we embed the JDOQueryMap in our IJDOLifecycleListenerFilter (which needs to be updated whenever we press the search button),
//			// since this would be more efficient than first sending all new issueIDs to the client and then again run the queries on the server (the filter
//			// is already on the server and could run the queries with 1 less round-trip to the client).
//			// We'll do this filtering thing for JFire 1.2 ;-) or later. It's fine for the beginning, if all new issues pop up.
//
//			final Collection<Issue> issues = IssueDAO.sharedInstance().getIssues(issueIDs, IssueTable.FETCH_GROUPS_ISSUE,
//					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//					getProgressMontitorWrapper());
//
//			Display.getDefault().asyncExec(new Runnable() {
//				public void run() {
//					for (Issue issue : issues) {
//						issueID2issue.put((IssueID) JDOHelper.getObjectId(issue), issue);
//					}
//					refresh();
//				}
//			});
//		}
//	};

//	private NotificationListener changedIssueListener = new NotificationAdapterJob() {
//		public void notify(org.nightlabs.notification.NotificationEvent notificationEvent) {
//			ProgressMonitor monitor = getProgressMonitorWrapper();
//			Set<IssueID> dirtyIssueIDs = new HashSet<IssueID>();
//			for (Iterator<?> it = notificationEvent.getSubjects().iterator(); it.hasNext(); ) {
//				DirtyObjectID dirtyObjectID = (DirtyObjectID) it.next();
//				switch (dirtyObjectID.getLifecycleState()) {
//					case DIRTY:
//						dirtyIssueIDs.add((IssueID) dirtyObjectID.getObjectID());
//						break;
//					case DELETED:
//						// TODO remove the object from the UI
//						break;
//					default:
//						break;
//				}
//			}
//
//			if (!dirtyIssueIDs.isEmpty()) {
//				final Collection<Issue> issues = IssueDAO.sharedInstance().getIssues(
//						dirtyIssueIDs,
//						IssueTable.FETCH_GROUPS_ISSUE,
//						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//						monitor);
//
//				Display.getDefault().asyncExec(new Runnable() {
//					public void run() {
//						for (Issue issue : issues)
//							issueID2issue.put((IssueID) JDOHelper.getObjectId(issue), issue);
//
//						refresh();
//					}
//				});
//			}
//		}
//	};

//	private void loadIssues()
//	{
//		Job job = new Job("Loading issues") {
//			@Override
//			protected IStatus run(ProgressMonitor monitor) throws Exception {
//				loadIssues(monitor);
//				return Status.OK_STATUS;
//			}
//		};
//		job.schedule();
//	}
//
//	private void loadIssues(ProgressMonitor monitor)
//	{
//		Display.getDefault().syncExec(new Runnable() {
//			public void run() {					
//				setInput("Loading data...");
//			}
//		});
//
//		final Collection<Issue> issues = IssueDAO.sharedInstance().getIssues(IssueTable.FETCH_GROUPS_ISSUE,
//				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//				monitor);
//
//		Display.getDefault().asyncExec(new Runnable() {
//			public void run() {
//				issueID2issue.clear();
//				for (Issue issue : issues) {
//					issueID2issue.put((IssueID) JDOHelper.getObjectId(issue), issue);
//				}
//
//				setInput(issueID2issue.values());
//			}
//		});
//	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		TableColumn tc;
		TableLayout layout = new TableLayout();

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText("ID");
		layout.addColumnData(new ColumnWeightData(15));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText("Date Submitted");
		layout.addColumnData(new ColumnWeightData(40));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText("Type");
		layout.addColumnData(new ColumnWeightData(20));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText("Subject");
		layout.addColumnData(new ColumnWeightData(20));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText("Description");
		layout.addColumnData(new ColumnWeightData(20));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText("Severity");
		layout.addColumnData(new ColumnWeightData(15));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText("Priority");
		layout.addColumnData(new ColumnWeightData(15));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText("State");
		layout.addColumnData(new ColumnWeightData(15));

		table.setLayout(layout);
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new IssueListLabelProvider());
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
					return Long.toString(issue.getIssueID());
				case(1):
					if(issue.getCreateTimestamp() != null)
						return issue.getCreateTimestamp().toString();
				break;
				case(2):
					if(issue.getIssueType() != null)
						return issue.getIssueType().getName().getText();
				break;
				case(3):
					if (issue.getSubject() != null)
						return issue.getSubject().getText();
				break;
				case(4):
					if (issue.getDescription() != null)
						return issue.getDescription().getText();
				break;
				case(5):
					if(issue.getIssueSeverityType() != null)
						return issue.getIssueSeverityType().getIssueSeverityTypeText().getText();
				break;
				case(6):
					if(issue.getIssuePriority() != null)
						return issue.getIssuePriority().getIssuePriorityText().getText();
				break;
				case(7):
					return getStateName(issue);					
				default:
					return ""; //$NON-NLS-1$
				}
			}
			return null;
		}		
	}

	protected String getStateName(Statable statable) 
	{
		// I think we need to look for the newest State in both, statableLocal and statable! Marco.
		StatableLocal statableLocal = statable.getStatableLocal();
		State state = statable.getState();
		State state2 = statableLocal.getState();
		if (state2 != null) {
			if (state == null)
				state = state2;
			else if (state.getCreateDT().compareTo(state2.getCreateDT()) < 0)
				state = state2;
		}

		if (state != null)
			return state.getStateDefinition().getName().getText();

		return ""; //$NON-NLS-1$
	}	

	public void setLoadingStatus()
	{
		super.setInput("Loading message");
	}
}
