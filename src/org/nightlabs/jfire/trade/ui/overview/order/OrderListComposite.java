package org.nightlabs.jfire.trade.ui.overview.order;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class OrderListComposite
extends AbstractArticleContainerListComposite 
{
	public OrderListComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Class<? extends ArticleContainer> getArticleContainerClass() {
		return Order.class;
	}

	@Implement
	@Override
	protected void createArticleContainerIDPrefixTableColumn(
			TableViewer tableViewer, Table table, TableLayout tableLayout)
	{
		TableColumn tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.order.OrderListComposite.orderIDPrefixTableColumn.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnWeightData(10));
	}

	@Implement
	@Override
	protected void createArticleContainerIDTableColumn(TableViewer tableViewer,
			Table table, TableLayout tableLayout)
	{
		TableColumn tc = new TableColumn(table, SWT.RIGHT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.order.OrderListComposite.orderIDTableColumn.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnWeightData(10));
	}

	@Implement
	@Override
	protected void createAdditionalTableColumns(TableViewer tableViewer,
			Table table, TableLayout tableLayout)
	{
		TableColumn tc = new TableColumn(table, SWT.RIGHT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.order.OrderListComposite.changeDateTableColumn.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnWeightData(10));

		tc = new TableColumn(table, SWT.RIGHT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.order.OrderListComposite.changeUserTableColumn.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnWeightData(10));
	}

	@Override
	protected String getColumnText(Object element, int columnIndex) 
	{
		int columnCount = getTable().getColumnCount();		
		if (element instanceof DeliveryNote) 
		{
			Order order = (Order) element;

			if (columnIndex == (columnCount-3)) {
				if (order.getChangeDT() != null)
					return DateFormatter.formatDateShort(order.getChangeDT(), false);				
			}						
			
			if (columnIndex == (columnCount-2)) {
				if (order.getChangeUser() != null)
					return order.getChangeUser().getName();
			}			
			
			if (columnIndex == (columnCount-1))
				return order.getOrderIDPrefix();			
		}
		return super.getColumnText(element, columnIndex);
	}

	@Implement
	@Override
	protected String getAdditionalColumnText(Object element,
			int additionalColumnIndex, int firstAdditionalColumnIndex, int columnIndex)
	{
		if (!(element instanceof Order))
			return ""; //$NON-NLS-1$

		Order order = (Order) element;
		switch (additionalColumnIndex) {
			case 0:
				if (order.getChangeDT() != null)
					return DateFormatter.formatDateShort(order.getChangeDT(), false);								
			break;
			case 1:
				if (order.getChangeUser() != null)
					return order.getChangeUser().getName();
			break;
		}

		return ""; //$NON-NLS-1$
	}
}
