package org.nightlabs.jfire.trade.ui.overview.offer.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.query.OfferQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerIDQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class OfferIDQuickSearchEntryFactory
extends AbstractArticleContainerIDQuickSearchEntryFactory<Offer, OfferQuery>
{
	public QuickSearchEntry<Offer, OfferQuery> createQuickSearchEntry() {
		return new OfferIDQuickSearchEntry(this);
	}

	@Override
	public Class<OfferQuery> getQueryType()
	{
		return OfferQuery.class;
	}
}
