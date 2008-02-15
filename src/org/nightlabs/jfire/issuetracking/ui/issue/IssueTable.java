package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleEvent;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleListener;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.jdo.notification.JDOLifecycleAdapterJob;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueDescription;
import org.nightlabs.jfire.issue.IssueFileAttachment;
import org.nightlabs.jfire.issue.IssueLocal;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueSubject;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.jfire.jdo.notification.IJDOLifecycleListenerFilter;
import org.nightlabs.jfire.jdo.notification.JDOLifecycleState;
import org.nightlabs.jfire.jdo.notification.SimpleLifecycleListenerFilter;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * This composite lists all {@link Issue}s of an issue in a table.
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssueTable
extends AbstractTableComposite<Issue>
{
	private IssueID issueID;

	private Collection<Issue> issues;
	/**
	 * The fetch groups of issue data.
	 */
	public static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT, 
		Issue.FETCH_GROUP_THIS,
		IssueType.FETCH_GROUP_THIS,
		IssueDescription.FETCH_GROUP_THIS, 
		IssueSubject.FETCH_GROUP_THIS,
		IssueFileAttachment.FETCH_GROUP_THIS,
		IssueSeverityType.FETCH_GROUP_THIS,
		IssuePriority.FETCH_GROUP_THIS,
		IssueLocal.FETCH_GROUP_THIS,
		StatableLocal.FETCH_GROUP_STATE,
		State.FETCH_GROUP_STATE_DEFINITION,
		StateDefinition.FETCH_GROUP_NAME};

	public IssueTable(Composite parent, int style)
	{
		super(parent, style);

		issues = new HashSet<Issue>();

		loadIssues();

		JDOLifecycleManager.sharedInstance().addLifecycleListener(newIssueListener);
		JDOLifecycleManager.sharedInstance().addNotificationListener(Issue.class, changedIssueListener);
		
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event)
			{
				JDOLifecycleManager.sharedInstance().removeLifecycleListener(newIssueListener);
				JDOLifecycleManager.sharedInstance().removeNotificationListener(Issue.class, changedIssueListener);
			}
		});
		
		getTableViewer().setComparator(new ViewerComparator() {
			@Override
			public void sort(Viewer viewer, Object[] elements) {
				Arrays.sort(elements, new Comparator() {
					public int compare(Object o1, Object o2) {
						return -((Issue)o1).getCreateTimestamp().compareTo(((Issue)o2).getCreateTimestamp());
					}
				});
			}
		});
	}		

	private JDOLifecycleListener newIssueListener = new JDOLifecycleAdapterJob("Loading Issue") {
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
			Set<DirtyObjectID> objectIDs = event.getDirtyObjectIDs();

			final Collection<Issue> newIssues = new ArrayList<Issue>();

			for (DirtyObjectID objectID : objectIDs) {
				Issue issue = IssueDAO.sharedInstance().getIssue((IssueID)objectID.getObjectID(), IssueTable.FETCH_GROUPS,
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new NullProgressMonitor());
				for (Issue is : issues) {
					if (!(is.getIssueID() == issue.getIssueID())) {
						newIssues.add(is);
					}
				}

				newIssues.add(issue);
			}

			issues = newIssues;

			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					setIssues(null, newIssues);
					refresh();
				}
			});
		}
	};

	private NotificationListener changedIssueListener = new NotificationAdapterJob() {
		public void notify(org.nightlabs.notification.NotificationEvent notificationEvent) {
			for (Iterator<DirtyObjectID> it = notificationEvent.getSubjects().iterator(); it.hasNext(); ) {
				DirtyObjectID dirtyObjectID = it.next();

					switch (dirtyObjectID.getLifecycleState()) {
					case DIRTY:
						loadIssues();
						break;
					case DELETED:
						// - remove the object from the UI
						break;
					}
			}
		}
	};

	private void loadIssues(){
		issues.clear();
		issues.addAll(IssueDAO.sharedInstance().getIssues(IssueTable.FETCH_GROUPS,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new NullProgressMonitor()));

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {					
				setIssues(null, issues);
				update();
			}
		});
	}

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

	public IssueID getIssueID()
	{
		return issueID;
	}

	private IssueID currentIssueID;

	public void setIssues(IssueID currentIssueID, Collection<Issue> issues)
	{
		this.issues = issues;
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
		this.currentIssueID = null;
		super.setInput("Loading message");
	}

	@Override
	public void setInput(Object input)
	{
		throw new UnsupportedOperationException("Use setIssues(...) or setLoadingStatus(...) instead!"); //$NON-NLS-1$
	}
}
