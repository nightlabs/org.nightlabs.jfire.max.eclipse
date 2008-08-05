package org.nightlabs.jfire.trade.ui.overview.invoice.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuery;
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

	@Override
	protected String getModifiedQueryFieldName()
	{
		return AbstractArticleContainerQuery.FieldName.customerName;
	}
}
