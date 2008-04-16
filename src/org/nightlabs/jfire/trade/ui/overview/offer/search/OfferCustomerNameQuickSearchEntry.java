package org.nightlabs.jfire.trade.ui.overview.offer.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.query.OfferQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerQuickSearchEntry;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class OfferCustomerNameQuickSearchEntry
extends AbstractArticleContainerQuickSearchEntry<OfferQuery>
{
	public OfferCustomerNameQuickSearchEntry(QuickSearchEntryFactory<OfferQuery> factory)
	{
		super(factory, OfferQuery.class);
	}

//	@Override
//	protected void doResetSearchCondition(OfferQuery query, String lastValue)
//	{
//		query.setCustomerName(lastValue);
//	}

	@Override
	protected void doSetSearchConditionValue(OfferQuery query, String value)
	{
		query.setCustomerName(value);
	}

	@Override
	protected void doUnsetSearchConditionValue(OfferQuery query)
	{
		query.setCustomerName(null);
	}

//	@Override
//	public String[] getFetchGroups() {
//		return OfferEntryViewer.FETCH_GROUPS_OFFERS;
//	}
//
//	@Override
//	public AbstractArticleContainerQuery getQuery() {
//		AbstractArticleContainerQuery query = new OfferQuery();
//		query.setCustomerName(getSearchText());
//		return query;
//	}
}
