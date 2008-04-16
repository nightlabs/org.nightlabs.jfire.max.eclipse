package org.nightlabs.jfire.trade.ui.overview.offer.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.query.OfferQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractCustomerNameQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class OfferCustomerNameQuickSearchEntryFactory
	extends AbstractCustomerNameQuickSearchEntryFactory<OfferQuery>
{
	public QuickSearchEntry<OfferQuery> createQuickSearchEntry()
	{
		return new OfferCustomerNameQuickSearchEntry(this);
	}

	@Override
	public Class<? extends OfferQuery> getQueryType()
	{
		return OfferQuery.class;
	}
}
