package org.nightlabs.jfire.trade.ui.overview.offer.search;

import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.query.OfferQuickSearchQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractVendorNameQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class OfferVendorNameQuickSearchEntryFactory
	extends AbstractVendorNameQuickSearchEntryFactory<Offer, OfferQuickSearchQuery>
{
	public OfferVendorNameQuickSearchEntry createQuickSearchEntry() {
		return new OfferVendorNameQuickSearchEntry(this);
	}

	@Override
	public Class<? extends OfferQuickSearchQuery> getQueryType()
	{
		return OfferQuickSearchQuery.class;
	}
}
