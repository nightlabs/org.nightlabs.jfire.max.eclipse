package org.nightlabs.jfire.trade.ui.overview.order.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.query.OrderQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerQuickSearchEntry;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class OrderIDQuickSearchEntry
	extends AbstractArticleContainerQuickSearchEntry<Order, OrderQuery>
{
	public OrderIDQuickSearchEntry(QuickSearchEntryFactory<Order, OrderQuery> factory) {
		super(factory, OrderQuery.class);
	}

//	@Override
//	protected void doResetSearchCondition(OrderQuery query, String lastValue)
//	{
//		query.setArticleContainerID(lastValue);
//	}

	@Override
	protected void doSetSearchConditionValue(OrderQuery query, String value)
	{
		query.setArticleContainerID(value);
	}

	@Override
	protected void doUnsetSearchConditionValue(OrderQuery query)
	{
		query.setArticleContainerID(null);
	}

//	@Override
//	public String[] getFetchGroups() {
//		return OrderEntryViewer.FETCH_GROUPS_ORDERS;
//	}
//
//	@Override
//	public AbstractArticleContainerQuery getQuery() {
//		AbstractArticleContainerQuery query = new OrderQuery();
//		query.setArticleContainerID(getSearchText());
//		return query;
//	}
}
