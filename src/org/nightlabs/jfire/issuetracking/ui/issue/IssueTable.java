package org.nightlabs.jfire.issuetracking.ui.issue;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageDimension;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issue.issueMarker.IssueMarker;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditor;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorInput;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.issueMarker.IssueMarkerSection;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;

/**
 * The table used for listing {@link Issue} elements.
 *
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
		Issue.FETCH_GROUP_SUBJECT,
		Issue.FETCH_GROUP_DESCRIPTION,
		Issue.FETCH_GROUP_ISSUE_SEVERITY_TYPE,
		Issue.FETCH_GROUP_ISSUE_PRIORITY,
		Statable.FETCH_GROUP_STATE,
		Issue.FETCH_GROUP_ISSUE_LOCAL,
		StatableLocal.FETCH_GROUP_STATE,
		State.FETCH_GROUP_STATE_DEFINITION,
		IssueType.FETCH_GROUP_NAME,
		IssueSeverityType.FETCH_GROUP_NAME,
		IssuePriority.FETCH_GROUP_NAME,
		StateDefinition.FETCH_GROUP_NAME,
		Issue.FETCH_GROUP_ISSUE_MARKERS, // <-- Since 14.05.2009
		IssueMarker.FETCH_GROUP_NAME,         // <-- Since 14.05.2009
	};

	/**
	 * Constructs the issue table.
	 *
	 * @param parent - the parent composite for holding this table
	 * @param style - SWT style constant
	 */
	public IssueTable(Composite parent, int style)
	{
		super(parent, style);

//		loadIssues();

//		JDOLifecycleManager.sharedInstance().addLifecycleListener(newIssueListener);
//		JDOLifecycleManager.sharedInstance().addNotificationListener(Issue.class, changedIssueListener);

//		addDisposeListener(new DisposeListener() {
//		public void widgetDisposed(DisposeEvent event)
//		{
//		JDOLifecycleManager.sharedInstance().removeLifecycleListener(newIssueListener);
//		JDOLifecycleManager.sharedInstance().removeNotificationListener(Issue.class, changedIssueListener);
//		}
//		});

		getTableViewer().setComparator(new ViewerComparator() {
			@Override
			public void sort(Viewer viewer, Object[] elements) {
				Arrays.sort(elements, new Comparator<Object>() {
					public int compare(Object object1, Object object2) {
						return -((Issue)object1).getCreateTimestamp().compareTo(((Issue)object2).getCreateTimestamp());
					}
				});
			}
		});
	}

//	private JDOLifecycleListener newIssueListener = new JDOLifecycleAdapterJob("Loading Issue") {
//	private SimpleLifecycleListenerFilter filter = new SimpleLifecycleListenerFilter(Issue.class,
//	true, JDOLifecycleState.NEW);

//	public IJDOLifecycleListenerFilter getJDOLifecycleListenerFilter()
//	{
//	return filter;
//	}

//	public void notify(JDOLifecycleEvent event)
//	{
//	Set<IssueID> issueIDs = new HashSet<IssueID>();
//	for (DirtyObjectID dirtyObjectID : event.getDirtyObjectIDs())
//	issueIDs.add((IssueID) dirtyObjectID.getObjectID());

//	// we should filter the new issueIDs against the conditions defined by the UI (i.e. the QueryMap that is managed by IssueEntryListViewer).
//	// TODO or even better we embed the JDOQueryMap in our IJDOLifecycleListenerFilter (which needs to be updated whenever we press the search button),
//	// since this would be more efficient than first sending all new issueIDs to the client and then again run the queries on the server (the filter
//	// is already on the server and could run the queries with 1 less round-trip to the client).
//	// We'll do this filtering thing for JFire 1.2 ;-) or later. It's fine for the beginning, if all new issues pop up.

//	final Collection<Issue> issues = IssueDAO.sharedInstance().getIssues(issueIDs, IssueTable.FETCH_GROUPS_ISSUE,
//	NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//	getProgressMontitorWrapper());

//	Display.getDefault().asyncExec(new Runnable() {
//	public void run() {
//	for (Issue issue : issues) {
//	issueID2issue.put((IssueID) JDOHelper.getObjectId(issue), issue);
//	}
//	refresh();
//	}
//	});
//	}
//	};

//	private NotificationListener changedIssueListener = new NotificationAdapterJob() {
//	public void notify(org.nightlabs.notification.NotificationEvent notificationEvent) {
//	ProgressMonitor monitor = getProgressMonitorWrapper();
//	Set<IssueID> dirtyIssueIDs = new HashSet<IssueID>();
//	for (Iterator<?> it = notificationEvent.getSubjects().iterator(); it.hasNext(); ) {
//	DirtyObjectID dirtyObjectID = (DirtyObjectID) it.next();
//	switch (dirtyObjectID.getLifecycleState()) {
//	case DIRTY:
//	dirtyIssueIDs.add((IssueID) dirtyObjectID.getObjectID());
//	break;
//	case DELETED:
//	// TODO remove the object from the UI
//	break;
//	default:
//	break;
//	}
//	}

//	if (!dirtyIssueIDs.isEmpty()) {
//	final Collection<Issue> issues = IssueDAO.sharedInstance().getIssues(
//	dirtyIssueIDs,
//	IssueTable.FETCH_GROUPS_ISSUE,
//	NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//	monitor);

