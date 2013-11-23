package org.nightlabs.jfire.trade.ui.overview.repository.search;

import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.store.query.RepositoryQuery;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class RepositoryOwnerNameQuickSearchEntry
	extends AbstractQuickSearchEntry<RepositoryQuery>
{
	public RepositoryOwnerNameQuickSearchEntry(QuickSearchEntryFactory<RepositoryQuery> factory)
	{
		super(factory, RepositoryQuery.class);
	}

	@Override
	protected String getModifiedQueryFieldName()
	{
		return RepositoryQuery.FieldName.ownerName;
	}
}
