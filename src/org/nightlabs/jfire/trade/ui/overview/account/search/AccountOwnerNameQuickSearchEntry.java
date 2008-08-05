package org.nightlabs.jfire.trade.ui.overview.account.search;

import org.nightlabs.jfire.accounting.query.AccountQuery;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class AccountOwnerNameQuickSearchEntry
extends AbstractQuickSearchEntry<AccountQuery>
{
	public AccountOwnerNameQuickSearchEntry(QuickSearchEntryFactory<AccountQuery> factory)
	{
		super(factory, AccountQuery.class);
	}

	@Override
	protected String getModifiedQueryFieldName()
	{
		return AccountQuery.FieldName.ownerName;
	}

}
