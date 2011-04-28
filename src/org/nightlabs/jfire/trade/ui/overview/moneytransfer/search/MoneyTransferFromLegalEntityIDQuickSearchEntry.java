package org.nightlabs.jfire.trade.ui.overview.moneytransfer.search;

import org.nightlabs.jfire.accounting.query.MoneyTransferQuery;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;

public class MoneyTransferFromLegalEntityIDQuickSearchEntry
	extends AbstractQuickSearchEntry<MoneyTransferQuery>
{
	public MoneyTransferFromLegalEntityIDQuickSearchEntry(QuickSearchEntryFactory<MoneyTransferQuery> factory)
	{
		super(factory, MoneyTransferQuery.class);
	}

	@Override
	protected void doSetSearchConditionValue(MoneyTransferQuery query, String legalEntityID)
	{
		query.setFromLegalEntityID(".*" + legalEntityID + ".*"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	protected String getModifiedQueryFieldName()
	{
		return MoneyTransferQuery.FieldName.fromLegalEntityID;
	}
}