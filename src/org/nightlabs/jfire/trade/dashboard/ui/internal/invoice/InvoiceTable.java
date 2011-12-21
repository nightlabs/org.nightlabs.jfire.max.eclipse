package org.nightlabs.jfire.trade.dashboard.ui.internal.invoice;

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

public class InvoiceTable extends AbstractTableComposite<InvoiceTableItem> {
	
	public InvoiceTable(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, final Table table) 
	{
		TableColumn col0 = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		col0.setText("ID");
		TableColumn col1 = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		col1.setText("Customer");
		TableColumn col2 = new TableColumn(tableViewer.getTable(), SWT.RIGHT);
		col2.setText("Amount");
		
		final TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(30));
		tableLayout.addColumnData(new ColumnWeightData(60));
		tableLayout.addColumnData(new ColumnWeightData(30));
		table.getDisplay().asyncExec(new Runnable() {
			@Override
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
					return ((InvoiceTableItem) element).getInvoiceId();
				if (columnIndex == 1)
					return ((InvoiceTableItem) element).getBusinessPartnerName();
				if (columnIndex == 2)
					return ((InvoiceTableItem) element).getInvoiceAmount();
				return "";
			}
		});
	}
	
}