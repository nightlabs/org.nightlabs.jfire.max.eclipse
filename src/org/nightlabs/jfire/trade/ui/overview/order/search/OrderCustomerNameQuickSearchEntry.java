package org.nightlabs.jfire.trade.ui.overview.order.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.query.OrderQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerQuickSearchEntry;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class OrderCustomerNameQuickSearchEntry
	extends AbstractArticleContainerQuickSearchEntry<Order, OrderQuery>
{
	public OrderCustomerNameQuickSearchEntry(QuickSearchEntryFactory<Order, OrderQuery> factory) {
		super(factory, OrderQuery.class);
	}

//	@Override
//	protected void doResetSearchCondition(OrderQuery query, String lastValue)
//	{
//		query.setCustomerName(lastValue);
//	}

	@Override
	protected void doSetSearchConditionValue(OrderQuery query, String value)
	{
		query.setCustomerName(value);
	}

	@Override
	protected void doUnsetSearchConditionValue(OrderQuery query)
	{
		query.setCustomerName(null);
	}

//	@Override
//	public String[] getFetchGroups() {
//		return OrderEntryViewer.FETCH_GROUPS_ORDERS;
//	}
//
//	@Override
//	public AbstractArticleContainerQuery getQuery() {
//		AbstractArticleContainerQuery query = new OrderQuery();
//		query.setCustomerName(getSearchText());
//		return query;
//	}
}
