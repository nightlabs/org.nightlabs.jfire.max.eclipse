package org.nightlabs.jfire.trade.ui.overview.repository;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterFactory;
import org.nightlabs.jfire.store.Repository;
import org.nightlabs.jfire.store.query.RepositoryQuery;

/**
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class RepositorySearchFilterFactory
	extends AbstractQueryFilterFactory<Repository, RepositoryQuery>
{

	@Override
	public AbstractQueryFilterComposite<Repository, RepositoryQuery> createQueryFilter(
		Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode,
		QueryProvider<Repository, ? super RepositoryQuery> queryProvider)
	{
		return new RepositoryFilterComposite(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

}
