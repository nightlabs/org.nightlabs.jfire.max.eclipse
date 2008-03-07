package org.nightlabs.jfire.trade.ui.overview.receptionnote.search;

import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.store.ReceptionNote;
import org.nightlabs.jfire.trade.query.ReceptionNoteQuery;

/**
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class ReceptionNoteVendorNameQuickSearchEntry
	extends AbstractQuickSearchEntry<ReceptionNote, ReceptionNoteQuery>
{

	public ReceptionNoteVendorNameQuickSearchEntry(
		QuickSearchEntryFactory<ReceptionNote, ReceptionNoteQuery> factory
		)
	{
		super(factory, ReceptionNoteQuery.class);
	}

	@Override
	protected void doSetSearchConditionValue(ReceptionNoteQuery query, String value)
	{
		query.setVendorName(value);
	}

	@Override
	protected void doUnsetSearchConditionValue(ReceptionNoteQuery query)
	{
		query.setVendorName(null);
	}

}
