package org.nightlabs.jfire.trade.ui.overview.receptionnote.search;

import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuery;
import org.nightlabs.jfire.trade.query.ReceptionNoteQuery;

/**
 *
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class ReceptionNoteCustomerNameQuickSearchEntry
	extends AbstractQuickSearchEntry<ReceptionNoteQuery>
{
	public ReceptionNoteCustomerNameQuickSearchEntry(
		QuickSearchEntryFactory<ReceptionNoteQuery> factory)
	{
		super(factory, ReceptionNoteQuery.class);
	}

	@Override
	protected String getModifiedQueryFieldName()
	{
		return AbstractArticleContainerQuery.FieldName.customerName;
	}
}
