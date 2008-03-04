package org.nightlabs.jfire.trade.ui.overview.deliverynote.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.query.DeliveryNoteQuickSearchQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerIDQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class DeliveryNoteIDQuickSearchEntryFactory
	extends AbstractArticleContainerIDQuickSearchEntryFactory<DeliveryNote, DeliveryNoteQuickSearchQuery>
{
	public QuickSearchEntry<DeliveryNote, DeliveryNoteQuickSearchQuery> createQuickSearchEntry() {
		return new DeliveryNoteIDQuickSearchEntry(this);
	}

	@Override
	public Class<DeliveryNoteQuickSearchQuery> getQueryType()
	{
		return DeliveryNoteQuickSearchQuery.class;
	}
}
