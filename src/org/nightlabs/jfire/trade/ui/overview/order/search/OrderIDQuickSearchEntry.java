package org.nightlabs.jfire.trade.ui.overview.order.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.query.OrderQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerQuickSearchEntry;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class OrderIDQuickSearchEntry
	extends AbstractArticleContainerQuickSearchEntry<OrderQuery>
{
	public OrderIDQuickSearchEntry(QuickSearchEntryFactory<OrderQuery> factory)
	{
		super(factory, OrderQuery.class);
	}

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
}
