package org.nightlabs.jfire.trade.ui.overview.invoice.search;

import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.trade.query.InvoiceQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractVendorNameQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class InvoiceVendorNameQuickSearchEntryFactory
	extends AbstractVendorNameQuickSearchEntryFactory<Invoice, InvoiceQuery>
{
	public InvoiceVendorNameQuickSearchEntry createQuickSearchEntry() {
		return new InvoiceVendorNameQuickSearchEntry(this);
	}

	@Override
	public Class<? extends InvoiceQuery> getQueryType()
	{
		return InvoiceQuery.class;
	}
}
