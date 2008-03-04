package org.nightlabs.jfire.trade.ui.overview.repository.search;

import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntryFactory;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.store.Repository;
import org.nightlabs.jfire.store.query.RepositoryQuery;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class RepositoryOwnerNameQuickSearchEntryFactory
extends AbstractQuickSearchEntryFactory<Repository, RepositoryQuery>
{
	@Override
	public String getName() {
		return Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.search.RepositoryOwnerNameQuickSearchEntry.name"); //$NON-NLS-1$
	}
	
	public QuickSearchEntry<Repository, RepositoryQuery> createQuickSearchEntry() {
		return new RepositoryOwnerNameQuickSearchEntry(this);
	}

	@Override
	public Class<? extends RepositoryQuery> getQueryType()
	{
		return RepositoryQuery.class;
	}
}
