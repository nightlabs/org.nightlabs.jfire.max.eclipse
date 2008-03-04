package org.nightlabs.jfire.trade.ui.overview.order;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.query.OrderQuery;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerFilterComposite;

/**
 * Order must not have a statable filter composite!
 * 
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class OrderFilterComposite
	extends AbstractArticleContainerFilterComposite<Order, OrderQuery>
{
	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 * @param queryProvider
	 */
	public OrderFilterComposite(Composite parent, int style, LayoutMode layoutMode,
		LayoutDataMode layoutDataMode, QueryProvider<Order, ? super OrderQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public OrderFilterComposite(Composite parent, int style,
		QueryProvider<Order, ? super OrderQuery> queryProvider)
	{
		super(parent, style, queryProvider);
	}

	@Override
	protected Class<OrderQuery> getQueryClass() {
		return OrderQuery.class;
	}

//	@Override
//	protected void createContents(Composite parent) {
//		createArticleContainerComposite(parent);
//	}
	
//	@Override
//	protected List<JDOQueryComposite> registerJDOQueryComposites()
//	{
//		List<JDOQueryComposite> queryComps = new ArrayList<JDOQueryComposite>(2);
//		queryComps.add(articleContainerFilterComposite);
//		return queryComps;
//	}
}
