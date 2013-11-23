package org.nightlabs.jfire.trade.ui.overview.invoice.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.query.InvoiceQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractCustomerNameQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class InvoiceCustomerNameQuickSearchEntryFactory
extends AbstractCustomerNameQuickSearchEntryFactory<InvoiceQuery>
{
	public QuickSearchEntry<InvoiceQuery> createQuickSearchEntry()
	{
		return new InvoiceCustomerNameQuickSearchEntry(this);
	}

	@Override
	public Class<? extends InvoiceQuery> getQueryType()
	{
		return InvoiceQuery.class;
	}
}
