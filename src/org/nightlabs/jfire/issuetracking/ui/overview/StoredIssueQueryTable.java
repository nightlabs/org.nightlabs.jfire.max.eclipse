package org.nightlabs.jfire.issuetracking.ui.overview;

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
import org.nightlabs.jfire.issue.config.StoredIssueQuery;

public class StoredIssueQueryTable
extends AbstractTableComposite<StoredIssueQuery>
{

	public StoredIssueQueryTable(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableLayout layout = new TableLayout();
		
		TableColumn tc = new TableColumn(table, SWT.NONE);
		tc.setResizable(true);
		layout.addColumnData(new ColumnWeightData(100));
		
		table.setLayout(layout);
		table.setHeaderVisible(false);
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new StoredIssueQueryLabelProvider());
	}
	
	class StoredIssueQueryLabelProvider
	extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex) 
		{
			if (element instanceof StoredIssueQuery) {
				StoredIssueQuery storedIssueQuery = (StoredIssueQuery) element;
				switch (columnIndex) 
				{
				case(0):
					return storedIssueQuery.getName();
				default:
					return ""; //$NON-NLS-1$
				}
			}
			return null;
		}		
	}
}
