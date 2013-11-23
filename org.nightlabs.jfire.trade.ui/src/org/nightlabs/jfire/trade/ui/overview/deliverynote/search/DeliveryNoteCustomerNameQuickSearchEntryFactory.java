package org.nightlabs.jfire.trade.ui.overview.deliverynote.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.query.DeliveryNoteQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractCustomerNameQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class DeliveryNoteCustomerNameQuickSearchEntryFactory
extends AbstractCustomerNameQuickSearchEntryFactory<DeliveryNoteQuery>
{
	public QuickSearchEntry<DeliveryNoteQuery> createQuickSearchEntry()
	{
		return new DeliveryNoteCustomerNameQuickSearchEntry(this);
	}

	@Override
	public Class<DeliveryNoteQuery> getQueryType()
	{
		return DeliveryNoteQuery.class;
	}
}
