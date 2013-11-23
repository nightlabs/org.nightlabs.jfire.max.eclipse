package org.nightlabs.jfire.trade.ui.overview.deliverynote.search;

import org.nightlabs.jfire.trade.query.DeliveryNoteQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractVendorNameQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class DeliveryNoteVendorNameQuickSearchEntryFactory
	extends AbstractVendorNameQuickSearchEntryFactory<DeliveryNoteQuery>
{
	public DeliveryNoteVendorNameQuickSearchEntry createQuickSearchEntry()
	{
		return new DeliveryNoteVendorNameQuickSearchEntry(this);
	}

	@Override
	public Class<? extends DeliveryNoteQuery> getQueryType()
	{
		return DeliveryNoteQuery.class;
	}
}
