/**
 *
 */
package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issuetracking.admin.ui.resource.Messages;

/**
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 *
 */
public class IssuePriorityTable
extends AbstractTableComposite<IssuePriority>
{
	public IssuePriorityTable(Composite parent, int style)
	{
		super(parent, style);
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn tc;
//		TableLayout layout = new TableLayout();
		TableLayout layout = new WeightedTableLayout(new int[] {1});

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssuePriorityTable.tableColumn.name.text")); //$NON-NLS-1$
//		layout.addColumnData(new ColumnWeightData(1, true));

		table.setLayout(layout);

	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new IssueTypePriorityLabelProvider());
	}

	class IssueTypePriorityLabelProvider
	extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof IssuePriority) {
				IssuePriority issuePriority = (IssuePriority) element;
				switch (columnIndex)
				{
				case(0):
					return issuePriority.getIssuePriorityText().getText();
				default:
					return ""; //$NON-NLS-1$
				}
			}
			return null;
		}
	}

}
