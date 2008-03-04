package org.nightlabs.jfire.trade.ui.overview.deliverynote.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.query.DeliveryNoteQuickSearchQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerQuickSearchEntry;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class DeliveryNoteIDQuickSearchEntry
	extends AbstractArticleContainerQuickSearchEntry<DeliveryNote, DeliveryNoteQuickSearchQuery>
{
	public DeliveryNoteIDQuickSearchEntry(QuickSearchEntryFactory<DeliveryNote, DeliveryNoteQuickSearchQuery> factory) {
		super(factory, DeliveryNoteQuickSearchQuery.class);
	}

	@Override
	protected void doSetSearchConditionValue(DeliveryNoteQuickSearchQuery query, String value)
	{
		query.setArticleContainerID(value);
	}

	@Override
	protected void doUnsetSearchConditionValue(DeliveryNoteQuickSearchQuery query)
	{
		query.setArticleContainerID(null);
	}

//	@Override
//	protected void doResetSearchCondition(DeliveryNoteQuickSearchQuery query, String lastValue)
//	{
//		query.setArticleContainerID(lastValue);
//	}

//	@Override
//	public AbstractArticleContainerQuickSearchQuery getQuery() {
//		AbstractArticleContainerQuickSearchQuery query = new DeliveryNoteQuickSearchQuery();
//		query.setArticleContainerID(getSearchText());
//		return query;
//	}

//	@Override
//	public String[] getFetchGroups() {
//		return DeliveryNoteEntryViewer.FETCH_GROUPS_DELIVERY_NOTES;
//	}
}
