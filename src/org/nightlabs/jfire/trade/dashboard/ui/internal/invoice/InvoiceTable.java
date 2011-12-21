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
import org.nightlabs.l10n.GlobalDateFormatter;
import org.nightlabs.l10n.IDateFormatter;

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
		col1.setText("Date");
		TableColumn col2 = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		col2.setText("Customer");
		TableColumn col3 = new TableColumn(tableViewer.getTable(), SWT.RIGHT);
		col3.setText("Amount");
		
		final TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(20));
		tableLayout.addColumnData(new ColumnWeightData(25));
		tableLayout.addColumnData(new ColumnWeightData(50));
		tableLayout.addColumnData(new ColumnWeightData(25));
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
				if (columnIndex == 1) {
					String date = GlobalDateFormatter.sharedInstance().formatDate(
							((InvoiceTableItem) element).getInvoiceCreationDate(), IDateFormatter.FLAGS_DATE_SHORT_TIME_HM);
					return date;
				}
				if (columnIndex == 2)
					return ((InvoiceTableItem) element).getBusinessPartnerName();
				if (columnIndex == 3)
					return ((InvoiceTableItem) element).getInvoiceAmount();
				return "";
			}
		});
	}
	
}