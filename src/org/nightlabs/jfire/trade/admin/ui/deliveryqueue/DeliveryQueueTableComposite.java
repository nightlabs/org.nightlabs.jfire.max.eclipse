package org.nightlabs.jfire.trade.admin.ui.deliveryqueue;

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
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.util.NLLocale;

public class DeliveryQueueTableComposite extends AbstractTableComposite<DeliveryQueue> {
	public DeliveryQueueTableComposite(Composite parent) {
		super(parent, SWT.NONE, true, AbstractTableComposite.DEFAULT_STYLE_SINGLE | SWT.CHECK | SWT.BORDER);
	}
	
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.deliveryqueue.DeliveryQueueTableComposite.nameTableColumn.text")); //$NON-NLS-1$
		table.setLayout(new WeightedTableLayout(new int[] {1}));
//		table.setSortColumn(nameColumn);
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setLabelProvider(new TableLabelProvider() {
			public String getColumnText(Object element, int columnIndex) {
				if (element instanceof DeliveryQueue) {
					DeliveryQueue pq = (DeliveryQueue) element;
					return pq.getName().getText(NLLocale.getDefault().getLanguage());
				}
				if (element instanceof String) {
					return (String) element;
				}
				return ""; //$NON-NLS-1$
			}
		});
		
		tableViewer.setContentProvider(new TableContentProvider());
	}

}
