package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.ui.labelprovider.ColumnSpanLabelProvider;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.config.IssueTableConfigModule;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditor;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorInput;
import org.nightlabs.jfire.table.config.ColumnDescriptor;
import org.nightlabs.jfire.table.config.IColumnConfiguration;
import org.nightlabs.jfire.table.config.IColumnContentDescriptor;
import org.nightlabs.jfire.table.config.IColumnDescriptor;
import org.nightlabs.progress.NullProgressMonitor;

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
	// There now exists an IssueTableConfigModule, which configures the columns of the IssueTable.
	// In that same config-module, there also exists all the necessary fetch-groups from which we
	// would need, based on the ColumnDescriptors that we have configured to want to use.
	private List<? extends IColumnContentDescriptor> columnContentDescriptors = null;
	private ColumnSpanLabelProvider issueTableLabelProvider;

	/**
	 * Constructs the issue table.
	 *
	 * @param parent - the parent composite for holding this table
	 * @param style - SWT style constant
	 *
	 * @deprecated should not be used anymore, use {@link #IssueTable(Composite, int, boolean)} instead
	 * and use {@link #setIssueTableConfigurations(IssueTableConfigModule)}.
	 * This constructor uses Server communication to load the {@link IssueTableConfigModule}.
	 */
	@Deprecated
	public IssueTable(Composite parent, int style) {
		// this(parent, style, true); // <-- OLD default, single constructor for the IssueTable.

		// Since 2010.04.26:
		//   The default constructor of this IssueTable will call upon the (default) IssueTableConfigModule, and sets its table
		//   layout and contents according to the instructions contained within the configuration's specifications.
		this(parent, style, false);

		// The setup --> According to the criteria defined in the (default) IssueTableConfigModule.
		// Currently, as of 2010.04.26, the default IssueTable has 9 columns.
		IssueTableConfigModule issueTableCfMod = ConfigUtil.getUserCfMod(
				IssueTableConfigModule.class,
				new String[] {FetchPlan.DEFAULT,
					IssueTableConfigModule.FETCH_GROUP_COLUMNDESCRIPTORS,
					ColumnDescriptor.FETCH_GROUP_COL_FIELD_NAMES,
					ColumnDescriptor.FETCH_GROUP_COL_NAME,
					ColumnDescriptor.FETCH_GROUP_COL_TOOLTIP_DESCRIPTION,
					ColumnDescriptor.FETCH_GROUP_COL_FETCH_GROUPS},
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new NullProgressMonitor());

		setIssueTableConfigurations(issueTableCfMod);
	}

	/**
	 * An alternative constructor that allows for independent initialisation of the IssueTable.
	 */
	public IssueTable(Composite parent, int style, boolean isInitTable) {
		super(parent, style, isInitTable);
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

		// Reorganised: From the original codes, the following lines were previously inside the createTableColumns() method.
		// TODO Consider before forward-porting to trunk: Use the new context-menu framework, which handles events such as this more effectively. @Kai
		addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
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

	/**
	 * This assumes that the super class's initTable() routine has been earlier by-passed during the construction of this
	 * {@link IssueTable}, and based on the given issueTableColumnConfiguration, we will proceed to configure the appearance (and later,
	 * the contents) of what the table is allowed to display in its columns and fields.
	 */
	public void setIssueTableConfigurations(IColumnConfiguration issueTableColumnConfiguration) {
		// Keep a reference of the ColumnDescriptors from the config module.
		columnContentDescriptors = issueTableColumnConfiguration.getColumnDescriptors();

		// After cleaning up and refactorisation, we are again able to call the super class's initTable() method
		// here, which basically performs the following two things:
		//   1. Creates the columns.
		//   2. Sets up the label-provider.
		initTable();

		// Since we now have set up what the table-columns can display, we can happily set it's fetch-group references.
		fetchGroups = issueTableColumnConfiguration.getAllColumnFetchGroups();
	}


//	@Override
//	protected void createTableColumns(TableViewer tableViewer, Table table) {
//		// Guard.
//		if (columnContentDescriptors == null)
//			return;
//
//		// Go through the list, in order of the intended appearance.
//		TableLayout layout = new TableLayout();
//		for (IColumnDescriptor columnDescriptor : columnContentDescriptors) {
//			TableColumn tc = new TableColumn(table, columnDescriptor.getStyle());
//			tc.setMoveable(columnDescriptor.isMovable());
//			tc.setResizable(columnDescriptor.isResizable());
//			tc.setText(columnDescriptor.getName());
//			tc.setToolTipText(columnDescriptor.getTooltipDescription());
//			layout.addColumnData(new ColumnWeightData(columnDescriptor.getWeight()));
//		}
//
//		// And finally...
//		table.setLayout(layout);
//	}
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		// Guard.
		if (columnContentDescriptors == null)
			return;

		// Go through the list, in order of the intended appearance.
		int[] weights = new int[columnContentDescriptors.size()];
		for (int i = 0; i<columnContentDescriptors.size(); i++) {
			IColumnDescriptor columnDescriptor = columnContentDescriptors.get(i);
			TableColumn tc = new TableColumn(table, columnDescriptor.getStyle());
			tc.setMoveable(columnDescriptor.isMovable());
			tc.setResizable(columnDescriptor.isResizable());
			tc.setText(columnDescriptor.getName());
			tc.setToolTipText(columnDescriptor.getTooltipDescription());
			weights[i] = columnDescriptor.getWeight();
//			layout.addColumnData(new ColumnWeightData(columnDescriptor.getWeight()));
		}

		// And finally...
		TableLayout layout = new WeightedTableLayout(weights);
		table.setLayout(layout);
	}

	public static final String SCOPE = "org.nightlabs.jfire.trade.ui.TradePlugin" + "#ZONE_SALE";

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		// Guard.
		if (columnContentDescriptors == null)
			return;

		// Only because of the scope there existed a dependency to org.nightlabs.jfire.trade.ui.TradePlugin although not needed at all
		// changed the CONSTANT to local SCOPE and left same value because of compatibility. If value is unimported it can be changed. Daniel
