package org.nightlabs.jfire.trade.ui.overview.repository.search;

import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntryFactory;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.store.query.RepositoryQuery;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class RepositoryNameQuickSearchEntryFactory
extends AbstractQuickSearchEntryFactory<RepositoryQuery>
{
	@Override
	public String getName()
	{
		return Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.search.RepositoryNameQuickSearchEntry.name"); //$NON-NLS-1$
	}

	public QuickSearchEntry<RepositoryQuery> createQuickSearchEntry()
	{
		return new RepositoryNameQuickSearchEntry(this);
	}

	@Override
	public Class<? extends RepositoryQuery> getQueryType()
	{
		return RepositoryQuery.class;
	}
}
