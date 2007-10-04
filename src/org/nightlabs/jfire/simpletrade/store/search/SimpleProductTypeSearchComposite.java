package org.nightlabs.jfire.simpletrade.store.search;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.store.search.ProductTypeQuery;
import org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchComposite;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SimpleProductTypeSearchComposite 
extends AbstractProductTypeSearchComposite 
{
	/**
	 * @param parent
	 * @param style
	 */
	public SimpleProductTypeSearchComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected ProductTypeQuery createNewQuery() {
		return new SimpleProductTypeQuery();
	}

}
