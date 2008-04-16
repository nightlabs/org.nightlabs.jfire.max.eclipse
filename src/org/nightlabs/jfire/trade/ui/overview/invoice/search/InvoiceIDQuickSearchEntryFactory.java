package org.nightlabs.jfire.trade.ui.overview.invoice.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.query.InvoiceQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerIDQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class InvoiceIDQuickSearchEntryFactory
	extends AbstractArticleContainerIDQuickSearchEntryFactory<InvoiceQuery>
{
	public QuickSearchEntry<InvoiceQuery> createQuickSearchEntry()
	{
		return new InvoiceIDQuickSearchEntry(this);
	}

	@Override
	public Class<InvoiceQuery> getQueryType()
	{
		return InvoiceQuery.class;
	}
}
