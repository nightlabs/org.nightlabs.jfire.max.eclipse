package org.nightlabs.jfire.issuetracking.ui.issue;

import java.awt.Dimension;
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

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewer;
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
import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.ui.labelprovider.ColumnSpanLabelProvider;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.NLJDOHelper;
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
 * @author marco schulze - marco at nightlabs dot de
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
		Issue.FETCH_GROUP_ISSUE_MARKERS,          // <-- Since 14.05.2009
		IssueMarker.FETCH_GROUP_NAME,             // <-- Since 14.05.2009
		IssueMarker.FETCH_GROUP_ICON_16X16_DATA,  // <-- Since 14.05.2009
	};

	/**
	 * Constructs the issue table.
	 *
	 * @param parent - the parent composite for holding this table
	 * @param style - SWT style constant
	 */
	public IssueTable(Composite parent, int style) {
		super(parent, style);
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
		for (Image image : imageKey2Image.values())
			image.dispose();
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
				sb.append("::"); //$NON-NLS-1$

			sb.append(issueMarker.getOrganisationID());
			sb.append('/');
			sb.append(issueMarker.getIssueMarkerID());
		}
		return sb.toString();
	}

	private Map<String, Image> imageKey2Image = new HashMap<String, Image>();
	private static Dimension ISSUE_MARKER_IMAGE_DIMENSION = new Dimension(16, 16);

	protected Image getCombiIssueMarkerImage(Issue issue)
	{
		if (maxIssueMarkerCountPerIssue < 0)
			throw new IllegalStateException("maxIssueMarkerCountPerIssue < 0"); //$NON-NLS-1$

		String imageKey = generateCombiIssueMarkerImageKey(issue);
		Image combiImage = imageKey2Image.get(imageKey);
		if (combiImage == null && maxIssueMarkerCountPerIssue > 0) { // It is possible that none of the Issues has a single IssueMarker; in which case, we dont need to display any icons.
			combiImage = new Image(
					getDisplay(),
					ISSUE_MARKER_IMAGE_DIMENSION.width * maxIssueMarkerCountPerIssue + maxIssueMarkerCountPerIssue - 1,
					ISSUE_MARKER_IMAGE_DIMENSION.height
			);

			//Create a transparent image, Chairat.
			// Commented because does not worked under windows.
//		    Color white = getDisplay().getSystemColor(SWT.COLOR_WHITE);
//		    Color black = getDisplay().getSystemColor(SWT.COLOR_BLACK);
//		    PaletteData palette = new PaletteData(new RGB[] { white.getRGB(), black.getRGB() });
//			ImageData imageData = new ImageData(ISSUE_MARKER_IMAGE_DIMENSION.width * maxIssueMarkerCountPerIssue + maxIssueMarkerCountPerIssue - 1,
//					ISSUE_MARKER_IMAGE_DIMENSION.height, 1, palette);
//			imageData.transparentPixel = 0;
//			combiImage = new Image(getDisplay(), imageData);

			GC gc = new GC(combiImage);
			try {
				Iterator<IssueMarker> itIssueMarkers = issue.getIssueMarkers().iterator();
				for(int i=0; i<maxIssueMarkerCountPerIssue; i++) {
					if (!itIssueMarkers.hasNext())
						break;

					IssueMarker issueMarker = itIssueMarkers.next();
					String issueMarkerIDString = JDOHelper.getObjectId(issueMarker).toString();
					Image icon = imageKey2Image.get(issueMarkerIDString);
					if (icon == null)
						if (issueMarker.getIcon16x16Data() != null) {
							ByteArrayInputStream in = new ByteArrayInputStream(issueMarker.getIcon16x16Data());
							icon = new Image(getDisplay(), in);

//							in.close(); // not necessary, because it is a ByteArrayInputStream working solely in RAM - unfortunately it is declared with a throws clause - thus commenting it out. Marco.
							imageKey2Image.put(issueMarkerIDString, icon);
						}

					if (icon != null)
						gc.drawImage(icon, ISSUE_MARKER_IMAGE_DIMENSION.width * i + i, 0);
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
		// Must be so big, because otherwise under windows id is not visible, first column behaves strange
		layout.addColumnData(new ColumnWeightData(10)); // Previously: 15

		tc = new TableColumn(table, SWT.LEFT); // @column 1
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.date.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(20)); // Previously: 40

		tc = new TableColumn(table, SWT.LEFT); // @column 2
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.type.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(12)); // Previously: 20

		tc = new TableColumn(table, SWT.LEFT); // @column 3
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.subject.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(22)); // Previously: 20

		tc = new TableColumn(table, SWT.LEFT); // @column 4
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.description.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(30)); // Previously: 20

		// ---->> Added to display IssueMarker icons, whenever they are available in an Issue ------------------------------------|
		tc = new TableColumn(table, SWT.LEFT); // @column 5
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.column.markers.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(10));
		// <<---------------------------------------------------------------------------------------------------------------------|

		tc = new TableColumn(table, SWT.LEFT); // @column 6
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.severity.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(12)); // Previously: 15

		tc = new TableColumn(table, SWT.LEFT); // @column 7
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.priority.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(12)); // Previously: 15

		tc = new TableColumn(table, SWT.LEFT); // @column 8
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.state.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(12)); // Previously: 15

		// commented because IMHO not necessary
