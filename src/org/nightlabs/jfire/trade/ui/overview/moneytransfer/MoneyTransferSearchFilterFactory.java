package org.nightlabs.jfire.trade.ui.overview.moneytransfer;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.accounting.query.MoneyTransferQuery;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterFactory;

public class MoneyTransferSearchFilterFactory
	extends AbstractQueryFilterFactory<MoneyTransferQuery>
{
	@Override
	public AbstractQueryFilterComposite<MoneyTransferQuery> createQueryFilter(
		Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode,
		QueryProvider<? super MoneyTransferQuery> queryProvider)
	{
		return new MoneyTransferFilterComposite(parent, style, layoutMode, layoutDataMode, queryProvider);
	}
}
