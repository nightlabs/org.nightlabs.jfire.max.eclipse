package org.nightlabs.jfire.trade.ui.overview.invoice.search;

import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.query.InvoiceQuickSearchQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerQuickSearchEntry;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class InvoiceCustomerNameQuickSearchEntry
	extends AbstractArticleContainerQuickSearchEntry<Invoice, InvoiceQuickSearchQuery>
{
	public InvoiceCustomerNameQuickSearchEntry(QuickSearchEntryFactory<Invoice, InvoiceQuickSearchQuery> factory) {
		super(factory, InvoiceQuickSearchQuery.class);
	}

//	@Override
//	protected void doResetSearchCondition(InvoiceQuickSearchQuery query, String lastValue)
//	{
//		query.setCustomerName(lastValue);
//	}

	@Override
	protected void doSetSearchConditionValue(InvoiceQuickSearchQuery query, String value)
	{
		query.setCustomerName(value);
	}

	@Override
	protected void doUnsetSearchConditionValue(InvoiceQuickSearchQuery query)
	{
		query.setCustomerName(null);
	}

//	@Override
//	public String[] getFetchGroups() {
//		return InvoiceEntryViewer.FETCH_GROUPS_INVOICES;
//	}
//
//	@Override
//	public AbstractArticleContainerQuickSearchQuery getQuery()
//	{
//		AbstractArticleContainerQuickSearchQuery query = new InvoiceQuickSearchQuery();
//		query.setCustomerName(getSearchText());
//		return query;
//	}

}
