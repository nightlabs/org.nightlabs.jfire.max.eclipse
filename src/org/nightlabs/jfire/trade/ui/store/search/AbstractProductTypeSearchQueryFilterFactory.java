/**
 * 
 */
package org.nightlabs.jfire.trade.ui.store.search;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterFactory;
import org.nightlabs.jfire.store.search.AbstractProductTypeQuery;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public abstract class AbstractProductTypeSearchQueryFilterFactory<Q extends AbstractProductTypeQuery>
extends AbstractQueryFilterFactory<Q>
{
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.overview.search.QueryFilterFactory#createQueryFilter(org.eclipse.swt.widgets.Composite, int, org.nightlabs.base.ui.composite.XComposite.LayoutMode, org.nightlabs.base.ui.composite.XComposite.LayoutDataMode, org.nightlabs.jdo.query.QueryProvider)
	 */
	@Override
	public AbstractQueryFilterComposite<Q> createQueryFilter(Composite parent,
			int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode,
			QueryProvider<? super Q> queryProvider) 
	{
		return new ProductTypeSearchCriteriaComposite<Q>(parent, style, layoutMode,
				layoutDataMode, queryProvider, getQueryClass());
	}
	
	protected abstract Class<Q> getQueryClass();	
}
