package org.nightlabs.jfire.trade.ui.overview.deliverynote.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuery;
import org.nightlabs.jfire.trade.query.DeliveryNoteQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerQuickSearchEntry;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public class DeliveryNoteVendorNameQuickSearchEntry
	extends AbstractArticleContainerQuickSearchEntry<DeliveryNoteQuery>
{
	public DeliveryNoteVendorNameQuickSearchEntry(QuickSearchEntryFactory<DeliveryNoteQuery> factory)
	{
		super(factory, DeliveryNoteQuery.class);
	}

	@Override
	protected String getModifiedQueryFieldName()
	{
		return AbstractArticleContainerQuery.FieldName.vendorName;
	}

}
