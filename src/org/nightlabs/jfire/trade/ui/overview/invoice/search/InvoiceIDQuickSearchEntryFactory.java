package org.nightlabs.jfire.trade.ui.overview.invoice.search;

import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.query.InvoiceQuickSearchQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerIDQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class InvoiceIDQuickSearchEntryFactory
	extends AbstractArticleContainerIDQuickSearchEntryFactory<Invoice, InvoiceQuickSearchQuery>
{
	public QuickSearchEntry<Invoice, InvoiceQuickSearchQuery> createQuickSearchEntry() {
		return new InvoiceIDQuickSearchEntry(this);
	}

	@Override
	public Class<InvoiceQuickSearchQuery> getQueryType()
	{
		return InvoiceQuickSearchQuery.class;
	}
}
