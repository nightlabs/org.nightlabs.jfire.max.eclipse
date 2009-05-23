package org.nightlabs.jfire.issuetracking.ui.issue;

import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issue.issuemarker.IssueMarker;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditor;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorInput;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;

/**
 * The table used for listing {@link Issue} elements.
 *
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 * @author Khaireel Mohamed - khaireel at nightlabs dot de
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
		IssueMarker.FETCH_GROUP_ICON_16X16_DATA
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
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				disposeAllImages();
			}
		});
	}

	private void disposeAllImages()
	{
		// dispose all images
		for (Image image : imageKey2Image.values()) {
			image.dispose();
		}
		imageKey2Image.clear();
	}

	private String generateCombiIssueMarkerImageKey(Issue issue)
	{
		List<IssueMarker> issueMarkers = new ArrayList<IssueMarker>(issue.getIssueMarkers());
		Collections.sort(issueMarkers, new Comparator<IssueMarker>() {
			@Override
			public int compare(IssueMarker o1, IssueMarker o2) {
				int c = o1.getOrganisationID().compareTo(o2.getOrganisationID());
				if (c != 0)
					return c;

				return o1.getIssueMarkerID() < o2.getIssueMarkerID() ? -1 : 1;
			}
		});

		StringBuilder sb = new StringBuilder();
		for (IssueMarker issueMarker : issueMarkers) {
			if (sb.length() > 0)
				sb.append("::");

			sb.append(issueMarker.getOrganisationID());
			sb.append('/');
			sb.append(issueMarker.getIssueMarkerID());
		}
		return sb.toString();
	}

	private Map<String, Image> imageKey2Image = new HashMap<String, Image>();
	private static Point ISSUE_MARKER_IMAGE_DIMENSION = new Point(16, 16);

	protected Image getCombiIssueMarkerImage(Issue issue)
	{
		if (maxIssueMarkerCountPerIssue < 0)
			throw new IllegalStateException("maxIssueMarkerCountPerIssue < 0");

		String imageKey = generateCombiIssueMarkerImageKey(issue);
		Image combiImage = imageKey2Image.get(imageKey);
		if (combiImage == null) {
			combiImage = new Image(
					getDisplay(),
					ISSUE_MARKER_IMAGE_DIMENSION.x * maxIssueMarkerCountPerIssue + maxIssueMarkerCountPerIssue - 1,
					ISSUE_MARKER_IMAGE_DIMENSION.y
			);
			GC gc = new GC(combiImage);
			try {
				Iterator<IssueMarker> itIssueMarkers = issue.getIssueMarkers().iterator();
				for(int i=0; i<maxIssueMarkerCountPerIssue; i++) {
					if (!itIssueMarkers.hasNext())
						break;

					IssueMarker issueMarker = itIssueMarkers.next();
					String issueMarkerIDString = JDOHelper.getObjectId(issueMarker).toString();
					Image icon = imageKey2Image.get(issueMarkerIDString);
					if (icon == null) {
						if (issueMarker.getIcon16x16Data() != null) {
							ByteArrayInputStream in = new ByteArrayInputStream(issueMarker.getIcon16x16Data());
							icon = new Image(getDisplay(), in);
//							in.close(); // not necessary, because it is a ByteArrayInputStream working solely in RAM - unfortunately it is declared with a throws clause - thus commenting it out. Marco.
							imageKey2Image.put(issueMarkerIDString, icon);
						}
					}

					if (icon != null)
						gc.drawImage(icon, ISSUE_MARKER_IMAGE_DIMENSION.x * i + i, 0);
				}
			} finally {
				gc.dispose();
			}

			imageKey2Image.put(imageKey, combiImage);
		}
		return combiImage;
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		TableColumn tc;
		TableLayout layout = new TableLayout();

		tc = new TableColumn(table, SWT.LEFT); // @column 0
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.id.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(5)); // Previously: 15

		tc = new TableColumn(table, SWT.LEFT); // @column 1
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.date.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(20)); // Previously: 40

		tc = new TableColumn(table, SWT.LEFT); // @column 2
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.type.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(10)); // Previously: 20

		tc = new TableColumn(table, SWT.LEFT); // @column 3
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.subject.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(22)); // Previously: 20

		tc = new TableColumn(table, SWT.LEFT); // @column 4
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.description.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(30)); // Previously: 20

		// ---->> Added to display IssueMarker icons, whenever they are avaible in an Issue ------------------------------------|
		tc = new TableColumn(table, SWT.LEFT); // @column 5
		tc.setMoveable(true);
		tc.setText("Markers");
		layout.addColumnData(new ColumnWeightData(10));
		// <<-------------------------------------------------------------------------------------------------------------------|

		tc = new TableColumn(table, SWT.LEFT); // @column 6
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.severity.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(10)); // Previously: 15

		tc = new TableColumn(table, SWT.LEFT); // @column 7
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.priority.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(10)); // Previously: 15

		tc = new TableColumn(table, SWT.LEFT); // @column 8
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.state.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(10)); // Previously: 15

		tc = new TableColumn(table, SWT.LEFT); // @column 9
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
				case 0: return issue.getIssueIDAsString();
				case 1: return dateTimeFormat.format(issue.getCreateTimestamp());
				case 2: return issue.getIssueType().getName().getText();
				case 3: return issue.getSubject().getText();
				case 4:
					//TODO: We should find another ways for displaying the description text if it's longer than the column width!!!!
					if (issue.getDescription() != null) {
						String descriptionText = issue.getDescription().getText();
						if (descriptionText.indexOf('\n') != -1)
							return descriptionText.substring(0, descriptionText.indexOf('\n')).concat("(...)"); //$NON-NLS-1$
						else
							return descriptionText;
					}
				break;
				// case 5 <-- Reserved for displaying IssueMarker icons only.
				case 6: return issue.getIssueSeverityType() == null ? Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumnText.severity.noData") : issue.getIssueSeverityType().getIssueSeverityTypeText().getText(); //$NON-NLS-1$
				case 7: return issue.getIssuePriority() == null ? Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumnText.priority.noData") : issue.getIssuePriority().getIssuePriorityText().getText(); //$NON-NLS-1$
				case 8: return getStateName(issue);
				case 9: return issue.isStarted()? Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumnText.working") : Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumnText.stopped"); //$NON-NLS-1$ //$NON-NLS-2$
				default: return ""; //$NON-NLS-1$
				}
			}
			return null;
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// --- 8< --- KaiExperiments: since 19.05.2009 ------------------
			// This accomodates for more than one Image icon. But we have a problem if not all fields have the same
			// number of icons.
			// FIXME Standardise the icons.
			// TODO Generalise this with the registry.
			if (element != null && element instanceof Issue && columnIndex == 5) {
				// Testing with multiple images.
//				Set<IssueMarker> issueMarkers = ((Issue)element).getIssueMarkers();
//				if (issueMarkers != null && !issueMarkers.isEmpty()) {
//					int n = issueMarkers.size();
//					int i=0;
//
//					Image[] imgIcons = new Image[n];
//					for (IssueMarker issueMarker : issueMarkers) {
//						String refText = issueMarker.getName().getText();
//						String suffix = refText.contains("Email") ? "Email" : (refText.contains("Phone") ? "Telephone" : "Suspended");
//						imgIcons[i++] = SharedImages.getSharedImage(IssueTrackingPlugin.getDefault(), IssueMarkerSection.class, suffix, ImageDimension._16x16, ImageFormat.gif);
//					}
//
////					Image combinedIcons = new Image(Display.getDefault(), 16*n + n-1, 16);
////					GC gc = new GC(combinedIcons);
////					try {
////						for(i=0; i<n; i++)
////							gc.drawImage(imgIcons[i], 16*i + i, 0);
////					} finally {
////						gc.dispose();
////					}
//
//					return combinedIcons;
//				}
				return getCombiIssueMarkerImage((Issue) element);
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

//	public void setLoadingStatus()
//	{
//		super.setInput(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.table.loading.text")); //$NON-NLS-1$
//	}

	private int maxIssueMarkerCountPerIssue = -1;

	@Override
	public void setInput(Object input) {
		// determine the maximum number of IssueMarkers per Issue
		// TODO we need to refactor this (ask the server) when refactoring this whole search stuff to SWT.VIRTUAL
		disposeAllImages();
		maxIssueMarkerCountPerIssue = -1;
		if (input instanceof Collection) {
			for (Object o : ((Collection<?>)input)) {
				if (o instanceof Issue) {
					maxIssueMarkerCountPerIssue = Math.max(maxIssueMarkerCountPerIssue, ((Issue)o).getIssueMarkers().size());
				}
			}
		}

		super.setInput(input);
	}
}
