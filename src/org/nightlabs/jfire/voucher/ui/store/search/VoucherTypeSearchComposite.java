package org.nightlabs.jfire.voucher.ui.store.search;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.store.search.ProductTypeQuery;
import org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchComposite;
import org.nightlabs.jfire.voucher.store.search.VoucherTypeQuery;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherTypeSearchComposite 
extends AbstractProductTypeSearchComposite 
{
	/**
	 * @param parent
	 * @param style
	 */
	public VoucherTypeSearchComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected ProductTypeQuery createNewQuery() {
		return new VoucherTypeQuery();
	}

}
