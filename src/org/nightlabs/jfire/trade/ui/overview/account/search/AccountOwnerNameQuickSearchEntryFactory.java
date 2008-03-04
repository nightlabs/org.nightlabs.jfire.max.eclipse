package org.nightlabs.jfire.trade.ui.overview.account.search;

import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.query.AccountQuery;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntryFactory;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class AccountOwnerNameQuickSearchEntryFactory
extends AbstractQuickSearchEntryFactory<Account, AccountQuery>
{
	public QuickSearchEntry<Account, AccountQuery> createQuickSearchEntry() {
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
}
