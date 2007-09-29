/**
 * 
 */
package org.nightlabs.jfire.trade.overview.offer.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.overview.offer.OfferEntryViewer;
import org.nightlabs.jfire.trade.overview.search.AbstractArticleContainerQuickSearchEntry;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuickSearchQuery;
import org.nightlabs.jfire.trade.query.OfferQuickSearchQuery;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public class OfferVendorNameQuickSearchEntry 
extends AbstractArticleContainerQuickSearchEntry 
{
	public OfferVendorNameQuickSearchEntry(QuickSearchEntryFactory factory) {
		super(factory);
	}

	@Override
	public String[] getFetchGroups() {
		return OfferEntryViewer.FETCH_GROUPS_OFFERS;
	}

	@Override
	public AbstractArticleContainerQuickSearchQuery getQuery() {
		AbstractArticleContainerQuickSearchQuery query = new OfferQuickSearchQuery();
		query.setVendorName(getSearchText());
		return query;
	}
}
