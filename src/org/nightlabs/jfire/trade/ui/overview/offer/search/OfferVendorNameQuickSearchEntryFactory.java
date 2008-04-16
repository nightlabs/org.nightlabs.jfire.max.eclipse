package org.nightlabs.jfire.trade.ui.overview.offer.search;

import org.nightlabs.jfire.trade.query.OfferQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractVendorNameQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class OfferVendorNameQuickSearchEntryFactory
	extends AbstractVendorNameQuickSearchEntryFactory<OfferQuery>
{
	public OfferVendorNameQuickSearchEntry createQuickSearchEntry()
	{
		return new OfferVendorNameQuickSearchEntry(this);
	}

	@Override
	public Class<? extends OfferQuery> getQueryType()
	{
		return OfferQuery.class;
	}
}
