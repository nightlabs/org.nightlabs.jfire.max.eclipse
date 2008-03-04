package org.nightlabs.jfire.trade.ui.overview.offer.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.query.OfferQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerQuickSearchEntry;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class OfferIDQuickSearchEntry
	extends AbstractArticleContainerQuickSearchEntry<Offer, OfferQuery>
{
	public OfferIDQuickSearchEntry(QuickSearchEntryFactory<Offer, OfferQuery> factory) {
		super(factory, OfferQuery.class);
	}

//	@Override
//	protected void doResetSearchCondition(OfferQuery query, String lastValue)
//	{
//		query.setArticleContainerID(lastValue);
//	}

	@Override
	protected void doSetSearchConditionValue(OfferQuery query, String value)
	{
		query.setArticleContainerID(value);
	}

	@Override
	protected void doUnsetSearchConditionValue(OfferQuery query)
	{
		query.setArticleContainerID(null);
	}

//	@Override
//	public String[] getFetchGroups() {
//		return OfferEntryViewer.FETCH_GROUPS_OFFERS;
//	}
//
//	@Override
//	public AbstractArticleContainerQuery getQuery() {
//		AbstractArticleContainerQuery query = new OfferQuery();
//		query.setArticleContainerID(getSearchText());
//		return query;
//	}
}
