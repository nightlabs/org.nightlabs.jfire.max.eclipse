package org.nightlabs.jfire.trade.ui.store.search;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.search.ProductTypeQuery;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class GenericProductTypeSearchComposite 
extends AbstractProductTypeSearchComposite 
{
	/**
	 * @param parent
	 * @param style
	 */
	public GenericProductTypeSearchComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected ProductTypeQuery createNewQuery() {
		return new ProductTypeQuery<ProductType>();
	}

}
