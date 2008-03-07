package org.nightlabs.jfire.trade.ui.overview.receptionnote.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.store.ReceptionNote;
import org.nightlabs.jfire.trade.query.ReceptionNoteQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractVendorNameQuickSearchEntryFactory;

/**
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class ReceptionNoteVendorNameQuickSearchFactory
	extends AbstractVendorNameQuickSearchEntryFactory<ReceptionNote, ReceptionNoteQuery>
{

	@Override
	public QuickSearchEntry<ReceptionNote, ReceptionNoteQuery> createQuickSearchEntry()
	{
		return new ReceptionNoteVendorNameQuickSearchEntry(this);
	}

	@Override
	public Class<? extends ReceptionNoteQuery> getQueryType()
	{
		return ReceptionNoteQuery.class;
	}

}