//		tc = new TableColumn(table, SWT.LEFT); // @column 9
//		tc.setMoveable(true);
//		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumn.status.text")); //$NON-NLS-1$
//		layout.addColumnData(new ColumnWeightData(10)); // Previously: 15

		table.setLayout(layout);

		addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				// --> [Observation] 18.06.2009
				// This table is used in several scenarios, and for all of them it makes perfect sense to open up an editor
				// to display the selected Issue on a double-click event.
				// All except for one: When the table is used in a Wizard. In which case, double-clicking the Issue should instead
				// trigger one of the (default?) action(/command)-buttons, rather than opening up the selected Issue in the background
				// application when the Wizard has got focus.
				if (isTableInWizard)
					return;

				StructuredSelection s = (StructuredSelection)e.getSelection();
				if (s.isEmpty())
					return;

				Issue issue = (Issue)s.getFirstElement();
				IssueEditorInput issueEditorInput = new IssueEditorInput(IssueID.create(issue.getOrganisationID(), issue.getIssueID()));
				try {
					Editor2PerspectiveRegistry.sharedInstance().openEditor(issueEditorInput, IssueEditor.EDITOR_ID);
				} catch (Exception e1) {
					throw new RuntimeException(e1);
				}
			}
		});
	}

	private boolean isTableInWizard = false;
	public void setIsTableInWizard(boolean isTableInWizard) { this.isTableInWizard = isTableInWizard; }


	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ArrayContentProvider()); //(new TableContentProvider());
		tableViewer.setLabelProvider(new IssueTableLabelProvider(tableViewer));
	}

	private static DateFormat dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

	class IssueTableLabelProvider
//	extends TableLabelProvider
	extends ColumnSpanLabelProvider
	{
		public IssueTableLabelProvider(ColumnViewer columnViewer) {
			super(columnViewer);
		}

		@Override
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
//				case 9: return issue.isStarted()? Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumnText.working") : Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumnText.stopped"); //$NON-NLS-1$ //$NON-NLS-2$
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
			if (element != null && element instanceof Issue && columnIndex == 5)
				return getCombiIssueMarkerImage((Issue) element);

			return null;
		}

		@Override
		protected int[][] getColumnSpan(Object element) { return null; }
	}

	protected String getStateName(Statable statable)
	{
		// I think we need to look for the newest State in both, statableLocal and statable! Marco.
		StatableLocal statableLocal = statable.getStatableLocal();
		State state = statable.getState();
		State state2 = statableLocal.getState();
		if (state2 != null)
			if (state == null)
				state = state2;
			else if (state.getCreateDT().compareTo(state2.getCreateDT()) < 0)
				state = state2;

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
		// Disposing & recreating this is a *very* *bad* idea because it causes all
		// listeners to be forgotten and has other side-effects, too.
		// The solution to our problem is to use a ColumnSpanLabelProvider (see above),
		// because it uses custom-draw (and works correctly).
		// Marco.
//		getTable().dispose();
//		createTableViewer(getViewerStyle());
//		createTableColumns(getTableViewer(), getTable());
//		setTableProvider(getTableViewer());
//		layout();

		// For placement settings: Determine the maximum number of IssueMarkers per Issue
		// OR do we need to refactor this (ask the server) when refactoring this whole search stuff to SWT.VIRTUAL?
		disposeAllImages();
		maxIssueMarkerCountPerIssue = -1;
		if (input instanceof Collection)
			for (Object o : ((Collection<?>)input))
				if (o instanceof Issue)
					maxIssueMarkerCountPerIssue = Math.max(maxIssueMarkerCountPerIssue, ((Issue)o).getIssueMarkers().size());

		super.setInput(input);
	}



	// ---[ Proposed additional helper methods ]----------------------------------------------------------------------|
	// --->> Which perhaps can be upgraded into the super class?
	/**
	 * @return the ObjectIDs of all the elements in this table. Note that it is possible that the Collection is empty.
	 */
	public Collection<IssueID> getElementsObjectIDs() {
		return NLJDOHelper.getObjectIDList( getElements() );
	}

	/**
	 * Performs an O(n) search in the currecnt Collection based on the given ObjectID.
	 * @return the element from this table that matches the given ObjectID. Returns null if no matching element is found.
	 */
	public Issue getElementByID(IssueID objectID) {
		Collection<Issue> issues = getElements();
		if (issues == null || issues.isEmpty())	return null;

		Collection<IssueID> issueIDs = NLJDOHelper.getObjectIDList(issues);
		Iterator<Issue> issueIter = issues.iterator();
		for (IssueID issueID : issueIDs) {
			Issue issue = issueIter.next();
			if (objectID.equals(issueID))
				return issue;
		}

		return null;
	}

	/**
	 * Removes an element from this table given its matching ObjectID. Performs a linear search here.
	 * @return the element that was removed from the table. Returns null if no matching element is found.
	 */
	public Issue removeElementByID(IssueID objectID) {
		Issue issue = getElementByID(objectID);
		if (issue != null) {
			Collection<Issue> issues = getElements();
			issues.remove(issue);
			this.setInput(issues);
		}

		return issue;
	}



}
