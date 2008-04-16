package org.nightlabs.jfire.trade.ui.overview.repository.search;

import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.store.query.RepositoryQuery;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class RepositoryNameQuickSearchEntry
	extends AbstractQuickSearchEntry<RepositoryQuery>
{
	public RepositoryNameQuickSearchEntry(QuickSearchEntryFactory<RepositoryQuery> factory)
	{
		super(factory, RepositoryQuery.class);
	}

	@Override
	protected void doSetSearchConditionValue(RepositoryQuery query, String value)
	{
		query.setName(value);
	}

	@Override
	protected void doUnsetSearchConditionValue(RepositoryQuery query)
	{
		query.setName(null);
	}
}
