package org.nightlabs.jfire.trade.ui.overview.offer.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.query.OfferQuickSearchQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerIDQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class OfferIDQuickSearchEntryFactory
extends AbstractArticleContainerIDQuickSearchEntryFactory<Offer, OfferQuickSearchQuery>
{
	public QuickSearchEntry<Offer, OfferQuickSearchQuery> createQuickSearchEntry() {
		return new OfferIDQuickSearchEntry(this);
	}

	@Override
	public Class<OfferQuickSearchQuery> getQueryType()
	{
		return OfferQuickSearchQuery.class;
	}
}
