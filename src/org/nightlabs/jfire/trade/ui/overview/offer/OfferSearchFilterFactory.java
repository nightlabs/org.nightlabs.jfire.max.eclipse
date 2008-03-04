package org.nightlabs.jfire.trade.ui.overview.offer;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterFactory;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.query.OfferQuickSearchQuery;

/**
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class OfferSearchFilterFactory
	extends AbstractQueryFilterFactory<Offer, OfferQuickSearchQuery>
{

	@Override
	public AbstractQueryFilterComposite<Offer, OfferQuickSearchQuery> createQueryFilter(
		Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode,
		QueryProvider<Offer, ? super OfferQuickSearchQuery> queryProvider)
	{
		return new OfferFilterComposite(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

}
