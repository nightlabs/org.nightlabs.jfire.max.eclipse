package org.nightlabs.jfire.trade.ui.overview.order;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.ui.JDOQueryComposite;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerFilterComposite;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class OrderFilterComposite 
extends AbstractArticleContainerFilterComposite 
{
	/**
	 * @param parent
	 * @param style
	 */
	public OrderFilterComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Class getQueryClass() {
		return Order.class;
	}

	@Override
	protected void createContents(Composite parent) {
		createArticleContainerComposite(parent);
	}
	
	@Override
	protected List<JDOQueryComposite> registerJDOQueryComposites() 
	{
		List<JDOQueryComposite> queryComps = new ArrayList<JDOQueryComposite>(2);
		queryComps.add(articleContainerFilterComposite);
		return queryComps;
	}	
}
