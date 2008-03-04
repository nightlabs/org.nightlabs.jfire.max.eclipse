package org.nightlabs.jfire.trade.ui.overview.account;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.query.AccountQuery;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterFactory;

/**
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class AdvancedAccountSearchFilterFactory
	extends AbstractQueryFilterFactory<Account, AccountQuery>
{

	@Override
	public AbstractQueryFilterComposite<Account, AccountQuery> createQueryFilter(Composite parent,
		int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode,
		QueryProvider<Account, ? super AccountQuery> queryProvider)
	{
		return new AccountFilterComposite(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

}
