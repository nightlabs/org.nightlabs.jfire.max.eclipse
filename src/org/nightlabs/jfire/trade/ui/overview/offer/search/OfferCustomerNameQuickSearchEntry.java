package org.nightlabs.jfire.trade.ui.overview.offer.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.query.OfferQuickSearchQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerQuickSearchEntry;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class OfferCustomerNameQuickSearchEntry
extends AbstractArticleContainerQuickSearchEntry<Offer, OfferQuickSearchQuery>
{
	public OfferCustomerNameQuickSearchEntry(QuickSearchEntryFactory<Offer, OfferQuickSearchQuery> factory) {
		super(factory, OfferQuickSearchQuery.class);
	}

//	@Override
//	protected void doResetSearchCondition(OfferQuickSearchQuery query, String lastValue)
//	{
//		query.setCustomerName(lastValue);
//	}

	@Override
	protected void doSetSearchConditionValue(OfferQuickSearchQuery query, String value)
	{
		query.setCustomerName(value);
	}

	@Override
	protected void doUnsetSearchConditionValue(OfferQuickSearchQuery query)
	{
		query.setCustomerName(null);
	}

//	@Override
//	public String[] getFetchGroups() {
//		return OfferEntryViewer.FETCH_GROUPS_OFFERS;
//	}
//
//	@Override
//	public AbstractArticleContainerQuickSearchQuery getQuery() {
//		AbstractArticleContainerQuickSearchQuery query = new OfferQuickSearchQuery();
//		query.setCustomerName(getSearchText());
//		return query;
//	}
}
