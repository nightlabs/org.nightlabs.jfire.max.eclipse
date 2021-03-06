package org.nightlabs.jfire.trade.ui.overview.repository.search;

import org.nightlabs.base.ui.validation.InputValidator;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntryFactory;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.StringIDStringValidator;
import org.nightlabs.jfire.store.query.RepositoryQuery;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class RepositoryOwnerNameQuickSearchEntryFactory
extends AbstractQuickSearchEntryFactory<RepositoryQuery>
{
	@Override
	public String getName()
	{
		return Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.search.RepositoryOwnerNameQuickSearchEntry.name"); //$NON-NLS-1$
	}

	public QuickSearchEntry<RepositoryQuery> createQuickSearchEntry()
	{
		return new RepositoryOwnerNameQuickSearchEntry(this);
	}

	@Override
	public Class<? extends RepositoryQuery> getQueryType()
	{
		return RepositoryQuery.class;
	}

	@Override
	protected InputValidator<?> createInputValidator()
	{
		return new StringIDStringValidator();
	}
}
