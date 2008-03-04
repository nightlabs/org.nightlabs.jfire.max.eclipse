package org.nightlabs.jfire.trade.ui.overview.invoice.search;

import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.query.InvoiceQuickSearchQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractCustomerNameQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class InvoiceCustomerNameQuickSearchEntryFactory
extends AbstractCustomerNameQuickSearchEntryFactory<Invoice, InvoiceQuickSearchQuery>
{
	public QuickSearchEntry<Invoice, InvoiceQuickSearchQuery> createQuickSearchEntry() {
		return new InvoiceCustomerNameQuickSearchEntry(this);
	}

	@Override
	public Class<? extends InvoiceQuickSearchQuery> getQueryType()
	{
		return InvoiceQuickSearchQuery.class;
	}
}
