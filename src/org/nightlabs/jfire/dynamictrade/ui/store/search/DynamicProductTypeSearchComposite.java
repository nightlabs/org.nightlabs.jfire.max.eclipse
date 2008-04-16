package org.nightlabs.jfire.dynamictrade.ui.store.search;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.dynamictrade.store.search.DynamicProductTypeQuery;
import org.nightlabs.jfire.store.search.AbstractProductTypeQuery;
import org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchComposite;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DynamicProductTypeSearchComposite
extends AbstractProductTypeSearchComposite
{
	/**
	 * @param parent
	 * @param style
	 */
	public DynamicProductTypeSearchComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected AbstractProductTypeQuery createNewQuery()
	{
		return new DynamicProductTypeQuery();
	}

}
