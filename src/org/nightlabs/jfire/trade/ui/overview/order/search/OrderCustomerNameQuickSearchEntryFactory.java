package org.nightlabs.jfire.trade.ui.overview.order.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.query.OrderQuickSearchQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractCustomerNameQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class OrderCustomerNameQuickSearchEntryFactory
extends AbstractCustomerNameQuickSearchEntryFactory<Order, OrderQuickSearchQuery>
{
	public QuickSearchEntry<Order, OrderQuickSearchQuery> createQuickSearchEntry() {
		return new OrderCustomerNameQuickSearchEntry(this);
	}

	@Override
	public Class<? extends OrderQuickSearchQuery> getQueryType()
	{
		return OrderQuickSearchQuery.class;
	}
}
