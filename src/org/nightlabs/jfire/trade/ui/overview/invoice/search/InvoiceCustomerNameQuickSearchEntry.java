package org.nightlabs.jfire.trade.ui.overview.invoice.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.query.InvoiceQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerQuickSearchEntry;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class InvoiceCustomerNameQuickSearchEntry
	extends AbstractArticleContainerQuickSearchEntry<InvoiceQuery>
{
	public InvoiceCustomerNameQuickSearchEntry(QuickSearchEntryFactory<InvoiceQuery> factory)
	{
		super(factory, InvoiceQuery.class);
	}

//	@Override
//	protected void doResetSearchCondition(InvoiceQuery query, String lastValue)
//	{
//		query.setCustomerName(lastValue);
//	}

	@Override
	protected void doSetSearchConditionValue(InvoiceQuery query, String value)
	{
		query.setCustomerName(value);
	}

	@Override
	protected void doUnsetSearchConditionValue(InvoiceQuery query)
	{
		query.setCustomerName(null);
	}

//	@Override
//	public String[] getFetchGroups() {
//		return InvoiceEntryViewer.FETCH_GROUPS_INVOICES;
//	}
//
//	@Override
//	public AbstractArticleContainerQuery getQuery()
//	{
//		AbstractArticleContainerQuery query = new InvoiceQuery();
//		query.setCustomerName(getSearchText());
//		return query;
//	}

}
