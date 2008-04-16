package org.nightlabs.jfire.trade.ui.overview.deliverynote.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.query.DeliveryNoteQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerQuickSearchEntry;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public class DeliveryNoteVendorNameQuickSearchEntry
	extends AbstractArticleContainerQuickSearchEntry<DeliveryNoteQuery>
{
	public DeliveryNoteVendorNameQuickSearchEntry(QuickSearchEntryFactory<DeliveryNoteQuery> factory)
	{
		super(factory, DeliveryNoteQuery.class);
	}

//	@Override
//	public AbstractArticleContainerQuery getQuery() {
//		AbstractArticleContainerQuery query = new DeliveryNoteQuery();
//		query.setVendorName(getSearchText());
//		return query;
//	}
//
//	@Override
//	public String[] getFetchGroups() {
//		return DeliveryNoteEntryViewer.FETCH_GROUPS_DELIVERY_NOTES;
//	}

	@Override
	protected void doSetSearchConditionValue(DeliveryNoteQuery query, String value)
	{
		query.setVendorName(value);
	}

	@Override
	protected void doUnsetSearchConditionValue(DeliveryNoteQuery query)
	{
		query.setVendorName(null);
	}

//	@Override
//	protected void doResetSearchCondition(DeliveryNoteQuery query, String lastValue)
//	{
//		query.setVendorName(lastValue);
//	}
}
