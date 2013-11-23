package org.nightlabs.jfire.trade.ui.overview.deliverynote.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.query.DeliveryNoteQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerIDQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class DeliveryNoteIDQuickSearchEntryFactory
	extends AbstractArticleContainerIDQuickSearchEntryFactory<DeliveryNoteQuery>
{
	public QuickSearchEntry<DeliveryNoteQuery> createQuickSearchEntry()
	{
		return new DeliveryNoteIDQuickSearchEntry(this);
	}

	@Override
	public Class<DeliveryNoteQuery> getQueryType()
	{
		return DeliveryNoteQuery.class;
	}
}
