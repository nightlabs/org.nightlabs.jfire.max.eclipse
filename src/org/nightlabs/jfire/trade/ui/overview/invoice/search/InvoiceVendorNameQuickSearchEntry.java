package org.nightlabs.jfire.trade.ui.overview.invoice.search;

import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.query.InvoiceQuickSearchQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerQuickSearchEntry;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class InvoiceVendorNameQuickSearchEntry
	extends AbstractArticleContainerQuickSearchEntry<Invoice, InvoiceQuickSearchQuery>
{
	public InvoiceVendorNameQuickSearchEntry(QuickSearchEntryFactory<Invoice, InvoiceQuickSearchQuery> factory) {
		super(factory, InvoiceQuickSearchQuery.class);
	}

//	@Override
//	protected void doResetSearchCondition(InvoiceQuickSearchQuery query, String lastValue)
//	{
//		query.setVendorName(lastValue);
//	}

	@Override
	protected void doSetSearchConditionValue(InvoiceQuickSearchQuery query, String value)
	{
		query.setVendorName(value);
	}

	@Override
	protected void doUnsetSearchConditionValue(InvoiceQuickSearchQuery query)
	{
		query.setVendorName(null);
	}

//	@Override
//	public String[] getFetchGroups() {
//		return InvoiceEntryViewer.FETCH_GROUPS_INVOICES;
//	}
//
//	@Override
//	public AbstractArticleContainerQuickSearchQuery getQuery() {
//		AbstractArticleContainerQuickSearchQuery query = new InvoiceQuickSearchQuery();
//		query.setVendorName(getSearchText());
//		return query;
//	}
}
