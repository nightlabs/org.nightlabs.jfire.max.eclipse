package org.nightlabs.jfire.trade.ui.overview.account.search;

import org.nightlabs.jfire.accounting.query.AccountQuery;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class AccountNameQuickSearchEntry
	extends AbstractQuickSearchEntry<AccountQuery>
{
	public AccountNameQuickSearchEntry(QuickSearchEntryFactory<AccountQuery> factory) {
		super(factory, AccountQuery.class);
	}

	@Override
	protected void doSetSearchConditionValue(AccountQuery query, String value)
	{
		if (value != null && !value.isEmpty())
			query.setAccountName(".*" + value + ".*"); //$NON-NLS-1$ //$NON-NLS-2$
		else
			query.setAccountName(null);
	}

	@Override
	protected String getModifiedQueryFieldName()
	{
		return AccountQuery.FieldName.accountName;
	}

}
