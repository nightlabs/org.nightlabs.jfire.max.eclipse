package org.nightlabs.jfire.trade.ui.overview.deliverynote.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.query.DeliveryNoteQuickSearchQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerQuickSearchEntry;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class DeliveryNoteCustomerNameQuickSearchEntry
	extends AbstractArticleContainerQuickSearchEntry<DeliveryNote, DeliveryNoteQuickSearchQuery>
{
//	private DeliveryNoteQuickSearchQuery query = null;
	
	public DeliveryNoteCustomerNameQuickSearchEntry(QuickSearchEntryFactory<DeliveryNote, DeliveryNoteQuickSearchQuery> factory) {
		super(factory, DeliveryNoteQuickSearchQuery.class);
	}

//	@Override
//	public AbstractArticleContainerQuickSearchQuery getQuery() {
//		AbstractArticleContainerQuickSearchQuery query = new DeliveryNoteQuickSearchQuery();
//		query.setCustomerName(getSearchText());
//		return query;
//	}

//	@Override
//	public String[] getFetchGroups() {
//		return DeliveryNoteEntryViewer.FETCH_GROUPS_DELIVERY_NOTES;
//	}

//	@Override
//	public void setSearchConditionValue(String searchText)
//	{
////		if (query == null)
////		{
////			query = getQueryRegistry().getQueryOfType(DeliveryNoteQuickSearchQuery.class);
////		}
////		query.setCustomerName(searchText);
//		
//		getQueryOfType(DeliveryNoteQuickSearchQuery.class).setCustomerName(searchText);
//	}

//	@Override
//	public void unsetSearchCondition()
//	{
////		if (query == null)
////			return;
////		
////		query.setCustomerName(null);
//		getQueryOfType(DeliveryNoteQuickSearchQuery.class).setCustomerName(null);
//	}

	@Override
	protected void doSetSearchConditionValue(DeliveryNoteQuickSearchQuery query, String value)
	{
		query.setCustomerName(value);
	}

	@Override
	protected void doUnsetSearchConditionValue(DeliveryNoteQuickSearchQuery query)
	{
		query.setCustomerName(null);
	}

//	@Override
//	protected void doResetSearchCondition(DeliveryNoteQuickSearchQuery query, String lastValue)
//	{
//		query.setCustomerName(lastValue);
//	}

}
