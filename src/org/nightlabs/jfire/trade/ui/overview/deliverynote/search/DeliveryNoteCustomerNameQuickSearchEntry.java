/**
 * 
 */
package org.nightlabs.jfire.trade.ui.overview.deliverynote.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuickSearchQuery;
import org.nightlabs.jfire.trade.query.DeliveryNoteQuickSearchQuery;
import org.nightlabs.jfire.trade.ui.overview.deliverynote.DeliveryNoteEntryViewer;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerQuickSearchEntry;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public class DeliveryNoteCustomerNameQuickSearchEntry
extends AbstractArticleContainerQuickSearchEntry
{
	public DeliveryNoteCustomerNameQuickSearchEntry(QuickSearchEntryFactory factory) {
		super(factory);
	}

	@Override
	public AbstractArticleContainerQuickSearchQuery getQuery() {
		AbstractArticleContainerQuickSearchQuery query = new DeliveryNoteQuickSearchQuery();
		query.setCustomerName(getSearchText());
		return query;
	}

	@Override
	public String[] getFetchGroups() {
		return DeliveryNoteEntryViewer.FETCH_GROUPS_DELIVERY_NOTES;
	}
}
