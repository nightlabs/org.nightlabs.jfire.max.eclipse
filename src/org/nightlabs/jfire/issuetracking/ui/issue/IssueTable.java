package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.Collection;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * This composite lists all {@link Issue}s of an issue in a table.
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssueTable
extends AbstractTableComposite<Issue>
{
	private IssueID issueID;

	/**
	 * The fetch groups of money transfer data.
	 */
	public static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT}	;

	public IssueTable(Composite parent, int style)
	{
		super(parent, style);
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
		if (currentIssueID == null)
			throw new IllegalArgumentException("currentIssueID == null"); //$NON-NLS-1$

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
					if (issue.getDescription() != null)
						return issue.getDescription().getText(); 
				break;
				case(2):
					return issue.getCreateTimestamp().toString();
				case(3):
					if (issue.getUpdateTimestamp() != null)
						return issue.getUpdateTimestamp().toString();
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
		super.setInput(Messages.getString("org.nightlabs.jfire.trade.ui.repository.transfer.ProductTransferTable.loadingDataPlaceholder")); //$NON-NLS-1$
	}

	@Override
	public void setInput(Object input)
	{
		throw new UnsupportedOperationException("Use setMoneyTransfers(...) or setLoadingStatus(...) instead!"); //$NON-NLS-1$
	}

}
