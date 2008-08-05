package org.nightlabs.jfire.trade.ui.overview.order.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuery;
import org.nightlabs.jfire.trade.query.OrderQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerQuickSearchEntry;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class OrderVendorNameQuickSearchEntry
	extends AbstractArticleContainerQuickSearchEntry<OrderQuery>
{
	public OrderVendorNameQuickSearchEntry(QuickSearchEntryFactory<OrderQuery> factory)
	{
		super(factory, OrderQuery.class);
	}

	@Override
	protected String getModifiedQueryFieldName()
	{
		return AbstractArticleContainerQuery.FieldName.vendorName;
	}
}
