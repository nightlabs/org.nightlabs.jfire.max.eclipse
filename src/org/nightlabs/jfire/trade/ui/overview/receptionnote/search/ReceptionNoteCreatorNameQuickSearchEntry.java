package org.nightlabs.jfire.trade.ui.overview.receptionnote.search;

import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.query.ReceptionNoteQuery;

/**
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class ReceptionNoteCreatorNameQuickSearchEntry
	extends AbstractQuickSearchEntry<ReceptionNoteQuery>
{

	public ReceptionNoteCreatorNameQuickSearchEntry(
		QuickSearchEntryFactory<ReceptionNoteQuery> factory)
	{
		super(factory, ReceptionNoteQuery.class);
	}

	@Override
	protected void doSetSearchConditionValue(ReceptionNoteQuery query, String value)
	{
		query.setCreatorName(value);
	}

	@Override
	protected void doUnsetSearchConditionValue(ReceptionNoteQuery query)
	{
		query.setCreatorName(null);
	}

}
