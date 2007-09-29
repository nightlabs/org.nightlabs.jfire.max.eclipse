/**
 * 
 */
package org.nightlabs.jfire.trade.overview.order.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.overview.order.OrderEntryViewer;
import org.nightlabs.jfire.trade.overview.search.AbstractArticleContainerQuickSearchEntry;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuickSearchQuery;
import org.nightlabs.jfire.trade.query.OrderQuickSearchQuery;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public class OrderVendorNameQuickSearchEntry 
extends AbstractArticleContainerQuickSearchEntry 
{
	public OrderVendorNameQuickSearchEntry(QuickSearchEntryFactory factory) {
		super(factory);
	}

	@Override
	public String[] getFetchGroups() {
		return OrderEntryViewer.FETCH_GROUPS_ORDERS;
	}

	@Override
	public AbstractArticleContainerQuickSearchQuery getQuery() {
		AbstractArticleContainerQuickSearchQuery query = new OrderQuickSearchQuery();
		query.setVendorName(getSearchText());
		return query;
	}
}
