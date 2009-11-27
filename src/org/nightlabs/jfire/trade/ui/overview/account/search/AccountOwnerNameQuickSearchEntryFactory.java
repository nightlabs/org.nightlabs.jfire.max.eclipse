package org.nightlabs.jfire.trade.ui.overview.account.search;

import org.nightlabs.base.ui.validation.InputValidator;
import org.nightlabs.jfire.accounting.query.AccountQuery;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntryFactory;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.StringIDStringValidator;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class AccountOwnerNameQuickSearchEntryFactory
extends AbstractQuickSearchEntryFactory<AccountQuery>
{
	public QuickSearchEntry<AccountQuery> createQuickSearchEntry()
	{
		return new AccountOwnerNameQuickSearchEntry(this);
	}

	@Override
	public String getName() {
		return Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.search.AccountOwnerNameQuickSearchEntry.name"); //$NON-NLS-1$
	}

	@Override
	public Class<? extends AccountQuery> getQueryType()
	{
		return AccountQuery.class;
	}

	@Override
	protected InputValidator<?> createInputValidator()
	{
		return new StringIDStringValidator();
	}
}
