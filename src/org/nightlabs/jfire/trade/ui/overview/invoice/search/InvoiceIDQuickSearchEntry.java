package org.nightlabs.jfire.trade.ui.overview.invoice.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.query.InvoiceQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerQuickSearchEntry;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class InvoiceIDQuickSearchEntry
	extends AbstractArticleContainerQuickSearchEntry<InvoiceQuery>
{
	public InvoiceIDQuickSearchEntry(QuickSearchEntryFactory<InvoiceQuery> factory)
	{
		super(factory, InvoiceQuery.class);
	}

//	@Override
//	protected void doResetSearchCondition(InvoiceQuery query, String lastValue)
//	{
//		query.setArticleContainerID(lastValue);
//	}

	@Override
	protected void doSetSearchConditionValue(InvoiceQuery query, String value)
	{
		query.setArticleContainerID(value);
	}

	@Override
	protected void doUnsetSearchConditionValue(InvoiceQuery query)
	{
		query.setArticleContainerID(null);
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
//		query.setArticleContainerID(getSearchText());
//		return query;
//	}
}