//		issueTableLabelProvider = new ConfigurableIssueTableLabelProvider(tableViewer, this, columnContentDescriptors, TradePlugin.ZONE_SALE);
		issueTableLabelProvider = new ConfigurableIssueTableLabelProvider(tableViewer, this, columnContentDescriptors, SCOPE);
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(issueTableLabelProvider);
	}


	private boolean isTableInWizard = false; // <-- Erh?? Why should this be here??
	public void setIsTableInWizard(boolean isTableInWizard) { this.isTableInWizard = isTableInWizard; }

	// The fetch-groups required to display the contents of the columns in this table should be set
	// based on the information conceived in the IColumnConfiguration (the config-module).
	private String[] fetchGroups;
	public String[] getIssueTableFetchGroups() { return fetchGroups; }

	@Override
	public void setInput(Object input) {
		// For placement settings: Determine the maximum number of IssueMarkers per Issue
		// OR do we need to refactor this (ask the server) when refactoring this whole search stuff to SWT.VIRTUAL?
		int maxIssueMarkerCountPerIssue = -1;
		if (input instanceof Collection<?> && issueTableLabelProvider instanceof ConfigurableIssueTableLabelProvider) {
			 // We might not want to display markers in the new configurable table, where the markers have not been duly detached.
			if (((ConfigurableIssueTableLabelProvider) issueTableLabelProvider).isFieldNameInConfiguration(Issue.FieldName.issueMarkers)) {
				for (Object o : ((Collection<?>)input))
					if (o instanceof Issue)
						maxIssueMarkerCountPerIssue = Math.max(maxIssueMarkerCountPerIssue, ((Issue)o).getIssueMarkers().size());

				((ConfigurableIssueTableLabelProvider) issueTableLabelProvider).setMaxIssueMarkerCountPerIssue(maxIssueMarkerCountPerIssue);
			}
		}

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
