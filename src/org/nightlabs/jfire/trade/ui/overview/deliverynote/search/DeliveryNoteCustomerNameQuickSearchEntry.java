package org.nightlabs.jfire.trade.ui.overview.deliverynote.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.query.DeliveryNoteQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerQuickSearchEntry;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class DeliveryNoteCustomerNameQuickSearchEntry
	extends AbstractArticleContainerQuickSearchEntry<DeliveryNote, DeliveryNoteQuery>
{
//	private DeliveryNoteQuery query = null;
	
	public DeliveryNoteCustomerNameQuickSearchEntry(QuickSearchEntryFactory<DeliveryNote, DeliveryNoteQuery> factory) {
		super(factory, DeliveryNoteQuery.class);
	}

//	@Override
//	public AbstractArticleContainerQuery getQuery() {
//		AbstractArticleContainerQuery query = new DeliveryNoteQuery();
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
////			query = getQueryRegistry().getQueryOfType(DeliveryNoteQuery.class);
////		}
////		query.setCustomerName(searchText);
//		
//		getQueryOfType(DeliveryNoteQuery.class).setCustomerName(searchText);
//	}

//	@Override
//	public void unsetSearchCondition()
//	{
////		if (query == null)
////			return;
////		
////		query.setCustomerName(null);
//		getQueryOfType(DeliveryNoteQuery.class).setCustomerName(null);
//	}

	@Override
	protected void doSetSearchConditionValue(DeliveryNoteQuery query, String value)
	{
		query.setCustomerName(value);
	}

	@Override
	protected void doUnsetSearchConditionValue(DeliveryNoteQuery query)
	{
		query.setCustomerName(null);
	}

//	@Override
//	protected void doResetSearchCondition(DeliveryNoteQuery query, String lastValue)
//	{
//		query.setCustomerName(lastValue);
//	}

}
