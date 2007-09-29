/**
 * 
 */
package org.nightlabs.jfire.trade.overview.invoice.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.overview.invoice.InvoiceEntryViewer;
import org.nightlabs.jfire.trade.overview.search.AbstractArticleContainerQuickSearchEntry;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuickSearchQuery;
import org.nightlabs.jfire.trade.query.InvoiceQuickSearchQuery;

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
