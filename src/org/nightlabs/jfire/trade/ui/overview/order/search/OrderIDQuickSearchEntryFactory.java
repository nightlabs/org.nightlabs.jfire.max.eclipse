package org.nightlabs.jfire.trade.ui.overview.order.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.query.OrderQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerIDQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class OrderIDQuickSearchEntryFactory
extends AbstractArticleContainerIDQuickSearchEntryFactory<Order, OrderQuery>
{
	public QuickSearchEntry<Order, OrderQuery> createQuickSearchEntry() {
		return new OrderIDQuickSearchEntry(this);
	}

	@Override
	public Class<OrderQuery> getQueryType()
	{
		return OrderQuery.class;
	}
}
