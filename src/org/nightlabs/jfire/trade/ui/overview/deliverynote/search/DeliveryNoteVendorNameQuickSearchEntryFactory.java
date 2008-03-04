package org.nightlabs.jfire.trade.ui.overview.deliverynote.search;

import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.query.DeliveryNoteQuickSearchQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractVendorNameQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class DeliveryNoteVendorNameQuickSearchEntryFactory
	extends AbstractVendorNameQuickSearchEntryFactory<DeliveryNote, DeliveryNoteQuickSearchQuery>
{
	public DeliveryNoteVendorNameQuickSearchEntry createQuickSearchEntry() {
		return new DeliveryNoteVendorNameQuickSearchEntry(this);
	}

	@Override
	public Class<? extends DeliveryNoteQuickSearchQuery> getQueryType()
	{
		return DeliveryNoteQuickSearchQuery.class;
	}
}
