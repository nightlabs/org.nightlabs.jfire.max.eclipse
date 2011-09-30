package org.nightlabs.jfire.issuetracking.ui.issue;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.labelprovider.ColumnSpanLabelProvider;
import org.nightlabs.base.ui.table.IColumnComparatorProvider;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.Issue.FieldName;
import org.nightlabs.jfire.issue.config.IssueTableConfigModule;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issue.issuemarker.IssueMarker;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.table.config.ColumnContentProperty;
import org.nightlabs.jfire.table.config.IColumnContentDescriptor;
import org.nightlabs.tableprovider.ui.TableLabelProvider;
import org.nightlabs.util.BaseComparator;

/**
 * This is essentially a label-provider, one that is configured on-the-fly for use in the context
 * of the {@link IssueTable}, based on instructions received from the {@link IssueTableConfigModule}.
 * This now supports both Text and Images to be correctly loaded from the server and displayed onto the
 * this label-provider.
 *
 * TESTING still in progress. This class might change... ideas mainly from Daniel's TableProviderBuilder.
 *
 * @author khaireel at nightlabs dot de
 */
public class ConfigurableIssueTableLabelProvider
extends ColumnSpanLabelProvider
implements TableLabelProvider<IssueID, Issue>, IColumnComparatorProvider
{
	// The scope of the fields; in retrospect, this should be the same for ALL JDOObjects represented and displayed in the table columns (right?).
	private String scope; // = TradePlugin.ZONE_SALE;

	// Keeps tab of the fieldNames from the given ColumnDescriptors.
	private Set<String> collatedFieldNames = null;
	private List<? extends IColumnContentDescriptor> columnContentDescriptors;

	// Helpers.
	private static DateFormat dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
	private static DateFormat deadlineDateTimeFormat = DateFormat.getDateInstance(DateFormat.SHORT);
	private static Dimension ISSUE_MARKER_IMAGE_DIMENSION = new Dimension(16, 16);

	// Special variables for handling Images loaded from the server.
	private int maxIssueMarkerCountPerIssue = -1;
	private Map<String, Image> imageKey2Image = new HashMap<String, Image>();
	private XComposite parent = null;


	/**
	 * Creates a new instance of a ConfigurableIssueTableLabelProvider.
	 * Note: If this LabelProvider is setup to also dispense Images, then it's internal tracking mechanism (involving disposals, etc.)
	 * will require a reference to the XComposite.
	 */
	public ConfigurableIssueTableLabelProvider
	(ColumnViewer columnViewer, XComposite parent, List<? extends IColumnContentDescriptor> columnContentDescriptors, String scope) {
		super(columnViewer);
		this.parent = parent;
		this.columnContentDescriptors = columnContentDescriptors;
		this.scope = scope;

		// Sets up the Image tracking mechanism.
		if (parent != null) {
			// Attach the images to the parent's dispose listener.
			parent.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent event) { disposeAllImages(); }
			});
		}
	}

	/**
	 * Creates a new instance of a ConfigurableTableLabelProvider, solely for use to handle Texts.
	 */
	public ConfigurableIssueTableLabelProvider(ColumnViewer columnViewer, List<? extends IColumnContentDescriptor> columnContentDescriptors, String scope) {
		this(columnViewer, null, columnContentDescriptors, scope);
	}

	/**
	 * @return true if the given fieldName is within the specifications of the columnDescriptors.
	 */
	public boolean isFieldNameInConfiguration(String fieldName) {
		if (collatedFieldNames == null) {
			collatedFieldNames = new HashSet<String>();
			for (IColumnContentDescriptor columnContentDescriptor : columnContentDescriptors)
				for (String fName : columnContentDescriptor.getFieldNames())
					collatedFieldNames.add(fName);
		}

		return collatedFieldNames.contains(fieldName);
	}

	/**
	 * Call this method when setting the input to the table.
	 */
	public void setMaxIssueMarkerCountPerIssue(int maxIssueMarkerCountPerIssue) {
		disposeAllImages();
		this.maxIssueMarkerCountPerIssue = maxIssueMarkerCountPerIssue;
	}


	// ---------------------------------------------------------------------------------------- || -------------------------------------------->>
	// [Section] The general known handlers of a LabelProvider.
	// ---------------------------------------------------------------------------------------- || -------------------------------------------->>
	@Override
	protected int[][] getColumnSpan(Object element) { return null; }

	@Override
	public String getColumnText(Object element, int spanColIndex) {
		if (!(element instanceof Issue) ||spanColIndex > columnContentDescriptors.size()-1)
			return null;

		// Retrieve the necessary instruction from the corresponding ColumnDescriptor.
		IColumnContentDescriptor columnContentDescriptor = columnContentDescriptors.get(spanColIndex);
		return !columnContentDescriptor.getContentProperty().equals(ColumnContentProperty.IMAGE_ONLY) ? getText(columnContentDescriptor.getFieldNames(), (Issue) element, scope) : null;
	}

	@Override
	public Image getColumnImage(Object element, int spanColIndex) {
		if (parent == null || !(element instanceof Issue) || spanColIndex > columnContentDescriptors.size()-1)
			return null;

		// Note: In the current codes, there is an internal management of Images, which are tracked, and properly disposed. See IssueTable's imageKey2Image.
		//       Also, in the current settings in the IssueTable, it handles multiple images, by building a new combined image, based on the combined keys.
		//
		// Retrieve the necessary instruction from the corresponding ColumnDescriptor.
		IColumnContentDescriptor columnContentDescriptor = columnContentDescriptors.get(spanColIndex);
		return !columnContentDescriptor.getContentProperty().equals(ColumnContentProperty.TEXT_ONLY) ? getColumnImage(columnContentDescriptor.getFieldNames(), (Issue) element, scope) : null;
	}



	// ---------------------------------------------------------------------------------------- || -------------------------------------------->>
	// [Section] The configurable handlers.
	// ---------------------------------------------------------------------------------------- || -------------------------------------------->>
	@Override
	public String getText(Set<String> fieldNames, Issue element, String scope) {
		String lblText = "";
		for (String fieldName : fieldNames)
			lblText = String.format("%s %s", lblText, getTextForField(fieldName, element, scope));

		return lblText;
	}

	@Override
	public Image getColumnImage(Set<String> fieldNames, Issue element, String scope) {
		for (String fieldName : fieldNames)
			if (fieldName.equals(Issue.FieldName.issueMarkers))
				return getCombiIssueMarkerImage(element);

		return null;
	}



	// ---------------------------------------------------------------------------------------- || -------------------------------------------->>
	// [Section] Central archive: The place to define how textual-information based on a given fieldName of a JDOObject is to be returned.
	// ---------------------------------------------------------------------------------------- || -------------------------------------------->>
	// Tests only. Not sure how to arrive at Daniel's final plan with properties and extension points... yet.
	protected String getTextForField(String fieldName, Issue issue, String scope) {
		// So far, we shall assume that the JDOObject has been successfully retrieved...
		if (fieldName.equals(Issue.FieldName.issueID))
			return issue.getIssueIDAsString();

		if (fieldName.equals(Issue.FieldName.createTimestamp))
			return dateTimeFormat.format(issue.getCreateTimestamp());

		if (fieldName.equals(Issue.FieldName.issueType))
			return issue.getIssueType().getName().getText();

		if (fieldName.equals(Issue.FieldName.subject))
			return issue.getSubject().getText();

		if (fieldName.equals(Issue.FieldName.issuePriority))
			return issue.getIssuePriority() == null
			             ? Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumnText.priority.noData") // <-- FIXME
			             : issue.getIssuePriority().getIssuePriorityText().getText();

		if (fieldName.equals(Issue.FieldName.issueSeverityType))
			return issue.getIssueSeverityType() == null
			             ? Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueTable.tableColumnText.severity.noData") // <-- FIXME
			             : issue.getIssueSeverityType().getIssueSeverityTypeText().getText();

		if (fieldName.equals(Issue.FieldName.description))
			if (issue.getDescription() != null) {
				String descriptionText = issue.getDescription().getText();
				if (descriptionText.indexOf('\n') != -1)
					return descriptionText.substring(0, descriptionText.indexOf('\n')).concat("(...)"); // From original codes: Which handles multi-lines.
				else
					return descriptionText;
			}

		if (fieldName.equals(Issue.FieldName.state)) {
			// From the original codes.
			Statable statable = issue;
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

			return "";
		}

		if (fieldName.equals(Issue.FieldName.deadlineTimestamp)) {
			if (issue.getDeadlineTimestamp() != null)
				return deadlineDateTimeFormat.format(issue.getDeadlineTimestamp());
		}
		
		if (fieldName.equals(Issue.FieldName.issueResolution)) {
			if (issue.getIssueResolution() != null)
				return issue.getIssueResolution().getName().getText();
		}

		return "";
	}


	// ---------------------------------------------------------------------------------------- || -------------------------------------------->>
	// [Section] Image management routine.
	// ---------------------------------------------------------------------------------------- || -------------------------------------------->>
	private void disposeAllImages() {
		for (Image image : imageKey2Image.values())
			image.dispose();

		imageKey2Image.clear();
	}

	protected Image getCombiIssueMarkerImage(Issue issue) {
		if (maxIssueMarkerCountPerIssue < 0)
			throw new IllegalStateException("maxIssueMarkerCountPerIssue < 0");

		String imageKey = generateCombiIssueMarkerImageKey(issue);
		Image combiImage = imageKey2Image.get(imageKey);
		if (combiImage == null && maxIssueMarkerCountPerIssue > 0) { // It is possible that none of the Issues has a single IssueMarker; in which case, we dont need to display any icons.
			combiImage = new Image(
					parent.getDisplay(),
					ISSUE_MARKER_IMAGE_DIMENSION.width * maxIssueMarkerCountPerIssue + maxIssueMarkerCountPerIssue - 1,
					ISSUE_MARKER_IMAGE_DIMENSION.height
			);

			// FIXME TODO RAP commented because of incompatibility with RAP
//			GC gc = new GC(combiImage);
			GC gc = new GC(combiImage.getDevice());
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
							icon = new Image(parent.getDisplay(), in);
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

	private String generateCombiIssueMarkerImageKey(Issue issue) {
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

	private List<String> specialComparableFieldNames;
	protected List<String> getSpecialComparableFieldNames() {
		if (specialComparableFieldNames == null) {
			specialComparableFieldNames = new ArrayList<String>();
			specialComparableFieldNames.add(FieldName.deadlineTimestamp);
			specialComparableFieldNames.add(FieldName.createTimestamp);
			specialComparableFieldNames.add(FieldName.issueMarkers);
			specialComparableFieldNames.add(FieldName.issuePriority);
			specialComparableFieldNames.add(FieldName.issueID);
		}
		return specialComparableFieldNames;
	}

	@Override
	public Comparator<?> getColumnComparator(Object element, int columnIndex)
	{
		if (element instanceof Issue)
		{
//			Issue issue = (Issue) element;
			IColumnContentDescriptor cd = getColumnDescriptor(columnIndex);
			for (String fieldName : cd.getFieldNames()) {
				if (getSpecialComparableFieldNames().contains(fieldName)) {
					return new IssueColumnComparator(cd.getFieldNames());
				}
			}
		}
		return null;
	}

	protected IColumnContentDescriptor getColumnDescriptor(int columnIndex)
	{
		return columnContentDescriptors.get(columnIndex);
	}

	class IssueColumnComparator implements Comparator<Issue>
	{
		private Set<String> fieldNames;

		public IssueColumnComparator(Set<String> fieldNames) {
			this.fieldNames = fieldNames;
		}

		@Override
		public int compare(Issue i1, Issue i2)
		{
			if (fieldNames.contains(FieldName.deadlineTimestamp)) {
				Date d1 = i1.getDeadlineTimestamp();
				Date d2 = i2.getDeadlineTimestamp();
				int result = BaseComparator.comparatorNullCheck(d2, d1);
				if (result == BaseComparator.COMPARE_RESULT_NOT_NULL) {
					return d1.compareTo(d2);
				}
				return result;
			}
			else if (fieldNames.contains(FieldName.createTimestamp)) {
				Date d1 = i1.getCreateTimestamp();
				Date d2 = i2.getCreateTimestamp();
				int result = BaseComparator.comparatorNullCheck(d2, d1);
				if (result == BaseComparator.COMPARE_RESULT_NOT_NULL) {
					return d1.compareTo(d2);
				}
				return result;
			}
			else if (fieldNames.contains(FieldName.issueMarkers)) {
				Set<IssueMarker> markers1 = i1.getIssueMarkers();
				Set<IssueMarker> markers2 = i2.getIssueMarkers();
				if (markers1.isEmpty() && markers2.isEmpty())
					return 0;
				else if (markers1.isEmpty() && !markers2.isEmpty())
					return -1;
				else if (!markers1.isEmpty() && markers2.isEmpty())
					return 1;
				else {
					// TODO: define priority order for issueMarkers
					return markers1.size() - markers2.size();
				}
			}
			else if (fieldNames.contains(FieldName.issuePriority)) {
				IssuePriority p1 = i1.getIssuePriority();
				IssuePriority p2 = i2.getIssuePriority();
				int result = BaseComparator.comparatorNullCheck(p2, p1);
				if (result == BaseComparator.COMPARE_RESULT_NOT_NULL)
				{
					List<IssuePriority> ips1 = i1.getIssueType().getIssuePriorities();
					List<IssuePriority> ips2 = i2.getIssueType().getIssuePriorities();
					int index1 = ips1.indexOf(p1);
					int index2 = ips2.indexOf(p2);
					return index1 - index2;
				}
			}
			else if (fieldNames.contains(FieldName.issueID)) {
				return ((int) i1.getIssueID() - (int)i2.getIssueID());
			}
			return 0;
		}
	}

	@Override
	public void update(ViewerCell cell) {
		// TODO Auto-generated method stub
		
	}
}
