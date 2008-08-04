package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import java.text.DateFormat;
import java.util.Collection;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.issue.IssueWorkTimeRange;
import org.nightlabs.jfire.issue.history.IssueHistory;
import org.nightlabs.jfire.issue.id.IssueID;

/**
 * This composite lists all {@link IssueWorkTimeRange}s of an issue in a table.
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssueWorkTimeRangeTable
extends AbstractTableComposite<IssueHistory>
{
	/**
	 * The fetch groups of issue work time range data.
	 */
	public static final String[] FETCH_GROUPS = new String[] {};

	public IssueWorkTimeRangeTable(Composite parent, int style)
	{
		super(parent, style);
	}		

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		TableColumn tc;

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText("User");

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText("From");

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText("To");
		
		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText("Duration");

		WeightedTableLayout layout = new WeightedTableLayout(new int[]{60, 30, 30, 30});
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
			if (element instanceof IssueWorkTimeRange) {
				IssueWorkTimeRange issueWorkTimeRange = (IssueWorkTimeRange) element;
				switch (columnIndex) 
				{
				case(0):
					return issueWorkTimeRange.getUser().getName();
				case(1):
					return dateTimeFormat.format(issueWorkTimeRange.getFrom());
				case(2):
					return issueWorkTimeRange.getTo() == null ? "(Still working)" : dateTimeFormat.format(issueWorkTimeRange.getTo());
				case(3):
					return issueWorkTimeRange.getDuration() == null ? "(Still working)" : DurationFormatUtils.formatDurationWords(issueWorkTimeRange.getDuration().longValue(), true, true);
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
	public void setIssueWorkTimeRanges(IssueID issueID, Collection<IssueWorkTimeRange> issueWorkTimeRanges)
	{
		if (issueID == null)
			throw new IllegalArgumentException("issueID == null"); //$NON-NLS-1$

		this.issueID = issueID;
		super.setInput(issueWorkTimeRanges);
	}

	@Override
	public void setInput(Object input)
	{
		throw new UnsupportedOperationException("Use setIssueHistories(...) or setLoadingStatus(...) instead!"); //$NON-NLS-1$
	}
}

