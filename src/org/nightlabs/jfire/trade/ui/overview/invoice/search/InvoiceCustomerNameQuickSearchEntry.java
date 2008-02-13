/**
 * 
 */
package org.nightlabs.jfire.trade.ui.overview.invoice.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuickSearchQuery;
import org.nightlabs.jfire.trade.query.InvoiceQuickSearchQuery;
import org.nightlabs.jfire.trade.ui.overview.invoice.InvoiceEntryViewer;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerQuickSearchEntry;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public class InvoiceCustomerNameQuickSearchEntry
extends AbstractArticleContainerQuickSearchEntry
{
	public InvoiceCustomerNameQuickSearchEntry(QuickSearchEntryFactory factory) {
		super(factory);
	}

	@Override
	public String[] getFetchGroups() {
		return InvoiceEntryViewer.FETCH_GROUPS_INVOICES;
	}

	@Override
	public AbstractArticleContainerQuickSearchQuery getQuery()
	{
		AbstractArticleContainerQuickSearchQuery query = new InvoiceQuickSearchQuery();
		query.setCustomerName(getSearchText());
		return query;
	}
}
