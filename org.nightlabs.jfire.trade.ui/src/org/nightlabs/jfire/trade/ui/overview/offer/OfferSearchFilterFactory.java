package org.nightlabs.jfire.trade.ui.overview.offer;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterFactory;
import org.nightlabs.jfire.trade.query.OfferQuery;

/**
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class OfferSearchFilterFactory
	extends AbstractQueryFilterFactory<OfferQuery>
{

	@Override
	public AbstractQueryFilterComposite<OfferQuery> createQueryFilter(
		Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode,
		QueryProvider<? super OfferQuery> queryProvider)
	{
		return new OfferFilterComposite(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

}
