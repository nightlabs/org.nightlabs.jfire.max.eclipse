package org.nightlabs.jfire.trade.ui.overview.account.search;

import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.query.AccountQuery;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class AccountOwnerNameQuickSearchEntry
extends AbstractQuickSearchEntry<Account, AccountQuery>
{
	public AccountOwnerNameQuickSearchEntry(QuickSearchEntryFactory<Account, AccountQuery> factory) {
		super(factory, AccountQuery.class);
	}

//	@Override
//	protected void doResetSearchCondition(AccountQuery query, String lastValue)
//	{
//		query.setOwnerName(lastValue);
//	}

	@Override
	protected void doSetSearchConditionValue(AccountQuery query, String value)
	{
		query.setOwnerName(value);
	}

	@Override
	protected void doUnsetSearchConditionValue(AccountQuery query)
	{
		query.setOwnerName(null);
	}

//	public Object search(ProgressMonitor monitor) {
//		AccountQuery query = new AccountQuery();
//		query.setOwnerName(getSearchText());
//		query.setFromInclude(getMinIncludeRange());
//		query.setToExclude(getMaxExcludeRange());
//		Collection<AccountQuery> queries = Collections.singleton(query);
//		return AccountDAO.sharedInstance().getAccountsForQueries(
//				queries,
//				AccountEntryViewer.FETCH_GROUPS_ACCOUNTS,
//				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
//	}
}
