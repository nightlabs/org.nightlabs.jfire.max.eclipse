package org.nightlabs.jfire.trade.ui.overview.moneytransfer.search;

import org.nightlabs.jfire.accounting.query.MoneyTransferQuery;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntryFactory;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;

public class MoneyTransferFromLegalEntityIDQuickSearchEntryFactory
extends AbstractQuickSearchEntryFactory<MoneyTransferQuery>
{
	@Override
	public QuickSearchEntry<MoneyTransferQuery> createQuickSearchEntry()
	{
		return new MoneyTransferFromLegalEntityIDQuickSearchEntry(this);
	}

	@Override
	public String getName() {
		return "From LegalEntity ID"; //$NON-NLS-1$
	}

	@Override
	public Class<? extends MoneyTransferQuery> getQueryType()
	{
		return MoneyTransferQuery.class;
	}
}