package org.nightlabs.jfire.trade.ui.transfer.deliver;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.store.deliver.Delivery;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * This table serves to display the deliveries in a {@link PrintQueue}.
 * 
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 */
class DeliveryTable extends AbstractTableComposite<Delivery> {

	private static DateFormat dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
	
	private static class LabelProvider extends TableLabelProvider {
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Delivery) {
				Delivery delivery = (Delivery) element;
				switch (columnIndex) {
				case 0: return delivery.getPartner().getPerson().getDisplayName();
				case 1: {
					Set<DeliveryNoteID> deliveryNoteIDs = delivery.getDeliveryNoteIDs();
					StringBuilder sb = new StringBuilder();
					for (DeliveryNoteID deliveryNoteID : deliveryNoteIDs)
						sb.append(ObjectIDUtil.longObjectIDFieldToString(deliveryNoteID.deliveryNoteID)).append(", "); //$NON-NLS-1$
					return sb.substring(0, sb.length()-2);
				}
				case 2: {
					Set<OrderID> orderIDs = new HashSet<OrderID>();
					for (Article article : delivery.getArticles()) {
						orderIDs.add(article.getOrderID());
					}
					
					StringBuilder sb = new StringBuilder();
					for (OrderID orderID : orderIDs) {
						sb.append(ObjectIDUtil.longObjectIDFieldToString(orderID.orderID)).append(", "); //$NON-NLS-1$
					}
					return sb.substring(0, sb.length()-2);
				}
				case 3: return dateTimeFormat.format(delivery.getEndDT());
				case 4: return Integer.toString(delivery.getArticleIDs().size());
				case 5: return ObjectIDUtil.longObjectIDFieldToString(delivery.getDeliveryID());
				case 6: return delivery.getUser().getName();
				default: return null;
				}
			}
			return null;
		}
	}
	
	public DeliveryTable(Composite parent, int style) {
		super(parent, style, true, SWT.CHECK | SWT.FULL_SELECTION | SWT.BORDER);
		getTableViewer().setComparator(new ViewerComparator() {
			@Override
			public void sort(Viewer viewer, Object[] elements) {
				Arrays.sort(elements, new Comparator<Object>() {
					public int compare(Object o1, Object o2) {
						return ((Delivery)o1).getEndDT().compareTo(((Delivery)o2).getEndDT());
					}
				});
			}
		});
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.deliver.DeliveryTable.customerNameTableColumn.text")); //$NON-NLS-1$
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.deliver.DeliveryTable.column.deliveryNoteIDs")); //$NON-NLS-1$
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.deliver.DeliveryTable.column.orderIDs")); //$NON-NLS-1$
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.deliver.DeliveryTable.enqueueDateTableColumn.text")); //$NON-NLS-1$
		new TableColumn(table, SWT.RIGHT).setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.deliver.DeliveryTable.articleCountTableColumn.text")); //$NON-NLS-1$
		new TableColumn(table, SWT.RIGHT).setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.deliver.DeliveryTable.deliveryIDTableColumn.text")); //$NON-NLS-1$
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.deliver.DeliveryTable.userTableColumn.text")); //$NON-NLS-1$
		table.setLayout(new WeightedTableLayout(new int[] { 3, -1, -1, -1, -1, -1, 1}, new int[] { -1, 120, 120, 120, 30, 60, -1}));
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setLabelProvider(new LabelProvider());
		tableViewer.setContentProvider(new TableContentProvider());
	}
}
