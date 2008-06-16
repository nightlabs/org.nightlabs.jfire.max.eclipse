package org.nightlabs.jfire.trade.ui.overview.account;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.accounting.query.AccountQuery;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterFactory;

/**
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class AdvancedAccountSearchFilterFactory
	extends AbstractQueryFilterFactory<AccountQuery>
{

	@Override
	public AbstractQueryFilterComposite<AccountQuery> createQueryFilter(Composite parent,
		int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode,
		QueryProvider<? super AccountQuery> queryProvider)
	{
		return new AccountFilterComposite(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

}
