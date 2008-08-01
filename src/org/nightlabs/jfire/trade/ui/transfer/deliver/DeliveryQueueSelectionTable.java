package org.nightlabs.jfire.trade.ui.transfer.deliver;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.store.deliver.DeliveryQueue;

class DeliveryQueueSelectionTable extends AbstractTableComposite<DeliveryQueue> {

	public DeliveryQueueSelectionTable(Composite parent) {
		super(parent, SWT.NONE);
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		table.setLayout(new WeightedTableLayout(new int[] { 1 }));
		table.setHeaderVisible(true);
		TableColumn column = new TableColumn(table, SWT.LEFT);
		column.setText("Delivery Queue Name");
//
//		column = new TableColumn(table, SWT.LEFT);
//		column.setText("# Pending Deliveries");
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setLabelProvider(new TableLabelProvider() {
			@Override
			public String getColumnText(Object obj, int column) {
				DeliveryQueue queue = (DeliveryQueue) obj;
				switch(column) {
				case 0: return queue.getName().getText();
//				case 1: return String.valueOf(queue.getPendingDeliveries().size());
				default: return ""; //$NON-NLS-1$
				}
				
			}
		});
		
		tableViewer.setContentProvider(new TableContentProvider());
		
	}

}
