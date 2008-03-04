package org.nightlabs.jfire.trade.ui.overview.deliverynote.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.query.DeliveryNoteQuickSearchQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractCustomerNameQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class DeliveryNoteCustomerNameQuickSearchEntryFactory
extends AbstractCustomerNameQuickSearchEntryFactory<DeliveryNote, DeliveryNoteQuickSearchQuery>
{
	public QuickSearchEntry<DeliveryNote, DeliveryNoteQuickSearchQuery> createQuickSearchEntry() {
		return new DeliveryNoteCustomerNameQuickSearchEntry(this);
	}

	@Override
	public Class<DeliveryNoteQuickSearchQuery> getQueryType()
	{
		return DeliveryNoteQuickSearchQuery.class;
	}
}
