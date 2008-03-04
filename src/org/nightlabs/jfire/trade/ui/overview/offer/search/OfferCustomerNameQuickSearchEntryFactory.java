package org.nightlabs.jfire.trade.ui.overview.offer.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.query.OfferQuickSearchQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractCustomerNameQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class OfferCustomerNameQuickSearchEntryFactory
extends AbstractCustomerNameQuickSearchEntryFactory<Offer, OfferQuickSearchQuery>
{
	public QuickSearchEntry<Offer, OfferQuickSearchQuery> createQuickSearchEntry() {
		return new OfferCustomerNameQuickSearchEntry(this);
	}

	@Override
	public Class<? extends OfferQuickSearchQuery> getQueryType()
	{
		return OfferQuickSearchQuery.class;
	}
}
