package org.nightlabs.jfire.trade.ui.overview.order.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.query.OrderQuickSearchQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerQuickSearchEntry;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class OrderIDQuickSearchEntry
	extends AbstractArticleContainerQuickSearchEntry<Order, OrderQuickSearchQuery>
{
	public OrderIDQuickSearchEntry(QuickSearchEntryFactory<Order, OrderQuickSearchQuery> factory) {
		super(factory, OrderQuickSearchQuery.class);
	}

//	@Override
//	protected void doResetSearchCondition(OrderQuickSearchQuery query, String lastValue)
//	{
//		query.setArticleContainerID(lastValue);
//	}

	@Override
	protected void doSetSearchConditionValue(OrderQuickSearchQuery query, String value)
	{
		query.setArticleContainerID(value);
	}

	@Override
	protected void doUnsetSearchConditionValue(OrderQuickSearchQuery query)
	{
		query.setArticleContainerID(null);
	}

//	@Override
//	public String[] getFetchGroups() {
//		return OrderEntryViewer.FETCH_GROUPS_ORDERS;
//	}
//
//	@Override
//	public AbstractArticleContainerQuickSearchQuery getQuery() {
//		AbstractArticleContainerQuickSearchQuery query = new OrderQuickSearchQuery();
//		query.setArticleContainerID(getSearchText());
//		return query;
//	}
}
