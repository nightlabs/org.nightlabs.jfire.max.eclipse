package org.nightlabs.jfire.trade.ui.overview.deliverynote.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.query.DeliveryNoteQuickSearchQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerQuickSearchEntry;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public class DeliveryNoteVendorNameQuickSearchEntry
	extends AbstractArticleContainerQuickSearchEntry<DeliveryNote, DeliveryNoteQuickSearchQuery>
{
	public DeliveryNoteVendorNameQuickSearchEntry(QuickSearchEntryFactory<DeliveryNote, DeliveryNoteQuickSearchQuery> factory) {
		super(factory, DeliveryNoteQuickSearchQuery.class);
	}

//	@Override
//	public AbstractArticleContainerQuickSearchQuery getQuery() {
//		AbstractArticleContainerQuickSearchQuery query = new DeliveryNoteQuickSearchQuery();
//		query.setVendorName(getSearchText());
//		return query;
//	}
//
//	@Override
//	public String[] getFetchGroups() {
//		return DeliveryNoteEntryViewer.FETCH_GROUPS_DELIVERY_NOTES;
//	}

	@Override
	protected void doSetSearchConditionValue(DeliveryNoteQuickSearchQuery query, String value)
	{
		query.setVendorName(value);
	}

	@Override
	protected void doUnsetSearchConditionValue(DeliveryNoteQuickSearchQuery query)
	{
		query.setVendorName(null);
	}

//	@Override
//	protected void doResetSearchCondition(DeliveryNoteQuickSearchQuery query, String lastValue)
//	{
//		query.setVendorName(lastValue);
//	}
}
