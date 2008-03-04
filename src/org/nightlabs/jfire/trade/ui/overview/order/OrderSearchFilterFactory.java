package org.nightlabs.jfire.trade.ui.overview.order;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterFactory;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.query.OrderQuery;

/**
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class OrderSearchFilterFactory
	extends AbstractQueryFilterFactory<Order, OrderQuery>
{

	@Override
	public AbstractQueryFilterComposite<Order, OrderQuery> createQueryFilter(
		Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode,
		QueryProvider<Order, ? super OrderQuery> queryProvider)
	{
		return new OrderFilterComposite(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

}
