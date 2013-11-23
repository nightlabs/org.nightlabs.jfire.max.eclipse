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
import org.nightlabs.jfire.issue.history.IssueHistoryItem;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 * This composite lists all {@link IssueWorkTimeRange}s of an issue in a table.
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssueWorkTimeRangeTable
extends AbstractTableComposite<IssueHistoryItem>
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
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeRangeTable.tableColumn.user.text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeRangeTable.tableColumn.from.text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeRangeTable.tableColumn.to.text")); //$NON-NLS-1$
		
		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeRangeTable.tableColumn.duration.text")); //$NON-NLS-1$

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
					return issueWorkTimeRange.getTo() == null ? Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeRangeTable.tableColumnText.workTimeRange.stillWorking.text") : dateTimeFormat.format(issueWorkTimeRange.getTo()); //$NON-NLS-1$
				case(3):
					return issueWorkTimeRange.getDuration() == 0? Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeRangeTable.tableColumnText.workTimeRange.stillWorking.text") : DurationFormatUtils.formatDurationWords(issueWorkTimeRange.getDuration(), true, true); //$NON-NLS-1$
				default:
					return ""; //$NON-NLS-1$
				}
			}
			return null;
		}		
	}

	public void setLoadingStatus()
	{
		super.setInput(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueWorkTimeRangeTable.table.loadingMessage.text")); //$NON-NLS-1$
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