//	Display.getDefault().asyncExec(new Runnable() {
//	public void run() {
//	for (Issue issue : issues)
//	issueID2issue.put((IssueID) JDOHelper.getObjectId(issue), issue);

//	refresh();
//	}
//	});
//	}
//	}
//	};

//	private void loadIssues()
//	{
//	Job job = new Job("Loading issues") {
//	@Override
//	protected IStatus run(ProgressMonitor monitor) throws Exception {
//	loadIssues(monitor);
//	return Status.OK_STATUS;
//	}
//	};
//	job.schedule();
//	}

//	private void loadIssues(ProgressMonitor monitor)
//	{
//	Display.getDefault().syncExec(new Runnable() {
//	public void run() {
//	setInput("Loading data...");
//	}
//	});

//	final Collection<Issue> issues = IssueDAO.sharedInstance().getIssues(IssueTable.FETCH_GROUPS_ISSUE,
//	NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//	monitor);

//	Display.getDefault().asyncExec(new Runnable() {
//	public void run() {
//	issueID2issue.clear();
//	for (Issue issue : issues) {
//	issueID2issue.put((IssueID) JDOHelper.getObjectId(issue), issue);
//	}

//	setInput(issueID2issue.values());
//	}
//	});
//	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		TableColumn tc;
		TableLayout layout = new TableLayout();

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.id.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(5)); // Previously: 15

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.date.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(20)); // Previously: 40

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.type.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(10)); // Previously: 20

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.subject.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(35)); // Previously: 20

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.description.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(40)); // Previously: 20

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.severity.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(10)); // Previously: 15

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.priority.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(10)); // Previously: 15

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.state.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(10)); // Previously: 15

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.status.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(10)); // Previously: 15

		table.setLayout(layout);

		addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				StructuredSelection s = (StructuredSelection)e.getSelection();
				if (s.isEmpty())
					return;

				Issue issue = (Issue)s.getFirstElement();

				IssueEditorInput issueEditorInput = new IssueEditorInput(IssueID.create(issue.getOrganisationID(), issue.getIssueID()));
				try {
					RCPUtil.openEditor(issueEditorInput, IssueEditor.EDITOR_ID);
				} catch (PartInitException e1) {
					throw new RuntimeException(e1);
				}
			}
		});
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new IssueTableLabelProvider());
	}

	private static DateFormat dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

	class IssueTableLabelProvider
	extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof Issue) {
				Issue issue = (Issue) element;
				switch (columnIndex) {
				case(0): return issue.getIssueIDAsString();
				case(1): return dateTimeFormat.format(issue.getCreateTimestamp());
				case(2): return issue.getIssueType().getName().getText();
				case(3): return issue.getSubject().getText();
				case(4):
					//TODO: We should find another ways for displaying the description text if it's longer than the column width!!!!
					if (issue.getDescription() != null) {
						String descriptionText = issue.getDescription().getText();
						if (descriptionText.indexOf('\n') != -1)
							return descriptionText.substring(0, descriptionText.indexOf('\n')).concat("(...)"); //$NON-NLS-1$
						else
							return descriptionText;
					}
				break;
				case(5): return issue.getIssueSeverityType() == null ? Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumnText.severity.noData") : issue.getIssueSeverityType().getIssueSeverityTypeText().getText(); //$NON-NLS-1$
				case(6): return issue.getIssuePriority() == null ? Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumnText.priority.noData") : issue.getIssuePriority().getIssuePriorityText().getText(); //$NON-NLS-1$
				case(7): return getStateName(issue);
				case(8): return issue.isStarted()? Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumnText.working") : Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumnText.stopped"); //$NON-NLS-1$ //$NON-NLS-2$
				default: return ""; //$NON-NLS-1$
				}
			}
			return null;
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// --- 8< --- KaiExperiments: since 19.05.2009 ------------------
			// May need to amend this to accomodate for more than one Image
			if (element != null && element instanceof Issue && columnIndex == 3) {
				// Testing with multiple images.
				Set<IssueMarker> issueMarkers = ((Issue)element).getIssueMarkers();
				if (issueMarkers != null && !issueMarkers.isEmpty()) {
					int n = issueMarkers.size();
					int i=0;

					Image[] imgIcons = new Image[n];
					for (IssueMarker issueMarker : issueMarkers) {
						String refText = issueMarker.getName().getText();
						String suffix = refText.contains("Email") ? "Email" : (refText.contains("Phone") ? "Telephone" : "Suspended");
						imgIcons[i++] = SharedImages.getSharedImage(IssueTrackingPlugin.getDefault(), IssueMarkerSection.class, suffix, ImageDimension._16x16, ImageFormat.gif);
					}

					Image combinedIcons = new Image(Display.getDefault(), 16*n + n-1, 16);
					GC gc = new GC(combinedIcons);
					try {
						for(i=0; i<n; i++)
							gc.drawImage(imgIcons[i], 16*i + i, 0);
					} finally {
						gc.dispose();
					}

					return combinedIcons;
				}
			}
			// ------ KaiExperiments ----- >8 -------------------------------

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
		super.setInput(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.table.loading.text")); //$NON-NLS-1$
	}
}
