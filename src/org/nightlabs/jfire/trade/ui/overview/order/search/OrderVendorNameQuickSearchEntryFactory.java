package org.nightlabs.jfire.trade.ui.overview.order.search;

import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.query.OrderQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractVendorNameQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class OrderVendorNameQuickSearchEntryFactory
	extends AbstractVendorNameQuickSearchEntryFactory<Order, OrderQuery>
{
	public OrderVendorNameQuickSearchEntry createQuickSearchEntry() {
		return new OrderVendorNameQuickSearchEntry(this);
	}

	@Override
	public Class<? extends OrderQuery> getQueryType()
	{
		return OrderQuery.class;
	}
}
