package org.nightlabs.jfire.trade.ui.overview.order;

import java.util.Comparator;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.util.BaseComparator;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class OrderListComposite
extends AbstractArticleContainerListComposite<Order>
{
	public static final String[] FETCH_GROUPS_ORDER =
		new String[] {FetchPlan.DEFAULT, Order.FETCH_GROUP_ARTICLES,
		Order.FETCH_GROUP_CHANGE_USER, Order.FETCH_GROUP_CREATE_USER,
		Order.FETCH_GROUP_CURRENCY, Order.FETCH_GROUP_CUSTOMER,
		Order.FETCH_GROUP_VENDOR, LegalEntity.FETCH_GROUP_PERSON};

	public static final Comparator<Order> ORDER_CHANGE_DT_COMPARATOR = new Comparator<Order>(){
		@Override
		public int compare(Order o1, Order o2)
		{
			int result = BaseComparator.comparatorNullCheck(o1, o2);
			if (result == BaseComparator.COMPARE_RESULT_NOT_NULL) {
				int result2 = BaseComparator.comparatorNullCheck(o1.getChangeDT(), o2.getChangeDT());
				if (result2== BaseComparator.COMPARE_RESULT_NOT_NULL) {
					return o1.getChangeDT().compareTo(o2.getChangeDT());
				}
				return result2;
			}
			return result;
		}
	};

	public OrderListComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Class<? extends ArticleContainer> getArticleContainerClass() {
		return Order.class;
	}

	@Override
	protected void createAdditionalTableColumns(TableViewer tableViewer,
			Table table)
	{
		TableColumn tc = new TableColumn(table, SWT.RIGHT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.order.OrderListComposite.changeDateTableColumn.text")); //$NON-NLS-1$
//		tableLayout.setColumnData(tc, new ColumnWeightData(10));
		addWeightedColumn(10);

		tc = new TableColumn(table, SWT.RIGHT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.order.OrderListComposite.changeUserTableColumn.text")); //$NON-NLS-1$
//		tableLayout.setColumnData(tc, new ColumnWeightData(10));
		addWeightedColumn(10);
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
					return DateFormatter.formatDateShortTimeHM(order.getChangeDT(), true);
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
					return formatDate(order.getChangeDT());
			break;
			case 1:
				if (order.getChangeUser() != null)
					return order.getChangeUser().getName();
			break;
		}

		return ""; //$NON-NLS-1$
	}

	@Override
	protected Comparator<?> getAdditionalColumnComparator(Object element,
			int additionalColumnIndex, int firstAdditionalColumnIndex,
			int columnIndex) {
		if (additionalColumnIndex == 0) {
			return ORDER_CHANGE_DT_COMPARATOR;
		}
		return null;
	}
}
