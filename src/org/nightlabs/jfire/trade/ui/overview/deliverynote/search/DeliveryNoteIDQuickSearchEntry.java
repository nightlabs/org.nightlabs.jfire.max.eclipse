package org.nightlabs.jfire.trade.ui.overview.deliverynote.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.query.DeliveryNoteQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerQuickSearchEntry;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class DeliveryNoteIDQuickSearchEntry
	extends AbstractArticleContainerQuickSearchEntry<DeliveryNote, DeliveryNoteQuery>
{
	public DeliveryNoteIDQuickSearchEntry(QuickSearchEntryFactory<DeliveryNote, DeliveryNoteQuery> factory) {
		super(factory, DeliveryNoteQuery.class);
	}

	@Override
	protected void doSetSearchConditionValue(DeliveryNoteQuery query, String value)
	{
		query.setArticleContainerID(value);
	}

	@Override
	protected void doUnsetSearchConditionValue(DeliveryNoteQuery query)
	{
		query.setArticleContainerID(null);
	}

//	@Override
//	protected void doResetSearchCondition(DeliveryNoteQuery query, String lastValue)
//	{
//		query.setArticleContainerID(lastValue);
//	}

//	@Override
//	public AbstractArticleContainerQuery getQuery() {
//		AbstractArticleContainerQuery query = new DeliveryNoteQuery();
//		query.setArticleContainerID(getSearchText());
//		return query;
//	}

//	@Override
//	public String[] getFetchGroups() {
//		return DeliveryNoteEntryViewer.FETCH_GROUPS_DELIVERY_NOTES;
//	}
}
