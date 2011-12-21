package org.nightlabs.jfire.trade.dashboard.ui.internal.lastCustomers;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.trade.dashboard.ui.resource.Messages;

public class TransactionInfoTable extends AbstractTableComposite<TransactionInfoTableItem> {

	public TransactionInfoTable(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void createTableColumns(final TableViewer tableViewer, final Table table) {
		TableColumn col1 = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		col1.setText(Messages.getString(
			"org.nightlabs.jfire.trade.dashboard.ui.internal.lastCustomers.DashboardGadgetLastCustomers.TransactionInfoTable.column1.text")); //$NON-NLS-1$
		TableColumn col2 = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		col2.setText(Messages.getString(
			"org.nightlabs.jfire.trade.dashboard.ui.internal.lastCustomers.DashboardGadgetLastCustomers.TransactionInfoTable.column2.text")); //$NON-NLS-1$
		final TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(100));
		tableLayout.addColumnData(new ColumnWeightData(50));
		table.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (!table.isDisposed()) {
					table.setLayout(tableLayout);
					table.layout(true, true);
				}
				
			}
		});
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new TableLabelProvider() {
			
			@Override
			public String getColumnText(Object element, int columnIndex) {
				if (columnIndex == 0)
					return ((TransactionInfoTableItem) element).getLegalEntityName();
				if (columnIndex == 1) {
					String type = ((TransactionInfoTableItem) element).getTransactionInfo().getTransactionType();
					return Messages.getString(
						"org.nightlabs.jfire.trade.dashboard.ui.internal.lastCustomers.DashboardGadgetLastCustomers.TransactionInfoTable." + type); //$NON-NLS-1$
				}
				return ""; //$NON-NLS-1$
			}
		});
	}
}